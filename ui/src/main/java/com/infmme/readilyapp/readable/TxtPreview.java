package com.infmme.readilyapp.readable;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * infm created it with love on 6/9/15. Enjoy ;)
 */
public class TxtPreview extends Preview {
  private static final int BUFFER_SIZE = 2048;

  private byte[] mInputData = new byte[BUFFER_SIZE];

  public TxtPreview(String path, double part) {
    super(path, part);
  }

  @Override
  public Preview readFile(Context c) throws IOException {
    mPath = FileStorable.takePath(c, mPath);
    if (mPath == null)
      return null;
    mFile = new File(mPath);
    long fileLen = mFile.length();
    FileInputStream encodingHelper = new FileInputStream(mFile);
    String encoding = FileStorable.guessCharset(encodingHelper);
    encodingHelper.close();

    fis = new FileInputStream(mFile);
    long skipping = (long) (fileLen * mPartRead - .5 * BUFFER_SIZE);
    long skipped = 0;
    if (skipping > 0) {
      skipped = fis.skip(skipping);
    }

    long readLen;
    mPreview = null;
    if ((readLen = fis.read(mInputData)) != - 1) {
      mPreview = new String(mInputData, encoding);
      if (readLen < mPreview.length())
        mPreview = mPreview.substring(0, (int) readLen);
    }

    return this;
  }
}
