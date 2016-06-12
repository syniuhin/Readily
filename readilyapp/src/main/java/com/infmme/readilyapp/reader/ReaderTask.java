package com.infmme.readilyapp.reader;

import android.text.TextUtils;
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
  private final ArrayDeque<Reading> mReadingDeque = new ArrayDeque<>();

  private final MonitorObject mMonitor;

  private Chunked mChunked;

  private ReaderTaskCallbacks mCallback;

  private boolean mOnceStarted = false;

  public ReaderTask(MonitorObject object, Chunked chunked,
                    ReaderTaskCallbacks callback) {
    this.mMonitor = object;
    this.mChunked = chunked;
    this.mCallback = callback;
  }

  @Override
  public void run() {
    while (mCallback.shouldContinue()) {
      try {
        synchronized (mReadingDeque) {
          // TODO: Ensure that reading is processed now.
          if (!mOnceStarted) {
            mReadingDeque.add(nextReading());
          }
          Reading currentReading = mReadingDeque.getLast();
          while (mReadingDeque.size() < DEQUE_SIZE_LIMIT &&
              mReadingDeque.size() > 0/* &&
              !TextUtils.isEmpty(currentReading.getText())*/) {

            Reading nextReading = null;
            do {
              // Therefore we're here for the first time.
              if (nextReading != null) {
                mChunked.skipLast();
              }
              nextReading = nextReading();
            } while (TextUtils.isEmpty(
                nextReading.getText()) && mChunked.hasNextReading());

            copyListPrefixes(currentReading, nextReading);
            mReadingDeque.addLast(nextReading);

            currentReading = nextReading;
          }
        }
        if (!mOnceStarted) {
          mCallback.startReader(removeDequeHead());
          mOnceStarted = true;
        }
        synchronized (mMonitor) {
          mMonitor.pauseTask();
        }
      } catch (InterruptedException | IOException e) {
        e.printStackTrace();
      }
    }
  }

  public Reading removeDequeHead() {
    synchronized (mReadingDeque) {
      return mReadingDeque.pollFirst();
    }
  }

  public synchronized boolean isNextLoaded() {
    return mReadingDeque.size() > 1;
  }

  private Reading nextReading() throws IOException {
    Reading nextReading = mChunked.readNext();
    TextParser result =
        TextParser.newInstance(nextReading, mCallback.getDelayCoefficients());
    result.process();
    return result.getReading();
  }

  private void copyListPrefixes(Reading currentReading, Reading nextReading) {
    List<String> wordList = currentReading.getWordList();
    wordList.addAll(
        nextReading.getWordList()
                   .subList(0, Math.min(nextReading.getWordList().size(),
                                        Reader.LAST_WORD_PREFIX_SIZE)));
    currentReading.setWordList(wordList);

    List<Integer> emphasisList = currentReading.getEmphasisList();
    emphasisList.addAll(
        nextReading.getEmphasisList()
                   .subList(0, Math.min(nextReading.getEmphasisList().size(),
                                        Reader.LAST_WORD_PREFIX_SIZE)));
    currentReading.setEmphasisList(emphasisList);

    List<Integer> delayList = currentReading.getDelayList();
    delayList.addAll(
        nextReading.getDelayList()
                   .subList(0, Math.min(nextReading.getDelayList().size(),
                                        Reader.LAST_WORD_PREFIX_SIZE)));
    currentReading.setDelayList(delayList);
  }

  public interface ReaderTaskCallbacks {
    void startReader(Reading first);

    List<Integer> getDelayCoefficients();

    boolean shouldContinue();
  }
}