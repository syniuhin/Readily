package com.infmme.readilyapp.readable.fb2;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import com.daimajia.androidanimations.library.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.infmme.readilyapp.provider.cachedbook.CachedBookColumns;
import com.infmme.readilyapp.provider.cachedbook.CachedBookContentValues;
import com.infmme.readilyapp.provider.cachedbook.CachedBookCursor;
import com.infmme.readilyapp.provider.cachedbook.CachedBookSelection;
import com.infmme.readilyapp.provider.fb2book.Fb2BookColumns;
import com.infmme.readilyapp.provider.fb2book.Fb2BookContentValues;
import com.infmme.readilyapp.provider.fb2book.Fb2BookCursor;
import com.infmme.readilyapp.provider.fb2book.Fb2BookSelection;
import com.infmme.readilyapp.readable.Readable;
import com.infmme.readilyapp.readable.interfaces.*;
import com.infmme.readilyapp.reader.Reader;
import com.infmme.readilyapp.util.ColorMatcher;
import com.infmme.readilyapp.util.Constants;
import com.infmme.readilyapp.xmlparser.FB2Tags;
import com.infmme.readilyapp.xmlparser.XMLEvent;
import com.infmme.readilyapp.xmlparser.XMLEventType;
import com.infmme.readilyapp.xmlparser.XMLParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import static com.infmme.readilyapp.readable.Utils.guessCharset;

/**
 * Created with love, by infm dated on 6/14/16.
 * <p>
 * Class to handle .fb2
 */
// TODO: Refactor parsing flow.
public class FB2Storable implements Storable, Chunked, Unprocessed,
    Structured {

  private static final int BUFFER_SIZE = 4096;

  private String mPath;
  private long mFileSize;

  /**
   * Creation time in order to keep track of db records.
   * Has joda LocalDateTime format.
   */
  private String mTimeOpened;

  private String mTitle = null;
  private Double mPercentile = .0;

  private String mCoverImageHref = null;
  private String mCoverImageEncoded = null;
  private String mCoverImageUri = null;
  private Integer mCoverImageMean = null;

  private XMLParser mParser;
  private XMLEvent mCurrentEvent;
  private XMLEventType mCurrentEventType;

  private Deque<ChunkInfo> mLoadedChunks = new ArrayDeque<>();
  private List<? extends AbstractTocReference> mTableOfContents = null;

  private transient String mCurrentPartId;
  private long mCurrentBytePosition = -1;
  private transient long mLastBytePosition = -1;

  private int mCurrentTextPosition = -1;
  private double mChunkPercentile = .0;

  private boolean mProcessed = false;
  private boolean mFullyProcessed = false;

  // Again, can be leaking.
  private transient Context mContext = null;

  public FB2Storable(Context context) {
    mContext = context;
  }

  public FB2Storable(Context context, String timeCreated) {
    this.mTimeOpened = timeCreated;
    this.mContext = context;
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    if (mParser != null) {
      // Closes inner input stream.
      mParser.close();
    }
  }

  @Override
  public Reading readNext() throws IOException {
    // Checks if we're reading for the first time.
    if (mParser.getPosition() == 0 && mCurrentBytePosition > 0) {
      mParser.skip(mCurrentBytePosition);
    }
    mCurrentBytePosition = mLastBytePosition;

    StringBuilder text = new StringBuilder();
    if (mCurrentEvent == null) {
      mCurrentEvent = mParser.next();
      mCurrentEventType = mCurrentEvent.getType();
      mLastBytePosition = mParser.getPosition();
    }

    // Stack needed to keep track of nested sections entered.
    Stack<String> sectionIdStack = new Stack<>();

    // Reads file section by section until reaches end of the file or text
    // grows bigger, than buffer size.
    while (mCurrentEventType != XMLEventType.DOCUMENT_CLOSE &&
        text.length() < BUFFER_SIZE) {
      if (mCurrentEvent.enteringTag(FB2Tags.SECTION)) {
        if (BuildConfig.DEBUG) {
          Log.d(getClass().getName(),
                "Entering section on " + String.valueOf(mParser.getPosition()));
        }
        mCurrentPartId = "section" + mParser.getPosition();
        sectionIdStack.add(mCurrentPartId);
      }
      if (mCurrentEvent.exitingTag(FB2Tags.SECTION)) {
        if (BuildConfig.DEBUG) {
          Log.d(getClass().getName(),
                String.format("Exiting section %s on %d", mCurrentPartId,
                              mParser.getPosition()));
        }
        // Checks if we're in a nested section.
        if (!sectionIdStack.isEmpty()) {
          sectionIdStack.pop();
        }
        // If we should use parent section as a current one.
        if (!sectionIdStack.isEmpty()) {
          mCurrentPartId = sectionIdStack.peek();
        }
      }

      if (mCurrentEventType == XMLEventType.CONTENT) {
        String contentType = mCurrentEvent.getContentType();
        String content = mCurrentEvent.getContent();
        if (contentType != null && !TextUtils.isEmpty(contentType)) {
          // Appends plain text to a text.
          // TODO: Check out other possible tags.
          if (contentType.equals(FB2Tags.PLAIN_TEXT)) {
            text.append(content).append(" ");
          }
        }
      }

      mLastBytePosition = mParser.getPosition();

      mCurrentEvent = mParser.next();
      mCurrentEventType = mCurrentEvent.getType();
    }

    Readable readable = new Readable();
    readable.setText(text.toString());
    readable.setPosition(0);

    mLoadedChunks.addLast(
        new ChunkInfo(mCurrentPartId, mCurrentBytePosition));

    return readable;
  }

  @Override
  public boolean hasNextReading() {
    return mCurrentEventType != XMLEventType.DOCUMENT_CLOSE;
  }

  @Override
  public void skipLast() {
    mLoadedChunks.removeLast();
  }

  // TODO: Transfer this method to model class.
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
                                CachedBookColumns.ALL_COLUMNS_FB2_JOINED,
                                where.sel(), where.args(), null);
      if (c == null) {
        throw new RuntimeException("Unexpected cursor fail.");
      } else if (c.moveToFirst()) {
        CachedBookCursor book = new CachedBookCursor(c);
        mTitle = book.getTitle();
        mTimeOpened = book.getTimeOpened();
        mPercentile = book.getPercentile();
        mCurrentPartId = book.getFb2BookCurrentPartId();
        mCurrentTextPosition = book.getFb2BookTextPosition();
        mCurrentBytePosition = book.getFb2BookBytePosition();
        mFullyProcessed = book.getFb2BookFullyProcessed();
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
  public void prepareForStoring(Reader reader) {
    if (mLoadedChunks != null && !mLoadedChunks.isEmpty()) {
      mCurrentBytePosition = mLoadedChunks.getFirst().mBytePosition;
      mChunkPercentile = reader.getPercentile();
      setCurrentPosition(reader.getPosition());
    }
  }

  /**
   * Synchronously decodes image from Base64 and stores as bitmap compressed
   * to PNG in a cache dir.
   *
   * @throws IOException
   */
  private void storeCoverImage() throws IOException {
    byte[] decodedBytes = Base64.decode(mCoverImageEncoded, 0);
    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0,
                                                  decodedBytes.length);

    String path = getCoverImagePath();
    File file = new File(path);
    file.createNewFile();
    FileOutputStream ostream = new FileOutputStream(file);
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
    ostream.close();

    mCoverImageMean = ColorMatcher.findClosestMaterialColor(bitmap);
  }

  private String getCoverImagePath() {
    return mContext.getCacheDir() + "/" + String.valueOf(
        mParser.hashCode()) + Constants.DEFAULT_COVER_PAGE_EXTENSION;
  }

  @Override
  public void storeToDb() {
    checkSectionByteIntegrity();
    double percent = calcPercentile();

    CachedBookContentValues values = new CachedBookContentValues();
    boolean updateValues = false;
    Fb2BookContentValues fb2Values = new Fb2BookContentValues();

    if (isStoredInDb()) {
      CachedBookSelection cachedWhere = new CachedBookSelection();
      cachedWhere.path(mPath);

      if (mTitle != null) {
        values.putTitle(mTitle);
        updateValues = true;
      }
      if (percent >= 0 && percent <= 1) {
        values.putPercentile(calcPercentile());
        updateValues = true;
      }
      if (mCoverImageUri != null) {
        values.putCoverImageUri(mCoverImageUri);
        values.putCoverImageMean(mCoverImageMean);
        updateValues = true;
      }
      if (updateValues) {
        values.update(mContext, cachedWhere);
      }

      fb2Values.putFullyProcessed(mFullyProcessed);
      if (mCurrentBytePosition >= 0) {
        fb2Values.putBytePosition((int) mCurrentBytePosition);
      }
      if (mCurrentTextPosition >= 0) {
        fb2Values.putTextPosition(mCurrentTextPosition);
      }
      if (mCurrentPartId != null) {
        fb2Values.putCurrentPartId(mCurrentPartId);
      }

      Fb2BookSelection fb2Where = new Fb2BookSelection();
      fb2Where.id(getFkFb2BookId());
      fb2Values.update(mContext, fb2Where);
    } else {
      fb2Values.putBytePosition((int) mCurrentBytePosition);
      fb2Values.putTextPosition(mCurrentTextPosition);
      fb2Values.putCurrentPartId(mCurrentPartId);
      fb2Values.putFullyProcessed(mFullyProcessed);

      if (percent >= 0 && percent <= 1) {
        values.putPercentile(calcPercentile());
      } else {
        values.putPercentile(0);
      }
      values.putTimeOpened(mTimeOpened);
      values.putPath(mPath);
      if (mTitle == null) {
        mTitle = getDefaultTitle();
      }
      values.putTitle(mTitle);
      values.putCoverImageUri(mCoverImageUri);
      values.putCoverImageMean(mCoverImageMean);

      Uri uri = fb2Values.insert(mContext.getContentResolver());
      long fb2Id = Long.parseLong(uri.getLastPathSegment());
      values.putFb2BookId(fb2Id);
      values.insert(mContext.getContentResolver());
    }
  }

  private String getDefaultTitle() {
    return mPath.substring(mPath.lastIndexOf('/') + 1, mPath.lastIndexOf('.'));
  }

  /**
   * Attempts to fix integrity issues between byte offset and section id.
   */
  private void checkSectionByteIntegrity() {
    if (mTableOfContents == null && isTocCached(mContext)) {
      try {
        mTableOfContents = readSavedTableOfContents();
      } catch (IOException e) {
        e.printStackTrace();
        return;
      }
    }
    if (mTableOfContents != null) {
      for (int i = 0; i < mTableOfContents.size(); i++) {
        FB2Part part = (FB2Part) mTableOfContents.get(i);
        if (part.getId().equals(mCurrentPartId)) {
          if (mCurrentBytePosition < part.getStreamByteStartLocation() ||
              mCurrentBytePosition > part.getStreamByteEndLocation()) {
            // Integrity is violated.
            mCurrentBytePosition = part.getStreamByteStartLocation();
          }
        }
      }
    }
  }

  @Override
  public void storeToFile() {
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

    FileInputStream inputStream = new FileInputStream(file);

    mParser = new XMLParser();
    mParser.setInput(inputStream, encoding);
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
    return mTitle;
  }

  @Override
  public void setTitle(String title) {
    mTitle = title;
  }

  @Override
  public void onReaderNext() {
    mLoadedChunks.removeFirst();
  }

  @Override
  public List<? extends AbstractTocReference> getTableOfContents() {
    if (mTableOfContents == null && mProcessed) {
      try {
        if (isTocCached(mContext)) {
          mTableOfContents = readSavedTableOfContents();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return mTableOfContents;
  }

  @Override
  public String getCurrentId() {
    return mCurrentPartId;
  }

  @Override
  public void setCurrentTocReference(AbstractTocReference tocReference) {
    FB2Part fb2Part = (FB2Part) tocReference;
    mCurrentPartId = fb2Part.getId();
    mCurrentBytePosition = fb2Part.getStreamByteStartLocation();
  }

  @Override
  public int getCurrentPosition() {
    return mCurrentTextPosition;
  }

  @Override
  public void setCurrentPosition(int position) {
    mCurrentTextPosition = position;
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
      if (isStoredInDb()) {
        readFromDb();
      } else {
        mCurrentTextPosition = 0;
      }

      mProcessed = true;
    } catch (IOException e) {
      e.printStackTrace();
      mProcessed = false;
    }
  }

  /**
   * Checks in db if the book specified by path fully processed.
   * Synchronous.
   *
   * @param context Context instance to work with db.
   */
  public static boolean isFullyProcessed(final Context context, String mPath) {
    CachedBookSelection where = new CachedBookSelection();
    where.path(mPath);
    Cursor c = context.getContentResolver()
                      .query(CachedBookColumns.CONTENT_URI,
                             new String[] { CachedBookColumns.FB2_BOOK_ID },
                             where.sel(), where.args(), null);
    boolean result = false;
    if (c != null) {
      CachedBookCursor book = new CachedBookCursor(c);
      if (book.moveToFirst()) {
        Long fb2BookId = book.getFb2BookId();

        Fb2BookSelection fb2Where = new Fb2BookSelection();
        fb2Where.id(fb2BookId);
        Fb2BookCursor fb2BookCursor = new Fb2BookCursor(
            context.getContentResolver()
                   .query(CachedBookColumns.CONTENT_URI,
                          new String[] { Fb2BookColumns.FULLY_PROCESSED },
                          where.sel(), where.args(), null));
        if (fb2BookCursor.moveToFirst()) {
          result = fb2BookCursor.getFullyProcessed();
        }
        fb2BookCursor.close();
      }
      book.close();
    }
    return result;
  }

  public boolean isFullyProcessed() {
    return mFullyProcessed;
  }

  /**
   * Generates and stores table of contents to a cache, reads cover image.
   *
   * @throws IOException
   */
  public void processFully() throws IOException {
    ArrayList<FB2Part> toc = new ArrayList<>();

    Stack<FB2Part> stack = new Stack<>();
    FB2Part currentPart = null;
    // Gets initial data for an algorithm.
    XMLEvent event = mParser.next();
    XMLEventType eventType = event.getType();
    boolean insideTitle = false;
    boolean insideBookTitle = false;
    boolean insideCoverPage = false;
    boolean insideCoverPageBinary = false;

    while (eventType != XMLEventType.DOCUMENT_CLOSE) {
      if (event.enteringTag(FB2Tags.SECTION)) {
        // Checks if we're not in the section
        if (currentPart == null) {
          currentPart = new FB2Part(mParser.getPosition(), mPath);
        } else {
          FB2Part childPart = new FB2Part(mParser.getPosition(), mPath);
          currentPart.addChild(childPart);
          stack.add(currentPart);
          currentPart = childPart;
        }
      }
      if (event.exitingTag(FB2Tags.SECTION)) {
        if (currentPart == null) {
          throw new IllegalStateException("Can't exit non-existing part");
        }
        // This is guaranteed to be unique
        currentPart.setId("section" + String.valueOf(
            currentPart.getStreamByteStartLocation()));
        currentPart.setStreamByteEndLocation(mParser.getPosition());
        if (stack.isEmpty()) {
          toc.add(currentPart);
          currentPart = null;
        } else {
          currentPart = stack.pop();
        }
      }

      if (event.enteringTag(FB2Tags.TITLE)) {
        insideTitle = true;
      }
      if (event.exitingTag(FB2Tags.TITLE)) {
        insideTitle = false;
      }

      if (event.enteringTag(FB2Tags.BOOK_TITLE)) {
        insideBookTitle = true;
      }
      if (event.exitingTag(FB2Tags.BOOK_TITLE)) {
        insideBookTitle = false;
      }

      if (event.enteringTag(FB2Tags.COVER_PAGE)) {
        insideCoverPage = true;
      }
      if (event.exitingTag(FB2Tags.COVER_PAGE)) {
        insideCoverPage = false;
      }

      if (event.enteringTag(FB2Tags.BINARY) &&
          event.checkHref(mCoverImageHref)) {
        insideCoverPageBinary = true;
      }
      if (insideCoverPageBinary && event.exitingTag(FB2Tags.BINARY)) {
        insideCoverPageBinary = false;
      }

      // Checks if we're inside tag contents.
      if (eventType == XMLEventType.CONTENT) {
        String contentType = event.getContentType();
        String content = event.getContent();
        if (insideTitle && currentPart != null) {
          // Appends title to an existent one.
          currentPart.setTitle(currentPart.getTitle() + " " + content);
        } else if (contentType.equals(FB2Tags.BOOK_TITLE)) {
          if (insideBookTitle && mTitle == null) {
            mTitle = content;
          }
        } else if (insideCoverPageBinary &&
            contentType.equals(FB2Tags.BINARY)) {
          mCoverImageEncoded = content;
        }
      } else if (insideCoverPage && event.isImageTag()) {
        HashMap<String, String> attrs = event.getTagAttributes();
        if (attrs != null) {
          for (HashMap.Entry<String, String> kv : attrs.entrySet()) {
            if (kv.getKey().contains("href")) {
              mCoverImageHref = kv.getValue();
              break;
            }
          }
        }
      }

      event = mParser.next();
      eventType = event.getType();
    }

    if (mCoverImageEncoded != null) {
      storeCoverImage();
      mCoverImageUri = getCoverImagePath();
    }

    mTableOfContents = toc;
    storeTableOfContents();
    mFullyProcessed = true;
  }

  public boolean isTocCached(final Context c) {
    return getCachedTocFile(c).exists();
  }

  public void storeTableOfContents()
      throws IOException {
    FileOutputStream fos = new FileOutputStream(getCachedTocFile(mContext));
    Gson gson = new Gson();
    String json = gson.toJson(mTableOfContents);
    fos.write(json.getBytes());
    fos.close();
  }

  public ArrayList<FB2Part> readSavedTableOfContents() throws IOException {
    FileInputStream fis = new FileInputStream(getCachedTocFile(mContext));

    byte[] buffer = new byte[BUFFER_SIZE];
    StringBuilder input = new StringBuilder();
    long bytesRead;
    do {
      bytesRead = fis.read(buffer);
      if (bytesRead != BUFFER_SIZE) {
        byte[] buffer0 = new byte[(int) bytesRead];
        System.arraycopy(buffer, 0, buffer0, 0, (int) bytesRead);
        input.append(new String(buffer0));
      } else {
        input.append(new String(buffer));
      }
    } while (bytesRead == BUFFER_SIZE);

    String json = input.toString();
    Gson gson = new Gson();
    Type listType = new TypeToken<ArrayList<FB2Part>>() {}.getType();

    // Path is needed in order to get preview and, basically, perform any
    // operation with fb2 itself.
    ArrayList<FB2Part> toc = gson.fromJson(json, listType);
    for (FB2Part part : toc) {
      part.setPath(mPath);
    }
    return toc;
  }

  private File getCachedTocFile(Context c) {
    return new File(c.getCacheDir(),
                    mPath.substring(mPath.lastIndexOf('/')) + "_TOC.json");
  }

  /**
   * Uses uniqueness of a path to get fb2_book_id from a cached_book table.
   *
   * @return fb2_book_id for an mPath.
   */
  private Long getFkFb2BookId() {
    Long id = null;

    CachedBookSelection cachedWhere = new CachedBookSelection();
    cachedWhere.path(mPath);
    CachedBookCursor cachedBookCursor =
        new CachedBookCursor(mContext.getContentResolver().query(
            CachedBookColumns.CONTENT_URI,
            new String[] { CachedBookColumns.FB2_BOOK_ID },
            cachedWhere.sel(), cachedWhere.args(), null));
    if (cachedBookCursor.moveToFirst()) {
      id = cachedBookCursor.getFb2BookId();
    }
    cachedBookCursor.close();
    return id;
  }

  private double calcPercentile() {
    long nextBytePosition;
    if (mLoadedChunks.isEmpty()) {
      nextBytePosition = mFileSize;
    } else {
      nextBytePosition = mLoadedChunks.getFirst().mBytePosition;
    }
    return (double) mCurrentBytePosition / mFileSize + mChunkPercentile *
        (nextBytePosition - mCurrentBytePosition) / mFileSize;
  }

  private class ChunkInfo {
    String mSectionId;
    long mBytePosition;

    public ChunkInfo(String sectionTitle, long bytePosition) {
      this.mSectionId = sectionTitle;
      this.mBytePosition = bytePosition;
    }
  }
}
