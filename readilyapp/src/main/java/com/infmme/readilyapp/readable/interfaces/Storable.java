package com.infmme.readilyapp.readable.interfaces;

/**
 * Created with love, by infm dated on 6/8/16.
 */

public interface Storable {
  /**
   * Stores (creates or updates) current state to database.
   */
  void storeToDb();

  /**
   * Stores current state to file.
   */
  void storeToFile();

  /**
   * Reads from filesystem.
   */
  Storable readFromFile();
}
