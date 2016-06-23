package com.infmme.readilyapp.reader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.daimajia.androidanimations.library.BuildConfig;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.infmme.readilyapp.R;
import com.infmme.readilyapp.readable.ClipboardReadable;
import com.infmme.readilyapp.readable.NetReadable;
import com.infmme.readilyapp.readable.Readable;
import com.infmme.readilyapp.readable.epub.EpubStorable;
import com.infmme.readilyapp.readable.fb2.FB2Storable;
import com.infmme.readilyapp.readable.interfaces.Chunked;
import com.infmme.readilyapp.readable.interfaces.ChunkedUnprocessedStorable;
import com.infmme.readilyapp.readable.interfaces.Reading;
import com.infmme.readilyapp.readable.interfaces.Storable;
import com.infmme.readilyapp.readable.txt.TxtStorable;
import com.infmme.readilyapp.readable.type.ReadableType;
import com.infmme.readilyapp.readable.type.ReadingSource;
import com.infmme.readilyapp.service.FB2ProcessingService;
import com.infmme.readilyapp.service.StorableService;
import com.infmme.readilyapp.settings.SettingsBundle;
import com.infmme.readilyapp.util.Constants;
import com.infmme.readilyapp.view.OnSwipeTouchListener;
import org.joda.time.LocalDateTime;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;

import static com.infmme.readilyapp.readable.type.ReadableType.RAW;

/**
 * infm : 16/05/14. Enjoy it ;)
 */
public class ReaderFragment extends Fragment
    implements Reader.ReaderCallbacks,
    ReaderProducerTask.ReaderTaskCallbacks {

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

  private static final int DEQUE_SIZE_LIMIT = 3;

  private Handler mHandler;
  private long mLocalTime = 0;
  private boolean mNotificationHided = true;
  private boolean mInfoHided = true;
  private Bundle mArgs;

  private RelativeLayout mReaderLayout;
  private RelativeLayout mInfoLayout;
  private TextView mWpmTextView;
  private TextView mPositionTextView;
  private TextView mCurrentTextView;
  private TextView mLeftTextView;
  private TextView mRightTextView;
  private TextView mNextTextView;
  private TextView mNotificationTextView;
  private ProgressBar mProgressBar;
  private ProgressBar mParsingProgressBar;
  private ImageButton mPrevButton;
  private View mUpLogo;

  private Reader mReader;
  private ReaderProducerTask mReaderProducerTask;
  private MonitorObject mFullMonitor;
  private boolean mStarted;
  private final ArrayDeque<Reading> mReadingDeque = new ArrayDeque<>();

  private Reading mReading = null;
  private Chunked mChunked = null;
  private Storable mStorable = null;

  private StringBuilder mFormatStrBuilder;

  private SettingsBundle mSettingsBundle;
  private Thread mReadingThread;

  private boolean mHasReaderStarted = false;
  private int mProgress;

  private ReaderFragmentCallback mCallback;

  private CompositeSubscription mCompositeSubscription;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      mCallback = (ReaderFragmentCallback) context;
    } catch (ClassCastException e) {
      e.printStackTrace();
    }
  }

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
    startShowingProgress();
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

  @Override
  public void onStop() {
    if (mReader != null && !mReader.isPaused()) {
      mReader.performPause();
    }
    if (mStorable != null && mReader != null) {
      mStorable.prepareForStoringSync(mReader);
      StorableService.startStoring(getActivity(), mStorable);
    }

    mSettingsBundle.updatePreferences();

    if (mReader != null) {
      mReader.setCompleted(true);
    } else if (mReadingThread != null && mReadingThread.isAlive()) {
      // We fail to initialize mReader, so at least stop reading thread.
      mReadingThread.interrupt();
    }
    if (mCallback != null) {
      mCallback.stop();
    }
    super.onStop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mCompositeSubscription != null &&
        mCompositeSubscription.hasSubscriptions()) {
      mCompositeSubscription.unsubscribe();
    }
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
   * @param nextWords A couple of next words to show in a reader view
   * @return Html format String
   */
  private String getFormattedNextWords(List<String> nextWords) {
    int wordListIndex = 0;
    if (mFormatStrBuilder == null) {
      mFormatStrBuilder = new StringBuilder(" ");
    } else {
      mFormatStrBuilder.setLength(1);
    }
    while (mFormatStrBuilder.length() < 40 &&
        wordListIndex < nextWords.size() - 1) {
      ++wordListIndex;
      if (!TextUtils.isEmpty(nextWords.get(wordListIndex))) {
        mFormatStrBuilder.append(nextWords.get(wordListIndex)).append(" ");
      }
    }
    return mFormatStrBuilder.toString();
  }

  /**
   * Finds all Views in main Fragment ViewGroup
   *
   * @param v ViewGroup in which views are found
   */
  private void findViews(ViewGroup v) {
    mReaderLayout = (RelativeLayout) v.findViewById(R.id.reader_layout);
    mParsingProgressBar = (ProgressBar) v.findViewById(R.id.parsingProgressBar);
    mCurrentTextView = (TextView) v.findViewById(R.id.reader_current_letter);
    mLeftTextView = (TextView) v.findViewById(R.id.reader_word_left);
    mRightTextView = (TextView) v.findViewById(R.id.reader_word_right);
    mNextTextView = (TextView) v.findViewById(R.id.reader_next_words);
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
            mReader.moveToPreviousPosition();
        }
      }

      @Override
      public void onSwipeLeft() {
        if (mSettingsBundle.isSwipesEnabled()) {
          if (!mReader.isPaused())
            mReader.performPause();
          else
            mReader.moveToNextPosition();
        }
      }

      @Override
      public void onClick() {
        if (mReader.isCompleted())
          onStop();
        else
          mReader.toggleCancelled();
      }
    });
  }

  /**
   * Initializes previous word button
   */
  private void initPrevButton() {
    if (!mSettingsBundle.isSwipesEnabled()) {
      mPrevButton.setImageResource(android.R.drawable.ic_media_previous);
      mPrevButton.setOnClickListener(v -> mReader.moveToPreviousPosition());
      mPrevButton.setVisibility(View.INVISIBLE);
    } else {
      mPrevButton.setVisibility(View.INVISIBLE);
    }
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
    mNextTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                              mSettingsBundle.getFontSize());
  }

  private void setReaderBackground() {
    // TODO: Finish styles for this.
/*
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
*/
  }

  private void showNotification(String text) {
    if (!TextUtils.isEmpty(text)) {
      mNotificationTextView.setText(text);
      setCurrentTime(System.currentTimeMillis());

      if (mNotificationHided) {
        YoYo.with(Techniques.SlideInDown)
            .duration(NOTIF_APPEARING_DURATION)
            .playOn(mNotificationTextView);
        mNotificationTextView.postDelayed(
            () -> mNotificationTextView.setVisibility(View.VISIBLE),
            NOTIF_APPEARING_DURATION);

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
    mCurrentTextView.setText(word.substring(emphasis, emphasis + 1));
    mLeftTextView.setText(word.substring(0, emphasis));
    mRightTextView.setText(word.substring(emphasis + 1, word.length()));
    mNextTextView.setText(getFormattedNextWords(nextWords));
    mProgressBar.setProgress(mProgress);
    hideNotification(false);
  }

  @Override
  public void showNotification(int resourceId) {
    if (isAdded()) {
      showNotification(getResources().getString(resourceId));
    }
  }

  @Override
  public void hideNotification(boolean force) {
    if (!mNotificationHided) {
      if (force || System.currentTimeMillis() - mLocalTime >
          NOTIF_SHOWING_LENGTH) {
        YoYo.with(Techniques.SlideOutUp)
            .duration(NOTIF_APPEARING_DURATION)
            .playOn(mNotificationTextView);
        mNotificationTextView.postDelayed(
            () -> mNotificationTextView.setVisibility(View.INVISIBLE),
            NOTIF_APPEARING_DURATION);

        YoYo.with(Techniques.FadeIn)
            .duration(NOTIF_APPEARING_DURATION)
            .playOn(mUpLogo);
        mNotificationHided = true;
      }
    }
  }

  @Override
  public void showInfo(Reader reader) {
    if (reader != null && mSettingsBundle != null)
      showInfo(mSettingsBundle.getWPM(), (100 - mProgress) + "%");
  }

  private void showInfo(int wpm, String percentLeft) {
    mWpmTextView.setText(
        String.valueOf(wpm) + " " + getResources().getString(R.string.wpm));
    mPositionTextView.setText(percentLeft);

    if (mInfoHided) {
      YoYo.with(Techniques.FadeIn)
          .duration(NOTIF_APPEARING_DURATION * 2)
          .playOn(mInfoLayout);
      mInfoLayout.postDelayed(() -> mInfoLayout.setVisibility(View.VISIBLE),
                              NOTIF_APPEARING_DURATION);
      mInfoHided = false;
    }
  }

  @Override
  public void hideInfo() {
    if (!mInfoHided) {
      YoYo.with(Techniques.FadeOut)
          .duration(NOTIF_APPEARING_DURATION)
          .playOn(mInfoLayout);
      mInfoLayout.postDelayed(() -> mInfoLayout.setVisibility(View.INVISIBLE),
                              NOTIF_APPEARING_DURATION);
      mInfoHided = true;
    }
  }

  @Override
  public Integer getWordsPerMinute() {
    return mSettingsBundle.getWPM();
  }

  @Override
  public boolean isNextLoaded() {
    return mReadingDeque.size() > 1;
  }

  @Override
  public Reading nextReading() throws IOException, InterruptedException {
    if (BuildConfig.DEBUG) {
      Log.d(getClass().getName(), "Next reading requested.");
    }
    mChunked.onReaderNext();
    Reading r;
    // No need for sync over mReadingDeque?
    r = mReadingDeque.removeFirst();
    if (mReadingDeque.size() == DEQUE_SIZE_LIMIT - 1) {
      mFullMonitor.resumeTask();
    }
    return r;
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

  public void startReader(@Nullable Reading reading) {
    if (BuildConfig.DEBUG) {
      Log.d(ReaderFragment.class.getName(), "Starting reader...");
    }
    Activity activity = getActivity();
    if (reading == null) {
      if (activity != null) {
        activity.runOnUiThread(() -> {
          stopShowingProgress();
          mReader.setCompleted(true);
        });
      }
    } else {
      mReading = reading;

      mReader.changeReading(reading);
      if (activity != null) {
        activity.runOnUiThread(() -> {
          stopShowingProgress();
          showNotification(R.string.tap_to_start);
          // mProgress = mReading.calcProgress(initialPosition, 0);
          mReader.setPosition(Math.max(
              0,
              mReader.getPosition() - Constants.READER_START_OFFSET));
          mReader.updateReaderView();
          showInfo(mReader);
          YoYo.with(Techniques.FadeIn)
              .duration(READER_PULSE_DURATION)
              .playOn(mReaderLayout);
        });
      }
    }
    mHasReaderStarted = true;
  }

  private void startShowingProgress() {
    mParsingProgressBar.setVisibility(View.VISIBLE);
    mReaderLayout.setVisibility(View.GONE);
  }

  private void stopShowingProgress() {
    mParsingProgressBar.clearAnimation();
    mParsingProgressBar.setVisibility(View.GONE);
    mReaderLayout.setVisibility(View.VISIBLE);
  }

  @Override
  public List<Integer> getDelayCoefficients() {
    return mSettingsBundle.getDelayCoefficients();
  }

  @Override
  public boolean shouldContinue() {
    return mReader == null || !mReader.isCompleted();
  }

  @Override
  public boolean hasStarted() {
    return mStarted;
  }

  @Override
  public void produce(@Nullable Reading reading) throws InterruptedException {
    if (!hasStarted()) {
      startReader(reading);
      mStarted = true;
    } else {
      synchronized (mReadingDeque) {
        if (mReadingDeque.size() == DEQUE_SIZE_LIMIT) {
          if (BuildConfig.DEBUG) {
            Log.d(ReaderFragment.class.getName(),
                  "Reading deque is full, pasusing task");
          }
          mFullMonitor.pauseTask();
        }
        mReadingDeque.add(reading);
        if (BuildConfig.DEBUG) {
          Log.d(ReaderFragment.class.getName(), String.format(
              "Added to a reading deque, new size: %d", mReadingDeque.size()));
        }
      }
    }
  }

  //it's very unflexible, TODO: fix it later
  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (mHasReaderStarted && mReader != null) {
      mReader.performPause();
    }
    View view = getView();
    if (view != null) {
      // TODO: Implement this in terms of different dimension folders.
/*
      FrameLayout.LayoutParams params =
          (FrameLayout.LayoutParams) view.getLayoutParams();
      int currentMargin = params.leftMargin;

      Resources resources = getResources();
      int portMargin,
          landMargin = (int) resources.getDimension(R.dimen.reader_margin_left);
      if (currentMargin <= landMargin) {
        portMargin = (int) resources.getDimension(R.dimen.reader_margin_left);
      } else {
        portMargin = (int) resources.getDimension(R.dimen.reader_margin_left);
        landMargin = (int) resources.getDimension(R.dimen.reader_margin_left);
      }
      int newMargin = (currentMargin == portMargin)
          ? landMargin
          : portMargin;
      params.setMargins(newMargin, 0, newMargin, 0);
      view.setLayoutParams(params);
*/
    }
  }

  private void handleArgs(Bundle args) {
    if (args.containsKey(Intent.EXTRA_TEXT)) {
      handleShareSource(args);
    } else {
      ReadingSource sourceType = ReadingSource.valueOf(
          args.getString(Constants.EXTRA_READING_SOURCE));
      switch (sourceType) {
        case CACHE:
          handleCacheSource(args);
          break;
        case SHARE:
          handleShareSource(args);
          break;
      }
    }
  }

  private void handleShareSource(Bundle args) {
    final ReadableType type;
    if (args.containsKey(Constants.EXTRA_TYPE)) {
      type = ReadableType.valueOf(args.getString(Constants.EXTRA_TYPE));
    } else {
      type = RAW;
    }
    final String text;
    if (args.containsKey(Constants.EXTRA_TEXT)) {
      text = args.getString(Constants.EXTRA_TEXT);
    } else {
      text = args.getString(Intent.EXTRA_TEXT);
    }
    switch (type) {
      case RAW: {
        String link;
        // Checks if text contains link to parse article from.
        if (!TextUtils.isEmpty(text) &&
            text.length() < Constants.NON_LINK_LENGTH &&
            !TextUtils.isEmpty(
                link = TextParser.findLink(TextParser.compilePattern(),
                                           text))) {
          final NetReadable netReadable =
              new NetReadable(getActivity(), link);
          Observable<NetReadable> o = Observable.create(
              subscriber -> {
                netReadable.process();
                subscriber.onNext(netReadable);
                subscriber.onCompleted();
              });
          addSubscription(
              o.subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(nr -> {
                 if (nr.isProcessed()) {
                   mReading = nr;
                   mStorable = nr;
                   startSingleReadingFlow();
                 } else {
                   stopShowingProgress();
                   showNotification(R.string.error_occurred);
                   mReader.setCompleted(true);
                 }
               })
          );
        } else {
          mReading = new Readable(); //neutral value
          mReading.setText(text);
          startSingleReadingFlow();
        }
      }
      break;
      case CLIPBOARD:
        ClipboardReadable clipboardReadable = new ClipboardReadable(
            getActivity());
        Observable<ClipboardReadable> o = Observable.create(
            subscriber -> {
              clipboardReadable.process();
              subscriber.onNext(clipboardReadable);
              subscriber.onCompleted();
            });
        addSubscription(
            o.subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread())
             .subscribe(cr -> {
               if (cr.isProcessed()) {
                 mReading = cr;
                 startSingleReadingFlow();
               } else {
                 stopShowingProgress();
                 showNotification(R.string.error_occurred);
                 mReader.setCompleted(true);
               }
             })
        );
        break;
    }
  }

  private void handleCacheSource(Bundle args) {
    final String stringType = args.getString(Constants.EXTRA_TYPE);
    final String path = args.getString(Constants.EXTRA_PATH);
    if (stringType != null) {
      final ChunkedUnprocessedStorable cus;
      switch (ReadableType.valueOf(stringType)) {
        case EPUB:
          cus = new EpubStorable(getActivity(), LocalDateTime.now().toString());
          break;
        case FB2:
          cus = new FB2Storable(getActivity(), LocalDateTime.now().toString());
          startFb2ProcessingService(path);
          break;
        case TXT:
          cus = new TxtStorable(getActivity(), LocalDateTime.now().toString());
          break;
        default:
          throw new IllegalArgumentException("Unsupported cache source.");
      }
      cus.setPath(path);
      Observable<ChunkedUnprocessedStorable> processingObservable =
          Observable.create(subscriber -> {
            cus.process();
            subscriber.onNext(cus);
            subscriber.onCompleted();
          });
      addSubscription(
          processingObservable
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(x -> {
                if (x.isProcessed()) {
                  mChunked = x;
                  mStorable = x;
                  startChunkedReadingFlow(mStorable.getCurrentPosition());
                } else {
                  stopShowingProgress();
                  showNotification(R.string.error_occurred);
                  mReader.setCompleted(true);
                }
              }, throwable -> {
                throwable.printStackTrace();
                // TODO: Report to Firebase.
                stopShowingProgress();
                showNotification(R.string.error_occurred);
                mReader.setCompleted(true);
              })
      );
    } else {
      throw new IllegalStateException(
          "Cache source can't be processed without an explicitly set type!");
    }
  }

  /**
   * Method for initializing reading flow using single Reading instance.
   */
  private void startSingleReadingFlow() {
    mFullMonitor = new MonitorObject();
    mReader = new Reader(mHandler, this);
    mReaderProducerTask = new ReaderProducerTask(mReading, this);
    mReadingThread = new Thread(mReaderProducerTask);
    mReadingThread.start();
  }

  /**
   * Method for initializing reading flow using chunked instance, which may
   * produce several Reading ones.
   *
   * @param initialPosition Initial position of a reading.
   */
  private void startChunkedReadingFlow(final int initialPosition) {
    mFullMonitor = new MonitorObject();
    mReader = new Reader(mHandler, this);
    mReader.setPosition(initialPosition);
    mReaderProducerTask = new ReaderProducerTask(mChunked, this);
    mReadingThread = new Thread(mReaderProducerTask);
    mReadingThread.start();
  }

  private void startFb2ProcessingService(@NonNull final String path) {
    Activity a = getActivity();
    Intent intent = new Intent(a, FB2ProcessingService.class);
    intent.putExtra(Constants.EXTRA_PATH, path);
    a.startService(intent);
  }

  private void addSubscription(Subscription s) {
    if (mCompositeSubscription == null) {
      mCompositeSubscription = new CompositeSubscription();
    }
    mCompositeSubscription.add(s);
  }

  public interface ReaderFragmentCallback {
    void stop();
  }
}
