package com.infmme.readilyapp.readable.txt;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.infmme.readilyapp.provider.cachedbook.CachedBookColumns;
import com.infmme.readilyapp.provider.cachedbook.CachedBookContentValues;
import com.infmme.readilyapp.provider.cachedbook.CachedBookCursor;
import com.infmme.readilyapp.provider.cachedbook.CachedBookSelection;
import com.infmme.readilyapp.provider.txtbook.TxtBookContentValues;
import com.infmme.readilyapp.provider.txtbook.TxtBookSelection;
import com.infmme.readilyapp.readable.Readable;
import com.infmme.readilyapp.readable.interfaces.ChunkedUnprocessedStorable;
import com.infmme.readilyapp.readable.interfaces.Reading;
import com.infmme.readilyapp.readable.interfaces.Storable;
import com.infmme.readilyapp.reader.Reader;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;

import static com.infmme.readilyapp.readable.Utils.guessCharset;

/**
 * Created with love, by infm dated on 6/15/16.
 */

public class TxtStorable implements ChunkedUnprocessedStorable {

  private static final int BUFFER_SIZE = 4096;
  private static final int CHAR_BUFFER_SIZE = BUFFER_SIZE / 2;

  private String mPath;

  private InputStreamReader mInputStreamReader;
  private long mFileSize;

  /**
   * Creation time in order to keep track of db records.
   * Has joda LocalDateTime format.
   */
  private String mTimeOpened;

  private String mTitle;
  private double mPercentile = .0;
  private double mChunkPercentile = .0;

  private Deque<TxtStorable.ChunkInfo> mLoadedChunks = new ArrayDeque<>();

  private long mCurrentBytePosition = 0;
  private transient long mLastBytePosition = 0;

  private int mCurrentTextPosition;

  private boolean mProcessed;

  // Again, can be leaking.
  private transient Context mContext = null;

  public TxtStorable(Context context) {
    mContext = context;
  }

  public TxtStorable(Context context, String timeCreated) {
    mContext = context;
    mTimeOpened = timeCreated;
  }

  @Override
  public Reading readNext() throws IOException {
    mCurrentBytePosition = mLastBytePosition;

    char[] data = new char[CHAR_BUFFER_SIZE];
    mLastBytePosition += mInputStreamReader.read(data);
    String text = new String(data);
    Readable readable = new Readable();
    readable.setText(text);
    readable.setPosition(0);

    mLoadedChunks.addLast(
        new TxtStorable.ChunkInfo(mCurrentBytePosition));

    return readable;
  }

  @Override
  public boolean hasNextReading() {
    return mCurrentBytePosition == 0 ||
        mCurrentBytePosition < mFileSize - BUFFER_SIZE;
  }

  @Override
  public void skipLast() {
    mLoadedChunks.removeLast();
  }

  @Override
  public boolean isStoredInDb() {
    CachedBookSelection where = new CachedBookSelection();
    where.path(mPath);
    Cursor c = mContext.getContentResolver()
                       .query(CachedBookColumns.CONTENT_URI,
                              new String[] { CachedBookColumns._ID },
                              where.sel(), where.args(), null);
    boolean result = true;
    if (c != null) {
      CachedBookCursor book = new CachedBookCursor(c);
      if (book.getCount() < 1) {
        result = false;
      }
      book.close();
    } else {
      result = false;
    }
    return result;
  }

  @Override
  public void readFromDb() {
    if (isStoredInDb()) {
      CachedBookSelection where = new CachedBookSelection();
      where.path(mPath);
      Cursor c = mContext.getContentResolver()
                         .query(CachedBookColumns.CONTENT_URI,
                                CachedBookColumns.ALL_COLUMNS_TXT_JOINED,
                                where.sel(), where.args(), null);
      if (c == null) {
        throw new RuntimeException("Unexpected cursor fail.");
      } else if (c.moveToFirst()) {
        CachedBookCursor book = new CachedBookCursor(c);
        mTitle = book.getTitle();
        mCurrentTextPosition = book.getTextPosition();
        mTimeOpened = book.getTimeOpened();
        mPercentile = book.getPercentile();
        mCurrentBytePosition = book.getTxtBookBytePosition();
        mLastBytePosition = mCurrentBytePosition;
        book.close();
      } else {
        c.close();
      }
    } else {
      throw new IllegalStateException("Not stored in a db yet!");
    }
  }

  @Override
  public Storable prepareForStoringSync(Reader reader) {
    if (mLoadedChunks != null && !mLoadedChunks.isEmpty()) {
      mCurrentBytePosition = mLoadedChunks.getFirst().mBytePosition;
      mCurrentTextPosition = reader.getPosition();
      mChunkPercentile = reader.getPercentile();
    }
    return this;
  }

  @Override
  public void beforeStoringToDb() { }

  @Override
  public void storeToDb() {
    CachedBookContentValues values = new CachedBookContentValues();
    values.putTextPosition(mCurrentTextPosition);
    // TODO: Solve this
    values.putPercentile(calcPercentile());

    TxtBookContentValues txtValues = new TxtBookContentValues();
    txtValues.putBytePosition((int) mCurrentBytePosition);

    if (isStoredInDb()) {
      CachedBookSelection cachedWhere = new CachedBookSelection();
      cachedWhere.path(mPath);
      TxtBookSelection txtWhere = new TxtBookSelection();
      txtWhere.id(getFkTxtBookId());
      txtValues.update(mContext, txtWhere);
      values.update(mContext, cachedWhere);
    } else {
      values.putTimeOpened(mTimeOpened);
      values.putPath(mPath);
      values.putTitle(mTitle);

      Uri uri = txtValues.insert(mContext.getContentResolver());
      long txtId = Long.parseLong(uri.getLastPathSegment());
      values.putTxtBookId(txtId);
      values.insert(mContext.getContentResolver());
    }
  }

  @Override
  public void storeToFile() throws IOException {
    throw new IllegalStateException(
        "You can't store FB2Storable to a filesystem.");
  }

  @Override
  public Storable readFromFile() throws IOException {
    File file = new File(mPath);
    mFileSize = file.length();
    FileInputStream encodingHelper = new FileInputStream(file);
    String encoding = guessCharset(encodingHelper);
    encodingHelper.close();

    FileInputStream fileInputStream = new FileInputStream(file);
    try {
      mInputStreamReader = new InputStreamReader(fileInputStream, encoding);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return this;
  }

  @Override
  public void setContext(Context context) {
    mContext = context;
  }

  @Override
  public String getPath() {
    return mPath;
  }

  @Override
  public void setPath(String path) {
    mPath = path;
  }

  @Override
  public String getTitle() {
    return mTitle;
  }

  @Override
  public void setTitle(String title) {
    mTitle = title;
  }

  @Override
  public int getCurrentPosition() {
    return mCurrentTextPosition;
  }

  @Override
  public void setCurrentPosition(int position) {
    mCurrentTextPosition = position;
  }

  public int getPosition() {
    return mCurrentTextPosition;
  }

  @Override
  public void onReaderNext() {
    mLoadedChunks.removeFirst();
  }

  @Override
  public boolean isProcessed() {
    return mProcessed;
  }

  @Override
  public void setProcessed(boolean processed) {
    mProcessed = processed;
  }

  @Override
  public void process() {
    try {
      readFromFile();
      if (mInputStreamReader == null) {
        mProcessed = false;
        return;
      }
      if (isStoredInDb()) {
        readFromDb();
        mInputStreamReader.skip(mCurrentBytePosition);
      } else {
        mTitle = mPath.substring(mPath.lastIndexOf('/'),
                                 mPath.lastIndexOf('.'));
        mCurrentTextPosition = 0;
      }

      mProcessed = true;
    } catch (IOException e) {
      e.printStackTrace();
      mProcessed = false;
    }
  }

  /**
   * Uses uniqueness of a path to get txt_book_id from a cached_book table.
   *
   * @return txt_book_id for an mPath.
   */
  private Long getFkTxtBookId() {
    Long id = null;

    CachedBookSelection cachedWhere = new CachedBookSelection();
    cachedWhere.path(mPath);
    CachedBookCursor cachedBookCursor =
        new CachedBookCursor(mContext.getContentResolver().query(
            CachedBookColumns.CONTENT_URI,
            new String[] { CachedBookColumns.TXT_BOOK_ID },
            cachedWhere.sel(), cachedWhere.args(), null));
    if (cachedBookCursor.moveToFirst()) {
      id = cachedBookCursor.getTxtBookId();
    }
    cachedBookCursor.close();
    return id;
  }

  private double calcPercentile() {
    return (mCurrentBytePosition + mChunkPercentile *
        mLoadedChunks.getFirst().mBytePosition -
        mCurrentBytePosition) / mFileSize;
  }

  private class ChunkInfo implements Serializable {
    long mBytePosition;

    public ChunkInfo(long bytePosition) {
      this.mBytePosition = bytePosition;
    }
  }
}
