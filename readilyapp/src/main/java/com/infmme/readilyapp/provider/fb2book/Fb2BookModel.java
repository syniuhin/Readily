package com.infmme.readilyapp.provider.fb2book;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.infmme.readilyapp.provider.base.BaseModel;

/**
 * FB2 book, which was opened at least once.
 */
public interface Fb2BookModel extends BaseModel {

  /**
   * Tells if this fb2 was fully processed (i.e. table of contents, cover
   * image).
   */
  boolean getFullyProcessed();

  /**
   * Byte position of block in a file, either FB2Part or simple chunk read
   * continuously.
   */
  int getBytePosition();

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
   * Path to .json cache of a table of contents.
   * Can be {@code null}.
   */
  @Nullable
  String getPathToc();
}
