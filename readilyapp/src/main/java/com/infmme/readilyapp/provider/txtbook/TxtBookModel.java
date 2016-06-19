package com.infmme.readilyapp.provider.txtbook;

import com.infmme.readilyapp.provider.base.BaseModel;

/**
 * Txt book, which was opened at least once.
 */
public interface TxtBookModel extends BaseModel {

  /**
   * Byte position of block in a file, either FB2Part or simple chunk read
   * continuously.
   */
  int getBytePosition();
}
