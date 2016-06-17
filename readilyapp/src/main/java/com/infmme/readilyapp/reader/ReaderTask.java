package com.infmme.readilyapp.reader;

import android.text.TextUtils;
import com.infmme.readilyapp.readable.interfaces.Chunked;
import com.infmme.readilyapp.readable.interfaces.Reading;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;

/**
 * Producer Runnable to load Chunked reading from the filesystem and feed it
 * into displaying logic.
 * <p>
 * Created with love, by infm dated on 6/10/16.
 */
public class ReaderTask implements Runnable {
  private static final int DEQUE_SIZE_LIMIT = 3;
  private final ArrayDeque<Reading> mReadingDeque = new ArrayDeque<>();

  private final MonitorObject mMonitor;

  private Chunked mChunked = null;
  private Reading mSingleReading = null;

  private ReaderTaskCallbacks mCallback;

  private boolean mOnceStarted = false;

  /**
   * Constructs ReaderTask for chunked reading source.
   *
   * @param monitorObject Monitor which manages this thread.
   * @param chunked       Chunked instance to load Readings.
   * @param callback      Callback to communicate with UI thread and another
   *                      parts of an app.
   */
  public ReaderTask(MonitorObject monitorObject, Chunked chunked,
                    ReaderTaskCallbacks callback) {
    this.mMonitor = monitorObject;
    this.mChunked = chunked;
    this.mCallback = callback;
  }

  public ReaderTask(MonitorObject object, Reading singleReading,
                    ReaderTaskCallbacks callback) {
    this.mMonitor = object;
    this.mSingleReading = singleReading;
    this.mCallback = callback;
  }

  private Reading nextNonEmptyReading() throws IOException {
    Reading nextReading = null;
    if (mChunked.hasNextReading()) {
      // Finds non-empty consecutive reading.
      do {
        // Therefore we're here not for the first time.
        if (nextReading != null) {
          mChunked.skipLast();
        }
        nextReading = nextReading();
      } while (TextUtils.isEmpty(
          nextReading.getText()) && mChunked.hasNextReading());
    }
    return nextReading;
  }

  @Override
  public void run() {
    while (mCallback.shouldContinue()) {
      try {
        synchronized (mReadingDeque) {
          // TODO: Ensure that reading is processed now.
          // Sort of initialization for a deque.
          Reading nextReading = null;
          if (!mOnceStarted) {
            nextReading = nextNonEmptyReading();
            if (nextReading != null && !TextUtils.isEmpty(
                nextReading.getText())) {
              mReadingDeque.add(nextReading);
            }
          }
          // Checks if we have more data to produce.
          if (mChunked != null && mReadingDeque.size() > 0) {
            Reading currentReading = mReadingDeque.getLast();
            while (mReadingDeque.size() < DEQUE_SIZE_LIMIT &&
                currentReading != null &&
                !TextUtils.isEmpty(currentReading.getText())) {
              nextReading = nextNonEmptyReading();
              if (nextReading != null && !TextUtils.isEmpty(
                  nextReading.getText())) {
                // Duplicates adjacent reading data to transit smoothly between
                // them.
                duplicateAdjacentData(currentReading, nextReading);
                mReadingDeque.addLast(nextReading);
              }
              currentReading = nextReading;
            }
          }
        }
        // If we haven't started Reader flow yet, we have to do it now.
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

  /**
   * Polls head of a deque. Called for start of an entire flow and from Reader
   * when it changes a Reading instance.
   *
   * @return Loaded Reading from a chunk.
   */
  public Reading removeDequeHead() {
    synchronized (mReadingDeque) {
      return mReadingDeque.pollFirst();
    }
  }

  public synchronized boolean isNextLoaded() {
    return mReadingDeque.size() > 1;
  }

  private Reading nextReading() throws IOException {
    Reading nextReading;
    if (mChunked == null) {
      nextReading = mSingleReading;
      mSingleReading = null;
    } else {
      nextReading = mChunked.readNext();
    }
    TextParser result =
        TextParser.newInstance(nextReading, mCallback.getDelayCoefficients());
    result.process();
    return result.getReading();
  }

  private void duplicateAdjacentData(Reading currentReading,
                                     Reading nextReading) {
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