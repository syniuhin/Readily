package com.infmme.readilyapp.readable.epub;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.infmme.readilyapp.provider.cachedbook.CachedBookColumns;
import com.infmme.readilyapp.provider.cachedbook.CachedBookContentValues;
import com.infmme.readilyapp.provider.cachedbook.CachedBookCursor;
import com.infmme.readilyapp.provider.cachedbook.CachedBookSelection;
import com.infmme.readilyapp.provider.cachedbookinfo
    .CachedBookInfoContentValues;
import com.infmme.readilyapp.provider.cachedbookinfo.CachedBookInfoSelection;
import com.infmme.readilyapp.provider.epubbook.EpubBookContentValues;
import com.infmme.readilyapp.provider.epubbook.EpubBookSelection;
import com.infmme.readilyapp.readable.Readable;
import com.infmme.readilyapp.readable.interfaces.*;
import com.infmme.readilyapp.reader.Reader;
import com.infmme.readilyapp.util.ColorMatcher;
import com.infmme.readilyapp.util.Constants;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import static com.infmme.readilyapp.provider.cachedbook.CachedBookCursor
    .getFkEpubBookId;
import static com.infmme.readilyapp.provider.cachedbook.CachedBookCursor
    .getFkInfoId;
import static com.infmme.readilyapp.readable.epub.EpubPart.parseRawText;

/**
 * Created with love, by infm dated on 6/8/16.
 */

public class EpubStorable implements ChunkedUnprocessedStorable, Structured {
  private String mPath;
  private long mFileSize;

  /**
   * Creation time in order to keep track of db records.
   * Has joda LocalDateTime format.
   */
  private String mTimeOpened;

  private Book mBook;
  private Metadata mMetadata;

  // Stored title.
  private String mTitle;
  private Double mPercentile = .0;

  private String mCoverImageUri;
  private Integer mCoverImageMean;
  private transient List<Resource> mContents;

  private Deque<ChunkInfo> mLoadedChunks = new ArrayDeque<>();
  private List<? extends AbstractTocReference> mTableOfContents;

  private String mCurrentResourceId;
  private String mCurrentResourceTitle;
  private int mCurrentResourceIndex;
  private int mLastResourceIndex;

  private int mCurrentTextPosition;
  private double mChunkPercentile = .0;

  private boolean mProcessed;

  // Again, can be leaking.
  private transient Context mContext;

  public EpubStorable(Context context) {
    mContext = context;
  }

  public EpubStorable(Context context, String timeCreated) {
    mContext = context;
    mTimeOpened = timeCreated;
  }

  @Override
  public Reading readNext() throws IOException {
    mCurrentResourceIndex = mLastResourceIndex;

    Readable readable = new Readable();
    Resource r = mContents.get(mLastResourceIndex);
    String parsed = parseRawText(new String(r.getData()));
    readable.setText(parsed);

    mLoadedChunks.addLast(new ChunkInfo(r.getId(), r.getTitle()));
    Log.d(EpubStorable.class.getName(),
          String.format("Added id: %s title: %s to loaded chunks.", r.getId(),
                        r.getTitle()));
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
                                CachedBookColumns.ALL_COLUMNS_EPUB_JOINED,
                                where.sel(), where.args(), null);
      if (c == null) {
        throw new RuntimeException("Unexpected cursor fail.");
      } else if (c.moveToFirst()) {
        CachedBookCursor book = new CachedBookCursor(c);
        mTitle = book.getTitle();
        mCurrentTextPosition = book.getTextPosition();
        mTimeOpened = book.getTimeOpened();
        mPercentile = book.getPercentile();
        mCurrentResourceId = book.getEpubBookCurrentResourceId();
        mCurrentResourceTitle = book.getCachedBookInfoCurrentPartTitle();
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
      ChunkInfo chunk = mLoadedChunks.getFirst();
      mCurrentResourceId = chunk.mResourceId;
      mCurrentResourceTitle = chunk.mResourceTitle;
      mChunkPercentile = reader.getPercentile();
      setCurrentPosition(reader.getPosition());
      Log.d(EpubStorable.class.getName(), String.format(
          "Preparing for storing sync id: %s title: %s position: %d",
          mCurrentResourceId, mCurrentResourceTitle, reader.getPosition()));
    }
    return this;
  }

  @Override
  public void beforeStoringToDb() {
    mContents = mBook.getContents();
    if (coverImageExists() && !isCoverImageStored()) {
      try {
        storeCoverImage();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  // TODO: Test this carefully.
  @Override
  public void storeToDb() {
    CachedBookContentValues values = new CachedBookContentValues();
    values.putTextPosition(mCurrentTextPosition);
    double percent = calcPercentile();

    EpubBookContentValues epubValues = new EpubBookContentValues();
    epubValues.putCurrentResourceId(mCurrentResourceId);

    CachedBookInfoContentValues infoValues = new CachedBookInfoContentValues();
    infoValues.putCurrentPartTitle(mCurrentResourceTitle);

    if (isStoredInDb()) {
      CachedBookSelection cachedWhere = new CachedBookSelection();
      cachedWhere.path(mPath);
      if (percent >= 0 && percent <= 1) {
        values.putPercentile(calcPercentile());
      }
      values.update(mContext, cachedWhere);
      EpubBookSelection epubWhere = new EpubBookSelection();
      epubWhere.id(getFkEpubBookId(mContext, mPath));
      epubValues.update(mContext, epubWhere);

      CachedBookInfoSelection infoWhere = new CachedBookInfoSelection();
      infoWhere.id(getFkInfoId(mContext, mPath));
      infoValues.update(mContext, infoWhere);
    } else {
      if (percent >= 0 && percent <= 1) {
        values.putPercentile(calcPercentile());
      } else {
        values.putPercentile(0);
      }
      values.putTimeOpened(mTimeOpened);
      values.putPath(mPath);
      values.putTitle(mTitle);
      values.putCoverImageUri(mCoverImageUri);
      values.putCoverImageMean(mCoverImageMean);

      StringBuilder stringBuilder = new StringBuilder();
      List<Author> authors = mMetadata.getAuthors();
      if (authors != null) {
        for (Author a : authors) {
          stringBuilder.append(a.getFirstname()).append(" ")
                       .append(a.getLastname()).append(" ");
        }
        if (stringBuilder.length() > 0) {
          infoValues.putAuthor(
              stringBuilder.substring(0, stringBuilder.length() - 1));
        } else {
          infoValues.putAuthor(stringBuilder.toString());
        }
      }

      stringBuilder = new StringBuilder();
      List<String> subjects = mMetadata.getSubjects();
      if (subjects != null) {
        for (String s : subjects) {
          stringBuilder.append(s).append(" ");
        }
        if (stringBuilder.length() > 0) {
          infoValues.putGenre(
              stringBuilder.substring(0, stringBuilder.length() - 1));
        } else {
          infoValues.putGenre(stringBuilder.toString());
        }
      }

      stringBuilder = new StringBuilder();
      List<String> descriptions = mMetadata.getDescriptions();
      if (descriptions != null) {
        for (String s : descriptions) {
          stringBuilder.append(s).append("\n");
        }
        if (stringBuilder.length() > 0) {
          infoValues.putDescription(
              stringBuilder.substring(0, stringBuilder.length() - 1));
        } else {
          infoValues.putDescription(stringBuilder.toString());
        }
      }

      infoValues.putLanguage(mMetadata.getLanguage());

      Uri infoUri = infoValues.insert(mContext.getContentResolver());
      long infoId = Long.parseLong(infoUri.getLastPathSegment());
      values.putInfoId(infoId);

      Uri epubUri = epubValues.insert(mContext.getContentResolver());
      long epubId = Long.parseLong(epubUri.getLastPathSegment());
      values.putEpubBookId(epubId);

      values.insert(mContext.getContentResolver());
    }
  }

  /**
   * May be very heavy, need to think if it's needed at all.
   *
   * @return Percent progress of reading this book.
   */
  private double calcPercentile() {
    if (mCurrentResourceIndex != -1) {
      List<Resource> passedResources =
          mContents.subList(0, mCurrentResourceIndex);
      long bytesPassed = 0;
      for (Resource r : passedResources) {
        bytesPassed += r.getSize();
      }
      long nextResBytes = bytesPassed +
          mContents.get(mCurrentResourceIndex).getSize();
      return (double) bytesPassed / mFileSize + mChunkPercentile *
          (nextResBytes - bytesPassed) / mFileSize;
    }
    return -1;
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
      mFileSize = new File(mPath).length();
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
  public String getTitle() {
    if (mMetadata != null) {
      return mMetadata.getFirstTitle();
    }
    return null;
  }

  @Override
  public void setTitle(String title) {
    mMetadata.getTitles().add(0, title);
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
        if (mCurrentResourceId != null) {
          mLastResourceIndex = -1;
          for (int i = 0; i < mContents.size() &&
              mLastResourceIndex == -1; ++i) {
            if (mCurrentResourceId.equals(mContents.get(i).getId())) {
              mLastResourceIndex = i;
            }
          }
        }
        mCurrentResourceIndex = mLastResourceIndex;
      } else {
        mCurrentTextPosition = 0;
        mCurrentResourceId = mContents.get(0).getId();
        mCurrentResourceTitle = mContents.get(0).getTitle();
        mTitle = mMetadata.getFirstTitle();
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
  public String getCurrentPartId() {
    return mCurrentResourceId;
  }

  @Override
  public String getCurrentPartTitle() {
    return mCurrentResourceTitle;
  }

  @Override
  public void setCurrentTocReference(AbstractTocReference tocReference) {
    EpubPart epubPart = (EpubPart) tocReference;
    mCurrentResourceId = epubPart.getId();
    mCurrentResourceTitle = epubPart.getTitle();
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
    mCoverImageMean = ColorMatcher.pickRandomMaterialColor();
    Log.d(EpubStorable.class.getName(), String.format(
        "Stored cover image: %s", mCoverImageUri));
  }

  private String getCoverImagePath(final Resource coverImage) {
    return mContext.getCacheDir() + mPath.substring(
        mPath.lastIndexOf('/')) + coverImage.getHref();
  }

  private class ChunkInfo implements Serializable {
    String mResourceId;
    String mResourceTitle;

    public ChunkInfo(String resourceId, String resourceTitle) {
      this.mResourceId = resourceId;
      this.mResourceTitle = resourceTitle;
    }
  }
}
