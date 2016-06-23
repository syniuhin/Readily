package com.infmme.readilyapp.readable.fb2;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.infmme.readilyapp.BuildConfig;
import com.infmme.readilyapp.provider.cachedbook.CachedBookColumns;
import com.infmme.readilyapp.provider.cachedbook.CachedBookContentValues;
import com.infmme.readilyapp.provider.cachedbook.CachedBookCursor;
import com.infmme.readilyapp.provider.cachedbook.CachedBookSelection;
import com.infmme.readilyapp.provider.cachedbookinfo
    .CachedBookInfoContentValues;
import com.infmme.readilyapp.provider.cachedbookinfo.CachedBookInfoSelection;
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
import org.joda.time.LocalDateTime;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import static com.infmme.readilyapp.provider.cachedbook.CachedBookCursor
    .getFkFb2BookId;
import static com.infmme.readilyapp.provider.cachedbook.CachedBookCursor
    .getFkInfoId;
import static com.infmme.readilyapp.readable.Utils.guessCharset;

/**
 * Created with love, by infm dated on 6/14/16.
 * <p>
 * Class to handle .fb2
 */
// TODO: Refactor parsing flow.
public class FB2Storable implements ChunkedUnprocessedStorable, Structured {

  private static final int BUFFER_SIZE = 4096;
  /**
   * Used to reduce allocations of local StringBuilder instances.
   */
  private transient StringBuilder mStrBuilder;

  private String mPath;
  private String mPathToc;
  private long mFileSize;

  /**
   * Creation time in order to keep track of db records.
   * Has joda LocalDateTime format.
   */
  private String mTimeOpened;

  private String mTitle;
  private Double mPercentile = .0;

  private String mCoverImageHref;
  private String mCoverImageEncoded;
  private String mCoverImageUri;
  private Integer mCoverImageMean;

  private transient XMLParser mParser;
  private transient XMLEvent mCurrentEvent;
  private transient XMLEventType mCurrentEventType;

  private Deque<ChunkInfo> mLoadedChunks = new ArrayDeque<>();
  private List<? extends AbstractTocReference> mTableOfContents;
  private Map<String, String> mSectionIdTitleMap;

  /**
   * Unique id to differentiate sections of a book.
   */
  private String mCurrentPartId;

  /**
   * Title of a part to display in UI.
   */
  private String mCurrentPartTitle;
  private long mCurrentBytePosition = -1;
  private long mLastBytePosition = -1;

  private int mCurrentTextPosition = -1;
  private double mChunkPercentile = .0;

  private boolean mProcessed;
  private boolean mFullyProcessed;
  private Boolean mFullyProcessingSuccess;

  /**
   * Used in interface overridden methods, so has to be retained in a state.
   */
  private transient Context mContext;

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
    mCurrentBytePosition = mLastBytePosition;

    if (mStrBuilder == null) {
      mStrBuilder = new StringBuilder();
    }

    // Checks if we're reading for the first time.
    if (mCurrentEvent == null) {
      mCurrentEvent = mParser.next();
      mCurrentEventType = mCurrentEvent.getType();
      mLastBytePosition = mParser.getPosition();
    }

    // Stack is needed to keep track of nested sections entered.
    Stack<String> sectionIdStack = new Stack<>();

    // Reads file section by section until reaches end of the file or text
    // grows bigger than buffer size.
    while (mCurrentEventType != XMLEventType.DOCUMENT_CLOSE &&
        mStrBuilder.length() < BUFFER_SIZE) {
      if (mCurrentEvent.enteringTag(FB2Tags.SECTION)) {
        mCurrentPartId = "section" + mParser.getPosition();
        sectionIdStack.add(mCurrentPartId);
        if (mSectionIdTitleMap != null &&
            mSectionIdTitleMap.containsKey(mCurrentPartId)) {
          mCurrentPartTitle = mSectionIdTitleMap.get(mCurrentPartId);
        }
        if (BuildConfig.DEBUG) {
          Log.d(getClass().getName(), String.format(
              "Entering section %s on %d", mCurrentPartTitle,
              mParser.getPosition()));
        }
      }

      if (mCurrentEvent.exitingTag(FB2Tags.SECTION)) {
        if (BuildConfig.DEBUG) {
          Log.d(getClass().getName(), String.format(
              "Exiting section %s on %d", mCurrentPartTitle,
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
            mStrBuilder.append(content).append(" ");
          }
        }
      }

      mLastBytePosition = mParser.getPosition();

      mCurrentEvent = mParser.next();
      mCurrentEventType = mCurrentEvent.getType();
    }

    Readable readable = new Readable();
    readable.setText(mStrBuilder.toString());
    mStrBuilder.setLength(0);
    readable.setPosition(0);

    mLoadedChunks.addLast(
        new ChunkInfo(mCurrentPartId, mCurrentPartTitle, mCurrentBytePosition));
    Log.d(FB2Storable.class.getName(),
          String.format("Added id: %s title: %s to loaded chunks.",
                        mCurrentPartId, mCurrentPartTitle));

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
      c.close();
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
        // Therefore we haven't set it in constructor.
        if (mTimeOpened == null) {
          mTimeOpened = book.getTimeOpened();
        }
        mPercentile = book.getPercentile();
        mCurrentTextPosition = book.getTextPosition();
        mCurrentPartId = book.getFb2BookCurrentPartId();
        mCurrentBytePosition = book.getFb2BookBytePosition();
        mLastBytePosition = mCurrentBytePosition;
        mFullyProcessed = book.getFb2BookFullyProcessed();
        mFullyProcessingSuccess = book.getFb2BookFullyProcessingSuccess();
        mCurrentPartTitle = book.getCachedBookInfoCurrentPartTitle();
        mCoverImageUri = book.getCoverImageUri();
        mCoverImageMean = book.getCoverImageMean();
        mPathToc = book.getFb2BookPathToc();
        book.close();
      } else {
        c.close();
      }
    } else {
      throw new IllegalStateException("Not stored in a db yet!");
    }
  }

  @Override
  public void prepareForStoringSync(Reader reader) {
    if (mLoadedChunks != null && !mLoadedChunks.isEmpty()) {
      ChunkInfo chunk = mLoadedChunks.getFirst();
      mCurrentBytePosition = chunk.mBytePosition;
      mCurrentPartId = chunk.mSectionId;
      mCurrentPartTitle = chunk.mSectionTitle;
      mChunkPercentile = reader.getPercentile();
      setCurrentPosition(reader.getPosition());
      Log.d(FB2Storable.class.getName(), String.format(
          "Preparing for storing sync id: %s title: %s position: %d",
          mCurrentPartId, mCurrentPartTitle, reader.getPosition()));
    }
  }

  @Override
  public void beforeStoringToDb() {
    if (mTitle == null) {
      mTitle = getDefaultTitle();
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

    mCoverImageUri = getCoverImagePath();
    File file = new File(mCoverImageUri);
    file.createNewFile();
    FileOutputStream ostream = new FileOutputStream(file);
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
    ostream.close();

    mCoverImageMean = ColorMatcher.findClosestMaterialColor(bitmap);
    Log.d(FB2Storable.class.getName(), String.format(
        "Stored cover image: %s", mCoverImageUri));
  }

  private String getCoverImagePath() {
    return mContext.getCacheDir() + "/" + String.valueOf(
        mParser.hashCode()) + Constants.DEFAULT_COVER_PAGE_EXTENSION;
  }

  @Override
  public void storeToDb() {
    checkSectionByteIntegrity();
    if (isStoredInDb()) {
      updateInDb();
    } else {
      insertToDb();
    }
  }

  @Override
  public void insertToDb() {
    CachedBookContentValues values = new CachedBookContentValues();
    Fb2BookContentValues fb2Values = new Fb2BookContentValues();
    CachedBookInfoContentValues infoValues = new CachedBookInfoContentValues();
    putVolatileValues(values, fb2Values, infoValues);

    values.putTitle(mTitle);
    values.putPath(mPath);

    // TODO: Put more information <=> Parse more information.
    Uri infoUri = infoValues.insert(mContext.getContentResolver());
    long infoId = Long.parseLong(infoUri.getLastPathSegment());
    values.putInfoId(infoId);

    fb2Values.putFullyProcessed(mFullyProcessed);
    Uri fb2Uri = fb2Values.insert(mContext.getContentResolver());
    long fb2Id = Long.parseLong(fb2Uri.getLastPathSegment());
    values.putFb2BookId(fb2Id);

    values.insert(mContext.getContentResolver());
  }

  @Override
  public void updateInDb() {
    CachedBookContentValues values = new CachedBookContentValues();
    Fb2BookContentValues fb2Values = new Fb2BookContentValues();
    CachedBookInfoContentValues infoValues = new CachedBookInfoContentValues();
    putVolatileValues(values, fb2Values, infoValues);

    CachedBookSelection cachedWhere = new CachedBookSelection();
    cachedWhere.path(mPath);
    values.update(mContext, cachedWhere);

    Fb2BookSelection fb2Where = new Fb2BookSelection();
    fb2Where.id(getFkFb2BookId(mContext, mPath));
    fb2Values.update(mContext, fb2Where);

    CachedBookInfoSelection infoWhere = new CachedBookInfoSelection();
    infoWhere.id(getFkInfoId(mContext, mPath));
    infoValues.update(mContext.getContentResolver(), infoWhere);
  }

  /**
   * Separate methods which only puts values updated during full processing.
   */
  public void storeAfterFullProcessing() {
    if (isStoredInDb()) {
      updateAfterFullProcessing();
    } else {
      insertAfterFullProcessing();
    }
  }

  private void insertAfterFullProcessing() {
    CachedBookContentValues values = new CachedBookContentValues();
    Fb2BookContentValues fb2Values = new Fb2BookContentValues();
    CachedBookInfoContentValues infoValues = new CachedBookInfoContentValues();

    values.putPath(mPath);
    if (mTimeOpened == null) {
      mTimeOpened = LocalDateTime.now().toString();
    }
    values.putTimeOpened(mTimeOpened);

    if (mTitle != null) {
      values.putTitle(mTitle);
    }

    values.putTextPosition(0);
    values.putPercentile(0);

    if (mCoverImageUri != null) {
      values.putCoverImageUri(mCoverImageUri);
      values.putCoverImageMean(mCoverImageMean);
    }

    fb2Values.putFullyProcessed(mFullyProcessed);
    fb2Values.putFullyProcessingSuccess(mFullyProcessingSuccess);
    fb2Values.putBytePosition(0);
    if (mTableOfContents != null && mTableOfContents.size() > 0) {
      fb2Values.putCurrentPartId(mTableOfContents.get(0).getId());
      infoValues.putCurrentPartTitle(mTableOfContents.get(0).getTitle());
    } else {
      fb2Values.putCurrentPartId("");
    }
    if (mPathToc != null) {
      fb2Values.putPathToc(mPathToc);
    }

    Uri fb2Uri = fb2Values.insert(mContext.getContentResolver());
    long fb2Id = Long.parseLong(fb2Uri.getLastPathSegment());
    values.putFb2BookId(fb2Id);

    if (mCurrentPartTitle != null) {
      infoValues.putCurrentPartTitle(mCurrentPartTitle);
    }
    // TODO: Put more

    Uri infoUri = infoValues.insert(mContext.getContentResolver());
    long infoId = Long.parseLong(infoUri.getLastPathSegment());
    values.putInfoId(infoId);

    values.insert(mContext.getContentResolver());
  }

  private void updateAfterFullProcessing() {
    Fb2BookContentValues fb2Values = new Fb2BookContentValues();

    if (mFullyProcessingSuccess) {
      CachedBookContentValues values = new CachedBookContentValues();
      values.putTitle(mTitle);
      values.putCoverImageUri(mCoverImageUri);
      values.putCoverImageMean(mCoverImageMean);

      CachedBookSelection cachedWhere = new CachedBookSelection();
      cachedWhere.path(mPath);
      values.update(mContext.getContentResolver(), cachedWhere);

      fb2Values.putPathToc(mPathToc);

      CachedBookInfoContentValues infoValues = new
          CachedBookInfoContentValues();
      infoValues.putCurrentPartTitle(mCurrentPartTitle);
      // TODO: Put more

      CachedBookInfoSelection infoWhere = new CachedBookInfoSelection();
      infoWhere.id(getFkInfoId(mContext, mPath));
      infoValues.update(mContext.getContentResolver(), infoWhere);
    }

    fb2Values.putFullyProcessed(mFullyProcessed);
    fb2Values.putFullyProcessingSuccess(mFullyProcessingSuccess);

    Fb2BookSelection fb2Where = new Fb2BookSelection();
    fb2Where.id(getFkFb2BookId(mContext, mPath));
    fb2Values.update(mContext, fb2Where);
  }

  private void putVolatileValues(CachedBookContentValues values,
                                 Fb2BookContentValues fb2Values,
                                 CachedBookInfoContentValues infoValues) {
    if (mTimeOpened != null) {
      values.putTimeOpened(mTimeOpened);
    }
    values.putTextPosition(mCurrentTextPosition);

    double percent = calcPercentile();
    if (percent >= 0 && percent <= 1) {
      values.putPercentile(percent);
    } else {
      values.putPercentile(mPercentile);
    }

    fb2Values.putBytePosition((int) mCurrentBytePosition);
    // We can read before storing for the first time
    fb2Values.putCurrentPartId(mCurrentPartId);
    infoValues.putCurrentPartTitle(mCurrentPartTitle);
  }

  private String getDefaultTitle() {
    return mPath.substring(mPath.lastIndexOf('/') + 1, mPath.lastIndexOf('.'));
  }

  /**
   * Attempts to fix integrity issues between byte offset and section id.
   */
  private void checkSectionByteIntegrity() {
    if (mTableOfContents == null && isTocCached()) {
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
  public void onReaderNext() {
    mLoadedChunks.removeFirst();
  }

  @Override
  public List<? extends AbstractTocReference> getTableOfContents() {
    if (mTableOfContents == null && mProcessed) {
      try {
        if (isTocCached()) {
          mTableOfContents = readSavedTableOfContents();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return mTableOfContents;
  }

  @Override
  public String getCurrentPartId() {
    return mCurrentPartId;
  }

  @Override
  public String getCurrentPartTitle() {
    return mCurrentPartTitle;
  }

  @Override
  public void setCurrentTocReference(AbstractTocReference tocReference) {
    FB2Part fb2Part = (FB2Part) tocReference;
    mCurrentPartId = fb2Part.getId();
    mCurrentPartTitle = fb2Part.getTitle();
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
        if (mCurrentBytePosition > 0) {
          mParser.skip(mCurrentBytePosition);
        }
      } else {
        mCurrentTextPosition = 0;
      }
      if (isSectionIdTitleMapCached()) {
        mSectionIdTitleMap = readSavedSectionIdTitleMap();
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
      c.close();
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
  //TODO: Refactor this mastodon.
  public void processFully() throws IOException {
    // If we enter this function and don't reach its end.
    mFullyProcessingSuccess = false;

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
        Log.d(FB2Storable.class.getName(), String.format(
            "Entered section on: %d", mParser.getPosition()));
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
        Log.d(FB2Storable.class.getName(), String.format(
            "Exited section on: %d", mParser.getPosition()));
      }

      if (event.enteringTag(FB2Tags.TITLE)) {
        Log.d(FB2Storable.class.getName(), String.format(
            "Entered title on: %d", mParser.getPosition()));
        insideTitle = true;
      }
      if (event.exitingTag(FB2Tags.TITLE)) {
        Log.d(FB2Storable.class.getName(), String.format(
            "Exited title on: %d", mParser.getPosition()));
        insideTitle = false;
      }

      if (event.enteringTag(FB2Tags.BOOK_TITLE)) {
        Log.d(FB2Storable.class.getName(), String.format(
            "Entered book-title on: %d", mParser.getPosition()));
        insideBookTitle = true;
      }
      if (event.exitingTag(FB2Tags.BOOK_TITLE)) {
        Log.d(FB2Storable.class.getName(), String.format(
            "Exited book-title on: %d", mParser.getPosition()));
        insideBookTitle = false;
      }

      if (event.enteringTag(FB2Tags.COVER_PAGE)) {
        Log.d(FB2Storable.class.getName(), String.format(
            "Entered coverpage on: %d", mParser.getPosition()));
        insideCoverPage = true;
      }
      if (event.exitingTag(FB2Tags.COVER_PAGE)) {
        Log.d(FB2Storable.class.getName(), String.format(
            "Exited coverpage on: %d", mParser.getPosition()));
        insideCoverPage = false;
      }

      if (event.enteringTag(FB2Tags.BINARY) &&
          event.checkHref(mCoverImageHref)) {
        Log.d(FB2Storable.class.getName(), String.format(
            "Entered binary on: %d", mParser.getPosition()));
        insideCoverPageBinary = true;
      }
      if (insideCoverPageBinary && event.exitingTag(FB2Tags.BINARY)) {
        Log.d(FB2Storable.class.getName(), String.format(
            "Exited coverpage on: %d", mParser.getPosition()));
        insideCoverPageBinary = false;
      }

      // Checks if we're inside tag contents.
      if (eventType == XMLEventType.CONTENT) {
        String contentType = event.getContentType();
        String content = event.getContent();
        if (insideTitle && currentPart != null) {
          // Appends title to an existent one.
          if (TextUtils.isEmpty(currentPart.getTitle())) {
            currentPart.setTitle(content);
          } else {
            currentPart.setTitle(currentPart.getTitle() + ", " + content);
          }
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
    }

    mTableOfContents = toc;
    storeTableOfContents();
    Log.d(FB2Storable.class.getName(), "TOC stored");
    storeSectionIdTitleMap();
    Log.d(FB2Storable.class.getName(), "IdTitleMap stored");
    mFullyProcessed = true;
    mFullyProcessingSuccess = true;
  }

  private boolean isTocCached() {
    return getCachedTocFile().exists();
  }

  private ArrayList<FB2Part> readSavedTableOfContents() throws IOException {
    FileInputStream fis = new FileInputStream(getCachedTocFile());

    byte[] buffer = new byte[BUFFER_SIZE];
    if (mStrBuilder == null) {
      mStrBuilder = new StringBuilder();
    }
    long bytesRead;
    do {
      bytesRead = fis.read(buffer);
      if (bytesRead != BUFFER_SIZE) {
        byte[] buffer0 = new byte[(int) bytesRead];
        System.arraycopy(buffer, 0, buffer0, 0, (int) bytesRead);
        mStrBuilder.append(new String(buffer0));
      } else {
        mStrBuilder.append(new String(buffer));
      }
    } while (bytesRead == BUFFER_SIZE);

    String json = mStrBuilder.toString();
    mStrBuilder.setLength(0);
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

  private void storeTableOfContents()
      throws IOException {
    FileOutputStream fos = new FileOutputStream(getCachedTocFile());
    Gson gson = new Gson();
    String json = gson.toJson(mTableOfContents);
    fos.write(json.getBytes());
    fos.close();
  }


  private File getCachedTocFile() {
    return new File(mContext.getCacheDir(),
                    mPath.substring(mPath.lastIndexOf('/')) + "_TOC.json");
  }

  private boolean isSectionIdTitleMapCached() {
    return getCachedIdTitleMapFile().exists();
  }

  private Map<String, String> readSavedSectionIdTitleMap() throws IOException {
    FileInputStream fis = new FileInputStream(getCachedIdTitleMapFile());

    byte[] buffer = new byte[BUFFER_SIZE];
    if (mStrBuilder == null) {
      mStrBuilder = new StringBuilder();
    }
    long bytesRead;
    do {
      bytesRead = fis.read(buffer);
      if (bytesRead != BUFFER_SIZE) {
        byte[] buffer0 = new byte[(int) bytesRead];
        System.arraycopy(buffer, 0, buffer0, 0, (int) bytesRead);
        mStrBuilder.append(new String(buffer0));
      } else {
        mStrBuilder.append(new String(buffer));
      }
    } while (bytesRead == BUFFER_SIZE);

    String json = mStrBuilder.toString();
    mStrBuilder.setLength(0);
    Gson gson = new Gson();
    Type mapType = new TypeToken<Map<String, String>>() {}.getType();

    // Path is needed in order to get preview and, basically, perform any
    // operation with fb2 itself.
    return gson.fromJson(json, mapType);
  }

  private void storeSectionIdTitleMap() throws IOException {
    // Create a map.
    Map<String, String> idTitleMap = new HashMap<>();
    for (AbstractTocReference part : mTableOfContents) {
      idTitleMap.put(part.getId(), part.getTitle());
    }

    FileOutputStream fos = new FileOutputStream(
        getCachedIdTitleMapFile());
    Gson gson = new Gson();
    String json = gson.toJson(idTitleMap);
    fos.write(json.getBytes());
    fos.close();
  }


  private File getCachedIdTitleMapFile() {
    return new File(mContext.getCacheDir(), mPath.substring(
        mPath.lastIndexOf('/')) + "_id_title_map.json");
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

  private class ChunkInfo implements Serializable {
    String mSectionId;
    String mSectionTitle;
    long mBytePosition;

    public ChunkInfo(String sectionId, String sectionTitle, long bytePosition) {
      this.mSectionId = sectionId;
      this.mSectionTitle = sectionTitle;
      this.mBytePosition = bytePosition;
    }
  }
}
