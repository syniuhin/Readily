package com.infmme.readilyapp.readable;

import com.infmme.readilyapp.util.Constants;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created with love, by infm dated on 6/15/16.
 */

public class Utils {

  private static final List<String> VALID_EXTENSIONS = Arrays.asList(
      Constants.EXTENSION_TXT, Constants.EXTENSION_FB2, Constants.EXTENSION_EPUB
  );

  public static String guessCharset(InputStream is) throws IOException {
    UniversalDetector detector = new UniversalDetector(null);
    byte[] buf = new byte[Constants.ENCODING_HELPER_BUFFER_SIZE];
    int nread;
    while ((nread = is.read(buf)) > 0 && !detector.isDone()) {
      detector.handleData(buf, 0, nread);
    }
    detector.dataEnd();
    String encoding = detector.getDetectedCharset();
    detector.reset();
    if (encoding != null)
      return encoding;
    return Constants.DEFAULT_ENCODING;
  }

  public static boolean isExtensionValid(String extension) {
    return VALID_EXTENSIONS.contains(extension);
  }
}
