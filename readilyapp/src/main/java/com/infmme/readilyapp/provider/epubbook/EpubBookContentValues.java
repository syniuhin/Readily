package com.infmme.readilyapp.provider.epubbook;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.infmme.readilyapp.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code epub_book} table.
 */
public class EpubBookContentValues extends AbstractContentValues {
  @Override
  public Uri uri() {
    return EpubBookColumns.CONTENT_URI;
  }

  /**
   * Update row(s) using the values stored by this object and the given
   * selection.
   *
   * @param contentResolver The content resolver to use.
   * @param where           The selection to use (can be {@code null}).
   */
  public int update(ContentResolver contentResolver,
                    @Nullable EpubBookSelection where) {
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
  public int update(Context context, @Nullable EpubBookSelection where) {
    return context.getContentResolver()
                  .update(uri(), values(), where == null ? null : where.sel(),
                          where == null ? null : where.args());
  }

  public EpubBookContentValues putTitle(@NonNull String value) {
    if (value == null)
      throw new IllegalArgumentException("title must not be null");
    mContentValues.put(EpubBookColumns.TITLE, value);
    return this;
  }


  /**
   * Path in a storage to read from.
   */
  public EpubBookContentValues putPath(@NonNull String value) {
    if (value == null)
      throw new IllegalArgumentException("path must not be null");
    mContentValues.put(EpubBookColumns.PATH, value);
    return this;
  }


  /**
   * Id of a resource, from which last read was made.
   */
  public EpubBookContentValues putCurrentResourceId(@NonNull String value) {
    if (value == null)
      throw new IllegalArgumentException("currentResourceId must not be null");
    mContentValues.put(EpubBookColumns.CURRENT_RESOURCE_ID, value);
    return this;
  }


  /**
   * Position in a parsed string, on which read was finished.
   */
  public EpubBookContentValues putTextPosition(int value) {
    mContentValues.put(EpubBookColumns.TEXT_POSITION, value);
    return this;
  }


  /**
   * Amount of book that is already read, percent.
   */
  public EpubBookContentValues putPercentile(double value) {
    mContentValues.put(EpubBookColumns.PERCENTILE, value);
    return this;
  }


  /**
   * Last open time, joda standard datetime format.
   */
  public EpubBookContentValues putTimeOpened(@NonNull String value) {
    if (value == null)
      throw new IllegalArgumentException("timeOpened must not be null");
    mContentValues.put(EpubBookColumns.TIME_OPENED, value);
    return this;
  }

}
