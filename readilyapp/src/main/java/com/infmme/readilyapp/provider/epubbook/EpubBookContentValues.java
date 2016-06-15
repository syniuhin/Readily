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
   * Uri of the cover image.
   */
  public EpubBookContentValues putCoverImageUri(@Nullable String value) {
    mContentValues.put(EpubBookColumns.COVER_IMAGE_URI, value);
    return this;
  }

  public EpubBookContentValues putCoverImageUriNull() {
    mContentValues.putNull(EpubBookColumns.COVER_IMAGE_URI);
    return this;
  }
}
