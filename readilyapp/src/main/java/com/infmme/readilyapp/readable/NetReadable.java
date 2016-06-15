package com.infmme.readilyapp.readable;

import android.content.Context;
import android.net.Uri;
import com.infmme.readilyapp.provider.cachedbook.CachedBookContentValues;
import com.infmme.readilyapp.provider.txtbook.TxtBookContentValues;
import com.infmme.readilyapp.readable.interfaces.Storable;
import com.infmme.readilyapp.readable.interfaces.Unprocessed;
import com.infmme.readilyapp.reader.Reader;
import com.infmme.readilyapp.util.Constants;
import de.jetwick.snacktory.HtmlFetcher;
import de.jetwick.snacktory.JResult;
import org.joda.time.LocalDateTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created with love, by infm dated on 6/8/16.
 */

public class NetReadable extends Readable implements Unprocessed, Storable {
  private String mTitle;
  // TODO: Add image fetching and saving.
  private String mImageUrl;
  private String mLink;
  private boolean mProcessed = false;

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
      if (mImageUrl != null) {
        fetchImage();
      }
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
    if (res.getImagesCount() > 0) {
      mImageUrl = res.getImageUrl();
    }
    return mTitle + " | " + res.getText();
  }

  private void fetchImage() {
    // TODO: Fetch image!
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
  public void prepareForStoring(Reader reader) {
    // It's already prepared since it's directly loaded into Reader.
    try {
      storeToFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void storeToDb() {
    CachedBookContentValues values = new CachedBookContentValues();
    values.putCoverImageUri(mImageUrl);
    // TODO: Solve this
    values.putPercentile(0);

    TxtBookContentValues txtValues = new TxtBookContentValues();
    txtValues.putBytePosition(0);
    txtValues.putTextPosition(mPosition);

    String currentTime = LocalDateTime.now().toString();
    values.putTimeOpened(currentTime);
    values.putPath(getPath());
    values.putTitle(mTitle);

    Uri uri = txtValues.insert(mContext.getContentResolver());
    long txtId = Long.parseLong(uri.getLastPathSegment());
    values.putTxtBookId(txtId);
    values.insert(mContext.getContentResolver());
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
  public String getPath() {
    return mContext.getCacheDir() + "/" + String.valueOf(
        mLink.hashCode()) + ".txt";
  }

  @Override
  public void setPath(String path) {}
}
