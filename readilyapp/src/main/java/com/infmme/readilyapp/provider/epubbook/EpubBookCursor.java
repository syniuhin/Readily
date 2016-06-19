package com.infmme.readilyapp.provider.epubbook;

import android.database.Cursor;
import android.support.annotation.NonNull;
import com.infmme.readilyapp.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code epub_book} table.
 */
public class EpubBookCursor extends AbstractCursor implements EpubBookModel {
  public EpubBookCursor(Cursor cursor) {
    super(cursor);
  }

  /**
   * Primary key.
   */
  public long getId() {
    Long res = getLongOrNull(EpubBookColumns._ID);
    if (res == null)
      throw new NullPointerException(
          "The value of '_id' in the database was null, which is not allowed " +
              "according to the model definition");
    return res;
  }

  /**
   * Id of a resource, from which last read was made.
   * Cannot be {@code null}.
   */
  @NonNull
  public String getCurrentResourceId() {
    String res = getStringOrNull(EpubBookColumns.CURRENT_RESOURCE_ID);
    if (res == null)
      throw new NullPointerException(
          "The value of 'current_resource_id' in the database was null, which" +
              " is not allowed according to the model definition");
    return res;
  }
}
