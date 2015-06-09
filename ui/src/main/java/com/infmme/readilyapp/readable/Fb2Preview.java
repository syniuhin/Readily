package com.infmme.readilyapp.readable;

import android.content.Context;
import android.text.TextUtils;
import com.infmme.readilyapp.xmlparser.XMLEvent;
import com.infmme.readilyapp.xmlparser.XMLParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * infm created it with love on 6/9/15. Enjoy ;)
 */
public class Fb2Preview extends Preview {
  private static final int BUFFER_SIZE = 4096;

  private XMLParser mParser;

  public Fb2Preview(Context c, String path) {
    super(c, path);
  }

  @Override
  public Preview readFile(Context c) throws IOException {
    if (mPath == null)
      return null;
    mFile = new File(mPath);
    mFileLen = mFile.length();
    FileInputStream encodingHelper = new FileInputStream(mFile);
    mEncoding = FileStorable.guessCharset(encodingHelper);
    encodingHelper.close();

    mParser = new XMLParser();
    return this;
  }

  @Override
  public Preview readAgain() throws IOException {
    fis = new FileInputStream(mFile);
    long skipping = (long) (mFileLen * mPartRead - .5 * BUFFER_SIZE);
    long skipped = 0;
    if (skipping > 0) {
      skipped = fis.skip(skipping);
    }

    mParser.setInput(fis);
    XMLEvent event = mParser.next();
    int eventType = event.getType();

    StringBuilder sb = new StringBuilder();
    while (eventType != XMLParser.DOCUMENT_CLOSE && sb.length() < BUFFER_SIZE) {
      if (eventType == XMLParser.CONTENT) {
        String contentType = event.getContentType();
        if (! TextUtils.isEmpty(contentType)) {
          if (contentType.equals("p"))
            sb.append(event.getContent());
        } else { //TODO: handle this situation carefully
          sb.append(event.getContent());
        }
        sb.append(" ");
      }
      event = mParser.next();
      eventType = event.getType();
    }
    mPreview = sb.toString();
    return this;
  }
}
