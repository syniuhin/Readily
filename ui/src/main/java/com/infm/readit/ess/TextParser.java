package com.infm.readit.ess;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.infm.readit.SettingsActivity;
import com.infm.readit.readable.Readable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.jetwick.snacktory.HtmlFetcher;
import de.jetwick.snacktory.JResult;

public class TextParser {

    public static final String LOGTAG = "TextParser";
    public static final Map<String, Integer> PRIORITIES;

    static {
        /**
         * a 	b 	c 	d 	e 	f 	g 	h 	i 	j 	k 	l 	m 	n 	o 	p 	q 	r 	s 	t 	u 	v 	w 	x 	y 	z
         * 9    4   4   4   10  12  10  12  8   10  8   6   6   5   8   6   12  5   15  12  14  12  14  13  14  12
         */
        final int[] p = {10, 4, 4, 4, 9, 12, 10, 12, 8, 10, 8, 6, 6, 5, 8, 6, 12, 5, 15, 12, 14, 12, 14, 13, 14, 12};
        Map<String, Integer> mMap = new HashMap<String, Integer>();
        mMap.put("a", p[0]);
        mMap.put("b", p[1]);
        mMap.put("c", p[2]);
        mMap.put("d", p[3]);
        mMap.put("e", p[4]);
        mMap.put("f", p[5]);
        mMap.put("g", p[6]);
        mMap.put("h", p[7]);
        mMap.put("i", p[8]);
        mMap.put("j", p[9]);
        mMap.put("k", p[10]);
        mMap.put("l", p[11]);
        mMap.put("m", p[12]);
        mMap.put("n", p[13]);
        mMap.put("o", p[14]);
        mMap.put("p", p[15]);
        mMap.put("q", p[16]);
        mMap.put("r", p[17]);
        mMap.put("s", p[18]);
        mMap.put("t", p[19]);
        mMap.put("u", p[20]);
        mMap.put("v", p[21]);
        mMap.put("w", p[22]);
        mMap.put("x", p[23]);
        mMap.put("y", p[24]);
        mMap.put("z", p[25]);

        /**
         А а 	Б б 	В в 	Г г 	Д д 	Е е 	Ё ё
         Ж ж 	З з 	И и 	Й й 	К к 	Л л 	М м
         Н н 	О о 	П п 	Р р 	С с 	Т т 	У у
         Ф ф 	Х х 	Ц ц 	Ч ч 	Ш ш 	Щ щ 	Ъ ъ
         Ы ы 	Ь ь 	Э э 	Ю ю 	Я я
         */
        mMap.put("а", 10);
        mMap.put("б", 4);
        mMap.put("в", 4);
        mMap.put("г", 7);
        mMap.put("д", 4);
        mMap.put("е", 7);
        mMap.put("ё", 14);
        mMap.put("ж", 9);
        mMap.put("з", 9);
        mMap.put("и", 6);
        mMap.put("й", 7);
        mMap.put("к", 5);
        mMap.put("л", 4);
        mMap.put("м", 4);
        mMap.put("н", 4);
        mMap.put("о", 10);
        mMap.put("п", 8);
        mMap.put("р", 10);
        mMap.put("с", 12);
        mMap.put("т", 5);
        mMap.put("у", 9);
        mMap.put("ф", 15);
        mMap.put("х", 14);
        mMap.put("ц", 14);
        mMap.put("ч", 13);
        mMap.put("ш", 10);
        mMap.put("щ", 10);
        mMap.put("ъ", 0);
        mMap.put("ы", 10);
        mMap.put("ь", 0);
        mMap.put("э", 10);
        mMap.put("ю", 12);
        mMap.put("я", 11);

        /**
         * ґ і ї є
         */
        mMap.put("ґ", 15);
        mMap.put("і", 14);
        mMap.put("ї", 18);
        mMap.put("є", 12);
        PRIORITIES = Collections.unmodifiableMap(mMap);
    }

    public static final String makeMeSpecial = " " + "." + "!" + "?" + "-" + "—" + ":" + ";" + "," + "\n" + '\"' + "(" + ")" + "\t";
    private com.infm.readit.readable.Readable readable;
    private int lengthPreference;
    private List<Integer> delayCoefficients;

    /**
     * TODO: design it in more elegant way
     */
    public TextParser(Readable mReadable, SharedPreferences sPref) {
        readable = mReadable;

        // for now, lol
        lengthPreference = 13;
        delayCoefficients = buildDelayListCoefficients(sPref);

        String mText = readable.getText();
        String mTextType = readable.getTextType();
        String link = readable.getLink();
        if (!TextUtils.isEmpty(link))
            try {
                mText = new ArticleHtmlParser().execute(link).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        else if ("text/html".equals(mTextType)) {
            try {
                Document doc = new InnerHtmlParser().execute(mText).get();
                mText = doc.title() + doc.select("p").text();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else if (!"text/plain".equals(mTextType)) {
            Log.e(LOGTAG, "Wrong text type");
            return;
        }

        readable.setText(mText);

        normalize(readable);
        cutLongWords(readable);
        buildDelayList(readable);
        buildTimeSuffixSum(readable);
        cleanFromLines(readable);
        buildEmphasis(readable);
    }

    public static String findLink(String text) {
        if (text.isEmpty()) return "";
        else if (text.length() < 500) {
            Pattern pattern = Pattern.compile(
                    "\\b(((ht|f)tp(s?)\\:\\/\\/|~\\/|\\/)|www.)" +
                            "(\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov" +
                            "|mil|biz|info|mobi|name|aero|jobs|museum" +
                            "|travel|[a-z]{2}))(:[\\d]{1,5})?" +
                            "(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?" +
                            "((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" +
                            "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)" +
                            "(&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" +
                            "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*" +
                            "(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b"
            );
            Matcher matcher = pattern.matcher(text);
            if (matcher.find())
                return matcher.group();
        }

        return "";
    }

    public int getLengthPreference() {
        return lengthPreference;
    }

    public List<Integer> getDelayCoefficients() {
        return delayCoefficients;
    }

    public Readable getReadable() {
        return readable;
    }

    public void setReadable(Readable readable) {
        this.readable = readable;
    }

    private int checkForRepetitions(char ch) {
        for (int i = 0; i < makeMeSpecial.length(); ++i) {
            if (Character.isWhitespace(ch))
                return 0;
            if (ch == makeMeSpecial.charAt(i))
                return i;
        }
        return -1;
    }

    protected void normalize(Readable readable) {
        String text = readable.getText();

        StringBuilder res = new StringBuilder();

		/* repetitions */
        int prev = -1;
        for (int i = 0; i < text.length(); ++i) {
            char ch = text.charAt(i);
            int pos = checkForRepetitions(ch);
            if (pos > 0) {
                if (prev != pos) {
                    prev = pos;
                    res.append(ch);
                }
            } else {
                prev = -1;
                res.append(ch);
            }
        }

		/* spaces before punctuation */
        text = res.toString();
        res = new StringBuilder();
        for (int i = 0; i < text.length(); ++i) {
            String ch = text.substring(i, i + 1);
            if (res.length() > 0 && makeMeSpecial.contains(ch) && !Character.isWhitespace(ch.charAt(0)) &&
                    res.charAt(res.length() - 1) == ' ')
                res.deleteCharAt(res.length() - 1);
            res.append(ch);
        }

		/* spaces after punct. */
        text = res.toString();
        res = new StringBuilder();
        for (int i = 0; i < text.length(); ++i) {
            String ch = text.substring(i, i + 1);
            res.append(ch);
            if (makeMeSpecial.contains(ch) && !Character.isWhitespace(ch.charAt(0)) && i < text.length() - 1 && Character.isLetter(text.charAt(i + 1)))
                res.append(" ");
        }

        /* abbreviations */
        text = res.toString();
        res = new StringBuilder();
        for (int i = 0; i < text.length(); ++i) {
            if (i > 0 && text.charAt(i - 1) == '.') {
                if (!(i + 2 < text.length() && text.charAt(i + 2) == '.'))
                    res.append(text.charAt(i));
            } else res.append(text.charAt(i));
        }
        readable.setText(res.toString());
    }

    protected void cutLongWords(Readable readable) {
        String text = readable.getText();
        List<String> res = new ArrayList<String>();
        for (String word : text.split(" ")) {
            boolean isComplex = false;
            while (word.length() - 1 > lengthPreference) {
                isComplex = true;
                String toAppend;
                int pos = word.length() - 3;
                while (pos > 1 && !Character.isLetter(word.charAt(pos)))
                    --pos;
                toAppend = word.substring(0, pos);
                word = word.substring(pos);
                res.add("-" + toAppend + "-");
            }
            if (isComplex)
                res.add("-" + word);
            else
                res.add(word);
        }
        StringBuilder sb = new StringBuilder();
        for (String s : res) sb.append(s).append(" ");
        readable.setText(sb.toString());
    }

    protected void cleanFromLines(Readable readable) {
        List<String> words = new ArrayList<String>(Arrays.asList(readable.getText().split(" ")));
        List<String> res = new ArrayList<String>();
        for (String word : words)
            if (word.length() == 0) continue;
            else if (word.charAt(0) == '-') res.add(word.substring(1, word.length()));
            else res.add(word);
        readable.setWordList(res);
    }

    protected int measureWord(String word) {
        if (word.length() == 0)
            return delayCoefficients.get(0);
        int res = 0;
        for (char ch : word.toCharArray()) {
            int tempRes = delayCoefficients.get(0);
            if (ch == '-')
                tempRes = delayCoefficients.get(1);
            if (ch == '\t')
                tempRes = delayCoefficients.get(4);
            switch (ch) {
                case ',':
                    tempRes = delayCoefficients.get(1);
                    break;
                case '.':
                    tempRes = delayCoefficients.get(2);
                    break;
                case '!':
                    tempRes = delayCoefficients.get(2);
                    break;
                case '?':
                    tempRes = delayCoefficients.get(2);
                    break;
                case '-':
                    tempRes = delayCoefficients.get(3);
                    break;
                case '—':
                    tempRes = delayCoefficients.get(3);
                    break;
                case ':':
                    tempRes = delayCoefficients.get(3);
                    break;
                case ';':
                    tempRes = delayCoefficients.get(3);
                    break;
                case '\n':
                    tempRes = delayCoefficients.get(4);
            }
            res = Math.max(res, tempRes);
        }
        return res;
    }

    protected void buildDelayList(Readable readable) {
        String text = readable.getText();
        List<Integer> res = new ArrayList<Integer>();
        String[] words = text.split(" ");
        for (String word : words) res.add(measureWord(word));
        readable.setDelayList(res);
    }

    protected void buildTimeSuffixSum(Readable readable) {
        List<Integer> delayList = readable.getDelayList();
        List<Integer> res = new ArrayList<Integer>();
        res.add(delayList.get(0));
        for (int i = delayList.size() - 2; i >= 0; --i)
            res.add(res.get(res.size() - 1) + delayList.get(i));
        Collections.reverse(res);
        readable.setTimeSuffixSum(res);
    }

    protected void buildEmphasis(Readable readable) {
        List<String> words = readable.getWordList();
        List<Integer> res = new ArrayList<Integer>();
        for (String word : words) {
            /* some kind of experiment, huh? */
            Map<String, Pair<Integer, Integer>> priorities = new HashMap<String, Pair<Integer, Integer>>();
            int len = word.length();
            for (int i = 0; i < len; ++i) {
                if (!Character.isLetter(word.charAt(i))) continue;

                String ch = word.substring(i, i + 1).toLowerCase();
                if (PRIORITIES.get(ch) != null &&
                        (priorities.get(ch) == null ||
                                priorities.get(ch).first < PRIORITIES.get(ch) * 100 / Math.max(1, Math.abs(len / 2 - i)))) {
                    priorities.put(ch, new Pair<Integer, Integer>(PRIORITIES.get(ch) * 100 / Math.max(1, Math.abs(len / 2 - i)), i));
                } else priorities.put(ch, new Pair<Integer, Integer>(0, i));
                if (i + 1 < word.length() && word.charAt(i) == word.charAt(i + 1)) {
                    priorities.put(ch, new Pair<Integer, Integer>(priorities.get(ch).first * 4, i));
                }
            }
            int resInd = word.length() / 2, mmax = 0;
            for (Map.Entry<String, Pair<Integer, Integer>> entry : priorities.entrySet()) {
                if (mmax < entry.getValue().first) {
                    mmax = entry.getValue().first;
                    resInd = entry.getValue().second;
                }
            }
            res.add(resInd);
        }
        readable.setEmphasisList(res);
    }

    /**
     * delayList: {default; coma/long word; end of sentence; '-' or ':' or ';'; beginning of a paragraph}
     * default value is 10
     */
    private ArrayList<Integer> buildDelayListCoefficients(SharedPreferences sPref) {
        ArrayList<Integer> delayCoeffs = new ArrayList<Integer>();
        delayCoeffs.add(10);
        if (!sPref.getBoolean(SettingsActivity.PREF_PUNCTUATION_DIFFERS, false))
            for (int i = 0; i < 4; ++i) delayCoeffs.add(10);
        else {
            delayCoeffs.add(Integer.parseInt(sPref.getString(SettingsActivity.PREF_COMA_OR_LONG, "15")));
            delayCoeffs.add(Integer.parseInt(sPref.getString(SettingsActivity.PREF_END_OF_SENTENCE, "20")));
            delayCoeffs.add(Integer.parseInt(sPref.getString(SettingsActivity.PREF_DASH_OR_COLON, "18")));
            delayCoeffs.add(Integer.parseInt(sPref.getString(SettingsActivity.PREF_BEGINNING_OF_PARAGRAPH, "20")));
        }
        return delayCoeffs;
    }

    private class InnerHtmlParser extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... params) {
            Document doc = null;
            try {
                doc = Jsoup.parse(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return doc;
        }
    }

    private class ArticleHtmlParser extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            HtmlFetcher fetcher = new HtmlFetcher();
            JResult res = null;
            try {
                res = fetcher.fetchAndExtract(url, 10000, true);
                return res.getTitle() + " || " + res.getText();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }
    }
}