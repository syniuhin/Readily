package com.infmme.readilyapp.provider.fb2book;

import android.net.Uri;
import android.provider.BaseColumns;
import com.infmme.readilyapp.provider.ReadilyProvider;

/**
 * FB2 book, which was opened at least once.
 */
public class Fb2BookColumns implements BaseColumns {
  public static final String TABLE_NAME = "fb2_book";
  public static final Uri CONTENT_URI = Uri.parse(
      ReadilyProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

  /**
   * Primary key.
   */
  public static final String _ID = BaseColumns._ID;

  /**
   * Id of a fb2part, from which last read was made.
   */
  public static final String CURRENT_PART_ID = "current_part_id";

  /**
   * Position in a parsed preview, on which read was finished.
   */
  public static final String TEXT_POSITION = "fb2_book__text_position";

  /**
   * Path to .json cache of a table of contents.
   */
  public static final String PATH_TOC = "path_toc";


  public static final String DEFAULT_ORDER = TABLE_NAME + "." + _ID;

  // @formatter:off
  public static final String[] ALL_COLUMNS = new String[] {
      _ID,
      CURRENT_PART_ID,
      TEXT_POSITION,
      PATH_TOC
  };
  // @formatter:on

  public static boolean hasColumns(String[] projection) {
    if (projection == null) return true;
    for (String c : projection) {
      if (c.equals(CURRENT_PART_ID) || c.contains("." + CURRENT_PART_ID))
        return true;
      if (c.equals(TEXT_POSITION) || c.contains("." + TEXT_POSITION))
        return true;
      if (c.equals(PATH_TOC) || c.contains("." + PATH_TOC)) return true;
    }
    return false;
  }

}
