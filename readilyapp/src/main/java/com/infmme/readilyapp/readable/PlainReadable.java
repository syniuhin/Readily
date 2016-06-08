package com.infmme.readilyapp.readable;

import com.infmme.readilyapp.readable.interfaces.AbstractReadable;
import com.infmme.readilyapp.readable.interfaces.Reading;

import java.io.InputStream;
import java.util.List;

/**
 * Created with love, by infm dated on 6/8/16.
 */

public class PlainReadable implements AbstractReadable, Reading {
  private String mText;
  private String mPath;

  private List<String> mWordList = null;
  private List<Integer> mEmphasisList = null;
  private List<Integer> mDelayList = null;

  @Override
  public AbstractReadable setText(String text) {
    this.mText = text;
    return this;
  }

  @Override
  public AbstractReadable setInputStream(InputStream is) {
    throw new IllegalStateException(
        "PlainReadable doesn't support reading from InputStream.");
  }

  @Override
  public AbstractReadable setPath(String path) {
    this.mPath = path;
    return this;
  }

  @Override
  public String getText() {
    return mText;
  }

  @Override
  public List<String> getWordList() {
    return mWordList;
  }

  @Override
  public void setWordList(List<String> wordList) {
    mWordList = wordList;
  }

  @Override
  public List<Integer> getEmphasisList() {
    return mEmphasisList;
  }

  @Override
  public List<Integer> getDelayList() {
    return mDelayList;
  }
}
