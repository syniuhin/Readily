package com.infm.readit;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.infm.readit.essential.TextParser;
import com.infm.readit.readable.Readable;
import com.infm.readit.readable.Storable;
import com.infm.readit.service.LastReadService;
import com.infm.readit.util.OnSwipeTouchListener;
import com.infm.readit.util.SettingsBundle;

import java.io.IOException;
import java.util.List;

/**
 * infm : 16/05/14. Enjoy it ;)
 */
public class ReaderFragment extends Fragment {

    private static final String LOGTAG = "ReaderFragment";

    private long localTime = 0;
    private boolean speedoHided = true;

    //initialized in onCreateView()
    private RelativeLayout readerLayout;
    private TextView currentTextView;
    private TextView leftTextView;
    private TextView rightTextView;
    private TextView speedo;
    private ProgressBar progressBar;
    private ProgressBar parsingProgressBar;
    private ImageButton prevButton;
    //initialized in onActivityCreated()
    private Reader reader;
    private Readable readable;
    private List<String> wordList;
    private List<Integer> emphasisList;
    private List<Integer> delayList;
    private TextParser parser;
    private SettingsBundle settingsBundle;
    private TextParserListener textParserListener;
    private LocalBroadcastManager manager;
    //receiving status
    private Boolean parserReceived = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.d(LOGTAG, "onCreateView() called");

        RelativeLayout fragmentLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_reader, container, false);
        findViews(fragmentLayout);
        periodicallyAnimate();

        (fragmentLayout.findViewById(R.id.reader_layout)).setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeTop(){
                changeWPM(50);
            }

            @Override
            public void onSwipeBottom(){
                changeWPM(-50);
            }

            @Override
            public void onSwipeRight(){
                if (settingsBundle.isSwipesEnabled()){
                    reader.performPause();
                    int pos = reader.getPosition();
                    if (pos > 0){
                        updateView(pos - 1);
                        reader.setPosition(pos - 1);
                    }
                }
            }

            @Override
            public void onSwipeLeft(){
                if (settingsBundle.isSwipesEnabled()){
                    reader.performPause();
                    int pos = reader.getPosition();
                    if (pos < wordList.size() - 1){
                        updateView(pos + 1);
                        reader.setPosition(pos + 1);
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
        return fragmentLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Log.d(LOGTAG, "onActivityCreated() called");

        Activity activity = getActivity();

        settingsBundle = new SettingsBundle(PreferenceManager.getDefaultSharedPreferences(activity));

        createReceiver(activity);

        initPrevButton();
    }

    public void setTime(long localTime){
        this.localTime = localTime;
        speedoHided = false;
    }

    public TextParser getParser(){
        return parser;
    }

    private Spanned getLeftFormattedText(int pos){
        String word = wordList.get(pos);
        int emphasisPosition = emphasisList.get(pos);
        String wordLeft = word.substring(0, emphasisPosition);
        String format = "<font color='#0A0A0A'>" + wordLeft + "</font>";
        return Html.fromHtml(format);
    }

    private Spanned getCurrentFormattedText(int pos){
        String word = wordList.get(pos);
        int emphasisPosition = emphasisList.get(pos);
        String wordEmphasis = word.substring(emphasisPosition, emphasisPosition + 1);
        String format = "<font color='#FA2828'>" + wordEmphasis + "</font>";
        return Html.fromHtml(format);
    }

    private Spanned getRightFormattedText(int pos){
        String word = wordList.get(pos);
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
        while (charLen < 40 && i < wordList.size() - 1 && wordList.get(i).charAt(wordList.get(i).length() - 1) != '\n'){
            String word = wordList.get(++i);
            charLen += word.length() + 1;
            format.append(word).append(" ");
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
        speedo = (TextView) v.findViewById(R.id.speedo);
        prevButton = (ImageButton) v.findViewById(R.id.previousWordImageButton);
    }

    private void initPrevButton(){
        if (!settingsBundle.isSwipesEnabled()){
            prevButton.setImageResource(R.drawable.abc_ic_ab_back_holo_light);
            prevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    int pos = reader.getPosition();
                    if (pos > 0){
                        updateView(pos - 1);
                        reader.setPosition(pos - 1);
                    }
                }
            });
            prevButton.setVisibility(View.INVISIBLE);
        } else
            prevButton.setVisibility(View.GONE); //consider INVISIBLE
    }

    private void updateView(int pos){
        currentTextView.setText(getCurrentFormattedText(pos));
        leftTextView.setText(getLeftFormattedText(pos));
        rightTextView.setText(getRightFormattedText(pos));

        progressBar.setProgress((int) (100f / wordList.size() * (pos + 1) + .5f));

        if (!speedoHided){
            if (System.currentTimeMillis() - localTime > Constants.SPEEDO_SHOWING_LENGTH){
                speedo.setVisibility(View.INVISIBLE);
                speedoHided = true;
            }
        }
    }

    private void showSpeedo(int wpm){
        speedo.setText(wpm + " wpm");
        speedo.setVisibility(View.VISIBLE);
        setTime(System.currentTimeMillis());
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
            showSpeedo(wpmNew);
        } else {
            Log.d(LOGTAG, "WPM remained the same: " + wpm);
        }
    }

    private void continueParserParty(){
        readable = parser.getReadable();

        wordList = readable.getWordList();
        emphasisList = readable.getEmphasisList();
        delayList = readable.getDelayList();

        YoYo.with(Techniques.FlipOutY).
                duration(Constants.SECOND).
                playOn(parsingProgressBar);
        //parsingProgressBar.setVisibility(View.GONE);

        readerLayout.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeIn).
                duration(2 * Constants.SECOND).
                playOn(readerLayout);

        final Handler handler = new Handler();
        reader = new Reader(handler, Math.max(readable.getPosition() - Constants.READER_START_OFFSET, 0));
        handler.postDelayed(reader, 3 * Constants.SECOND);
    }

    private Intent createLastReadServiceIntent(Storable storable, int operation){
        Intent intent = new Intent(getActivity(), LastReadService.class);
        storable.setPosition(reader.getPosition());
        storable.putDataInIntent(intent);
        intent.putExtra(Constants.EXTRA_DB_OPERATION, operation);
        return intent;
    }

    private void createReceiver(Context context){
        textParserListener = new TextParserListener();
        manager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter(Constants.TEXT_PARSER_READY);
        intentFilter.addAction(Constants.TEXT_PARSER_NOT_READY);
        manager.registerReceiver(textParserListener, intentFilter);
    }

    /**
     * periodically animates progressBar
     */
    private void periodicallyAnimate(){
        final Handler handler = new Handler();
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

    @Override
    public void onPause(){
        if (reader != null && !reader.isCancelled())
            reader.incCancelled();
        Log.d(LOGTAG, "onPause() called");
        super.onPause();
    }

    @Override
    public void onStop(){
        Log.d(LOGTAG, "OnStop() called");
        if (parserReceived && !TextUtils.isEmpty(readable.getPath()) && settingsBundle.isCachingEnabled())
            if (reader.isCompleted())
                getActivity().startService(createLastReadServiceIntent((Storable) readable, Constants.DB_OPERATION_DELETE));
            else
                getActivity().startService(createLastReadServiceIntent((Storable) readable, Constants.DB_OPERATION_INSERT));

        settingsBundle.updatePreferences();
        manager.unregisterReceiver(textParserListener);

        getActivity().finish();
        super.onStop();
    }

    /**
     * don't sure that it must be inner class
     */
    private class Reader implements Runnable {

        private Handler handler;
        private int cancelled;
        private int position = 0;
        private boolean completed = false;

        public Reader(Handler handler, int position){
            this.handler = handler;
            this.position = position;
        }

        @Override
        public void run(){
            int wordLength = wordList.size();
            int i = position;
            if (i < wordLength){
                completed = false;
                if (!isCancelled()){
                    updateView(i % wordLength);
                    handler.postDelayed(this, calcDelay(wordLength));
                } else {
                    handler.postDelayed(this, Constants.READER_SLEEP_IDLE);
                }
            } else {
                completed = true;
                cancelled = 1;
            }
        }

        public int getPosition(){ return position; }

        public void setPosition(int position){ this.position = position; }

        public boolean isCompleted(){ return completed; }

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
                        duration(500).
                        playOn(readerLayout);
                if (!settingsBundle.isSwipesEnabled())
                    prevButton.setVisibility(View.VISIBLE);
            }
        }

        public void performPlay(){
            if (isCancelled()){
                cancelled++;
                if (!settingsBundle.isSwipesEnabled())
                    prevButton.setVisibility(View.INVISIBLE);
            }
        }

        private int calcDelay(int wordLength){
            return delayList.get(position++ % wordLength) * Math.round(100 * 60 * 1f / settingsBundle.getWPM());
        }
    }

    private class TextParserListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent){
            try {
                parserReceived = true;
                parser = TextParser.fromString(intent.getStringExtra(Constants.EXTRA_PARSER));
                continueParserParty();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
