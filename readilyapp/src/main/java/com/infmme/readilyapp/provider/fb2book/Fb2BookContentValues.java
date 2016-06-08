package com.infmme.readilyapp.provider.fb2book;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.infmme.readilyapp.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code fb2_book} table.
 */
public class Fb2BookContentValues extends AbstractContentValues {
  @Override
  public Uri uri() {
    return Fb2BookColumns.CONTENT_URI;
  }

  /**
   * Update row(s) using the values stored by this object and the given
   * selection.
   *
   * @param contentResolver The content resolver to use.
   * @param where           The selection to use (can be {@code null}).
   */
  public int update(ContentResolver contentResolver,
                    @Nullable Fb2BookSelection where) {
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
  public int update(Context context, @Nullable Fb2BookSelection where) {
    return context.getContentResolver()
                  .update(uri(), values(), where == null ? null : where.sel(),
                          where == null ? null : where.args());
  }

  public Fb2BookContentValues putTitle(@NonNull String value) {
    if (value == null)
      throw new IllegalArgumentException("title must not be null");
    mContentValues.put(Fb2BookColumns.TITLE, value);
    return this;
  }


  /**
   * Path in a storage to read from.
   */
  public Fb2BookContentValues putPath(@NonNull String value) {
    if (value == null)
      throw new IllegalArgumentException("path must not be null");
    mContentValues.put(Fb2BookColumns.PATH, value);
    return this;
  }


  /**
   * Id of a fb2part, from which last read was made.
   */
  public Fb2BookContentValues putCurrentPartId(@NonNull String value) {
    if (value == null)
      throw new IllegalArgumentException("currentPartId must not be null");
    mContentValues.put(Fb2BookColumns.CURRENT_PART_ID, value);
    return this;
  }


  /**
   * Position in a parsed preview, on which read was finished.
   */
  public Fb2BookContentValues putTextPosition(int value) {
    mContentValues.put(Fb2BookColumns.TEXT_POSITION, value);
    return this;
  }


  /**
   * Amount of book that is already read, percent.
   */
  public Fb2BookContentValues putPercentile(double value) {
    mContentValues.put(Fb2BookColumns.PERCENTILE, value);
    return this;
  }


  /**
   * Path to .json cache of a table of contents.
   */
  public Fb2BookContentValues putPathToc(@Nullable String value) {
    mContentValues.put(Fb2BookColumns.PATH_TOC, value);
    return this;
  }

  public Fb2BookContentValues putPathTocNull() {
    mContentValues.putNull(Fb2BookColumns.PATH_TOC);
    return this;
  }

  /**
   * Last open time, joda standard datetime format.
   */
  public Fb2BookContentValues putTimeOpened(@NonNull String value) {
    if (value == null)
      throw new IllegalArgumentException("timeOpened must not be null");
    mContentValues.put(Fb2BookColumns.TIME_OPENED, value);
    return this;
  }

}
