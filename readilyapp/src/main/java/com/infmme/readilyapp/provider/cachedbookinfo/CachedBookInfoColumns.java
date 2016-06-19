package com.infmme.readilyapp.provider.cachedbookinfo;

import android.net.Uri;
import android.provider.BaseColumns;
import com.infmme.readilyapp.provider.ReadilyProvider;

/**
 * Extended information about a cached book.
 */
public class CachedBookInfoColumns implements BaseColumns {
  public static final String TABLE_NAME = "cached_book_info";
  public static final Uri CONTENT_URI = Uri.parse(
      ReadilyProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

  /**
   * Primary key.
   */
  public static final String _ID = BaseColumns._ID;

  /**
   * Author of a book or net article.
   */
  public static final String AUTHOR = "author";

  /**
   * Genre or category of a book or net article.
   */
  public static final String GENRE = "genre";

  /**
   * Language of a book.
   */
  public static final String LANGUAGE = "language";

  /**
   * Current part title of a book.
   */
  public static final String CURRENT_PART_TITLE = "current_part_title";


  public static final String DEFAULT_ORDER = TABLE_NAME + "." + _ID;

  // @formatter:off
  public static final String[] ALL_COLUMNS = new String[] {
      _ID,
      AUTHOR,
      GENRE,
      LANGUAGE,
      CURRENT_PART_TITLE
  };
  // @formatter:on

  public static boolean hasColumns(String[] projection) {
    if (projection == null) return true;
    for (String c : projection) {
      if (c.equals(AUTHOR) || c.contains("." + AUTHOR)) return true;
      if (c.equals(GENRE) || c.contains("." + GENRE)) return true;
      if (c.equals(LANGUAGE) || c.contains("." + LANGUAGE)) return true;
      if (c.equals(CURRENT_PART_TITLE) || c.contains("." + CURRENT_PART_TITLE))
        return true;
    }
    return false;
  }

}
