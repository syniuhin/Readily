package com.infmme.readilyapp.provider.fb2book;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.infmme.readilyapp.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code fb2_book} table.
 */
public class Fb2BookCursor extends AbstractCursor implements Fb2BookModel {
  public Fb2BookCursor(Cursor cursor) {
    super(cursor);
  }

  /**
   * Primary key.
   */
  public long getId() {
    Long res = getLongOrNull(Fb2BookColumns._ID);
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
    String res = getStringOrNull(Fb2BookColumns.TITLE);
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
    String res = getStringOrNull(Fb2BookColumns.PATH);
    if (res == null)
      throw new NullPointerException(
          "The value of 'path' in the database was null, which is not allowed" +
              " according to the model definition");
    return res;
  }

  /**
   * Id of a fb2part, from which last read was made.
   * Cannot be {@code null}.
   */
  @NonNull
  public String getCurrentPartId() {
    String res = getStringOrNull(Fb2BookColumns.CURRENT_PART_ID);
    if (res == null)
      throw new NullPointerException(
          "The value of 'current_part_id' in the database was null, which is " +
              "not allowed according to the model definition");
    return res;
  }

  /**
   * Position in a parsed preview, on which read was finished.
   */
  public int getTextPosition() {
    Integer res = getIntegerOrNull(Fb2BookColumns.TEXT_POSITION);
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
    Double res = getDoubleOrNull(Fb2BookColumns.PERCENTILE);
    if (res == null)
      throw new NullPointerException(
          "The value of 'percentile' in the database was null, which is not " +
              "allowed according to the model definition");
    return res;
  }

  /**
   * Path to .json cache of a table of contents.
   * Can be {@code null}.
   */
  @Nullable
  public String getPathToc() {
    String res = getStringOrNull(Fb2BookColumns.PATH_TOC);
    return res;
  }

  /**
   * Last open time, joda standard datetime format.
   * Cannot be {@code null}.
   */
  @NonNull
  public String getTimeOpened() {
    String res = getStringOrNull(Fb2BookColumns.TIME_OPENED);
    if (res == null)
      throw new NullPointerException(
          "The value of 'time_opened' in the database was null, which is not " +
              "allowed according to the model definition");
    return res;
  }
}
