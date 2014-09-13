package com.infmme.readilyapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import com.infmme.readilyapp.essential.TextParser;
import com.infmme.readilyapp.readable.FileStorable;
import com.infmme.readilyapp.readable.Readable;
import com.infmme.readilyapp.readable.Storable;
import com.infmme.readilyapp.service.LastReadService;
import com.infmme.readilyapp.settings.SettingsBundle;
import com.infmme.readilyapp.util.OnSwipeTouchListener;

import java.util.ArrayDeque;
import java.util.List;

/**
 * infm : 16/05/14. Enjoy it ;)
 */
public class ReaderFragment extends Fragment {

	private static final int NOTIF_APPEARING_DURATION = 300;
	private static final int NOTIF_SHOWING_LENGTH = 1500; //time in ms for which notification becomes visible
	private static final int READER_PULSE_DURATION = 400;
	private static final float POINTER_LEFT_PADDING_COEFFICIENT = 5f / 18f;
	private static final String[] LIGHT_COLOR_SET = new String[]{"#0A0A0A", "#AAAAAA"};
	private static final String[] DARK_COLOR_SET = new String[]{"#FFFFFF", "#999999", "#FF282828"};

	private ReaderListener callback;
	//initialized in onCreate()
	private Handler handler;
	private long localTime = 0;
	private boolean notificationHided = true;
	private boolean infoHided = true;
	private Bundle args;
	//initialized in onCreateView()
	private RelativeLayout readerLayout;
	private RelativeLayout infoLayout;
	private TextView wpmTextView;
	private TextView positionTextView;
	private TextView currentTextView;
	private TextView leftTextView;
	private TextView rightTextView;
	private TextView notification;
	private ProgressBar progressBar;
	private ProgressBar parsingProgressBar;
	private ImageButton prevButton;
	private View upLogo;
	//initialized in onActivityCreated()
	private Reader reader;
	private Readable readable;
	private List<String> wordList;
	private List<Integer> emphasisList;
	private List<Integer> delayList;
	private SettingsBundle settingsBundle;
	private Thread parserThread;
	private ReaderTask readerTask;
	private MonitorObject monitorObject;
	private long fileSize;
	private long bytePosition;
	//receiving status
	private Boolean parserReceived = false;
	private String primaryTextColor = LIGHT_COLOR_SET[0];
	private String secondaryTextColor = LIGHT_COLOR_SET[1];
	private boolean isFileStorable;
	private int progress;

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			callback = (ReaderListener) activity;
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		args = getArguments();
		handler = new Handler();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View fragmentLayout = inflater.inflate(R.layout.fragment_reader, container, false);
		findViews(fragmentLayout);
		periodicallyAnimate();
		return fragmentLayout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);

		Activity activity = getActivity();
		setReaderLayoutListener(activity);
		settingsBundle = new SettingsBundle(PreferenceManager.getDefaultSharedPreferences(activity));

		setReaderBackground();
		initPrevButton();
		setReaderFontSize();

		readable = Readable.createReadable(activity, args);
		monitorObject = new MonitorObject();
		readerTask = new ReaderTask(monitorObject, readable);
		parserThread = new Thread(readerTask);
		parserThread.start();
	}

	public void onSwipeTop(){
		changeWPM(Constants.WPM_STEP_READER);
	}

	public void onSwipeBottom(){
		changeWPM(-1 * Constants.WPM_STEP_READER);
	}

	private void setCurrentTime(long localTime){
		this.localTime = localTime;
	}

	private Spanned getLeftFormattedText(int pos){
		String word = wordList.get(pos);
		if (TextUtils.isEmpty(word)){ return Html.fromHtml(""); }
		int emphasisPosition = emphasisList.get(pos);
		String wordLeft = word.substring(0, emphasisPosition);
		String format = "<font color='" + primaryTextColor + "'>" + wordLeft + "</font>";
		return Html.fromHtml(format);
	}

	private Spanned getCurrentFormattedText(int pos){
		String word = wordList.get(pos);
		if (TextUtils.isEmpty(word)){ return Html.fromHtml(""); }
		int emphasisPosition = emphasisList.get(pos);
		String wordEmphasis = word.substring(emphasisPosition, emphasisPosition + 1);
		String format = "<font color='#FA2828'>" + wordEmphasis + "</font>";
		return Html.fromHtml(format);
	}

	private Spanned getRightFormattedText(int pos){
		String word = wordList.get(pos);
		if (TextUtils.isEmpty(word)){ return Html.fromHtml(""); }
		int emphasisPosition = emphasisList.get(pos);
		String wordRight = word.substring(emphasisPosition + 1, word.length());
		String format = "<font><font color='" + primaryTextColor + "'>" + wordRight + "</font>";
		if (settingsBundle.isShowingContextEnabled()){ format += getNextFormat(pos); }
		format += "</font>";
		return Html.fromHtml(format);
	}

	private String getNextFormat(int pos){
		int charLen = 0;
		int i = pos;
		StringBuilder format = new StringBuilder("&nbsp;<font color='" + secondaryTextColor + "'>");
		while (charLen < 40 && i < wordList.size() - 1){
			String word = wordList.get(++i);
			if (!TextUtils.isEmpty(word)){
				charLen += word.length() + 1;
				format.append(word).append(" ");
			}
		}
		format.append("</font>");
		return format.toString();
	}

	private void findViews(View v){
		readerLayout = (RelativeLayout) v.findViewById(R.id.reader_layout);
		parsingProgressBar = (ProgressBar) v.findViewById(R.id.parsingProgressBar);
		currentTextView = (TextView) v.findViewById(R.id.currentWordTextView);
		leftTextView = (TextView) v.findViewById(R.id.leftWordTextView);
		rightTextView = (TextView) v.findViewById(R.id.rightWordTextView);
		progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
		notification = (TextView) v.findViewById(R.id.reader_notification);
		prevButton = (ImageButton) v.findViewById(R.id.previousWordImageButton);
		upLogo = v.findViewById(R.id.logo_up);
		infoLayout = (RelativeLayout) v.findViewById(R.id.reader_info_layout);
		wpmTextView = (TextView) v.findViewById(R.id.text_view_info_speed_value);
		positionTextView = (TextView) v.findViewById(R.id.text_view_info_position_value);
	}

	private void setReaderLayoutListener(Context context){
		readerLayout.setOnTouchListener(new OnSwipeTouchListener(context) {
			@Override
			public void onSwipeTop(){
				ReaderFragment.this.onSwipeTop();
			}

			@Override
			public void onSwipeBottom(){
				ReaderFragment.this.onSwipeBottom();
			}

			@Override
			public void onSwipeRight(){
				if (settingsBundle.isSwipesEnabled()){
					if (!reader.isPaused()){
						reader.performPause();
					} else {
						reader.moveToPrevious();
					}
				}
			}

			@Override
			public void onSwipeLeft(){
				if (settingsBundle.isSwipesEnabled()){
					if (!reader.isPaused()){
						reader.performPause();
					} else {
						reader.moveToNext();
					}
				}
			}

			@Override
			public void onClick(){
				if (reader.isCompleted()){ onStop(); } else { reader.incCancelled(); }
			}
		});
	}

	private void initPrevButton(){
		if (!settingsBundle.isSwipesEnabled()){
			prevButton.setImageResource(R.drawable.abc_ic_ab_back_holo_light);
			prevButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v){
					reader.moveToPrevious();
				}
			});
			prevButton.setVisibility(View.INVISIBLE);
		} else { prevButton.setVisibility(View.INVISIBLE); }
	}

	private float spToPixels(Context context, float sp){
		float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return sp * scaledDensity;
	}

	private void setReaderFontSize(){
		View pointerTop = readerLayout.findViewById(R.id.pointerTopImageView);
		View pointerBottom = readerLayout.findViewById(R.id.pointerBottomImageView);
		float fontSizePx = spToPixels(getActivity(), (float) settingsBundle.getFontSize());
		pointerTop.setPadding((int) (fontSizePx * POINTER_LEFT_PADDING_COEFFICIENT + .5f), pointerTop.getPaddingTop(),
							  pointerTop.getPaddingRight(), pointerTop.getPaddingBottom());
		pointerBottom.setPadding((int) (fontSizePx * POINTER_LEFT_PADDING_COEFFICIENT + .5f),
								 pointerBottom.getPaddingTop(),
								 pointerBottom.getPaddingRight(), pointerBottom.getPaddingBottom());
		currentTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, settingsBundle.getFontSize());
		leftTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, settingsBundle.getFontSize());
		rightTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, settingsBundle.getFontSize());
	}

	private void setReaderBackground(){
		if (settingsBundle.isDarkTheme()){
			((View) readerLayout.getParent()).setBackgroundColor(Color.parseColor(DARK_COLOR_SET[2]));
			primaryTextColor = DARK_COLOR_SET[0];
			secondaryTextColor = DARK_COLOR_SET[1];
			((ImageView) readerLayout.findViewById(R.id.pointerTopImageView)).
					setImageResource(R.drawable.word_pointer_dark);
			((ImageView) readerLayout.findViewById(R.id.pointerBottomImageView)).
					setImageResource(R.drawable.word_pointer_dark);
		}
	}

	private void showNotification(String text){
		if (!TextUtils.isEmpty(text)){
			notification.setText(text);
			setCurrentTime(System.currentTimeMillis());

			if (notificationHided){
				YoYo.with(Techniques.SlideInDown).
						duration(NOTIF_APPEARING_DURATION).
						playOn(notification);
				notification.postDelayed(new Runnable() {
					@Override
					public void run(){
						notification.setVisibility(View.VISIBLE);
					}
				}, NOTIF_APPEARING_DURATION);

				YoYo.with(Techniques.FadeOut).
						duration(NOTIF_APPEARING_DURATION).
						playOn(upLogo);
				notificationHided = false;
			}
		}
	}

	private void showNotification(int resourceId){
		if (isAdded()){ showNotification(getResources().getString(resourceId)); }
	}

	private boolean hideNotification(boolean force){
		if (!notificationHided){
			if (force || System.currentTimeMillis() - localTime > NOTIF_SHOWING_LENGTH){
				YoYo.with(Techniques.SlideOutUp).
						duration(NOTIF_APPEARING_DURATION).
						playOn(notification);
				notification.postDelayed(new Runnable() {
					@Override
					public void run(){
						notification.setVisibility(View.INVISIBLE);
					}
				}, NOTIF_APPEARING_DURATION);

				YoYo.with(Techniques.FadeIn).
						duration(NOTIF_APPEARING_DURATION).
						playOn(upLogo);
				notificationHided = true;
			}
		}
		return notificationHided;
	}

	private void showInfo(Reader reader){
		if (reader != null && settingsBundle != null) showInfo(settingsBundle.getWPM(), (100 - progress) + "%");
	}

	private void showInfo(int wpm, String percentLeft){
		wpmTextView.setText(wpm + " WPM");
		positionTextView.setText(percentLeft);

		if (infoHided){
			YoYo.with(Techniques.FadeIn).
					duration(NOTIF_APPEARING_DURATION * 2).
					playOn(infoLayout);
			infoLayout.postDelayed(new Runnable() {
				@Override
				public void run(){
					infoLayout.setVisibility(View.VISIBLE);
				}
			}, NOTIF_APPEARING_DURATION);
			infoHided = false;
		}
	}

	private void hideInfo(){
		if (!infoHided){
			YoYo.with(Techniques.FadeOut).
					duration(NOTIF_APPEARING_DURATION).
					playOn(infoLayout);
			infoLayout.postDelayed(new Runnable() {
				@Override
				public void run(){
					infoLayout.setVisibility(View.INVISIBLE);
				}
			}, NOTIF_APPEARING_DURATION);
			infoHided = true;
		}
	}

	/**
	 * TODO: make max/min optional
	 *
	 * @param delta: delta itself. Default value: 50
	 */
	private void changeWPM(int delta){
		if (settingsBundle != null){
			int wpm = settingsBundle.getWPM();
			int wpmNew = Math.min(Constants.MAX_WPM, Math.max(wpm + delta, Constants.MIN_WPM));

			if (wpm != wpmNew){
				settingsBundle.setWPM(wpmNew);
				showNotification(wpmNew + " WPM");
				wpmTextView.setText(wpmNew + " WPM");
			}
		}
	}

	private void startReader(TextParser parser){
		changeParser(parser);
		final int resultCode = parser.getResultCode();
		if (resultCode == TextParser.RESULT_CODE_OK){
			final int initialPosition = Math.max(readable.getPosition() - Constants.READER_START_OFFSET, 0);
			reader = new Reader(handler, initialPosition);
			Activity activity = getActivity();
			if (activity != null){
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run(){
						parsingProgressBar.setVisibility(View.GONE);
						readerLayout.setVisibility(View.VISIBLE);
						if (initialPosition < wordList.size()){
							showNotification(R.string.tap_to_start);
							progress = readable.calcProgress(initialPosition, 0);
							reader.updateView(initialPosition);
							showInfo(reader);
						} else {
							showNotification(R.string.reading_is_completed);
							reader.setCompleted(true);
						}
						YoYo.with(Techniques.FadeIn).
								duration(READER_PULSE_DURATION).
								playOn(readerLayout);
					}
				});
			}
		} else {
			notifyBadParser(resultCode);
		}
	}

	private void notifyBadParser(final int resultCode){
		final Activity a = getActivity();
		if (a != null){
			a.runOnUiThread(new Runnable() {
				@Override
				public void run(){
					int stringId;
					switch (resultCode){
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

	private void changeParser(final TextParser parser){
		parserReceived = true;

		readable = parser.getReadable();
		wordList = readable.getWordList();
		emphasisList = readable.getEmphasisList();
		delayList = readable.getDelayList();
		if (isStorable(readable)){
			bytePosition = ((Storable) readable).getBytePosition();
		}
	}

	/**
	 * periodically animates progressBar
	 */
	private void periodicallyAnimate(){
		Runnable anim = new Runnable() {
			@Override
			public void run(){
				if (!parserReceived){
					final int durationTime = 500 + (int) (Math.random() * 200);
					final int sleepTime = 4 * Constants.SECOND + (int) (Math.random() * 2 * Constants.SECOND);
					Techniques choice;
					int r = (int) (Math.random() * 3);
					switch (r){
						case 0:
							choice = Techniques.Pulse;
							break;
						case 1:
							choice = Techniques.Wave;
							break;
						case 2:
							choice = Techniques.Flash;
							break;
						default:
							choice = Techniques.Wobble;
							break;
					}
					YoYo.with(choice).
							duration(durationTime).
							playOn(parsingProgressBar);
					handler.postDelayed(this, durationTime + sleepTime);
				}
			}
		};
		handler.postDelayed(anim, Constants.SECOND);
	}

	private boolean canBeSaved(Readable readable){
		return parserReceived &&
				readable != null &&
				settingsBundle != null &&
/*
				settingsBundle.isCachingEnabled() &&
*/
				!TextUtils.isEmpty(readable.getPath()) &&
				isStorable(readable);
	}

	private boolean isStorable(Readable readable){
		return (readable.getType() == Readable.TYPE_FILE ||
						readable.getType() == Readable.TYPE_TXT ||
						readable.getType() == Readable.TYPE_FB2 ||
						readable.getType() == Readable.TYPE_EPUB ||
						readable.getType() == Readable.TYPE_NET ||
						readable.getType() == Readable.TYPE_RAW);
	}

	private boolean isFileStorable(Readable readable){
		return isStorable(readable) &&
				readable.getType() != Readable.TYPE_NET &&
				readable.getType() != Readable.TYPE_RAW;
	}

	@Override
	public void onStop(){
		reader.performPause();
		if (canBeSaved(readable) && reader != null){
			Activity activity = getActivity();
			Storable storable = (Storable) readable;
			storable.setPosition(reader.getPosition());
			storable.setApproxCharCount(reader.getApproxCharCount());
			storable.onClose(activity, reader.isCompleted(), settingsBundle.isStoringComplete());
		}

		settingsBundle.updatePreferences();

		if (reader != null){
			reader.setCompleted(true);
		} else if (parserThread != null && parserThread.isAlive()){
			parserThread.interrupt();
		}
		callback.stop();
		super.onStop();
	}

	//it's very unflexible, TODO: fix it later
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		if (parserReceived && reader != null){ reader.performPause(); }
		View view = getView();
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
		int currentMargin = params.leftMargin;

		Resources resources = getResources();
		int portMargin,
				landMargin = (int) resources.getDimension(R.dimen.land_margin_left);
		if (currentMargin <= landMargin){
			portMargin = (int) resources.getDimension(R.dimen.port_margin_left);
		} else {
			portMargin = (int) resources.getDimension(R.dimen.port_tablet_margin_left);
			landMargin = (int) resources.getDimension(R.dimen.land_tablet_margin_left);
		}
		int newMargin = (currentMargin == portMargin)
				? landMargin
				: portMargin;
		params.setMargins(newMargin, 0, newMargin, 0);
		view.setLayoutParams(params);
/*
		ViewGroup rootView = (ViewGroup) getView().getParent();
		if (rootView != null){
            Activity activity = getActivity();

            LayoutInflater inflater = LayoutInflater.from(activity);
            ViewGroup newView = (ViewGroup) inflater.inflate(R.layout.fragment_reader, rootView, false);

            rootView.removeAllViews();
            rootView.addView(newView);

            findViews(newView);
            parsingProgressBar.setVisibility(View.GONE);
            readerLayout.setVisibility(View.VISIBLE);
            reader.updateView();
            setReaderLayoutListener(activity);
        }
*/
	}

	public interface ReaderListener {
		public void stop();
	}

	/**
	 * don't sure that it must be an inner class
	 */
	private class Reader implements Runnable {

		private Handler readerHandler;
		private int paused;
		private int position;
		private boolean completed;
		private int approxCharCount;

		public Reader(Handler readerHandler, int position){
			this.readerHandler = readerHandler;
			this.position = position;
			paused = 1;
			approxCharCount = 0;
		}

		@Override
		public void run(){
			if ((position < wordList.size() && !readerTask.isChunkAvailable()) ||
					(position < wordList.size() - FileStorable.LAST_WORD_PREFIX_SIZE && readerTask.isChunkAvailable())){
				if (wordList.size() - position < 100 && monitorObject.isPaused()){
					try {
						monitorObject.resumeTask();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				completed = false;
				if (!isPaused()){
					approxCharCount += wordList.get(position).length() + 1;
					progress = readable.calcProgress(position, approxCharCount);
					updateView(position);
					readerHandler.postDelayed(this, calcDelay());
					position++;
				}
			} else if (readerTask.isChunkAvailable()){
				changeParser(readerTask.removeDequeHead());
				position = 0;
				readerHandler.postDelayed(this, calcDelay());
			} else {
				showNotification(R.string.reading_is_completed);
				completed = true;
				paused = 1;
			}
		}

		public int getPosition(){
			return position;
		}

		public void setPosition(int position){
			if (wordList != null && emphasisList != null && delayList != null &&
					position < wordList.size() && position >= 0){
				this.position = position;
				updateView(position);
				showInfo(this);
			}
		}

		public void moveToPrevious(){
			setPosition(position - 1);
		}

		public void moveToNext(){
			setPosition(position + 1);
		}

		public boolean isCompleted(){
			return completed;
		}

		public void setCompleted(boolean completed){
			this.completed = completed;
		}

		public boolean isPaused(){
			return paused % 2 == 1;
		}

		public int getApproxCharCount(){
			return approxCharCount;
		}

		public void incCancelled(){
			if (!isPaused()){ performPause(); } else { performPlay(); }
		}

		public void performPause(){
			if (!isPaused()){
				paused++;
				YoYo.with(Techniques.Pulse).
						duration(READER_PULSE_DURATION).
						playOn(readerLayout);
				if (!settingsBundle.isSwipesEnabled()){ prevButton.setVisibility(View.VISIBLE); }
				showNotification(R.string.pause);
				showInfo(this);
			}
		}

		public void performPlay(){
			if (isPaused()){
				paused++;
				YoYo.with(Techniques.Pulse).
						duration(READER_PULSE_DURATION).
						playOn(readerLayout);
				if (!settingsBundle.isSwipesEnabled()){ prevButton.setVisibility(View.INVISIBLE); }
				hideNotification(true);
				hideInfo();
				readerHandler.postDelayed(this, READER_PULSE_DURATION + 100);
			}
		}

		private int calcDelay(){
			return (delayList.isEmpty())
					? 10 * Math.round(100 * 60 * 1f / settingsBundle.getWPM())
					: delayList.get(position) * Math.round(100 * 60 * 1f / settingsBundle.getWPM());
		}

		private void updateView(int pos){
			if (pos >= wordList.size())
				return;
			currentTextView.setText(getCurrentFormattedText(pos));
			leftTextView.setText(getLeftFormattedText(pos));
			rightTextView.setText(getRightFormattedText(pos));
			progressBar.setProgress(progress);
			hideNotification(false);
		}
	}

	private class ReaderTask implements Runnable {
		private static final int DEQUE_SIZE_LIMIT = 3;
		private final ArrayDeque<TextParser> parserDeque;
		private MonitorObject object = new MonitorObject();
		private Readable currentReadable;

		public ReaderTask(MonitorObject object, Readable storable){
			this.object = object;
			currentReadable = storable;
			parserDeque = new ArrayDeque<TextParser>();
		}

		@Override
		public void run(){
			while (reader == null || !reader.isCompleted()){
				try {
					synchronized (parserDeque){
						if (!currentReadable.isProcessed()){
							currentReadable.process(getActivity());
							if (isFileStorable = isFileStorable(readable)){
								fileSize = ((FileStorable) readable).getFileSize();
							}
							currentReadable.readData();
							TextParser toAdd = TextParser.newInstance(currentReadable, settingsBundle);
							toAdd.process();
							parserDeque.add(toAdd);
						}
						while (parserDeque.size() < DEQUE_SIZE_LIMIT &&
								parserDeque.size() > 0 &&
								!TextUtils.isEmpty(parserDeque.getLast().getReadable().getText())){
							parserDeque.addLast(getNextParser(parserDeque.getLast()));
						}
					}
					if (reader == null){ startReader(removeDequeHead()); }
					if (reader != null){
						object.pauseTask();
					} else {
						break;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public TextParser removeDequeHead(){
			synchronized (parserDeque){
				return parserDeque.pollFirst();
			}
		}

		public synchronized boolean isChunkAvailable(){
			return parserDeque.size() > 1;
		}

		private TextParser getNextParser(TextParser current){
			Readable currentReadable = current.getReadable();
			TextParser result = TextParser.newInstance(currentReadable.getNext(), settingsBundle);
			result.process();
			if (isFileStorable){
				((FileStorable) currentReadable).copyListPrefix(result.getReadable());
			}
			return result;
		}
	}

	private class MonitorObject {

		private boolean paused;

		public synchronized boolean isPaused(){return paused;}

		public synchronized void pauseTask() throws InterruptedException{
			if (!isPaused()){
				paused = true;
				wait();
			}
		}

		public synchronized void resumeTask() throws InterruptedException{
			if (isPaused()){
				paused = false;
				notify();
			}
		}
	}
}
