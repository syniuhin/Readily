package com.infmme.readilyapp.readable;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * infm created it with love on 6/9/15. Enjoy ;)
 */
abstract public class Preview {

  protected final String mPath;

  protected double mPartRead;
  protected File mFile;
  protected long mFileLen;
  protected String mEncoding;
  protected FileInputStream fis;

  protected String mPreview = null;

  public Preview(Context c, String path) {
    mPath = FileStorable.takePath(c, path);
  }

  abstract public Preview readFile(Context c) throws IOException;

  abstract public Preview readAgain() throws IOException;

  public String getPreview() {
    return mPreview;
  }

  public String getPath() {
    return mPath;
  }

  public double getPartRead() {
    return mPartRead;
  }

  public Preview setPartRead(double mPartRead) {
    this.mPartRead = mPartRead;
    return this;
  }
}
