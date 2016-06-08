package com.infmme.readilyapp.provider.epubbook;

import android.support.annotation.NonNull;
import com.infmme.readilyapp.provider.base.BaseModel;

/**
 * Epub book, which was opened at least once.
 */
public interface EpubBookModel extends BaseModel {

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
   * Id of a resource, from which last read was made.
   * Cannot be {@code null}.
   */
  @NonNull
  String getCurrentResourceId();

  /**
   * Position in a parsed string, on which read was finished.
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
}
