package com.infmme.readilyapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.infmme.readilyapp.R;
import com.infmme.readilyapp.essential.TextParser;
import com.infmme.readilyapp.readable.NetReadable;
import com.infmme.readilyapp.readable.Readable;
import com.infmme.readilyapp.readable.interfaces.Chunked;
import com.infmme.readilyapp.readable.interfaces.Reading;
import com.infmme.readilyapp.readable.interfaces.Storable;
import com.infmme.readilyapp.readable.storable.epub.EpubStorable;
import com.infmme.readilyapp.readable.type.ReadableType;
import com.infmme.readilyapp.readable.type.ReadingSource;
import com.infmme.readilyapp.reader.MonitorObject;
import com.infmme.readilyapp.reader.Reader;
import com.infmme.readilyapp.reader.ReaderTask;
import com.infmme.readilyapp.settings.SettingsBundle;
import com.infmme.readilyapp.util.Constants;
import com.infmme.readilyapp.view.OnSwipeTouchListener;
import org.joda.time.LocalDateTime;

import java.io.IOException;
import java.util.List;

/**
 * infm : 16/05/14. Enjoy it ;)
 */
public class ReaderFragment extends Fragment implements Reader.ReaderCallbacks,
    ReaderTask.ReaderTaskCallbacks {

  private static final int NOTIF_APPEARING_DURATION = 300;
  /**
   * Time in ms for which mNotificationTextView becomes visible.
   */
  private static final int NOTIF_SHOWING_LENGTH = 1500;

  /**
   * ms duration of 'Bounce' animation on mReader window
   */
  public static final int READER_PULSE_DURATION = 400;

  private static final float POINTER_LEFT_PADDING_COEFFICIENT = 5f / 18f;
  //some magic number
  private static final String[] LIGHT_COLOR_SET = new String[] { "#0A0A0A",
      "#AAAAAA" };
  private static final String[] DARK_COLOR_SET = new String[] { "#FFFFFF",
      "#999999", "#FF282828" };
  private static final String EMPHASIS_CHAR_COLOR = "#FA2828";

  //initialized in onCreate()
  private Handler mHandler;
  private long mLocalTime = 0;
  private boolean mNotificationHided = true;
  private boolean mInfoHided = true;
  private Bundle mArgs;

  //initialized in onCreateView()
  private RelativeLayout mReaderLayout;
  private RelativeLayout mInfoLayout;
  private TextView mWpmTextView;
  private TextView mPositionTextView;
  private TextView mCurrentTextView;
  private TextView mLeftTextView;
  private TextView mRightTextView;
  private TextView mNotificationTextView;
  private ProgressBar mProgressBar;
  private ProgressBar mParsingProgressBar;
  private ImageButton mPrevButton;
  private View mUpLogo;

  //initialized in onActivityCreated()
  private Reader mReader;
  private ReaderTask mReaderTask;
  private MonitorObject mMutex;

  private Reading mReading;
  private Chunked mChunked = null;
  private Storable mStorable = null;

  private SettingsBundle mSettingsBundle;
  private Thread mParserThread;

  //receiving status
  private boolean mParserReceived = false;
  private String mPrimaryTextColor = LIGHT_COLOR_SET[0];
  private String mSecondaryTextColor = LIGHT_COLOR_SET[1];
  private int mProgress;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mArgs = getArguments();
    mHandler = new Handler();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View fragmentLayout = inflater.inflate(R.layout.fragment_reader, container,
                                           false);
    findViews((ViewGroup) fragmentLayout);
    return fragmentLayout;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    Activity activity = getActivity();
    setReaderLayoutListener(activity);
    mSettingsBundle = new SettingsBundle(
        PreferenceManager.getDefaultSharedPreferences(activity));

    setReaderBackground();
    initPrevButton();
    setReaderFontSize();

    handleArgs(mArgs);
  }

  public void onSwipeTop() {
    changeWPM(Constants.WPM_STEP_READER);
  }

  public void onSwipeBottom() {
    changeWPM(-1 * Constants.WPM_STEP_READER);
  }

  private void setCurrentTime(long localTime) {
    this.mLocalTime = localTime;
  }

  /**
   * Generates formatted text before emphasis point
   *
   * @param word     Word to show
   * @param emphasis Word emphasis position
   * @return Spanned object to draw
   */
  private Spanned getFormattedLeft(String word, int emphasis) {
    if (TextUtils.isEmpty(word))
      return Html.fromHtml("");
    return Html.fromHtml("<font color='" + mPrimaryTextColor + "'>" +
                             word.substring(0, emphasis) + "</font>");
  }

  /**
   * Generates formatted emphasis character
   *
   * @param word     Word to show
   * @param emphasis Word emphasis position
   * @return Spanned object to draw
   */
  private Spanned getFormattedEmphasis(String word, int emphasis) {
    if (TextUtils.isEmpty(word))
      return Html.fromHtml("");
    return Html.fromHtml("<font color='" + EMPHASIS_CHAR_COLOR + "'>" +
                             word.substring(emphasis,
                                            emphasis + 1) + "</font>");
  }

  /**
   * Generates formatted text after emphasis character
   * (part of current word, if exists and next ones, if option is enabled)
   *
   * @param word     Word to show
   * @param emphasis Word emphasis position
   * @return Spanned object to draw
   */
  private Spanned getFormattedRight(String word, List<String> nextWords,
                                    int emphasis) {
    if (TextUtils.isEmpty(word))
      return Html.fromHtml("");
    StringBuilder format = new StringBuilder(
        "<font><font color='" + mPrimaryTextColor + "'>" +
            word.substring(emphasis + 1,
                           word.length()) + "</font>");
    if (mSettingsBundle.isShowingContextEnabled())
      format.append(getNextWordsFormat(nextWords));
    format.append("</font>");
    return Html.fromHtml(format.toString());
  }

  /**
   * Generates Html formatted String of next words in text (called if
   * 'context' option is enabled)
   *
   * @param nextWords A couple of next words to show in a reader view
   * @return Html format String
   */
  private String getNextWordsFormat(List<String> nextWords) {
    int charLen = 0;
    int wordListIndex = 0;
    StringBuilder format = new StringBuilder(
        "&nbsp;<font color='" + mSecondaryTextColor + "'>");
    while (charLen < 40 && wordListIndex < nextWords.size() - 1) {
      String word = nextWords.get(++wordListIndex);
      if (!TextUtils.isEmpty(word)) {
        charLen += word.length() + 1;
        format.append(word).append(" ");
      }
    }
    format.append("</font>");
    return format.toString();
  }

  /**
   * Finds all Views in main Fragment ViewGroup
   *
   * @param v ViewGroup in which views are found
   */
  private void findViews(ViewGroup v) {
    mReaderLayout = (RelativeLayout) v.findViewById(R.id.reader_layout);
    mParsingProgressBar = (ProgressBar) v.findViewById(R.id.parsingProgressBar);
    mCurrentTextView = (TextView) v.findViewById(R.id.currentWordTextView);
    mLeftTextView = (TextView) v.findViewById(R.id.leftWordTextView);
    mRightTextView = (TextView) v.findViewById(R.id.rightWordTextView);
    mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
    mNotificationTextView = (TextView) v.findViewById(R.id.reader_notification);
    mPrevButton = (ImageButton) v.findViewById(R.id.previousWordImageButton);
    mUpLogo = v.findViewById(R.id.logo_up);
    mInfoLayout = (RelativeLayout) v.findViewById(R.id.reader_info_layout);
    mWpmTextView = (TextView) v.findViewById(R.id.text_view_info_speed_value);
    mPositionTextView = (TextView) v.findViewById(
        R.id.text_view_info_position_value);
  }

  private void setReaderLayoutListener(Context context) {
    mReaderLayout.setOnTouchListener(new OnSwipeTouchListener(context) {
      @Override
      public void onSwipeTop() {
        ReaderFragment.this.onSwipeTop();
      }

      @Override
      public void onSwipeBottom() {
        ReaderFragment.this.onSwipeBottom();
      }

      @Override
      public void onSwipeRight() {
        if (mSettingsBundle.isSwipesEnabled()) {
          if (!mReader.isPaused())
            mReader.performPause();
          else
            mReader.moveToPrevious();
        }
      }

      @Override
      public void onSwipeLeft() {
        if (mSettingsBundle.isSwipesEnabled()) {
          if (!mReader.isPaused())
            mReader.performPause();
          else
            mReader.moveToNext();
        }
      }

      @Override
      public void onClick() {
        if (mReader.isCompleted())
          onStop();
        else
          mReader.incCancelled();
      }
    });
  }

  /**
   * Initializes previous word button
   */
  private void initPrevButton() {
    if (!mSettingsBundle.isSwipesEnabled()) {
      mPrevButton.setImageResource(android.R.drawable.ic_media_previous);
      mPrevButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          mReader.moveToPrevious();
        }
      });
      mPrevButton.setVisibility(View.INVISIBLE);
    } else { mPrevButton.setVisibility(View.INVISIBLE); }
  }

  private float spToPixels(Context context, float sp) {
    float scaledDensity = context.getResources()
                                 .getDisplayMetrics().scaledDensity;
    return sp * scaledDensity;
  }

  private void setReaderFontSize() {
    View pointerTop = mReaderLayout.findViewById(R.id.pointerTopImageView);
    View pointerBottom = mReaderLayout.findViewById(
        R.id.pointerBottomImageView);

    float fontSizePx = spToPixels(getActivity(),
                                  (float) mSettingsBundle.getFontSize());
    pointerTop.setPadding(
        (int) (fontSizePx * POINTER_LEFT_PADDING_COEFFICIENT + .5f),
        pointerTop.getPaddingTop(),
        pointerTop.getPaddingRight(), pointerTop.getPaddingBottom());
    pointerBottom.setPadding(
        (int) (fontSizePx * POINTER_LEFT_PADDING_COEFFICIENT + .5f),
        pointerBottom.getPaddingTop(),
        pointerBottom.getPaddingRight(), pointerBottom.getPaddingBottom());

    mCurrentTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                                 mSettingsBundle.getFontSize());
    mLeftTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                              mSettingsBundle.getFontSize());
    mRightTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                               mSettingsBundle.getFontSize());
  }

  private void setReaderBackground() {
    if (mSettingsBundle.isDarkTheme()) {
      ((View) mReaderLayout.getParent()).setBackgroundColor(
          Color.parseColor(DARK_COLOR_SET[2]));
      mPrimaryTextColor = DARK_COLOR_SET[0];
      mSecondaryTextColor = DARK_COLOR_SET[1];
      ((ImageView) mReaderLayout.findViewById(R.id.pointerTopImageView))
          .setImageResource(R.drawable.word_pointer_dark);
      ((ImageView) mReaderLayout.findViewById(R.id.pointerBottomImageView))
          .setImageResource(R.drawable.word_pointer_dark);
    }
  }

  private void showNotification(String text) {
    if (!TextUtils.isEmpty(text)) {
      mNotificationTextView.setText(text);
      setCurrentTime(System.currentTimeMillis());

      if (mNotificationHided) {
        YoYo.with(Techniques.SlideInDown)
            .duration(NOTIF_APPEARING_DURATION)
            .playOn(mNotificationTextView);
        mNotificationTextView.postDelayed(new Runnable() {
          @Override
          public void run() {
            mNotificationTextView.setVisibility(View.VISIBLE);
          }
        }, NOTIF_APPEARING_DURATION);

        YoYo.with(Techniques.FadeOut)
            .duration(NOTIF_APPEARING_DURATION)
            .playOn(mUpLogo);
        mNotificationHided = false;
      }
    }
  }

  @Override
  public void animatePlay() {
    YoYo.with(Techniques.Pulse)
        .duration(READER_PULSE_DURATION)
        .playOn(mReaderLayout);
    if (!mSettingsBundle.isSwipesEnabled()) {
      mPrevButton.setVisibility(View.INVISIBLE);
    }
  }

  @Override
  public void animatePause() {
    YoYo.with(Techniques.Pulse)
        .duration(READER_PULSE_DURATION)
        .playOn(mReaderLayout);
    if (!mSettingsBundle.isSwipesEnabled()) {
      mPrevButton.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public void updateReaderView(String word, List<String> nextWords,
                               int emphasis) {
    mCurrentTextView.setText(getFormattedEmphasis(word, emphasis));
    mLeftTextView.setText(getFormattedLeft(word, emphasis));
    mRightTextView.setText(getFormattedRight(word, nextWords, emphasis));
    mProgressBar.setProgress(mProgress);
    hideNotification(false);
  }

  @Override
  public void showNotification(int resourceId) {
    if (isAdded())
      showNotification(getResources().getString(resourceId));
  }

  @Override
  public boolean hideNotification(boolean force) {
    if (!mNotificationHided) {
      if (force || System.currentTimeMillis() - mLocalTime >
          NOTIF_SHOWING_LENGTH) {
        YoYo.with(Techniques.SlideOutUp)
            .duration(NOTIF_APPEARING_DURATION)
            .playOn(mNotificationTextView);
        mNotificationTextView.postDelayed(new Runnable() {
          @Override
          public void run() {
            mNotificationTextView.setVisibility(View.INVISIBLE);
          }
        }, NOTIF_APPEARING_DURATION);

        YoYo.with(Techniques.FadeIn)
            .duration(NOTIF_APPEARING_DURATION)
            .playOn(mUpLogo);
        mNotificationHided = true;
      }
    }
    return mNotificationHided;
  }

  @Override
  public void showInfo(Reader reader) {
    if (reader != null && mSettingsBundle != null)
      showInfo(mSettingsBundle.getWPM(), (100 - mProgress) + "%");
  }

  private void showInfo(int wpm, String percentLeft) {
    mWpmTextView.setText(wpm + " WPM");
    mPositionTextView.setText(percentLeft);

    if (mInfoHided) {
      YoYo.with(Techniques.FadeIn)
          .duration(NOTIF_APPEARING_DURATION * 2)
          .playOn(mInfoLayout);
      mInfoLayout.postDelayed(new Runnable() {
        @Override
        public void run() {
          mInfoLayout.setVisibility(View.VISIBLE);
        }
      }, NOTIF_APPEARING_DURATION);
      mInfoHided = false;
    }
  }

  @Override
  public void hideInfo() {
    if (!mInfoHided) {
      YoYo.with(Techniques.FadeOut)
          .duration(NOTIF_APPEARING_DURATION)
          .playOn(mInfoLayout);
      mInfoLayout.postDelayed(new Runnable() {
        @Override
        public void run() {
          mInfoLayout.setVisibility(View.INVISIBLE);
        }
      }, NOTIF_APPEARING_DURATION);
      mInfoHided = true;
    }
  }

  @Override
  public Integer getWordsPerMinute() {
    return mSettingsBundle.getWPM();
  }

  @Override
  public boolean hasNextParser() {
    return mReaderTask.hasNextParser();
  }

  @Override
  public TextParser nextParser() throws IOException {
    return mReaderTask.nextParser();
  }

  /**
   * TODO: make max/min optional
   *
   * @param delta : delta itself. Default value: 50
   */
  private void changeWPM(int delta) {
    if (mSettingsBundle != null) {
      int wpm = mSettingsBundle.getWPM();
      int wpmNew = Math.min(Constants.MAX_WPM,
                            Math.max(wpm + delta, Constants.MIN_WPM));

      if (wpm != wpmNew) {
        mSettingsBundle.setWPM(wpmNew);
        showNotification(wpmNew + " WPM");
        mWpmTextView.setText(wpmNew + " WPM");
      }
    }
  }

  @Override
  public Reader startReader(TextParser parser) {
    mReader.changeParser(parser);
    final int initialPosition = Math.max(
        mReading.getPosition() - Constants.READER_START_OFFSET, 0);
    Activity activity = getActivity();
    if (activity != null) {
      activity.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          mParsingProgressBar.clearAnimation();
          mParsingProgressBar.setVisibility(View.GONE);
          mReaderLayout.setVisibility(View.VISIBLE);
          if (true /*initialPosition < wordList.size()*/) {
            showNotification(R.string.tap_to_start);
            // mProgress = mReading.calcProgress(initialPosition, 0);
            mReader.setPosition(initialPosition);
            showInfo(mReader);
          } else {
            showNotification(R.string.reading_is_completed);
            mReader.setCompleted(true);
          }
          YoYo.with(Techniques.FadeIn)
              .duration(READER_PULSE_DURATION)
              .playOn(mReaderLayout);
        }
      });
    }
    return mReader;
  }

  @Override
  public List<Integer> getDelayCoefficients() {
    return mSettingsBundle.getDelayCoefficients();
  }

  @Override
  public boolean shouldContinue() {
    return mReader == null || !mReader.isCompleted();
  }

/*
  private void notifyBadParser(final int resultCode) {
    final Activity a = getActivity();
    if (a != null) {
      a.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          int stringId;
          switch (resultCode) {
            case TextParser.RESULT_CODE_WRONG_EXT:
              stringId = R.string.wrong_ext;
              break;
            case TextParser.RESULT_CODE_EMPTY_CLIPBOARD:
              stringId = R.string.clipboard_empty;
              break;
            case TextParser.RESULT_CODE_CANT_FETCH:
              stringId = R.string.cant_fetch;
              break;
            default:
              stringId = R.string.text_null;
              break;
          }
          Toast.makeText(a, stringId, Toast.LENGTH_SHORT).show();
          onStop();
        }
      });
    }
  }

  private boolean canBeSaved(
      com.infmme.readilyapp.readable.old.Readable readable) {
    return mParserReceived &&
        readable != null &&
        mSettingsBundle != null &&
        !TextUtils.isEmpty(readable.getPath()) &&
        isStorable(readable);
  }

  private boolean isStorable(
      com.infmme.readilyapp.readable.old.Readable readable) {
    return (readable.getType() == com.infmme.readilyapp.readable.old.Readable
        .TYPE_FILE ||
        readable.getType() == com.infmme.readilyapp.readable.old.Readable
            .TYPE_TXT ||
        readable.getType() == com.infmme.readilyapp.readable.old.Readable
            .TYPE_FB2 ||
        readable.getType() == com.infmme.readilyapp.readable.old.Readable
            .TYPE_EPUB ||
        readable.getType() == com.infmme.readilyapp.readable.old.Readable
            .TYPE_NET ||
        readable.getType() == com.infmme.readilyapp.readable.old.Readable
            .TYPE_RAW);
  }

  private boolean isFileStorable(
      com.infmme.readilyapp.readable.old.Readable readable) {
    return isStorable(readable) &&
        readable.getType() != com.infmme.readilyapp.readable.old.Readable
            .TYPE_NET &&
        readable.getType() != com.infmme.readilyapp.readable.old.Readable
            .TYPE_RAW;
  }
*/

  @Override
  public void onStop() {
    if (mReader != null && !mReader.isPaused())
      mReader.performPause();
    if (mStorable != null && mReader != null) {
      new Thread(new Runnable() {
        @Override
        public void run() {
          mStorable.storeToDb();
        }
      }).start();
    }

    mSettingsBundle.updatePreferences();

    if (mReader != null) {
      mReader.setCompleted(true);
    } else if (mParserThread != null && mParserThread.isAlive()) {
      mParserThread.interrupt();
    }
    super.onStop();
  }

  //it's very unflexible, TODO: fix it later
  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (mParserReceived && mReader != null) { mReader.performPause(); }
    View view = getView();
    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view
        .getLayoutParams();
    int currentMargin = params.leftMargin;

    Resources resources = getResources();
    int portMargin,
        landMargin = (int) resources.getDimension(R.dimen.land_margin_left);
    if (currentMargin <= landMargin) {
      portMargin = (int) resources.getDimension(R.dimen.port_margin_left);
    } else {
      portMargin = (int) resources.getDimension(
          R.dimen.port_tablet_margin_left);
      landMargin = (int) resources.getDimension(
          R.dimen.land_tablet_margin_left);
    }
    int newMargin = (currentMargin == portMargin)
        ? landMargin
        : portMargin;
    params.setMargins(newMargin, 0, newMargin, 0);
    view.setLayoutParams(params);
  }

  private void handleArgs(Bundle args) {
    ReadingSource sourceType = ReadingSource.valueOf(
        args.getString(Constants.EXTRA_READING_SOURCE));
    switch (sourceType) {
      case SHARE:
        handleShareSource(args);
        break;
      case CACHE:
        handleCacheSource(args);
        break;
    }
  }

  private void handleShareSource(Bundle args) {
    String intentText;
    if (args.containsKey(Intent.EXTRA_TEXT)) {
      intentText = args.getString(Intent.EXTRA_TEXT);
    } else {
      intentText = getActivity().getResources()
                                .getString(R.string.sample_text);
    }
    String link;
    if (!TextUtils.isEmpty(intentText) &&
        intentText.length() < Constants.NON_LINK_LENGTH &&
        !TextUtils.isEmpty(
            link = TextParser.findLink(TextParser.compilePattern(),
                                       intentText))) {
      mReading = new NetReadable(link);
    } else {
      mReading = new Readable(); //neutral value
      mReading.setText(intentText);
    }
  }

  private void handleCacheSource(Bundle args) {
    String stringType = args.getString(Constants.EXTRA_TYPE);
    if (stringType != null) {
      switch (ReadableType.valueOf(stringType)) {
        case EPUB:
          final EpubStorable epubStorable =
              new EpubStorable(getActivity(), LocalDateTime.now().toString());
          epubStorable.setPath(args.getString(Constants.EXTRA_PATH));
          // TODO: Think about handling location params
          // epubStorable.setTextPosition(args.getInt(Constants.EXTRA_POSITION));
          new Thread(new Runnable() {
            @Override
            public void run() {
              epubStorable.process();
              if (epubStorable.isProcessed()) {
                mChunked = epubStorable;
                mStorable = epubStorable;
                try {
                  mReading = epubStorable.readNext();
                  start();
                } catch (IOException e) {
                  e.printStackTrace();
                }
              }
            }
          }).start();
          break;
        case FB2:
          break;
        case TXT:
          break;
      }
    } else {
      throw new IllegalStateException(
          "Cache source can't be processed without an explicitly set type!");
    }
  }

  private void start() {
    mMutex = new MonitorObject();
    mReader = new Reader(mHandler, mReading, mMutex, this);
    mReaderTask = new ReaderTask(mMutex, mChunked, this);
    mParserThread = new Thread(mReaderTask);
    mParserThread.start();
  }
}
