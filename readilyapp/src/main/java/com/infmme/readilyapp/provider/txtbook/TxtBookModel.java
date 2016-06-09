package com.infmme.readilyapp.provider.txtbook;

import com.infmme.readilyapp.provider.base.BaseModel;

/**
 * Txt book, which was opened at least once.
 */
public interface TxtBookModel extends BaseModel {

  /**
   * Position in a parsed string, on which read was finished.
   */
  int getTextPosition();
}
