package com.infmme.readilyapp.readable.epub;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.infmme.readilyapp.provider.cachedbook.CachedBookColumns;
import com.infmme.readilyapp.provider.cachedbook.CachedBookContentValues;
import com.infmme.readilyapp.provider.cachedbook.CachedBookCursor;
import com.infmme.readilyapp.provider.cachedbook.CachedBookSelection;
import com.infmme.readilyapp.provider.epubbook.EpubBookColumns;
import com.infmme.readilyapp.provider.epubbook.EpubBookContentValues;
import com.infmme.readilyapp.provider.epubbook.EpubBookCursor;
import com.infmme.readilyapp.provider.epubbook.EpubBookSelection;
import com.infmme.readilyapp.readable.Readable;
import com.infmme.readilyapp.readable.interfaces.*;
import com.infmme.readilyapp.reader.Reader;
import com.infmme.readilyapp.util.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Created with love, by infm dated on 6/8/16.
 */

public class EpubStorable implements Storable, Chunked, Unprocessed,
    Structured {
  private String mPath;

  /**
   * Creation time in order to keep track of db records.
   * Has joda LocalDateTime format.
   */
  private String mTimeCreated;

  private transient Book mBook;
  private Metadata mMetadata;
  private String mCoverImageUri;
  private List<Resource> mContents;

  private Deque<ChunkInfo> mLoadedChunks = new ArrayDeque<>();
  private List<? extends AbstractTocReference> mTableOfContents = null;

  private transient String mCurrentResourceId;
  private int mCurrentResourceIndex = -1;
  private transient int mLastResourceIndex = -1;

  private int mCurrentTextPosition;

  private boolean mProcessed;

  // Again, can be leaking.
  private transient Context mContext = null;

  public EpubStorable(Context context) {
    mContext = context;
  }

  public EpubStorable(Context context, String timeCreated) {
    mContext = context;
    mTimeCreated = timeCreated;
  }

  @Override
  public Reading readNext() throws IOException {
    Readable readable = new Readable();
    if (mLastResourceIndex == -1) {
      for (int i = 0; i < mContents.size() &&
          mLastResourceIndex == -1; ++i) {
        if (mCurrentResourceId.equals(mContents.get(i).getId())) {
          mLastResourceIndex = i;
        }
      }
    }
    mCurrentResourceIndex = mLastResourceIndex;

    String parsed = parseRawText(
        new String(mContents.get(mLastResourceIndex).getData()));
    readable.setText(parsed);

    mLoadedChunks.addLast(new ChunkInfo(mCurrentResourceIndex));
    mLastResourceIndex++;
    return readable;
  }

  @Override
  public boolean hasNextReading() {
    return mContents != null && mLastResourceIndex < mContents.size();
  }

  @Override
  public void skipLast() {
    mLoadedChunks.removeLast();
  }

  private String parseRawText(String rawText) {
    Document doc = Jsoup.parse(rawText);
    return doc.select("p").text();
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
      if (c != null && c.moveToFirst()) {
        if (c.moveToFirst()) {
          EpubBookCursor book = new EpubBookCursor(c);
          mCurrentTextPosition = book.getTextPosition();
          mCurrentResourceId = book.getCurrentResourceId();
          book.close();
        } else {
          c.close();
        }
      } else {
        throw new RuntimeException("Unexpected cursor fail.");
      }
    } else {
      throw new IllegalStateException("Not stored in a db yet!");
    }
  }

  @Override
  public void prepareForStoring(Reader reader) {
    if (mLoadedChunks != null && !mLoadedChunks.isEmpty()) {
      mCurrentResourceIndex = mLoadedChunks.getFirst().mResourceIndex;
      mCurrentResourceId = mContents.get(mCurrentResourceIndex).getId();
      setCurrentPosition(reader.getPosition());
    }

    if (coverImageExists() && !isCoverImageStored()) {
      try {
        storeCoverImage();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void storeToDb() {
    CachedBookContentValues values = new CachedBookContentValues();
    // TODO: Solve this
    values.putPercentile(0);

    EpubBookContentValues epubValues = new EpubBookContentValues();
    epubValues.putCurrentResourceId(mCurrentResourceId);
    epubValues.putTextPosition(mCurrentTextPosition);

    if (isStoredInDb()) {
      CachedBookSelection cachedWhere = new CachedBookSelection();
      cachedWhere.path(mPath);
      EpubBookSelection epubWhere = new EpubBookSelection();
      epubWhere.id(getFkEpubBookId());
      epubValues.update(mContext, epubWhere);
      values.update(mContext, cachedWhere);
    } else {
      values.putTimeOpened(mTimeCreated);
      values.putPath(mPath);
      values.putTitle(mMetadata.getFirstTitle());
      values.putCoverImageUri(mCoverImageUri);

      Uri uri = epubValues.insert(mContext.getContentResolver());
      long epubId = Long.parseLong(uri.getLastPathSegment());
      values.putEpubBookId(epubId);
      values.insert(mContext.getContentResolver());
    }
  }

  /**
   * Uses uniqueness of a path to get epub_book_id from a cached_book table.
   *
   * @return epub_book_id for an mPath.
   */
  private Long getFkEpubBookId() {
    Long id = null;

    CachedBookSelection cachedWhere = new CachedBookSelection();
    cachedWhere.path(mPath);
    CachedBookCursor cachedBookCursor =
        new CachedBookCursor(mContext.getContentResolver().query(
            CachedBookColumns.CONTENT_URI,
            new String[] { CachedBookColumns.EPUB_BOOK_ID },
            cachedWhere.sel(), cachedWhere.args(), null));
    if (cachedBookCursor.moveToFirst()) {
      id = cachedBookCursor.getEpubBookId();
    }
    cachedBookCursor.close();
    return id;
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
    } else {
      throw new IllegalStateException("No path to read file from.");
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
      mContents = mBook.getContents();
      mMetadata = mBook.getMetadata();
      if (isStoredInDb()) {
        readFromDb();
      } else {
        mCurrentTextPosition = 0;
        mCurrentResourceId = mContents.get(0).getId();
      }
      mProcessed = true;
    } catch (IOException e) {
      e.printStackTrace();
      mProcessed = false;
    }
  }

  @Override
  public List<? extends AbstractTocReference> getTableOfContents() {
    if (mTableOfContents == null && mProcessed) {
      mTableOfContents = EpubPart.adaptList(
          mBook.getTableOfContents().getTocReferences());
    }
    return mTableOfContents;
  }

  @Override
  public String getCurrentId() {
    return mCurrentResourceId;
  }

  @Override
  public void setCurrentId(String id) {
    mCurrentResourceId = id;
  }

  @Override
  public int getCurrentPosition() {
    return mCurrentTextPosition;
  }

  @Override
  public void setCurrentPosition(int position) {
    mCurrentTextPosition = position;
  }

  private boolean coverImageExists() {
    return mBook.getCoverImage() != null;
  }

  private boolean isCoverImageStored() {
    Resource coverImage = mBook.getCoverImage();
    return new File(getCoverImagePath(coverImage)).exists();
  }

  private void storeCoverImage() throws IOException {
    Resource coverImage = mBook.getCoverImage();
    byte[] imageBytes = coverImage.getData();
    String coverImagePath = getCoverImagePath(coverImage);
    FileOutputStream fos = new FileOutputStream(coverImagePath);
    fos.write(imageBytes);
    fos.close();
    mCoverImageUri = coverImagePath;
  }

  private String getCoverImagePath(final Resource coverImage) {
    return mContext.getCacheDir() + mPath.substring(
        mPath.lastIndexOf('/')) + coverImage.getHref();
  }

  private class ChunkInfo {
    public int mResourceIndex;

    public ChunkInfo(int resourceIndex) {
      this.mResourceIndex = resourceIndex;
    }
  }
}
