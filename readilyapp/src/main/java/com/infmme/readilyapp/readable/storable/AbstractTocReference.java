package com.infmme.readilyapp.readable.storable;

/**
 * Created with love, by infm dated on 6/6/16.
 */

import java.util.List;

/**
 * Defines abstraction for a reference to table of contents of a book.
 */
public interface AbstractTocReference {
  String getTitle();

  String getPreview();

  double getPercentile();

  List<? extends AbstractTocReference> getChildren();
}
