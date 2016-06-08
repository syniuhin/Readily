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

  public static final String TITLE = "txt_book__title";

  /**
   * Path in a storage to read from.
   */
  public static final String PATH = "txt_book__path";

  /**
   * Position in a parsed string, on which read was finished.
   */
  public static final String TEXT_POSITION = "txt_book__text_position";

  /**
   * Amount of book that is already read, percent.
   */
  public static final String PERCENTILE = "txt_book__percentile";

  /**
   * Last open time, joda standard datetime format.
   */
  public static final String TIME_OPENED = "txt_book__time_opened";


  public static final String DEFAULT_ORDER = TABLE_NAME + "." + _ID;

  // @formatter:off
  public static final String[] ALL_COLUMNS = new String[] {
      _ID,
      TITLE,
      PATH,
      TEXT_POSITION,
      PERCENTILE,
      TIME_OPENED
  };
  // @formatter:on

  public static boolean hasColumns(String[] projection) {
    if (projection == null) return true;
    for (String c : projection) {
      if (c.equals(TITLE) || c.contains("." + TITLE)) return true;
      if (c.equals(PATH) || c.contains("." + PATH)) return true;
      if (c.equals(TEXT_POSITION) || c.contains("." + TEXT_POSITION))
        return true;
      if (c.equals(PERCENTILE) || c.contains("." + PERCENTILE)) return true;
      if (c.equals(TIME_OPENED) || c.contains("." + TIME_OPENED)) return true;
    }
    return false;
  }

}
