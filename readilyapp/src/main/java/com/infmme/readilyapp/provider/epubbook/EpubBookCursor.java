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
   * Get the {@code title} value.
   * Cannot be {@code null}.
   */
  @NonNull
  public String getTitle() {
    String res = getStringOrNull(EpubBookColumns.TITLE);
    if (res == null)
      throw new NullPointerException(
          "The value of 'title' in the database was null, which is not " +
              "allowed according to the model definition");
    return res;
  }

  /**
   * Path in a storage to read from.
   * Cannot be {@code null}.
   */
  @NonNull
  public String getPath() {
    String res = getStringOrNull(EpubBookColumns.PATH);
    if (res == null)
      throw new NullPointerException(
          "The value of 'path' in the database was null, which is not allowed" +
              " according to the model definition");
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

  /**
   * Position in a parsed string, on which read was finished.
   */
  public int getTextPosition() {
    Integer res = getIntegerOrNull(EpubBookColumns.TEXT_POSITION);
    if (res == null)
      throw new NullPointerException(
          "The value of 'text_position' in the database was null, which is " +
              "not allowed according to the model definition");
    return res;
  }

  /**
   * Amount of book that is already read, percent.
   */
  public double getPercentile() {
    Double res = getDoubleOrNull(EpubBookColumns.PERCENTILE);
    if (res == null)
      throw new NullPointerException(
          "The value of 'percentile' in the database was null, which is not " +
              "allowed according to the model definition");
    return res;
  }

  /**
   * Last open time, joda standard datetime format.
   * Cannot be {@code null}.
   */
  @NonNull
  public String getTimeOpened() {
    String res = getStringOrNull(EpubBookColumns.TIME_OPENED);
    if (res == null)
      throw new NullPointerException(
          "The value of 'time_opened' in the database was null, which is not " +
              "allowed according to the model definition");
    return res;
  }
}
