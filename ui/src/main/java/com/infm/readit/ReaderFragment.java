package com.infm.readit;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.infm.readit.essential.TextParser;
import com.infm.readit.readable.Readable;
import com.infm.readit.readable.Storable;
import com.infm.readit.service.LastReadService;
import com.infm.readit.settings.SettingsBundle;
import com.infm.readit.util.OnSwipeTouchListener;

import java.util.List;

/**
 * infm : 16/05/14. Enjoy it ;)
 */
public class ReaderFragment extends Fragment {

    public interface ReaderListener {
        public void stop();
    }

    private ReaderListener callback;
    private static final String LOGTAG = "ReaderFragment";
    private static final int NOTIF_APPEARING_DURATION = 300;
    private static final int NOTIF_SHOWING_LENGTH = 1500; //time in ms for which speedo becomes visible
    private static final int READER_PULSE_DURATION = 400;

    //initialized in onCreate()
    private Handler handler;
    private long localTime = 0;
    private boolean notificationHided = true;
    private Bundle args;
    //initialized in onCreateView()
    private RelativeLayout readerLayout;
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
    private TextParser parser;
    private SettingsBundle settingsBundle;
    private Thread parserThread;
    //receiving status
    private Boolean parserReceived = false;

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
        Log.d(LOGTAG, "onCreateView() called");

        View fragmentLayout = inflater.inflate(R.layout.fragment_reader, container, false);
        findViews(fragmentLayout);
        periodicallyAnimate();
        return fragmentLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Log.d(LOGTAG, "onActivityCreated() called");

        Activity activity = getActivity();
        setReaderLayoutListener(activity);
        settingsBundle = new SettingsBundle(PreferenceManager.getDefaultSharedPreferences(activity));

        initPrevButton();

        parseText(getActivity(), args);
    }

    private void setCurrentTime(long localTime){
        this.localTime = localTime;
    }

    public TextParser getParser(){
        return parser;
    }

    private Spanned getLeftFormattedText(int pos){
        String word = wordList.get(pos);
        if (TextUtils.isEmpty(word))
            return Html.fromHtml("");
        int emphasisPosition = emphasisList.get(pos);
        String wordLeft = word.substring(0, emphasisPosition);
        String format = "<font color='#0A0A0A'>" + wordLeft + "</font>";
        return Html.fromHtml(format);
    }

    private Spanned getCurrentFormattedText(int pos){
        String word = wordList.get(pos);
        if (TextUtils.isEmpty(word))
            return Html.fromHtml("");
        int emphasisPosition = emphasisList.get(pos);
        String wordEmphasis = word.substring(emphasisPosition, emphasisPosition + 1);
        String format = "<font color='#FA2828'>" + wordEmphasis + "</font>";
        return Html.fromHtml(format);
    }

    private Spanned getRightFormattedText(int pos){
        String word = wordList.get(pos);
        if (TextUtils.isEmpty(word))
            return Html.fromHtml("");
        int emphasisPosition = emphasisList.get(pos);
        String wordRight = word.substring(emphasisPosition + 1, word.length());
        String format = "<font><font color='#0A0A0A'>" + wordRight + "</font>";
        if (settingsBundle.isShowingContextEnabled())
            format += getNextFormat(pos);
        format += "</font>";
        return Html.fromHtml(format);
    }

    public String getNextFormat(int pos){
        int charLen = 0;
        int i = pos;
        StringBuilder format = new StringBuilder("&nbsp;<font color='#AAAAAA'>");
        while (charLen < 40 && i < wordList.size() - 1/* && wordList.get(i).charAt(wordList.get(i).length() - 1) != '\n'*/){
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
        notification = (TextView) v.findViewById(R.id.speedo);
        prevButton = (ImageButton) v.findViewById(R.id.previousWordImageButton);
        upLogo = v.findViewById(R.id.logo_up);
    }

    private void setReaderLayoutListener(Context context){
        readerLayout.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeTop(){
                changeWPM(Constants.WPM_STEP_READER);
            }

            @Override
            public void onSwipeBottom(){
                changeWPM(-1 * Constants.WPM_STEP_READER);
            }

            @Override
            public void onSwipeRight(){
                Log.v(LOGTAG, "onSwipeRight called, isSwipesEnabled: " + settingsBundle.isSwipesEnabled());
                if (settingsBundle.isSwipesEnabled()){
                    if (!reader.isCancelled()){
                        reader.performPause();
                    } else {
                        reader.moveToPrevious();
                    }
                }
            }

            @Override
            public void onSwipeLeft(){
                Log.i(LOGTAG, "onSwipeLeft called, isSwipesEnabled: " + settingsBundle.isSwipesEnabled());
                if (settingsBundle.isSwipesEnabled()){
                    if (!reader.isCancelled()){
                        reader.performPause();
                    } else {
                        reader.moveToNext();
                    }
                }
            }

            @Override
            public void onClick(){
                if (reader.isCompleted())
                    onStop();
                else
                    reader.incCancelled();
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
        } else
            prevButton.setVisibility(View.INVISIBLE);
    }

    private void showNotification(String text){
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

    private void showNotification(int resourceId){
        showNotification(getResources().getString(resourceId));
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

    /**
     * TODO: make max/min optional
     *
     * @param delta: delta itself. Default value: 50
     */
    private void changeWPM(int delta){
        int wpm = settingsBundle.getWPM();
        int wpmNew = Math.min(Constants.MAX_WPM, Math.max(wpm + delta, Constants.MIN_WPM));

        if (wpm != wpmNew){
            settingsBundle.setWPM(wpmNew);
            Log.d(LOGTAG, "WPM changed from " + wpm + " to " + wpmNew);
            showNotification(wpmNew + " WPM");
        } else {
            Log.d(LOGTAG, "WPM remained the same: " + wpm);
        }
    }

    private void processParser(final Context context){
        final int resultCode = parser.getResultCode();
        Log.d(LOGTAG, "processParser() called, result code: " + resultCode);
        if (resultCode == TextParser.RESULT_CODE_OK){
            parserReceived = true;
            readable = parser.getReadable();

            wordList = readable.getWordList();
            emphasisList = readable.getEmphasisList();
            delayList = readable.getDelayList();

            readable.setPosition(Math.max(readable.getPosition() - Constants.READER_START_OFFSET, 0));

            final int initialPosition = readable.getPosition();
            reader = new Reader(handler, initialPosition);
            Activity activity = getActivity();
            if (activity != null){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run(){
                        Log.d(LOGTAG, "runOnUiThread called");
                        parsingProgressBar.setVisibility(View.GONE);
                        readerLayout.setVisibility(View.VISIBLE);
                        showNotification(R.string.tap_to_start);
                        reader.updateView(initialPosition);
                        YoYo.with(Techniques.FadeIn).
                                duration(READER_PULSE_DURATION).
                                playOn(readerLayout);
                    }
                });
            } else {
                Log.e(LOGTAG, "WTF!?!??!! getActivity returned null");
            }
        } else {
            Activity a = getActivity();
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
                    Toast.makeText(context, stringId, Toast.LENGTH_SHORT).show();
                    onStop();
                }
            });
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
        handler.postDelayed(anim, Constants.SECOND); //TODO: make time relatively large
    }

    private Boolean isStorable(){
        return parserReceived &&
                readable != null &&
                settingsBundle != null &&
/*
                settingsBundle.isCachingEnabled() &&
*/
                !TextUtils.isEmpty(readable.getPath());
    }

    @Override
    public void onStop(){
        Log.d(LOGTAG, "OnStop() called");
        Activity activity = getActivity();
        if (isStorable() && reader != null){
            reader.performPause();
            Storable storable = (Storable) readable;
            storable.setPosition(reader.getPosition());
            int operation = (reader.isCompleted())
                    ? Constants.DB_OPERATION_DELETE
                    : Constants.DB_OPERATION_INSERT;
            LastReadService.start(activity, storable, operation);
        }

        settingsBundle.updatePreferences();

        if (parserThread != null && parserThread.isAlive()){
            parserThread.interrupt();
            Log.d(LOGTAG, "parserThread has been interrupted");
        }
        callback.stop();
        super.onStop();
    }

    private int pxFromDp(int dp){
        return (int)(dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    //it's very unflexible, TODO: fix it later
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        if (parserReceived && reader != null)
            reader.performPause();
        View view = getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        int currentMargin = params.leftMargin;

        Resources resources = getResources();
        int portMargin,
                landMargin = (int)resources.getDimension(R.dimen.land_margin_left);
        if (currentMargin <= landMargin){
            portMargin = (int)resources.getDimension(R.dimen.port_margin_left);
        } else {
            portMargin = (int)resources.getDimension(R.dimen.port_tablet_margin_left);
            landMargin = (int)resources.getDimension(R.dimen.land_tablet_margin_left);
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

    private void parseText(final Context context, final Bundle bundle){
        Log.d(LOGTAG, "parseText() called");
        parserThread = new Thread(new Runnable() {
            @Override
            public void run(){
                Log.d(LOGTAG, "parserThread.run() called");
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        Readable readable = Readable.createReadable(context, bundle);
                readable.process(context);
                Log.d(LOGTAG, "readable processed");

                parser = TextParser.newInstance(readable, settingsBundle);
                parser.process();
                Log.d(LOGTAG, "parser processed()");

                processParser(context);
                Log.d(LOGTAG, "parserThread.run() finished");
            }
        });
        parserThread.start();
    }

    /**
     * don't sure that it must be an inner class
     */
    private class Reader implements Runnable {

        private Handler readerHandler;
        private int cancelled;
        private int position;
        private boolean completed;

        public Reader(Handler readerHandler, int position){
            this.readerHandler = readerHandler;
            this.position = position;
            completed = false;
            cancelled = 1;
        }

        @Override
        public void run(){
            if (position < wordList.size()){
                completed = false;
                if (!isCancelled()){
                    updateView(position);
                    readerHandler.postDelayed(this, calcDelay());
                    position++;
                }
            } else {
                completed = true;
                cancelled = 1;
            }
        }

        public int getPosition(){
            return position;
        }

        public void setPosition(int position){
            if (wordList != null &&
                    position < wordList.size() && position >= 0){
                this.position = position;
                updateView(position);
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

        public boolean isCancelled(){
            return cancelled % 2 == 1;
        }

        public void incCancelled(){
            if (!isCancelled())
                performPause();
            else
                performPlay();
        }

        public void performPause(){
            if (!isCancelled()){
                cancelled++;
                YoYo.with(Techniques.Pulse).
                        duration(READER_PULSE_DURATION).
                        playOn(readerLayout);
                if (!settingsBundle.isSwipesEnabled())
                    prevButton.setVisibility(View.VISIBLE);
                showNotification(R.string.pause);
            }
        }

        public void performPlay(){
            if (isCancelled()){
                cancelled++;
                YoYo.with(Techniques.Pulse).
                        duration(READER_PULSE_DURATION).
                        playOn(readerLayout);
                if (!settingsBundle.isSwipesEnabled())
                    prevButton.setVisibility(View.INVISIBLE);
                hideNotification(true);
                readerHandler.postDelayed(this, READER_PULSE_DURATION + 100);
            }
        }

        private int calcDelay(){
            return delayList.get(position) * Math.round(100 * 60 * 1f / settingsBundle.getWPM());
        }

        private void updateView(int pos){
            currentTextView.setText(getCurrentFormattedText(pos));
            leftTextView.setText(getLeftFormattedText(pos));
            rightTextView.setText(getRightFormattedText(pos));

            progressBar.setProgress((int) (100f / wordList.size() * (pos + 1) + .5f));
            hideNotification(false);
        }
    }
}
