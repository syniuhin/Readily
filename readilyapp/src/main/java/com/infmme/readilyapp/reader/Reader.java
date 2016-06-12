package com.infmme.readilyapp.reader;

import android.os.Handler;
import android.support.annotation.NonNull;
import com.infmme.readilyapp.R;
import com.infmme.readilyapp.readable.interfaces.Reading;
import com.infmme.readilyapp.util.Constants;

import java.io.IOException;
import java.util.List;

import static com.infmme.readilyapp.reader.ReaderFragment.READER_PULSE_DURATION;

/**
 * Created with love, by infm dated on 6/10/16.
 */
public class Reader implements Runnable {

  public static final int LAST_WORD_PREFIX_SIZE = 10;
  private static final int NEXT_WORDS_LENGTH = 10;

  private Handler mReaderHandler;
  private int mPaused;
  private int mPosition;
  private boolean mCompleted;
  private int mApproxCharCount;

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

  @Override
  public void run() {
    int wordListSize = mWordList.size();
    if ((mPosition < wordListSize && !mCallback.isNextLoaded()) ||
        (mPosition < wordListSize - LAST_WORD_PREFIX_SIZE &&
            mCallback.isNextLoaded())) {
      synchronized (mTaskMonitor) {
        if (wordListSize - mPosition < Constants.WORDS_ENDING_COUNT &&
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
    } else if (mCallback.isNextLoaded()) {
      try {
        // Set next Reading to this object.
        changeReading(mCallback.nextReading());
        mPosition = 0;
        mReaderHandler.postDelayed(this, calcDelay());
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      mCallback.showNotification(R.string.reading_is_completed);
      mCompleted = true;
      mPaused = 1;
    }
  }

  public int getPosition() {
    return mPosition;
  }

  public void setPosition(int position) {
    this.mPosition = position;
  }

  public void moveToPrevious() {
    setPosition(mPosition - 1);
    updateReaderView(mPosition - 1);
    mCallback.showInfo(this);
  }

  public void moveToNext() {
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

  public void incCancelled() {
    if (!isPaused()) { performPause(); } else { performPlay(); }
  }

  public void performPause() {
    if (!isPaused()) {
      mPaused++;
      mCallback.animatePause();
      mCallback.showNotification(R.string.pause);
      mCallback.showInfo(this);
    }
  }

  public void performPlay() {
    if (isPaused()) {
      mPaused++;
      mCallback.animatePlay();
      mCallback.hideNotification(true);
      mCallback.hideInfo();
      mReaderHandler.postDelayed(this, READER_PULSE_DURATION + 100);
    }
  }

  public void changeReading(@NonNull final Reading reading) {
    mWordList = reading.getWordList();
    mEmphasisList = reading.getEmphasisList();
    mDelayList = reading.getDelayList();
  }

  private int calcDelay() {
    return (mDelayList.isEmpty())
        ? 10 * Math.round(100 * 60 * 1f / mCallback.getWordsPerMinute())
        : mDelayList.get(mPosition) * Math.round(
        100 * 60 * 1f / mCallback.getWordsPerMinute());
  }

  public void updateReaderView() {
    updateReaderView(mPosition);
  }

  private void updateReaderView(int position) {
    if (mWordList != null && mEmphasisList != null && mDelayList != null &&
        position < mWordList.size() && position >= 0) {
      List<String> nextWords =
          mWordList.subList(position, Math.min(position + NEXT_WORDS_LENGTH,
                                               mWordList.size()));
      mCallback.updateReaderView(mWordList.get(position), nextWords,
                                 mEmphasisList.get(position));
    }
  }

  /**
   * Needed to decouple fragment and this class's code. Therefore, most likely
   * to be implemented by ReaderFragment.
   */
  public interface ReaderCallbacks {
    void animatePlay();

    void animatePause();

    void updateReaderView(String word, List<String> nextWords, int emphasis);

    void showNotification(int stringResId);

    boolean hideNotification(boolean force);

    void showInfo(Reader reader);

    void hideInfo();

    Integer getWordsPerMinute();

    // TODO: Change for real hasNext()
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
