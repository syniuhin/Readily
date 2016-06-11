package com.infmme.readilyapp.readable.interfaces;

import java.io.IOException;

/**
 * Created with love, by infm dated on 6/8/16.
 */

public interface Storable {
  boolean isStoredInDb();

  /**
   * Reads currently cached metadata, last saved position etc.
   */
  void readFromDb();

  // void prepareForStoring(Reader reader);
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
  Storable readFromFile() throws IOException;

  String getPath();

  void setPath(String path);
}
