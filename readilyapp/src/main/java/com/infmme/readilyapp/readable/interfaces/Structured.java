package com.infmme.readilyapp.readable.interfaces;

import java.util.List;

/**
 * Created with love, by infm dated on 6/14/16.
 */

public interface Structured {
  List<? extends AbstractTocReference> getTableOfContents();

  void setCurrentTocReference(AbstractTocReference tocReference);

  String getCurrentPartId();

  String getCurrentPartTitle();
}
