package com.infmme.readilyapp.readable.old;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.infmme.readilyapp.util.Constants;
import com.infmme.readilyapp.R;
import com.infmme.readilyapp.database.DataBundle;
import com.infmme.readilyapp.essential.TextParser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by infm on 6/12/14. Enjoy ;)
 */
abstract public class Readable implements Serializable {

  public static final int TYPE_RAW = 0;
  public static final int TYPE_CLIPBOARD = 1;
  public static final int TYPE_FILE = 2;
  public static final int TYPE_TXT = 3;
  public static final int TYPE_EPUB = 4;
  public static final int TYPE_NET = 5;
  public static final int TYPE_FB2 = 6;

  protected StringBuilder text;
  protected String header;
  protected long seconds;
  protected String path;
  protected int position;
  protected int type;
  protected DataBundle rowData;
  protected boolean processFailed;
  protected boolean processed;
  protected List<String> wordList;
  protected List<Integer> delayList;
  protected List<Integer> emphasisList;

  public Readable() {
    text = new StringBuilder();
    wordList = new ArrayList<String>();
    delayList = new ArrayList<Integer>();
    emphasisList = new ArrayList<Integer>();
    rowData = new DataBundle();
  }

  public Readable(Readable that) {
    text = that.getTextBuilder();
    header = that.getHeader();
    seconds = that.getSeconds();
    path = that.getPath();
    position = that.getPosition();
    type = that.getType();
    rowData = that.getRowData();
    processFailed = that.isProcessFailed();
    processed = that.isProcessed();

    wordList = new ArrayList<String>(that.getWordList());
    delayList = new ArrayList<Integer>(that.getDelayList());
    emphasisList = new ArrayList<Integer>(that.getEmphasisList());
  }

  public static Readable createReadable(Context context, Bundle bundle) {
    Readable readable = null;
    if (bundle != null) {
      String extraText = bundle.getString(Intent.EXTRA_TEXT);
      if (extraText == null)
        extraText = context.getResources().getString(R.string.sample_text);
      readable = createReadable(
          bundle.getInt(Constants.EXTRA_TYPE, -1),
          extraText,
          bundle.getString(Constants.EXTRA_PATH),
          PreferenceManager.getDefaultSharedPreferences(context)
                           .getBoolean(Constants.Preferences.STORAGE,
                                       true));
      readable.setPosition(
          Math.max(bundle.getInt(Constants.EXTRA_POSITION), 0));
      readable.setHeader(bundle.getString(Constants.EXTRA_HEADER));
    }
    return readable;
  }

  public static Readable createReadable(Integer intentType, String intentText,
                                        String intentPath,
                                        Boolean cacheEnabled) {
    Readable readable;
    switch (intentType) {
      case TYPE_RAW:
        readable = new RawReadable(intentText,
                                   false); //currently it's hold only for test
        break;
      case TYPE_CLIPBOARD:
        readable = new ClipboardReadable();
        break;
      case TYPE_FILE:
        readable = FileStorable.createFileStorable(intentPath);
        break;
      case TYPE_TXT:
        readable = new TxtFileStorable(intentPath);
        break;
      case TYPE_EPUB:
        readable = new EpubFileStorable(intentPath);
        break;
      case TYPE_FB2:
        readable = new FB2FileStorable(intentPath);
        break;
      default:
        String link;
        if (!TextUtils.isEmpty(intentText) &&
            intentText.length() < Constants.NON_LINK_LENGTH &&
            !TextUtils.isEmpty(
                link = TextParser.findLink(TextParser.compilePattern(),
                                           intentText))) {
          readable = new NetStorable(link);
        } else {
          readable = new RawReadable(intentText, cacheEnabled); //neutral value
        }
    }
    return readable;
  }

  abstract public void process(Context context);

  abstract public void readData();

  abstract public Readable getNext();

  public int calcProgress(int pos, long apc) {
    return (int) (100f / wordList.size() * (pos + 1) + .5f);
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public Long getSeconds() {
    return seconds;
  }

  public Boolean isProcessFailed() {
    return processFailed;
  }

  public String getText() {
    return text.toString();
  }

  public void setText(String text) {
    this.text = new StringBuilder(text);
  }

  public StringBuilder getTextBuilder() {
    return text;
  }

  public String getHeader() {
    return header;
  }

  public void setHeader(String header) {
    this.header = header;
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

  public boolean isProcessed() {
    return processed;
  }

  public DataBundle getRowData() {
    return rowData;
  }

  public void insertLastWord(String lastWord) {
    text = new StringBuilder(lastWord).append(text);
  }
}
