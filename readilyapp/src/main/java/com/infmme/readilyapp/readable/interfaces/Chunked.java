package com.infmme.readilyapp.readable.interfaces;

import java.io.IOException;

/**
 * Created with love, by infm dated on 6/8/16.
 */

public interface Chunked {
  Reading readNext() throws IOException;

  boolean hasNextReading();

  /**
   * Called when Reader object starts showing next Reading.
   */
  void onReaderNext();

  void skipLast();
}
