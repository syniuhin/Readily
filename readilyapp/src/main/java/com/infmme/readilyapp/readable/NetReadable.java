package com.infmme.readilyapp.readable;

import com.infmme.readilyapp.readable.interfaces.Unprocessed;
import de.jetwick.snacktory.HtmlFetcher;
import de.jetwick.snacktory.JResult;

/**
 * Created with love, by infm dated on 6/8/16.
 */

public class NetReadable extends Readable implements Unprocessed {
  private String mTitle;
  private String mLink;
  private boolean mProcessed = false;

  public NetReadable(String link) {
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
    res = fetcher.fetchAndExtract(url, 10000, true);
    mTitle = res.getTitle();
    return mTitle + " | " + res.getText();
  }
}
