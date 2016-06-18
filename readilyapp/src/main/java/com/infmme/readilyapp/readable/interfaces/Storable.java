package com.infmme.readilyapp.readable.interfaces;

import android.content.Context;
import com.infmme.readilyapp.reader.Reader;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created with love, by infm dated on 6/8/16.
 */
public interface Storable extends Serializable {
  boolean isStoredInDb();

  /**
   * Reads currently cached metadata, last saved position etc.
   */
  void readFromDb();

  Storable prepareForStoringSync(Reader reader);

  /**
   * Processes this storable in order to save it to a db (e.g. fetches cover
   * image).
   */
  void beforeStoringToDb();

  /**
   * Stores (creates or updates) current state to database.
   */
  void storeToDb();

  /**
   * Stores current state to file.
   */
  void storeToFile() throws IOException;

  /**
   * Reads from filesystem.
   */
  Storable readFromFile() throws IOException;

  /**
   * Setter needed since we have transient Context in each implementor and it
   * will be lost during serialization.
   */
  void setContext(Context context);

  String getPath();

  void setPath(String path);

  String getTitle();

  void setTitle(String title);
}
