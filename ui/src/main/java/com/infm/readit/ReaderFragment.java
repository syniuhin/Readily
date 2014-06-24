package com.infm.readit;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.infm.readit.database.LastReadDBHelper;
import com.infm.readit.database.LastReadService;
import com.infm.readit.essential.TextParser;
import com.infm.readit.utils.OnSwipeTouchListener;

import java.util.List;

/**
 * infm : 16/05/14. Enjoy it ;)
 */
public class ReaderFragment extends Fragment {
    public static final String LOGTAG = "ReaderFragment";

    private long localTime = 0;
    private boolean speedoHided = true;

    //initialized in onCreateView()
    private RelativeLayout fragmentLayout;
    private TextView currentTextView;
    private TextView leftTextView;
    private TextView rightTextView;
    private TextView speedo;
    private ProgressBar pBar;
    private ImageButton prevButton;

    //initialized in onActivityCreated()
    private Reader reader;
    private SharedPreferences sPref;
    private com.infm.readit.readable.Readable readable;
    private List<String> wordList;
    private List<Integer> emphasisList;
    private List<Integer> delayList;
    private TextParser parser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOGTAG, "onCreateView() called");

        fragmentLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_reader, container, false);
        findViews(fragmentLayout);

        RelativeLayout rl = (RelativeLayout) fragmentLayout.findViewById(R.id.reader_layout);
        rl.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeTop() {
                changeWPM(50);
            }

            @Override
            public void onSwipeBottom() {
                changeWPM(-50);
            }

            @Override
            public void onSwipeRight() {
                if (reader.isCancelled() && sPref.getBoolean(SettingsActivity.PREF_SWIPE, false)) {
                    int pos = reader.getPosition();
                    if (pos > 0) {
                        updateView(pos - 1);
                        reader.setPosition(pos - 1);
                    }
                } else if (!reader.isCancelled())
                    Toast.makeText(getActivity(), getResources().getString(R.string.pause), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwipeLeft() {
                if (reader.isCancelled() && sPref.getBoolean(SettingsActivity.PREF_SWIPE, false)) {
                    int pos = reader.getPosition();
                    if (pos < getParser().getReadable().getWordList().size() - 1) {
                        updateView(pos + 1);
                        reader.setPosition(pos + 1);
                    }
                } else if (!reader.isCancelled())
                    Toast.makeText(getActivity(), getResources().getString(R.string.pause), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClick() {
                if (reader.isCompleted()) {
                    getActivity().getFragmentManager().popBackStack();
                } else {
                    reader.incCancelled();
                    String toShow = (reader.isCancelled())
                            ? getResources().getString(R.string.pause)
                            : getResources().getString(R.string.play);
                    Toast.makeText(getActivity(), toShow, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return fragmentLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(LOGTAG, "onActivityCreated() called");

        Activity activity = getActivity();
        mkReadable(activity);
        sPref = PreferenceManager.getDefaultSharedPreferences(activity);
        initPrevButton();
        mkParser();

        final Handler handler = new Handler();
        reader = new Reader(handler, activity, readable.getPosition());
        handler.postDelayed(reader, 2000);
    }

    public void setTime(long localTime) {
        this.localTime = localTime;
        speedoHided = false;
    }

    public TextParser getParser() {
        return parser;
    }

    public Spanned getLeftFormattedText(int pos) {
        String word = wordList.get(pos);
        int emphasisPosition = emphasisList.get(pos);
        String wordLeft = word.substring(0, emphasisPosition);
        String format = "<font color='#0A0A0A'>" + wordLeft + "</font>";
        return Html.fromHtml(format);
    }

    public Spanned getCurrentFormattedText(int pos) {
        String word = wordList.get(pos);
        int emphasisPosition = emphasisList.get(pos);
        String wordEmphasis = word.substring(emphasisPosition, emphasisPosition + 1);
        String format = "<font color='#FA2828'>" + wordEmphasis + "</font>";
        return Html.fromHtml(format);
    }

    public Spanned getRightFormattedText(int pos) {
        String word = wordList.get(pos);
        int emphasisPosition = emphasisList.get(pos);
        String wordRight = word.substring(emphasisPosition + 1, word.length());
        String format = "<font><font color='#0A0A0A'>" + wordRight + "</font>";
        if (sPref.getBoolean(SettingsActivity.PREF_SHOW_CONTEXT, true))
            format += getNextFormat(pos);
        format += "</font>";
        return Html.fromHtml(format);
    }

    public String getNextFormat(int pos) {
        int charLen = 0;
        int i = pos;
        StringBuilder format = new StringBuilder("&nbsp;<font color='#AAAAAA'>");
        while (charLen < 40 && i < wordList.size() - 1 && wordList.get(i).charAt(wordList.get(i).length() - 1) != '\n') {
            String word = wordList.get(++i);
            charLen += word.length() + 1;
            format.append(word).append(" ");
        }
        format.append("</font>");
        return format.toString();
    }

    private void findViews(View v) {
        currentTextView = (TextView) v.findViewById(R.id.currentWordTextView);
        leftTextView = (TextView) v.findViewById(R.id.leftWordTextView);
        rightTextView = (TextView) v.findViewById(R.id.rightWordTextView);
        pBar = (ProgressBar) v.findViewById(R.id.progressBar);
        speedo = (TextView) v.findViewById(R.id.speedo);
        prevButton = (ImageButton) v.findViewById(R.id.previousWordImageButton);
    }

    private void initPrevButton() {
        if (!sPref.getBoolean(SettingsActivity.PREF_SWIPE, false)) {
            prevButton.setImageResource(R.drawable.abc_ic_ab_back_holo_light);
            prevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = reader.getPosition();
                    if (pos > 0) {
                        updateView(pos - 1);
                        reader.setPosition(pos - 1);
                    }
                }
            });
            prevButton.setVisibility(View.INVISIBLE);
        } else
            prevButton.setVisibility(View.GONE); //consider INVISIBLE
    }

    private void updateView(int pos) {
        currentTextView.setText(getCurrentFormattedText(pos));
        leftTextView.setText(getLeftFormattedText(pos));
        rightTextView.setText(getRightFormattedText(pos));

        pBar.setProgress((int) (100f / wordList.size() * (pos + 1) + .5f));

        if (!speedoHided) {
            if (System.currentTimeMillis() - localTime > 1500) {
                speedo.setVisibility(View.INVISIBLE);
                speedoHided = true;
            }
        }

        if (sPref.getBoolean(SettingsActivity.PREF_SWIPE, false) && reader.isCancelled())
            prevButton.setVisibility(View.VISIBLE);
        else if (!reader.isCancelled())
            prevButton.setVisibility(View.INVISIBLE); //consider GONE
    }

    private void showSpeedo(int wpm) {
        speedo.setText(wpm + " wpm");
        speedo.setVisibility(View.VISIBLE);
        setTime(System.currentTimeMillis());
    }

    /**
     * TODO: make max/min optional
     *
     * @param delta: delta itself. Default value: 50
     */
    private void changeWPM(int delta) {
        int wpm = Integer.parseInt(sPref.getString(SettingsActivity.PREF_WPM, "250"));
        int wpmNew = Math.min(1200, Math.max(wpm + delta, 50));

        SharedPreferences.Editor q = sPref.edit();
        q.putString(SettingsActivity.PREF_WPM, Integer.toString(wpmNew));
        q.commit();

        Log.d(LOGTAG, "WPM changed from " + wpm + " to " + wpmNew);
        showSpeedo(wpmNew);
    }

    private void mkReadable(Context context) {
        readable = com.infm.readit.readable.Readable.newInstance(context,
                getArguments().getInt("source_type", -1),
                getArguments().getString("text", getResources().getString(R.string.sample_text)),
                getArguments().getString(LastReadDBHelper.KEY_PATH));
        readable.setPosition(Math.max(getArguments().getInt(LastReadDBHelper.KEY_POSITION), 0));
    }

    /**
     * just in order to wrap this in ProgressBar
     */
    private void mkParser() {
        parser = new TextParser(readable, sPref);
        initParserData();
    }

    private void initParserData() {
        wordList = parser.getReadable().getWordList();
        emphasisList = parser.getReadable().getEmphasisList();
        delayList = parser.getReadable().getDelayList();
    }

/*
    private void wrapInProgressBar(View v){
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);

        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mkParser();
                progressDialog.dismiss();
            }
        }).start();
    }
*/

    private Intent makeLastReadServiceIntent() {
        Intent intent = new Intent(getActivity(), LastReadService.class);
        readable.setPosition(reader.getPosition());
        readable.putDataInIntent(intent);
        return intent;
    }

    @Override
    public void onPause() {
        if (!reader.isCancelled())
            reader.incCancelled();
        Log.d(LOGTAG, "onPause() called");
        super.onPause();
    }
    @Override
    public void onStop() {
        getActivity().startService(makeLastReadServiceIntent());
        Log.d(LOGTAG, "OnStop() called");
        super.onStop();
    }

    /**
     * don't sure that it must be inner class
     */
    private class Reader implements Runnable, SharedPreferences.OnSharedPreferenceChangeListener {
        private Handler handler;
        private Context context;
        private int cancelled;
        private int pos = 0;
        private double SPM;
        private boolean completed = false;

        public Reader(Handler handler, Context context, int pos) {
            this.handler = handler;
            this.context = context;
            this.pos = pos;

            SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(this.context);
            PreferenceManager.getDefaultSharedPreferences(this.context).registerOnSharedPreferenceChangeListener(this);

            final int WPM = Integer.parseInt(sPref.getString(SettingsActivity.PREF_WPM, "250"));
            SPM = 60 * 1f / WPM;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (SettingsActivity.PREF_WPM.equals(key))
                SPM = 60f / Integer.parseInt(sharedPreferences.getString(key, "250"));
        }

        @Override
        public void run() {
            int wlen = wordList.size();
            int i = pos;
            if (i < wlen) {
                completed = false;
                if (!isCancelled()) {
                    updateView(i % wlen);
                    handler.postDelayed(this, delayList.get(pos++ % wlen) * Math.round(100 * SPM));
                } else {
                    handler.postDelayed(this, 500);
                }
            } else {
                completed = true;
                cancelled = 1;
            }
        }

        public int getPosition() {
            return pos;
        }

        public void setPosition(int pos) {
            this.pos = pos;
        }

        public boolean isCompleted() {
            return completed;
        }

        public boolean isCancelled() {
            return cancelled % 2 == 1;
        }

        public void incCancelled() {
            cancelled++;
        }
    }
}
