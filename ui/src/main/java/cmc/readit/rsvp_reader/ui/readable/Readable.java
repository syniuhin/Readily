package cmc.readit.rsvp_reader.ui.readable;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Pair;

import java.util.List;

import cmc.readit.rsvp_reader.ui.utils.LastReadDBHelper;

/**
 * Created by infm on 6/12/14. Enjoy ;)
 */
abstract public class Readable {
    String text;
    String header;
    String textType;
    Long seconds;
    String path;
    Integer position;

    List<String> wordList;
    List<Integer> delayList;
    List<Integer> emphasisList;
    List<Integer> timeSuffixSum;

    public static Pair<Integer, String> getRowData(Cursor cursor, String path) {
        cursor.moveToFirst();
        if (!TextUtils.isEmpty(path))
            while (cursor.moveToNext())
                if (path.equals(cursor.getString(2)))
                    return new Pair<Integer, String>(cursor.getInt(5), cursor.getString(2));
        return null;
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

    public void makeHeader() {
        int charLen = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < wordList.size() && charLen < 15; ++i) {
            String word = wordList.get(i);
            sb.append(word).append(" ");
            charLen += word.length() + 1;
        }
        setHeader(sb.toString());
    }

    public ContentValues getContentValues() {
        makeHeader();
        ContentValues vals = new ContentValues();
        vals.put(LastReadDBHelper.KEY_HEADER, header);
        vals.put(LastReadDBHelper.KEY_PATH, path);
        vals.put(LastReadDBHelper.KEY_POSITION, position);
        vals.put(LastReadDBHelper.KEY_PERCENT, (int) (position * 100f / wordList.size() + .5f) + "% of text read");
        return vals;
    }
}
