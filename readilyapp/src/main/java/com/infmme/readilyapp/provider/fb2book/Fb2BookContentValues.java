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

  /**
   * Tells if this fb2 was fully processed (i.e. table of contents, cover
   * image).
   */
  public Fb2BookContentValues putFullyProcessed(boolean value) {
    mContentValues.put(Fb2BookColumns.FULLY_PROCESSED, value);
    return this;
  }


  /**
   * Tells if processing of this record was failed.
   */
  public Fb2BookContentValues putFullyProcessingSuccess(
      @Nullable Boolean value) {
    mContentValues.put(Fb2BookColumns.FULLY_PROCESSING_SUCCESS, value);
    return this;
  }

  public Fb2BookContentValues putFullyProcessingSuccessNull() {
    mContentValues.putNull(Fb2BookColumns.FULLY_PROCESSING_SUCCESS);
    return this;
  }

  /**
   * Byte position of block in a file, either FB2Part or simple chunk read
   * continuously.
   */
  public Fb2BookContentValues putBytePosition(int value) {
    mContentValues.put(Fb2BookColumns.BYTE_POSITION, value);
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
}
