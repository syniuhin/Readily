package com.infmme.readilyapp.readable;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import com.infmme.readilyapp.util.Constants;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created with love, by infm dated on 6/15/16.
 */

public class Utils {
  public static final HashMap<String, Integer> extensionsMap = new
      HashMap<String, Integer>();

  static {
    extensionsMap.put(Constants.EXTENSION_TXT, com.infmme.readilyapp.readable
        .old.Readable.TYPE_TXT);
    extensionsMap.put(Constants.EXTENSION_EPUB, com.infmme.readilyapp
        .readable.old.Readable.TYPE_EPUB);
    extensionsMap.put(Constants.EXTENSION_FB2,
                      com.infmme.readilyapp.readable.old.Readable.TYPE_FB2);
  }

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
    return extensionsMap.containsKey(extension);
  }

  public static Target generateCachingTarget(final String path) {
    return new Target() {
      @Override
      public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
        File file = new File(path);
        try {
          file.createNewFile();
          FileOutputStream ostream = new FileOutputStream(file);
          bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
          ostream.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      @Override
      public void onBitmapFailed(Drawable errorDrawable) {}

      @Override
      public void onPrepareLoad(Drawable placeHolderDrawable) {}
    };
  }
}
