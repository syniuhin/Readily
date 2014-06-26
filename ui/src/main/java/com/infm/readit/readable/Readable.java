package com.infm.readit.readable;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.infm.readit.Constants;
import com.infm.readit.database.DataBundle;
import com.infm.readit.database.LastReadDBHelper;
import com.infm.readit.essential.TextParser;
import com.infm.readit.utils.Utils;

import java.util.List;

/**
 * Created by infm on 6/12/14. Enjoy ;)
 */
abstract public class Readable {

    public static String LOGTAG = "Readable";

    protected String text;
    protected String header;
    protected String textType;
    protected Long seconds;
    protected String path;
    protected Integer position;

    protected List<String> wordList;
    protected List<Integer> delayList;
    protected List<Integer> emphasisList;
    protected List<Integer> timeSuffixSum;

    public static Pair<Integer, Integer> getRowData(Cursor cursor, String path) {
        Log.d(LOGTAG, "getRowData() called; cursor size: " + cursor.getCount() + "; path: " + path);
        Integer rowId = -1;
        Integer position = -1;
        if (!TextUtils.isEmpty(path)) {
            while (cursor.moveToNext() && rowId == -1) {
                if (path.equals(cursor.getString(LastReadDBHelper.COLUMN_PATH))) {
                    rowId = cursor.getInt(LastReadDBHelper.COLUMN_ROWID);
                    position = cursor.getInt(LastReadDBHelper.COLUMN_POSITION);
                }
            }
        }
        cursor.close();
        Log.d(LOGTAG, "getRowData(); rowId = " + rowId + "; position = " + position);
        if (rowId == -1)
            return null;
        return new Pair<Integer, Integer>(rowId, position);
    }

    public static Readable newInstance(Context context, Integer intentType, String intentText, String intentPath) {
        Readable readable;
        if (TextUtils.isEmpty(intentText))
            readable = new TestSettingsText(context);
        else {
            if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.PREF_CACHE, true))
                intentPath = null;
            switch (intentType) {
                case Utils.TYPE_CLIPBOARD:
                    readable = new CopiedFromClipboard();
                    readable.setText(intentText);
                    break;
                case Utils.TYPE_TXT:
                    readable = new FileReadable();
                    readable.setText(intentText);
                    readable.setTextType("text/plain");
                    break;
                case Utils.TYPE_EPUB:
                    readable = new FileReadable();
                    readable.setText(intentText);
                    readable.setTextType("text/html");
                    break;
                default:
                    String link;
                    if (intentText.length() < Constants.NON_LINK_LENGTH &&
                            !TextUtils.isEmpty(link = TextParser.findLink(TextParser.compilePattern(), intentText)))
                        readable = new HtmlReadable(link);
                    else
                        throw new IllegalArgumentException("wtf, desired Readable doesn't fit any subclass"); // actually I don't know what to do here
            }
            readable.setPath(intentPath);
        }
        return readable;
    }

    public static ContentValues getContentValues(DataBundle dataBundle) {
        ContentValues values = new ContentValues();
        values.put(LastReadDBHelper.KEY_HEADER, dataBundle.getHeader());
        values.put(LastReadDBHelper.KEY_PATH, dataBundle.getPath());
        values.put(LastReadDBHelper.KEY_POSITION, dataBundle.getPosition());
        values.put(LastReadDBHelper.KEY_PERCENT, dataBundle.getPercent());
        return values;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getTextType() {
        return textType;
    }

    public void setTextType(String textType) {
        this.textType = textType;
    }

    public Long getDateChanged() {
        return seconds;
    }

    public void setDateChanged(Long seconds) {
        this.seconds = seconds;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public List<String> getWordList() {
        return wordList;
    }

    public void setWordList(List<String> wordList) {
        this.wordList = wordList;
    }

    public List<Integer> getDelayList() {
        return delayList;
    }

    public void setDelayList(List<Integer> delayList) {
        this.delayList = delayList;
    }

    public List<Integer> getEmphasisList() {
        return emphasisList;
    }

    public void setEmphasisList(List<Integer> emphasisList) {
        this.emphasisList = emphasisList;
    }

    public List<Integer> getTimeSuffixSum() {
        return timeSuffixSum;
    }

    public void setTimeSuffixSum(List<Integer> timeSuffixSum) {
        this.timeSuffixSum = timeSuffixSum;
    }

    abstract public String getLink();

    abstract public void setLink(String link);

    abstract public ChunkData getChunkData();

    abstract public void setChunkData(ChunkData data);

    private void makeHeader() {
        int charLen = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < wordList.size() && charLen < 20; ++i) {
            String word = wordList.get(i);
            sb.append(word).append(" ");
            charLen += word.length() + 1;
        }
        header = sb.toString();
    }

    /**
     * TODO: design class that contain all this data (completed)
     *
     * @param intent: intent to put
     */
    public void putDataInIntent(Intent intent) {
        makeHeader();
        intent.putExtra(Constants.EXTRA_HEADER, header);
        intent.putExtra(Constants.EXTRA_PATH, path);
        intent.putExtra(Constants.EXTRA_POSITION, position);
        intent.putExtra(Constants.EXTRA_PERCENT, 100 - (int) (position * 100f / wordList.size() + .5f) + "% left");
    }
}
