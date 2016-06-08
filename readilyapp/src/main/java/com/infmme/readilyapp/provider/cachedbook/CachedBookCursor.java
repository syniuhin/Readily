package com.infmme.readilyapp.provider.cachedbook;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.infmme.readilyapp.provider.base.AbstractCursor;
import com.infmme.readilyapp.provider.epubbook.EpubBookColumns;
import com.infmme.readilyapp.provider.fb2book.Fb2BookColumns;
import com.infmme.readilyapp.provider.txtbook.TxtBookColumns;

/**
 * Cursor wrapper for the {@code cached_book} table.
 */
public class CachedBookCursor extends AbstractCursor
    implements CachedBookModel {
  public CachedBookCursor(Cursor cursor) {
    super(cursor);
  }

  /**
   * Primary key.
   */
  public long getId() {
    Long res = getLongOrNull(CachedBookColumns._ID);
    if (res == null)
      throw new NullPointerException(
          "The value of '_id' in the database was null, which is not allowed " +
              "according to the model definition");
    return res;
  }

  /**
   * Last open time, joda standard datetime format.
   * Cannot be {@code null}.
   */
  @NonNull
  public String getTimeOpened() {
    String res = getStringOrNull(CachedBookColumns.TIME_OPENED);
    if (res == null)
      throw new NullPointerException(
          "The value of 'time_opened' in the database was null, which is not " +
              "allowed according to the model definition");
    return res;
  }

  /**
   * Optional link to epub_book.
   * Can be {@code null}.
   */
  @Nullable
  public Long getEpubBookId() {
    Long res = getLongOrNull(CachedBookColumns.EPUB_BOOK_ID);
    return res;
  }

  /**
   * Get the {@code title} value.
   * Can be {@code null}.
   */
  @Nullable
  public String getEpubBookTitle() {
    String res = getStringOrNull(EpubBookColumns.TITLE);
    return res;
  }

  /**
   * Path in a storage to read from.
   * Can be {@code null}.
   */
  @Nullable
  public String getEpubBookPath() {
    String res = getStringOrNull(EpubBookColumns.PATH);
    return res;
  }

  /**
   * Id of a resource, from which last read was made.
   * Can be {@code null}.
   */
  @Nullable
  public String getEpubBookCurrentResourceId() {
    String res = getStringOrNull(EpubBookColumns.CURRENT_RESOURCE_ID);
    return res;
  }

  /**
   * Position in a parsed string, on which read was finished.
   * Can be {@code null}.
   */
  @Nullable
  public Integer getEpubBookTextPosition() {
    Integer res = getIntegerOrNull(EpubBookColumns.TEXT_POSITION);
    return res;
  }

  /**
   * Amount of book that is already read, percent.
   * Can be {@code null}.
   */
  @Nullable
  public Double getEpubBookPercentile() {
    Double res = getDoubleOrNull(EpubBookColumns.PERCENTILE);
    return res;
  }

  /**
   * Last open time, joda standard datetime format.
   * Can be {@code null}.
   */
  @Nullable
  public String getEpubBookTimeOpened() {
    String res = getStringOrNull(EpubBookColumns.TIME_OPENED);
    return res;
  }

  /**
   * Optional link to fb2_book.
   * Can be {@code null}.
   */
  @Nullable
  public Long getFb2BookId() {
    Long res = getLongOrNull(CachedBookColumns.FB2_BOOK_ID);
    return res;
  }

  /**
   * Get the {@code title} value.
   * Can be {@code null}.
   */
  @Nullable
  public String getFb2BookTitle() {
    String res = getStringOrNull(Fb2BookColumns.TITLE);
    return res;
  }

  /**
   * Path in a storage to read from.
   * Can be {@code null}.
   */
  @Nullable
  public String getFb2BookPath() {
    String res = getStringOrNull(Fb2BookColumns.PATH);
    return res;
  }

  /**
   * Id of a fb2part, from which last read was made.
   * Can be {@code null}.
   */
  @Nullable
  public String getFb2BookCurrentPartId() {
    String res = getStringOrNull(Fb2BookColumns.CURRENT_PART_ID);
    return res;
  }

  /**
   * Position in a parsed preview, on which read was finished.
   * Can be {@code null}.
   */
  @Nullable
  public Integer getFb2BookTextPosition() {
    Integer res = getIntegerOrNull(Fb2BookColumns.TEXT_POSITION);
    return res;
  }

  /**
   * Amount of book that is already read, percent.
   * Can be {@code null}.
   */
  @Nullable
  public Double getFb2BookPercentile() {
    Double res = getDoubleOrNull(Fb2BookColumns.PERCENTILE);
    return res;
  }

  /**
   * Path to .json cache of a table of contents.
   * Can be {@code null}.
   */
  @Nullable
  public String getFb2BookPathToc() {
    String res = getStringOrNull(Fb2BookColumns.PATH_TOC);
    return res;
  }

  /**
   * Last open time, joda standard datetime format.
   * Can be {@code null}.
   */
  @Nullable
  public String getFb2BookTimeOpened() {
    String res = getStringOrNull(Fb2BookColumns.TIME_OPENED);
    return res;
  }

  /**
   * Optional link to txt_book.
   * Can be {@code null}.
   */
  @Nullable
  public Long getTxtBookId() {
    Long res = getLongOrNull(CachedBookColumns.TXT_BOOK_ID);
    return res;
  }

  /**
   * Get the {@code title} value.
   * Can be {@code null}.
   */
  @Nullable
  public String getTxtBookTitle() {
    String res = getStringOrNull(TxtBookColumns.TITLE);
    return res;
  }

  /**
   * Path in a storage to read from.
   * Can be {@code null}.
   */
  @Nullable
  public String getTxtBookPath() {
    String res = getStringOrNull(TxtBookColumns.PATH);
    return res;
  }

  /**
   * Position in a parsed string, on which read was finished.
   * Can be {@code null}.
   */
  @Nullable
  public Integer getTxtBookTextPosition() {
    Integer res = getIntegerOrNull(TxtBookColumns.TEXT_POSITION);
    return res;
  }

  /**
   * Amount of book that is already read, percent.
   * Can be {@code null}.
   */
  @Nullable
  public Double getTxtBookPercentile() {
    Double res = getDoubleOrNull(TxtBookColumns.PERCENTILE);
    return res;
  }

  /**
   * Last open time, joda standard datetime format.
   * Can be {@code null}.
   */
  @Nullable
  public String getTxtBookTimeOpened() {
    String res = getStringOrNull(TxtBookColumns.TIME_OPENED);
    return res;
  }
}
