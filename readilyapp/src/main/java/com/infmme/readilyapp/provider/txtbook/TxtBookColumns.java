package com.infmme.readilyapp.provider.txtbook;

import android.net.Uri;
import android.provider.BaseColumns;
import com.infmme.readilyapp.provider.ReadilyProvider;

/**
 * Txt book, which was opened at least once.
 */
public class TxtBookColumns implements BaseColumns {
  public static final String TABLE_NAME = "txt_book";
  public static final Uri CONTENT_URI = Uri.parse(
      ReadilyProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

  /**
   * Primary key.
   */
  public static final String _ID = BaseColumns._ID;

  /**
   * Position in a parsed string, on which read was finished.
   */
  public static final String TEXT_POSITION = "txt_book__text_position";


  public static final String DEFAULT_ORDER = TABLE_NAME + "." + _ID;

  // @formatter:off
  public static final String[] ALL_COLUMNS = new String[] {
      _ID,
      TEXT_POSITION
  };
  // @formatter:on

  public static boolean hasColumns(String[] projection) {
    if (projection == null) return true;
    for (String c : projection) {
      if (c.equals(TEXT_POSITION) || c.contains("." + TEXT_POSITION))
        return true;
    }
    return false;
  }

}
