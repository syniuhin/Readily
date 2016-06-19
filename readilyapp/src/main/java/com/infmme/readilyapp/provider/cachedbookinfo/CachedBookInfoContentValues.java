package com.infmme.readilyapp.provider.cachedbookinfo;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import com.infmme.readilyapp.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code cached_book_info} table.
 */
public class CachedBookInfoContentValues extends AbstractContentValues {
  @Override
  public Uri uri() {
    return CachedBookInfoColumns.CONTENT_URI;
  }

  /**
   * Update row(s) using the values stored by this object and the given
   * selection.
   *
   * @param contentResolver The content resolver to use.
   * @param where           The selection to use (can be {@code null}).
   */
  public int update(ContentResolver contentResolver,
                    @Nullable CachedBookInfoSelection where) {
    return contentResolver.update(uri(), values(),
                                  where == null ? null : where.sel(),
                                  where == null ? null : where.args());
  }

  /**
   * Update row(s) using the values stored by this object and the given
   * selection.
   *
   * @param contentResolver The content resolver to use.
   * @param where           The selection to use (can be {@code null}).
   */
  public int update(Context context, @Nullable CachedBookInfoSelection where) {
    return context.getContentResolver()
                  .update(uri(), values(), where == null ? null : where.sel(),
                          where == null ? null : where.args());
  }

  /**
   * Author of a book or net article.
   */
  public CachedBookInfoContentValues putAuthor(@Nullable String value) {
    mContentValues.put(CachedBookInfoColumns.AUTHOR, value);
    return this;
  }

  public CachedBookInfoContentValues putAuthorNull() {
    mContentValues.putNull(CachedBookInfoColumns.AUTHOR);
    return this;
  }

  /**
   * Genre or category of a book or net article.
   */
  public CachedBookInfoContentValues putGenre(@Nullable String value) {
    mContentValues.put(CachedBookInfoColumns.GENRE, value);
    return this;
  }

  public CachedBookInfoContentValues putGenreNull() {
    mContentValues.putNull(CachedBookInfoColumns.GENRE);
    return this;
  }

  /**
   * Language of a book.
   */
  public CachedBookInfoContentValues putLanguage(@Nullable String value) {
    mContentValues.put(CachedBookInfoColumns.LANGUAGE, value);
    return this;
  }

  public CachedBookInfoContentValues putLanguageNull() {
    mContentValues.putNull(CachedBookInfoColumns.LANGUAGE);
    return this;
  }

  /**
   * Current part title of a book.
   */
  public CachedBookInfoContentValues putCurrentPartTitle(
      @Nullable String value) {
    mContentValues.put(CachedBookInfoColumns.CURRENT_PART_TITLE, value);
    return this;
  }

  public CachedBookInfoContentValues putCurrentPartTitleNull() {
    mContentValues.putNull(CachedBookInfoColumns.CURRENT_PART_TITLE);
    return this;
  }

  /**
   * Description of a book or net article.
   */
  public CachedBookInfoContentValues putDescription(@Nullable String value) {
    mContentValues.put(CachedBookInfoColumns.DESCRIPTION, value);
    return this;
  }

  public CachedBookInfoContentValues putDescriptionNull() {
    mContentValues.putNull(CachedBookInfoColumns.DESCRIPTION);
    return this;
  }
}
