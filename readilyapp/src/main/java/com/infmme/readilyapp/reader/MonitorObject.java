package com.infmme.readilyapp.reader;

/**
 * Created with love, by infm dated on 6/10/16.
 */

import android.util.Log;

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
      Log.d(MonitorObject.class.getName(), "Pausing task");
      mPaused = true;
      wait();
    }
  }

  public synchronized void resumeTask() {
    if (isPaused()) {
      Log.d(MonitorObject.class.getName(), "Resuming task");
      mPaused = false;
      notify();
    }
  }
}

