package com.infmme.readilyapp;

/**
 * Created by infm on 6/25/14. Enjoy ;)
 */
public class Constants {

  public static final String EXTRA_PATH = "path";
  public static final String EXTRA_PATH_ARRAY = "path_array";
  public static final String EXTRA_POSITION = "position";
  public static final String EXTRA_BYTE_POSITION = "byte_position";
  public static final String EXTRA_TYPE = "source_type";
  public static final String EXTRA_HEADER = "header";
  public static final String EXTRA_PERCENT = "percent_left";
  public static final String EXTRA_DB_OPERATION = "db_operation";

  public static final String DEFAULT_WPM = "250";
  public static final String DEFAULT_FONT_SIZE = "18";
  public static final int MAX_WPM = 1200;
  public static final int MIN_WPM = 50;
  public static final int WPM_STEP_PREFERENCES = 10;
  public static final int WPM_STEP_READER = 50;
  public static final int DIALOG_PICKER_WIDTH = 250;
  public static final int DIALOG_PICKER_HEIGHT = 300;
  public static final int MIN_FONT_SIZE = 12;
  public static final int MAX_FONT_SIZE = 30;

  //defines a length, which limits links with description(to reduce working
  // time of regexp)
  public static final int NON_LINK_LENGTH = 500;

  public static final int READER_START_OFFSET = 10;

  public static final int DB_OPERATION_INSERT = 0;
  public static final int DB_OPERATION_DELETE = 1;

  public static final String EXTENSION_TXT = ".txt";
  public static final String EXTENSION_EPUB = ".epub";
  public static final String EXTENSION_FB2 = ".fb2";

  public static final String DEFAULT_ENCODING = "UTF-8";
  public static final int ENCODING_HELPER_BUFFER_SIZE = 1024;

  public static class Preferences {
    public static final String NEWCOMER = "is_anybody_out_there?";

    public static final String TEST = "pref_test";
    public static final String STORAGE = "pref_cache";
    public static final String WPM = "pref_wpm";
    public static final String SHOW_CONTEXT = "pref_showContext";
    public static final String SWIPE = "pref_swipe";
    public static final String TYPEFACE = "pref_typeface";
    public static final String PUNCTUATION_DIFFERS = "pref_punct";
    public static final String STORE_COMPLETE = "pref_again";
    public static final String FEEDBACK = "pref_feedback";
    public static final String FONT_SIZE = "pref_font_size";
    public static final String DARK_THEME = "pref_dark_theme";
    public static final String[] STR_PUNCTUATION_DEFAULTS =
        { "10", "15", "20", "18", "20", "18" };
  }
}
