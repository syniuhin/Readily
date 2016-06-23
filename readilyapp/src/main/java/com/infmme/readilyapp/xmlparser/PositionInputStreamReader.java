package com.infmme.readilyapp.xmlparser;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created with love, by infm dated on 6/16/16.
 */

public class PositionInputStreamReader extends InputStreamReader {

  private long pos = 0;

  private long mark = 0;

  public PositionInputStreamReader(InputStream in, String charsetName)
      throws UnsupportedEncodingException {
    super(in, charsetName);
  }

  /**
   * <p>Get the stream position.</p>
   * <p>
   * <p>Eventually, the position will roll over to a negative number.
   * Reading 1 Tb per second, this would occur after approximately three
   * months. Applications should account for this possibility in their
   * design.</p>
   *
   * @return the current stream position.
   */
  public synchronized long getPosition() {
    return pos;
  }

/*
  @Override
  public synchronized int read()
      throws IOException {
    int b = super.read();
    if (b >= 0)
      pos += 1;
    return b;
  }
*/

  @Override
  public synchronized int read(@NonNull char[] b, int off, int len)
      throws IOException {
    int n = super.read(b, off, len);
    if (n > 0)
      pos += n;
    return n;
  }

/*
  @Override
  public synchronized long skip(long skip)
      throws IOException {
    long n = super.skip(skip);
    if (n > 0)
      pos += n;
    return n;
  }
*/

  @Override
  public synchronized void mark(int readlimit) throws IOException {
    super.mark(readlimit);
    mark = pos;
  }

  @Override
  public synchronized void reset()
      throws IOException {
    /* A call to reset can still succeed if mark is not supported, but the
     * resulting stream position is undefined, so it's not allowed here. */
    if (!markSupported())
      throw new IOException("Mark not supported.");
    super.reset();
    pos = mark;
  }
}
