package com.infmme.readilyapp.provider.epubbook;

import android.net.Uri;
import android.provider.BaseColumns;
import com.infmme.readilyapp.provider.ReadilyProvider;

/**
 * Epub book, which was opened at least once.
 */
public class EpubBookColumns implements BaseColumns {
  public static final String TABLE_NAME = "epub_book";
  public static final Uri CONTENT_URI = Uri.parse(
      ReadilyProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

  /**
   * Primary key.
   */
  public static final String _ID = BaseColumns._ID;

  /**
   * Id of a resource, from which last read was made.
   */
  public static final String CURRENT_RESOURCE_ID = "current_resource_id";

  /**
   * Title of a resource, from which last read was made.
   */
  public static final String CURRENT_RESOURCE_TITLE = "current_resource_title";


  public static final String DEFAULT_ORDER = TABLE_NAME + "." + _ID;

  // @formatter:off
  public static final String[] ALL_COLUMNS = new String[] {
      _ID,
      CURRENT_RESOURCE_ID,
      CURRENT_RESOURCE_TITLE
  };
  // @formatter:on

  public static boolean hasColumns(String[] projection) {
    if (projection == null) return true;
    for (String c : projection) {
      if (c.equals(CURRENT_RESOURCE_ID) || c.contains(
          "." + CURRENT_RESOURCE_ID)) return true;
      if (c.equals(CURRENT_RESOURCE_TITLE) || c.contains(
          "." + CURRENT_RESOURCE_TITLE)) return true;
    }
    return false;
  }

}
