package com.infmme.readilyapp.readable.interfaces;

/**
 * Created with love, by infm dated on 6/8/16.
 */

public interface Unprocessed {
  boolean isProcessed();

  void setProcessed(boolean processed);

  /**
   * Blocking!
   */
  void process();
}
