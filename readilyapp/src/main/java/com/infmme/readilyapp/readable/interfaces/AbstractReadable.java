package com.infmme.readilyapp.readable.interfaces;

import java.io.InputStream;

/**
 * Created with love, by infm dated on 6/8/16.
 */

public interface AbstractReadable {
  AbstractReadable setRawText(String text);

  AbstractReadable setInputStream(InputStream is);

  AbstractReadable setPath(String path);
}
