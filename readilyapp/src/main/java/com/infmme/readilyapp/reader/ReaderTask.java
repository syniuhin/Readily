package com.infmme.readilyapp.reader;

import android.text.TextUtils;
import com.infmme.readilyapp.essential.TextParser;
import com.infmme.readilyapp.readable.interfaces.Chunked;
import com.infmme.readilyapp.readable.interfaces.Reading;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;

/**
 * Created with love, by infm dated on 6/10/16.
 */

public class ReaderTask implements Runnable {
  private static final int DEQUE_SIZE_LIMIT = 3;
  private final ArrayDeque<TextParser> mParserDeque = new ArrayDeque<>();

  private final MonitorObject mMutex;

  private Chunked mChunked;

  private ReaderTaskCallbacks mCallback;

  private boolean mOnceStarted = false;

  public ReaderTask(MonitorObject object, Chunked chunked,
                    ReaderTaskCallbacks callback) {
    this.mMutex = object;
    this.mChunked = chunked;
    this.mCallback = callback;
  }

  @Override
  public void run() {
    while (mCallback.shouldContinue()) {
      try {
        synchronized (mParserDeque) {
          // TODO: Ensure that reading is processed now.
          if (!mOnceStarted) {
            mParserDeque.add(nextParser());
          }
          while (mParserDeque.size() < DEQUE_SIZE_LIMIT &&
              mParserDeque.size() > 0 &&
              !TextUtils.isEmpty(
                  mParserDeque.getLast().getReading().getText())) {
            mParserDeque.addLast(nextParser());
          }
        }
        if (!mOnceStarted) {
          mCallback.startReader(removeDequeHead());
          mOnceStarted = true;
        }
        synchronized (mMutex) {
          mMutex.pauseTask();
        }
      } catch (InterruptedException | IOException e) {
        e.printStackTrace();
      }
    }
  }

  public TextParser removeDequeHead() {
    synchronized (mParserDeque) {
      return mParserDeque.pollFirst();
    }
  }

  public synchronized boolean hasNextParser() {
    return mParserDeque.size() > 1;
  }

  public TextParser nextParser() throws IOException {
    Reading nextReading = mChunked.readNext();
    TextParser result =
        TextParser.newInstance(nextReading, mCallback.getDelayCoefficients());
    result.process();
/*
    if (isFileStorable) //looks strangely, may be better I think
      ((FileStorable) currentReadable).copyListPrefix(result.getReadable());
*/
    return result;
  }

  public interface ReaderTaskCallbacks {
    Reader startReader(TextParser parser);

    List<Integer> getDelayCoefficients();

    boolean shouldContinue();
  }
}