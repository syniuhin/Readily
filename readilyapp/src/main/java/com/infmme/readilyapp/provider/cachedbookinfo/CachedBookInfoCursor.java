package com.infmme.readilyapp.provider.cachedbookinfo;

import android.database.Cursor;
import android.support.annotation.Nullable;
import com.infmme.readilyapp.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code cached_book_info} table.
 */
public class CachedBookInfoCursor extends AbstractCursor
    implements CachedBookInfoModel {
  public CachedBookInfoCursor(Cursor cursor) {
    super(cursor);
  }

  /**
   * Primary key.
   */
  public long getId() {
    Long res = getLongOrNull(CachedBookInfoColumns._ID);
    if (res == null)
      throw new NullPointerException(
          "The value of '_id' in the database was null, which is not allowed " +
              "according to the model definition");
    return res;
  }

  /**
   * Author of a book or net article.
   * Can be {@code null}.
   */
  @Nullable
  public String getAuthor() {
    String res = getStringOrNull(CachedBookInfoColumns.AUTHOR);
    return res;
  }

  /**
   * Genre or category of a book or net article.
   * Can be {@code null}.
   */
  @Nullable
  public String getGenre() {
    String res = getStringOrNull(CachedBookInfoColumns.GENRE);
    return res;
  }

  /**
   * Language of a book.
   * Can be {@code null}.
   */
  @Nullable
  public String getLanguage() {
    String res = getStringOrNull(CachedBookInfoColumns.LANGUAGE);
    return res;
  }

  /**
   * Current part title of a book.
   * Can be {@code null}.
   */
  @Nullable
  public String getCurrentPartTitle() {
    String res = getStringOrNull(CachedBookInfoColumns.CURRENT_PART_TITLE);
    return res;
  }

  /**
   * Description of a book or net article.
   * Can be {@code null}.
   */
  @Nullable
  public String getDescription() {
    String res = getStringOrNull(CachedBookInfoColumns.DESCRIPTION);
    return res;
  }
}
