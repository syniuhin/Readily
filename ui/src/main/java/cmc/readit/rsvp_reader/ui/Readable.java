package cmc.readit.rsvp_reader.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Pair;

import java.util.List;

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

    protected String getText() {
        return text;
    }

    protected void setText(String text) {
        this.text = text;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    protected String getTextType() {
        return textType;
    }

    protected void setTextType(String textType) {
        this.textType = textType;
    }

    protected Long getDateChanged() {
        return seconds;
    }

    protected void setDateChanged(Long seconds) {
        this.seconds = seconds;
    }

    protected String getPath() {
        return path;
    }

    protected void setPath(String path) {
        this.path = path;
    }

    protected Integer getPosition() {
        return position;
    }

    protected void setPosition(Integer position) {
        this.position = position;
    }

    protected List<String> getWordList() {
        return wordList;
    }

    protected void setWordList(List<String> wordList) {
        this.wordList = wordList;
    }

    protected List<Integer> getDelayList() {
        return delayList;
    }

    protected void setDelayList(List<Integer> delayList) {
        this.delayList = delayList;
    }

    protected List<Integer> getEmphasisList() {
        return emphasisList;
    }

    protected void setEmphasisList(List<Integer> emphasisList) {
        this.emphasisList = emphasisList;
    }

    protected List<Integer> getTimeSuffixSum() {
        return timeSuffixSum;
    }

    protected void setTimeSuffixSum(List<Integer> timeSuffixSum) {
        this.timeSuffixSum = timeSuffixSum;
    }

    abstract protected String getLink();

    abstract protected void setLink(String link);

    abstract protected ChunkData getChunkData();

    abstract protected void setChunkData(ChunkData data);

    protected void makeHeader() {
        int charLen = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < wordList.size() && charLen < 15; ++i) {
            String word = wordList.get(i);
            sb.append(word).append(" ");
            charLen += word.length() + 1;
        }
        setHeader(sb.toString());
    }

    protected ContentValues getContentValues() {
        makeHeader();
        ContentValues vals = new ContentValues();
        vals.put(LastReadDBHelper.KEY_HEADER, header);
        vals.put(LastReadDBHelper.KEY_PATH, path);
        vals.put(LastReadDBHelper.KEY_POSITION, position);
        vals.put(LastReadDBHelper.KEY_PERCENT, (int) (position * 100f / wordList.size() + .5f) + "% of text read");
        return vals;
    }

    public static Pair<Integer, String> getRowData(Cursor cursor, String path) {
        cursor.moveToFirst();
        if (!TextUtils.isEmpty(path))
            while (cursor.moveToNext())
                if (path.equals(cursor.getString(2)))
                    return new Pair<Integer, String>(cursor.getInt(5), cursor.getString(2));
        return null;
    }
}
