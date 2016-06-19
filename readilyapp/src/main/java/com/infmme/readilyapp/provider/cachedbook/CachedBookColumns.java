package com.infmme.readilyapp.provider.cachedbook;

import android.net.Uri;
import android.provider.BaseColumns;
import com.infmme.readilyapp.provider.ReadilyProvider;
import com.infmme.readilyapp.provider.epubbook.EpubBookColumns;
import com.infmme.readilyapp.provider.fb2book.Fb2BookColumns;
import com.infmme.readilyapp.provider.txtbook.TxtBookColumns;

/**
 * General book, which was opened at least once.
 */
public class CachedBookColumns implements BaseColumns {
  public static final String TABLE_NAME = "cached_book";
  public static final Uri CONTENT_URI = Uri.parse(
      ReadilyProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

  /**
   * Primary key.
   */
  public static final String _ID = BaseColumns._ID;

  public static final String TITLE = "title";

  /**
   * Path in a storage to read from.
   */
  public static final String PATH = "path";

  /**
   * Position in word list.
   */
  public static final String TEXT_POSITION = "text_position";

  /**
   * Amount of book that is already read, percent.
   */
  public static final String PERCENTILE = "percentile";

  /**
   * Last open time, joda standard datetime format.
   */
  public static final String TIME_OPENED = "time_opened";

  /**
   * Uri of the cover image.
   */
  public static final String COVER_IMAGE_URI = "cover_image_uri";

  /**
   * Mean color of the cover image in form of argb.
   */
  public static final String COVER_IMAGE_MEAN = "cover_image_mean";

  /**
   * Optional link to epub_book.
   */
  public static final String EPUB_BOOK_ID = "epub_book_id";

  /**
   * Optional link to fb2_book.
   */
  public static final String FB2_BOOK_ID = "fb2_book_id";

  /**
   * Optional link to txt_book.
   */
  public static final String TXT_BOOK_ID = "txt_book_id";


  public static final String DEFAULT_ORDER = TABLE_NAME + "." + _ID;

  // @formatter:off
  public static final String[] ALL_COLUMNS = new String[] {
      _ID,
      TITLE,
      PATH,
      TEXT_POSITION,
      PERCENTILE,
      TIME_OPENED,
      COVER_IMAGE_URI,
      COVER_IMAGE_MEAN,
      EPUB_BOOK_ID,
      FB2_BOOK_ID,
      TXT_BOOK_ID
  };

  // @formatter:off
  public static final String[] ALL_COLUMNS_FB2_JOINED = new String[] {
      _ID,
      TITLE,
      PATH,
      TEXT_POSITION,
      PERCENTILE,
      TIME_OPENED,
      COVER_IMAGE_URI,
      COVER_IMAGE_MEAN,
      EPUB_BOOK_ID,
      FB2_BOOK_ID,
      TXT_BOOK_ID,
      Fb2BookColumns.FULLY_PROCESSED,
      Fb2BookColumns.BYTE_POSITION,
      Fb2BookColumns.CURRENT_PART_ID,
      Fb2BookColumns.CURRENT_PART_TITLE,
      Fb2BookColumns.PATH_TOC
  };

  // @formatter:off
  public static final String[] ALL_COLUMNS_EPUB_JOINED = new String[] {
      _ID,
      TITLE,
      PATH,
      TEXT_POSITION,
      PERCENTILE,
      TIME_OPENED,
      COVER_IMAGE_URI,
      COVER_IMAGE_MEAN,
      EPUB_BOOK_ID,
      FB2_BOOK_ID,
      TXT_BOOK_ID,
      EpubBookColumns.CURRENT_RESOURCE_ID,
      EpubBookColumns.CURRENT_RESOURCE_TITLE
  };

  // @formatter:off
  public static final String[] ALL_COLUMNS_TXT_JOINED = new String[] {
      _ID,
      TITLE,
      PATH,
      TEXT_POSITION,
      PERCENTILE,
      TIME_OPENED,
      COVER_IMAGE_URI,
      COVER_IMAGE_MEAN,
      EPUB_BOOK_ID,
      FB2_BOOK_ID,
      TXT_BOOK_ID,
      TxtBookColumns.BYTE_POSITION
  };

  // @formatter:off
  public static final String[] ALL_COLUMNS_FULL_JOIN = new String[] {
      _ID,
      TITLE,
      PATH,
      TEXT_POSITION,
      PERCENTILE,
      TIME_OPENED,
      COVER_IMAGE_URI,
      COVER_IMAGE_MEAN,
      EPUB_BOOK_ID,
      FB2_BOOK_ID,
      TXT_BOOK_ID,
      Fb2BookColumns.FULLY_PROCESSED,
      Fb2BookColumns.BYTE_POSITION,
      Fb2BookColumns.CURRENT_PART_ID,
      Fb2BookColumns.CURRENT_PART_TITLE,
      Fb2BookColumns.PATH_TOC,
      EpubBookColumns.CURRENT_RESOURCE_ID,
      EpubBookColumns.CURRENT_RESOURCE_TITLE,
      TxtBookColumns.BYTE_POSITION
  };
  // @formatter:on
  public static final String PREFIX_EPUB_BOOK = TABLE_NAME + "__" +
      EpubBookColumns.TABLE_NAME;
  public static final String PREFIX_FB2_BOOK = TABLE_NAME + "__" +
      Fb2BookColumns.TABLE_NAME;
  public static final String PREFIX_TXT_BOOK = TABLE_NAME + "__" +
      TxtBookColumns.TABLE_NAME;

  public static boolean hasColumns(String[] projection) {
    if (projection == null) return true;
    for (String c : projection) {
      if (c.equals(TITLE) || c.contains("." + TITLE)) return true;
      if (c.equals(PATH) || c.contains("." + PATH)) return true;
      if (c.equals(TEXT_POSITION) || c.contains("." + TEXT_POSITION))
        return true;
      if (c.equals(PERCENTILE) || c.contains("." + PERCENTILE)) return true;
      if (c.equals(TIME_OPENED) || c.contains("." + TIME_OPENED)) return true;
      if (c.equals(COVER_IMAGE_URI) || c.contains("." + COVER_IMAGE_URI))
        return true;
      if (c.equals(COVER_IMAGE_MEAN) || c.contains("." + COVER_IMAGE_MEAN))
        return true;
      if (c.equals(EPUB_BOOK_ID) || c.contains("." + EPUB_BOOK_ID)) return true;
      if (c.equals(FB2_BOOK_ID) || c.contains("." + FB2_BOOK_ID)) return true;
      if (c.equals(TXT_BOOK_ID) || c.contains("." + TXT_BOOK_ID)) return true;
    }
    return false;
  }
}
