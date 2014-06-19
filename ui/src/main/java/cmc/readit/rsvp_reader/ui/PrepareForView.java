package cmc.readit.rsvp_reader.ui;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * infm : 16/05/14. Enjoy it ;)
 */
public class PrepareForView {
    public static final String PFVTAG = "PrepareForView class";

    private List<String> wordList;
    private List<Integer> emphasisList;
    private TextParser parser;
    private Display display;
    private long tm = 0;
    private boolean speedoHided = true;
    private SharedPreferences preferences;

    /**
     * @param readable : readable instance to parse. Needed only as a parameter to the parser
     * @param _display : display instance to get size of screen
     * @param prefs    : prefs. Needed only as a parameter to the parser
     */
    public PrepareForView(Readable readable, Display _display, SharedPreferences prefs) {
        Log.d(PFVTAG, "constructor called");
        parser = new TextParser(readable, prefs);
        wordList = parser.getReadable().getWordList();
        emphasisList = parser.getReadable().getEmphasisList();
        display = _display;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        preferences = prefs;
    }

    public void setTime(long _tm) {
        tm = _tm;
        speedoHided = false;
    }

    public TextParser getParser() {
        return parser;
    }

    public int getAppWidth() {
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public int getAppHeight() {
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public Spanned getLeftFormattedText(int pos) {
        String word = wordList.get(pos);
        int emphasisPosition = emphasisList.get(pos);
        String word_left = word.substring(0, emphasisPosition);
        String format = "<font color='#0A0A0A'>" + word_left + "</font>";
        return Html.fromHtml(format);
    }

    public Spanned getCurrentFormattedText(int pos) {
        String word = wordList.get(pos);
        int emphasisPosition = emphasisList.get(pos);
        String word_emphasis = word.substring(emphasisPosition, emphasisPosition + 1);
        String format = "<font color='#FA2828'>" + word_emphasis + "</font>";
        return Html.fromHtml(format);
    }

    public Spanned getRightFormattedText(int pos) {
        String word = wordList.get(pos);
        int emphasisPosition = emphasisList.get(pos);
        String word_right = word.substring(emphasisPosition + 1, word.length());
        String format = "<font><font color='#0A0A0A'>" + word_right + "</font>";
        if (preferences.getBoolean(SettingsActivity.PREF_SHOW_CONTEXT, true))
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

    public void updateView(View v, int pos) {
        TextView currentTextView = (TextView) v.findViewById(R.id.currentWordTextView);
        currentTextView.setText(getCurrentFormattedText(pos));

        TextView leftTextView = (TextView) v.findViewById(R.id.leftWordTextView);
        leftTextView.setText(getLeftFormattedText(pos));

        TextView rightTextView = (TextView) v.findViewById(R.id.rightWordTextView);
        rightTextView.setText(getRightFormattedText(pos));

        ProgressBar pBar = (ProgressBar) v.findViewById(R.id.progressBar);
        pBar.setProgress((int) (100f / wordList.size() * (pos + 1) + .5f));

        if (!speedoHided) {
            if (System.currentTimeMillis() - tm > 1500) {
                TextView speedo = (TextView) v.findViewById(R.id.speedo);
                speedo.setVisibility(View.INVISIBLE);
                speedoHided = true;
            }
        }
    }

    public void initializeView(View v) {
        int paddingLeft = (int) (getAppWidth() * 150f / 480f + .5f);

        ImageView top = (ImageView) v.findViewById(R.id.pointerTopImageView);
        RelativeLayout.LayoutParams paramsTop = (RelativeLayout.LayoutParams) top.getLayoutParams();
        paramsTop.setMargins(paddingLeft, 0, 0, 0);
        top.setLayoutParams(paramsTop);

        ImageView bot = (ImageView) v.findViewById(R.id.pointerBottomImageView);
        RelativeLayout.LayoutParams paramsBot = (RelativeLayout.LayoutParams) bot.getLayoutParams();
        paramsBot.setMargins(paddingLeft, 0, 0, 0);
        bot.setLayoutParams(paramsBot);

        TextView leftTextView = (TextView) v.findViewById(R.id.leftWordTextView);
        RelativeLayout.LayoutParams paramsLeft = (RelativeLayout.LayoutParams) leftTextView.getLayoutParams();
        paramsLeft.addRule(RelativeLayout.LEFT_OF, R.id.currentWordTextView);
        paramsLeft.addRule(RelativeLayout.ALIGN_BASELINE, R.id.currentWordTextView);
        leftTextView.setLayoutParams(paramsLeft);

        TextView rightTextView = (TextView) v.findViewById(R.id.rightWordTextView);
        RelativeLayout.LayoutParams paramsRight = (RelativeLayout.LayoutParams) rightTextView.getLayoutParams();
        paramsRight.addRule(RelativeLayout.RIGHT_OF, R.id.currentWordTextView);
        paramsRight.addRule(RelativeLayout.ALIGN_BASELINE, R.id.currentWordTextView);
        rightTextView.setLayoutParams(paramsRight);

        TextView centerTextView = (TextView) v.findViewById(R.id.currentWordTextView);
        switch (Integer.parseInt(preferences.getString(SettingsActivity.PREF_TYPEFACE, "0"))) {
            case 0:
                leftTextView.setTypeface(Typeface.MONOSPACE);
                rightTextView.setTypeface(Typeface.MONOSPACE);
                centerTextView.setTypeface(Typeface.MONOSPACE);
                break;
            case 1:
                leftTextView.setTypeface(Typeface.SANS_SERIF);
                rightTextView.setTypeface(Typeface.SANS_SERIF);
                centerTextView.setTypeface(Typeface.SANS_SERIF);
                break;
            case 2:
                leftTextView.setTypeface(Typeface.SERIF);
                rightTextView.setTypeface(Typeface.SERIF);
                centerTextView.setTypeface(Typeface.SERIF);
                break;
        }
    }

    public void showSpeedo(int wpm, View v) {
        TextView speedo = (TextView) v.findViewById(R.id.speedo);
        speedo.setText(wpm + " wpm");
        speedo.setVisibility(View.VISIBLE);
        setTime(System.currentTimeMillis());
    }
}
