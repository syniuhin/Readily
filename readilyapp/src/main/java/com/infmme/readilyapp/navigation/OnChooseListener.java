package com.infmme.readilyapp.navigation;

import com.infmme.readilyapp.readable.interfaces.AbstractTocReference;

/**
 * Created with love, by infm dated on 6/14/16.
 */

public interface OnChooseListener {
  void chooseItem(AbstractTocReference tocReference, int textPosition);
}
