package com.infmme.readilyapp.readable;

import com.infmme.readilyapp.readable.interfaces.Reading;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with love, by infm dated on 6/8/16.
 */

public class Readable implements Reading, Serializable {
  protected String mText;

  protected int mPosition;

  private List<String> mWordList = null;
  private List<Integer> mEmphasisList = null;
  private List<Integer> mDelayList = null;

  @Override
  public String getText() {
    return mText;
  }

  @Override
  public void setText(String text) {
    mText = text;
  }

  @Override
  public List<String> getWordList() {
    return mWordList;
  }

  @Override
  public void setWordList(List<String> wordList) {
    mWordList = new ArrayList<>(wordList);
  }

  @Override
  public List<Integer> getEmphasisList() {
    return mEmphasisList;
  }

  @Override
  public void setEmphasisList(List<Integer> emphasisList) {
    mEmphasisList = new ArrayList<>(emphasisList);
  }

  @Override
  public List<Integer> getDelayList() {
    return mDelayList;
  }

  @Override
  public void setDelayList(List<Integer> delayList) {
    mDelayList = new ArrayList<>(delayList);
  }

  @Override
  public int getPosition() {
    return mPosition;
  }

  @Override
  public void setPosition(int position) {
    mPosition = position;
  }
}
