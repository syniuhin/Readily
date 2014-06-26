package com.infm.readit;

/**
 * Created by infm on 6/25/14. Enjoy ;)
 */
public class Constants {
    public static final String EXTRA_PATH = "path";
    public static final String EXTRA_ROWID = "_id";
    public static final String EXTRA_POSITION = "position";
    public static final String EXTRA_TYPE = "source_type";
    public static final String EXTRA_TEXT = "text";
    public static final String EXTRA_HEADER = "header";
    public static final String EXTRA_PERCENT = "percent_left";

    public static final Integer READER_SLEEP_IDLE = 500;
    public static final String DEFAULT_WPM = "250";

    public static final String PREF_SAVE_CLIPBOARD = "pref_save_clipboard";
    public static final String PREF_WPM = "pref_wpm";
    public static final String PREF_PUNCTUATION_DIFFERS = "pref_punct";
    public static final String PREF_COMA_OR_LONG = "pref_comaOrLong";
    public static final String PREF_END_OF_SENTENCE = "pref_endOfSentence";
    public static final String PREF_DASH_OR_COLON = "pref_dashOrColon";
    public static final String PREF_BEGINNING_OF_PARAGRAPH = "pref_begOfPar";
    public static final String PREF_SHOW_CONTEXT = "pref_showContext";
    public static final String PREF_SWIPE = "pref_swipe";
    public static final String PREF_TYPEFACE = "pref_typeface";

    public static final Integer NON_LINK_LENGTH = 500; //defines a length, which limits links with description(to reduce working time of regexp)
}
