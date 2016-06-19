package com.infmme.readilyapp.provider.epubbook;

import android.support.annotation.NonNull;
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
   * Title of a resource, from which last read was made.
   */
  String getCurrentResourceTitle();
}
