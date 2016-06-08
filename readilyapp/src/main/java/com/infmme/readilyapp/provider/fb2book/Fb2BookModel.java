package com.infmme.readilyapp.provider.fb2book;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.infmme.readilyapp.provider.base.BaseModel;

/**
 * FB2 book, which was opened at least once.
 */
public interface Fb2BookModel extends BaseModel {

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
   * Id of a fb2part, from which last read was made.
   * Cannot be {@code null}.
   */
  @NonNull
  String getCurrentPartId();

  /**
   * Position in a parsed preview, on which read was finished.
   */
  int getTextPosition();

  /**
   * Amount of book that is already read, percent.
   */
  double getPercentile();

  /**
   * Path to .json cache of a table of contents.
   * Can be {@code null}.
   */
  @Nullable
  String getPathToc();

  /**
   * Last open time, joda standard datetime format.
   * Cannot be {@code null}.
   */
  @NonNull
  String getTimeOpened();
}
