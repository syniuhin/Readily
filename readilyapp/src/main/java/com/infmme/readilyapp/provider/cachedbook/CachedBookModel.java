package com.infmme.readilyapp.provider.cachedbook;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.infmme.readilyapp.provider.base.BaseModel;

/**
 * General book, which was opened at least once.
 */
public interface CachedBookModel extends BaseModel {

  /**
   * Get the {@code title} value.
   * Cannot be {@code null}.
   */
  @NonNull
  String getTitle();

  /**
   * Path in a storage to read from.
   * Cannot be {@code null}.
   */
  @NonNull
  String getPath();

  /**
   * Position in word list.
   */
  int getTextPosition();

  /**
   * Amount of book that is already read, percent.
   */
  double getPercentile();

  /**
   * Last open time, joda standard datetime format.
   * Cannot be {@code null}.
   */
  @NonNull
  String getTimeOpened();

  /**
   * Uri of the cover image.
   * Can be {@code null}.
   */
  @Nullable
  String getCoverImageUri();

  /**
   * Mean color of the cover image in form of argb.
   * Can be {@code null}.
   */
  @Nullable
  Integer getCoverImageMean();

  /**
   * Optional link to epub_book.
   * Can be {@code null}.
   */
  @Nullable
  Long getEpubBookId();

  /**
   * Optional link to fb2_book.
   * Can be {@code null}.
   */
  @Nullable
  Long getFb2BookId();

  /**
   * Optional link to txt_book.
   * Can be {@code null}.
   */
  @Nullable
  Long getTxtBookId();

  /**
   * Link to an extended information record.
   * Can be {@code null}.
   */
  @Nullable
  Long getInfoId();
}
