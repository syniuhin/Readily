package com.infmme.readilyapp.readable.storable.epub;

import android.content.Context;
import android.database.Cursor;
import com.infmme.readilyapp.provider.cachedbook.CachedBookColumns;
import com.infmme.readilyapp.provider.cachedbook.CachedBookContentValues;
import com.infmme.readilyapp.provider.cachedbook.CachedBookCursor;
import com.infmme.readilyapp.provider.cachedbook.CachedBookSelection;
import com.infmme.readilyapp.provider.epubbook.EpubBookColumns;
import com.infmme.readilyapp.provider.epubbook.EpubBookContentValues;
import com.infmme.readilyapp.provider.epubbook.EpubBookCursor;
import com.infmme.readilyapp.provider.epubbook.EpubBookSelection;
import com.infmme.readilyapp.readable.Readable;
import com.infmme.readilyapp.readable.interfaces.Chunked;
import com.infmme.readilyapp.readable.interfaces.Reading;
import com.infmme.readilyapp.readable.interfaces.Storable;
import com.infmme.readilyapp.readable.interfaces.Unprocessed;
import com.infmme.readilyapp.util.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

import java.io.IOException;
import java.util.List;

/**
 * Created with love, by infm dated on 6/8/16.
 */

public class EpubStorable implements Storable, Chunked, Unprocessed {
  public static final int BUFFER_SIZE = 1024;

  private String mPath;

  /**
   * Creation time in order to keep track of db records.
   * Has joda LocalDateTime format.
   */
  private String mTimeCreated;

  private transient Book mBook;
  private Metadata mMetadata;
  private List<Resource> mContents;

  private transient String mCurrentResourceId;
  private int mCurrentResourceIndex = -1;
  private int mCurrentTextPosition;

  private boolean mProcessed;

  // Again, can be leaking.
  private transient Context mContext = null;

  public EpubStorable(Context context, String timeCreated) {
    mContext = context;
    mTimeCreated = timeCreated;
  }

  @Override
  public Reading readNext() throws IOException {
    Readable readable = new Readable();
    if (mCurrentResourceIndex == -1) {
      for (int i = 0; i < mContents.size() &&
          mCurrentResourceIndex == -1; ++i) {
        if (mCurrentResourceId.equals(mContents.get(i).getId())) {
          mCurrentResourceIndex = i;
        }
      }
    }
    long bytesProcessed = 0;
    while (bytesProcessed < BUFFER_SIZE) {
      Resource currentResource = mContents.get(mCurrentResourceIndex);
      mCurrentResourceId = currentResource.getId();

      readable.appendText(new String(currentResource.getData()));
      bytesProcessed += currentResource.getSize();
      mCurrentResourceIndex++;
    }
    readable.finishAppendingText();
    return readable;
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
                                EpubBookColumns.ALL_COLUMNS,
                                where.sel(), where.args(), null);
      if (c != null) {
        EpubBookCursor book = new EpubBookCursor(c);
        mCurrentTextPosition = book.getTextPosition();
        mCurrentResourceId = book.getCurrentResourceId();
      } else {
        throw new RuntimeException("Unexpected cursor fail.");
      }
    } else {
      throw new IllegalStateException("Not stored in a db yet!");
    }
  }

  @Override
  public void storeToDb() {
    CachedBookContentValues values = new CachedBookContentValues();
    values.putPath(mPath);
    values.putTimeOpened(mTimeCreated);
    values.putTitle(mMetadata.getFirstTitle());

    EpubBookContentValues epubValues = new EpubBookContentValues();
    epubValues.putCurrentResourceId(mCurrentResourceId);
    epubValues.putTextPosition(mCurrentTextPosition);

    if (isStoredInDb()) {
      CachedBookSelection cachedWhere = new CachedBookSelection();
      cachedWhere.path(mPath);
      EpubBookSelection epubWhere = new EpubBookSelection();
      epubWhere.id(getEpubBookId());
      epubValues.update(mContext, epubWhere);
      values.update(mContext, cachedWhere);
    } else {
      epubValues.insert(mContext.getContentResolver());
      values.putEpubBookId(getEpubBookId());
      values.insert(mContext.getContentResolver());
    }
  }

  /**
   * Uses uniqueness of a path to get epub_book_id from a cached_book table.
   * @return epub_book_id for an mPath.
   */
  private Long getEpubBookId() {
    CachedBookSelection cachedWhere = new CachedBookSelection();
    cachedWhere.path(mPath);
    CachedBookCursor cachedBookCursor =
        new CachedBookCursor(mContext.getContentResolver().query(
            CachedBookColumns.CONTENT_URI,
            new String[] { EpubBookColumns._ID },
            cachedWhere.sel(), cachedWhere.args(), null));
    return cachedBookCursor.getEpubBookId();
  }

  @Override
  public void storeToFile() {
    throw new IllegalStateException(
        "You can't store EpubStorable to a filesystem.");
  }

  @Override
  public Storable readFromFile() throws IOException {
    if (mPath != null) {
      mBook = (new EpubReader()).readEpubLazy(
          mPath, Constants.DEFAULT_ENCODING);
    }
    return this;
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
  public boolean isProcessed() {
    return mProcessed;
  }

  @Override
  public void setProcessed(boolean processed) {
    mProcessed = processed;
  }

  public Context getContext() {
    return mContext;
  }

  public void setContext(Context mContext) {
    this.mContext = mContext;
  }

  @Override
  public void process() {
    try {
      if (mBook == null) {
        readFromFile();
      }
      if (isStoredInDb()) {
        readFromDb();
      } else {
        mMetadata = mBook.getMetadata();
        mCurrentTextPosition = 0;
        mCurrentResourceId = mContents.get(0).getId();
      }
      mContents = mBook.getContents();
      mProcessed = true;
    } catch (IOException e) {
      e.printStackTrace();
      mProcessed = false;
    }
  }
}
