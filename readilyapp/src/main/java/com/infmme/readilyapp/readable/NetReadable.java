package com.infmme.readilyapp.readable;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import com.infmme.readilyapp.provider.cachedbook.CachedBookContentValues;
import com.infmme.readilyapp.provider.cachedbookinfo
    .CachedBookInfoContentValues;
import com.infmme.readilyapp.provider.txtbook.TxtBookContentValues;
import com.infmme.readilyapp.readable.interfaces.Storable;
import com.infmme.readilyapp.readable.interfaces.Unprocessed;
import com.infmme.readilyapp.reader.Reader;
import com.infmme.readilyapp.util.ColorMatcher;
import com.infmme.readilyapp.util.Constants;
import com.squareup.picasso.Picasso;
import de.jetwick.snacktory.HtmlFetcher;
import de.jetwick.snacktory.JResult;
import org.joda.time.LocalDateTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

/**
 * Created with love, by infm dated on 6/8/16.
 */

public class NetReadable extends Readable implements Unprocessed, Storable {
  private String mTitle;
  private String mDescription;
  private String mImageUrl;
  private Collection<String> mKeywords;

  private int mCoverImageMean;
  private String mLink;
  private boolean mProcessed = false;

  private double mChunkPercentile;

  private transient Context mContext;

  public NetReadable(final Context context, String link) {
    mContext = context;
    mLink = link;
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
      setText(parseArticle(mLink));
      mProcessed = true;
    } catch (Exception e) {
      mProcessed = false;
      e.printStackTrace();
    }
  }

  private String parseArticle(String url) throws Exception {
    HtmlFetcher fetcher = new HtmlFetcher();
    JResult res;
    // I don't know what it means, need to read docs/source
    res = fetcher.fetchAndExtract(url, Constants.NET_READABLE_TIMEOUT, true);
    mTitle = res.getTitle();
    mDescription = res.getDescription();
    mImageUrl = res.getImageUrl();
    mKeywords = res.getKeywords();
    return mTitle + " " + res.getText();
  }

  /**
   * Synchronously caches an image into a filesystem.
   */
  private void fetchCoverImage() throws IOException {
    String path = getCoverImagePath();
    Bitmap bitmap = Picasso.with(mContext)
                           .load(mImageUrl)
                           .get();

    File file = new File(path);
    file.createNewFile();
    FileOutputStream ostream = new FileOutputStream(file);
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
    ostream.close();

    mCoverImageMean = ColorMatcher.findClosestMaterialColor(bitmap);
  }

  public boolean isCachedToFile() {
    return new File(getPath()).exists();
  }

  @Override
  public boolean isStoredInDb() {
    throw new IllegalStateException(
        "NetReadable instance can be retrieved only from net!");
  }

  @Override
  public void readFromDb() {
    throw new IllegalStateException(
        "NetReadable instance can be retrieved only from net!");
  }

  @Override
  public Storable prepareForStoringSync(Reader reader) {
    mChunkPercentile = reader.getPercentile();
    mPosition = reader.getPosition();
    // It's already prepared since it's directly loaded into Reader.
    return this;
  }

  @Override
  public void beforeStoringToDb() {
    try {
      storeToFile();
      fetchCoverImage();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void storeToDb() {
    insertToDb();
  }

  @Override
  public void insertToDb() {
    CachedBookContentValues values = new CachedBookContentValues();
    values.putTextPosition(mPosition);
    if (hasCoverImage()) {
      values.putCoverImageUri(getCoverImagePath());
      values.putCoverImageMean(mCoverImageMean);
    }
    values.putPercentile(mChunkPercentile);

    String currentTime = LocalDateTime.now().toString();
    values.putTimeOpened(currentTime);
    values.putPath(getPath());
    values.putTitle(mTitle);

    CachedBookInfoContentValues infoValues = new CachedBookInfoContentValues();
    infoValues.putDescription(mDescription);

    StringBuilder stringBuilder = new StringBuilder();
    for (String kw : mKeywords) {
      stringBuilder.append(kw).append(", ");
    }
    if (stringBuilder.length() > 0) {
      infoValues.putGenre(
          stringBuilder.substring(0, stringBuilder.length() - 2));
    }

    TxtBookContentValues txtValues = new TxtBookContentValues();
    // Since we have only 1 chunk.
    txtValues.putBytePosition(0);

    Uri uri = txtValues.insert(mContext.getContentResolver());
    long txtId = Long.parseLong(uri.getLastPathSegment());
    values.putTxtBookId(txtId);
    values.insert(mContext.getContentResolver());
  }

  @Override
  public void updateInDb() {
    // Empty
  }

  /**
   * Stores fetched readable to a cache folder and creates appropriate
   * records in db tables.
   */
  @Override
  public void storeToFile() throws IOException {
    FileOutputStream fileOutputStream = new FileOutputStream(
        new File(getPath()));
    fileOutputStream.write(mText.getBytes());
  }

  @Override
  public Storable readFromFile() throws IOException {
    throw new IllegalStateException(
        "NetReadable instance can be retrieved only from net!");
  }

  @Override
  public void setContext(Context context) {
    mContext = context;
  }

  @Override
  public String getPath() {
    return mContext.getCacheDir() + "/" + String.valueOf(
        mLink.hashCode()) + ".txt";
  }

  private String getCoverImagePath() {
    return mContext.getCacheDir() + "/" + String.valueOf(
        mLink.hashCode()) + Constants.DEFAULT_COVER_PAGE_EXTENSION;
  }

  private boolean hasCoverImage() {
    return mImageUrl != null;
  }

  @Override
  public void setPath(String path) {}

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
    return mPosition;
  }

  @Override
  public void setCurrentPosition(int position) {
    mPosition = position;
  }
}
