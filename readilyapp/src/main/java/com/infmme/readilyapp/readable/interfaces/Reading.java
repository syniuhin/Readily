package com.infmme.readilyapp.readable.interfaces;

import java.util.List;

/**
 * Created with love, by infm dated on 6/8/16.
 */

public interface Reading {
  /**
   * @return currently stored text of this reading.
   */
  String getText();

  void setText(String text);

  /**
   * @return word list to display in Reader.
   */
  List<String> getWordList();

  void setWordList(List<String> wordList);

  /**
   * @return list of emphasis positions for each word.
   */
  List<Integer> getEmphasisList();

  void setEmphasisList(List<Integer> emphasisList);

  /**
   * @return list of delays for each word.
   */
  List<Integer> getDelayList();

  void setDelayList(List<Integer> setDelayList);
}
