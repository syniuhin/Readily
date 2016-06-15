package com.infmme.readilyapp.reader;

import android.os.Handler;
import android.support.annotation.NonNull;
import com.infmme.readilyapp.R;
import com.infmme.readilyapp.readable.interfaces.Reading;

import java.io.IOException;
import java.util.List;

import static com.infmme.readilyapp.reader.ReaderFragment.READER_PULSE_DURATION;

/**
 * Encapsulates logic for handling current Reading chunk, loading it into view
 * and asking for a next part.
 *
 * Created with love, by infm dated on 6/10/16.
 */
public class Reader implements Runnable {

  /**
   * Amount of words to share between consecutive Reading objects.
   */
  static final int LAST_WORD_PREFIX_SIZE = 10;

  /**
   * Handler to communicate with UI thread.
   */
  private Handler mReaderHandler;
  private int mPaused;
  private int mPosition;
  private boolean mCompleted;
  private int mApproxCharCount; // ???

  private List<String> mWordList;
  private List<Integer> mEmphasisList;
  private List<Integer> mDelayList;

  private final MonitorObject mTaskMonitor;

  private ReaderCallbacks mCallback;

  public Reader(Handler readerHandler, MonitorObject monitorObject,
                ReaderCallbacks callback) {
    this.mReaderHandler = readerHandler;

    this.mTaskMonitor = monitorObject;
    this.mCallback = callback;

    mPaused = 1;
    mApproxCharCount = 0;
  }

  /**
   * Main method of this class, represents one iteration of Reader flow, which
   * basically shows current word, next words and checks if we have next reading
   * if we need to.
   */
  @Override
  public void run() {
    // Checks if we should show current reading.
    if (isCurrentReading()) {
      // Block mTaskMonitor to have a condition below satisfied safely.
      synchronized (mTaskMonitor) {
        if (mWordList.size() - mPosition < LAST_WORD_PREFIX_SIZE &&
            mTaskMonitor.isPaused()) {
          try {
            mTaskMonitor.resumeTask();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
      mCompleted = false;
      if (!isPaused()) {
        // mApproxCharCount += mWordList.get(mPosition).length() + 1;
        // progress = reading.calcProgress(mPosition, mApproxCharCount);
        updateReaderView();
        mReaderHandler.postDelayed(this, calcDelay());
        mPosition++;
      }
    // Checks if we have next reading loaded, so we shouldn't stop out flow.
    } else if (mCallback.isNextLoaded()) {
      try {
        // Set next Reading to this object.
        changeReading(mCallback.nextReading());
        mPosition = 0;
        mReaderHandler.postDelayed(this, calcDelay());
      } catch (IOException e) {
        e.printStackTrace();
      }
    // Final check is about end of our reading.
    } else {
      mCallback.showNotification(R.string.reading_is_completed);
      mCompleted = true;
      mPaused = 1;
    }
  }

  public double getPercentile() {
    if (mWordList.size() != 0)
      return (double) mPosition / mWordList.size();
    return 0;
  }

  public int getPosition() {
    return mPosition;
  }

  public void setPosition(int position) {
    this.mPosition = position;
  }

  public void moveToPreviousPosition() {
    setPosition(mPosition - 1);
    updateReaderView(mPosition - 1);
    mCallback.showInfo(this);
  }

  public void moveToNextPosition() {
    setPosition(mPosition + 1);
    updateReaderView(mPosition + 1);
    mCallback.showInfo(this);
  }

  public boolean isCompleted() {
    return mCompleted;
  }

  public void setCompleted(boolean completed) {
    this.mCompleted = completed;
  }

  public boolean isPaused() {
    return mPaused % 2 == 1;
  }

  public int getApproxCharCount() {
    return mApproxCharCount;
  }

  /**
   * Toggles cancelled state, convenient to use as an onClick() callback.
   */
  public void toggleCancelled() {
    if (!isPaused()) {
      performPause();
    } else {
      performPlay();
    }
  }

  /**
   * Pauses a reader and does supporting actions.
   */
  public void performPause() {
    if (!isPaused()) {
      mPaused++;
      mCallback.animatePause();
      mCallback.showNotification(R.string.pause);
      mCallback.showInfo(this);
    }
  }

  /**
   * Plays a reader and does supporting actions.
   */
  public void performPlay() {
    if (isPaused()) {
      mPaused++;
      mCallback.animatePlay();
      mCallback.hideNotification(true);
      mCallback.hideInfo();
      mReaderHandler.postDelayed(this, READER_PULSE_DURATION + 100);
    }
  }

  /**
   * Substitutes current reading contents with another's ones.
   * @param another Reading instance to load neccessary components from.
   */
  public void changeReading(@NonNull final Reading another) {
    mWordList = another.getWordList();
    mEmphasisList = another.getEmphasisList();
    mDelayList = another.getDelayList();
  }

  /**
   * Calculates delay of a reader in a current position.
   */
  private int calcDelay() {
    return (mDelayList.isEmpty())
        ? 10 * Math.round(100 * 60 * 1f / mCallback.getWordsPerMinute())
        : mDelayList.get(mPosition) * Math.round(
        100 * 60 * 1f / mCallback.getWordsPerMinute());
  }

  public void updateReaderView() {
    updateReaderView(mPosition);
  }

  /**
   * Wrapper function to call appropriate callback's (fragment's) method.
   * @param position Position to fetch parameters (word, next words,
   *                 emphasis position) for a callback's method.
   */
  private void updateReaderView(int position) {
    if (mWordList != null && mEmphasisList != null && mDelayList != null &&
        position < mWordList.size() && position >= 0) {
      List<String> nextWords =
          mWordList.subList(position, Math.min(position + LAST_WORD_PREFIX_SIZE,
                                               mWordList.size()));
      mCallback.updateReaderView(mWordList.get(position), nextWords,
                                 mEmphasisList.get(position));
    }
  }

  /**
   * Checks if we can stay in terms of current reading loaded.
   * @return Condition to be satisfied in the beginning of run().
   */
  private boolean isCurrentReading() {
    final int wordListSize = mWordList.size();
    // Either we're in the end of an entire reading or have to take prefix
    // margin into account.
    return (mPosition < wordListSize && !mCallback.isNextLoaded()) ||
        (mPosition < wordListSize - LAST_WORD_PREFIX_SIZE &&
            mCallback.isNextLoaded());
  }

  /**
   * Needed to decouple fragment (or another android component) and this class's
   * code. Therefore, most likely to be implemented by ReaderFragment.
   */
  interface ReaderCallbacks {
    void animatePlay();

    void animatePause();

    void updateReaderView(String word, List<String> nextWords, int emphasis);

    void showNotification(int stringResId);

    boolean hideNotification(boolean force);

    void showInfo(Reader reader);

    void hideInfo();

    /**
     * Fetches this setting from SharedPreferences.
     * @return WPM to use in calcDelay().
     */
    Integer getWordsPerMinute();

    /**
     * Communicates with a producer thread (ReaderTask).
     * @return If we have next reading to load to here.
     */
    boolean isNextLoaded();

    /**
     * Requests next reading from ReaderTask deque.
     *
     * @return Reading  to load into reader window
     * @throws IOException from Chunked.nextReading()
     */
    Reading nextReading() throws IOException;
  }
}
