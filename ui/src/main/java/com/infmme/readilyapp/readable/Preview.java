package com.infmme.readilyapp.readable;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * infm created it with love on 6/9/15. Enjoy ;)
 */
abstract public class Preview {

  protected String mPath;
  protected double mPartRead;

  protected File mFile;
  protected FileInputStream fis;

  protected String mPreview = null;

  public Preview(String path, double part) {
    mPath = path;
    mPartRead = part;
  }

  abstract public Preview readFile(Context c) throws IOException;

  public String getPreview() {
    return mPreview;
  }

  ;

  public String getPath() {
    return mPath;
  }

  public void setPath(String mPath) {
    this.mPath = mPath;
  }

  public double getPartRead() {
    return mPartRead;
  }

  public void setPartRead(double mPartRead) {
    this.mPartRead = mPartRead;
  }
}
