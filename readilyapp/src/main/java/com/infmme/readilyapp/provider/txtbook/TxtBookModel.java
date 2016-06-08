package com.infmme.readilyapp.provider.txtbook;

import android.support.annotation.NonNull;
import com.infmme.readilyapp.provider.base.BaseModel;

/**
 * Txt book, which was opened at least once.
 */
public interface TxtBookModel extends BaseModel {

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
