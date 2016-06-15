package com.infmme.readilyapp.provider.epubbook;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.infmme.readilyapp.provider.base.BaseModel;

/**
 * Epub book, which was opened at least once.
 */
public interface EpubBookModel extends BaseModel {

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
   * Uri of the cover image.
   * Can be {@code null}.
   */
  @Nullable
  String getCoverImageUri();
}
