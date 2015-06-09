package com.infmme.readilyapp.navigation;

import android.content.Context;
import com.infmme.readilyapp.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * infm created it with love on 6/9/15. Enjoy ;)
 */
public class EpubPreview extends Preview {
  public static final int BUFFER_SIZE = 1024;
  private Book mBook;
  private String mResourceText;

  public EpubPreview(Context c, String path) {
    super(c, path);
  }

  @Override
  public Preview readFile(Context c) throws IOException {
    if (mPath == null) {
      return null;
    }
    File file = new File(mPath);
    mFileLen = file.length();
    mEncoding = Constants.DEFAULT_ENCODING;

    mBook = (new EpubReader()).readEpubLazy(mPath, mEncoding);
    return this;
  }

  public List<TOCReference> getTocTree() {
    return mBook.getTableOfContents().getTocReferences();
  }

  public Preview setResource(Resource r) throws IOException {
    mResourceText = parseEpub(new String(r.getData()));
    return this;
  }

  @Override
  public Preview readAgain() throws IOException {
    mPreview = "";
    int skipping = (int) (mResourceText.length() * mPartRead - .5 * BUFFER_SIZE);
    if (skipping < 0)
      skipping = 0;
    int right = Math.min(skipping + BUFFER_SIZE,
                         mResourceText.length());
    mPreview = mResourceText.substring(skipping, right);
    return this;
  }

  private String parseEpub(String text) {
    try {
      Document doc = Jsoup.parse(text);
      return doc.select("p").text();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }
}
