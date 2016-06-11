package com.infmme.readilyapp.reader;

/**
 * Created with love, by infm dated on 6/10/16.
 */

/**
 * Class to preserve lock of task
 */
public class MonitorObject {

  private boolean mPaused;

  public synchronized boolean isPaused() {
    return mPaused;
  }

  public synchronized void pauseTask() throws InterruptedException {
    if (!isPaused()) {
      mPaused = true;
      wait();
    }
  }

  public synchronized void resumeTask() throws InterruptedException {
    if (isPaused()) {
      mPaused = false;
      notify();
    }
  }
}

