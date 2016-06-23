package com.infmme.readilyapp.reader;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.infmme.readilyapp.readable.interfaces.Chunked;
import com.infmme.readilyapp.readable.interfaces.Reading;

import java.io.IOException;
import java.util.List;

/**
 * Producer Runnable to load Chunked reading from the filesystem and feed it
 * into displaying logic.
 * <p>
 * Created with love, by infm dated on 6/10/16.
 */
public class ReaderProducerTask implements Runnable {
  private Reading mCurrentReading;

  private Chunked mChunked = null;
  private Reading mSingleReading = null;

  private ReaderTaskCallbacks mCallback;

  private TextParser mTextParser;

  /**
   * Constructs ReaderTask for chunked reading source.
   *
   * @param chunked  Chunked instance to load Readings.
   * @param callback Callback to communicate with UI thread and another
   *                 parts of an app.
   */
  public ReaderProducerTask(Chunked chunked, ReaderTaskCallbacks callback) {
    this.mChunked = chunked;
    this.mCallback = callback;
  }

  public ReaderProducerTask(Reading singleReading,
                            ReaderTaskCallbacks callback) {
    this.mSingleReading = singleReading;
    this.mCallback = callback;
  }

  /**
   * Fetches next non-empty Reading to produce.
   * Blocking.
   */
  private Reading nextNonEmptyReading() throws IOException {
    Reading nextReading = null;
    // Checks if we have only one reading to process.
    if (mChunked == null) {
      Log.d(ReaderProducerTask.class.getName(), "mChunked is null");
      nextReading = mSingleReading;
      mSingleReading = null;
    } else if (mChunked.hasNextReading()) {
      Log.d(ReaderProducerTask.class.getName(), "mChunked has next reading");
      // Finds non-empty consecutive reading.
      do {
        // Therefore we're here not for the first time.
        if (nextReading != null) {
          Log.d(ReaderProducerTask.class.getName(),
                "mChunked skipping last reading");
          mChunked.skipLast();
        }
        nextReading = mChunked.readNext();
      } while (TextUtils.isEmpty(
          nextReading.getText()) && mChunked.hasNextReading());
    }
    if (nextReading != null) {
      if (mTextParser == null) {
        mTextParser = TextParser.newInstance(
            nextReading, mCallback.getDelayCoefficients());
      } else {
        mTextParser.clearWith(nextReading);
      }
      mTextParser.process();
      nextReading = mTextParser.getReading();
    }
    return nextReading;
  }

  @Override
  public void run() {
    while (mCallback.shouldContinue()) {
      try {
        // Sort of initialization for a deque.
        Reading nextReading;
        if (!mCallback.hasStarted()) {
          nextReading = nextNonEmptyReading();
          if (nextReading != null && !TextUtils.isEmpty(
              nextReading.getText())) {
            Log.d(ReaderProducerTask.class.getName(),
                  "Producing reading for the first time");
            mCallback.produce(nextReading);
            mCurrentReading = nextReading;
          } else {
            Log.d(ReaderProducerTask.class.getName(),
                  "First reading is invalid");
          }
        }
        // Checks if we have more data to produce.
        if (mChunked != null) {
          while (mCurrentReading != null &&
              !TextUtils.isEmpty(mCurrentReading.getText())) {
            nextReading = nextNonEmptyReading();
            if (nextReading != null && !TextUtils.isEmpty(
                nextReading.getText())) {
              // Duplicates adjacent reading data to transit smoothly between
              // them.
              duplicateAdjacentData(mCurrentReading, nextReading);
              Log.d(ReaderProducerTask.class.getName(), "Producing a reading.");
              mCallback.produce(nextReading);
            } else {
              mCallback.produce(null);
            }
            mCurrentReading = nextReading;
          }
        }
      } catch (InterruptedException | IOException e) {
        e.printStackTrace();
      }
    }
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
    List<Integer> getDelayCoefficients();

    boolean shouldContinue();

    boolean hasStarted();

    void produce(@Nullable Reading reading) throws InterruptedException;
  }
}