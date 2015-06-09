package com.infmme.readilyapp.navigation;

import android.content.Context;
import com.infmme.readilyapp.readable.FileStorable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * infm created it with love on 6/9/15. Enjoy ;)
 */
public class TxtPreview extends Preview {
  private static final int BUFFER_SIZE = 2048;

  private byte[] mInputData = new byte[BUFFER_SIZE];

  public TxtPreview(Context c, String path) {
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

    long readLen;
    mPreview = null;
    if ((readLen = fis.read(mInputData)) != - 1) {
      mPreview = new String(mInputData, mEncoding);
      if (readLen < mPreview.length())
        mPreview = mPreview.substring(0, (int) readLen);
    }
    return this;
  }
}
