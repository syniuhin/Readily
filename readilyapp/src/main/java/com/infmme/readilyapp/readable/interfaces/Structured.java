package com.infmme.readilyapp.readable.interfaces;

import com.infmme.readilyapp.readable.structure.AbstractTocReference;

import java.util.List;

/**
 * Created with love, by infm dated on 6/14/16.
 */

public interface Structured {
  List<? extends AbstractTocReference> getTableOfContents();

  String getCurrentId();

  void setCurrentId(String id);

  int getCurrentPosition();

  void setCurrentPosition(int position);
}
