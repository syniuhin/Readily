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
	public static final String EXTRA_PARSER = "parser";
	public static final String EXTRA_DB_OPERATION = "db_operation";

	public static final int READER_SLEEP_IDLE = 500;
	public static final String DEFAULT_WPM = "250";
	public static final int MAX_WPM = 1200;
	public static final int MIN_WPM = 50;
	public static final int SPEEDO_SHOWING_LENGTH = 1500; //time in ms for which speedo becomes visible

	public static final String PREF_STORAGE = "pref_cache";
	public static final String PREF_WPM = "pref_wpm";
	public static final String PREF_SHOW_CONTEXT = "pref_showContext";
	public static final String PREF_SWIPE = "pref_swipe";
	public static final String PREF_TYPEFACE = "pref_typeface";
	public static final String PREF_PUNCTUATION_DIFFERS = "pref_punct";

	public static final String PREF_PUNCTUATION_DEFAULT = "pref_punctuationDefault";
	public static final String PREF_COMA_OR_LONG = "pref_comaOrLong";
	public static final String PREF_END_OF_SENTENCE = "pref_endOfSentence";
	public static final String PREF_DASH_OR_COLON = "pref_dashOrColon";
	public static final String PREF_BEGINNING_OF_PARAGRAPH = "pref_begOfPar";
	public static final String[] STR_PUNCTUATION_PREFS =
			{PREF_PUNCTUATION_DEFAULT, PREF_COMA_OR_LONG, PREF_END_OF_SENTENCE, PREF_DASH_OR_COLON,
					PREF_BEGINNING_OF_PARAGRAPH};
	public static final String[] STR_PUNCTUATION_DEFAULTS =
			{"10", "15", "20", "18", "20"};

	public static final int NON_LINK_LENGTH = 500;
	//defines a length, which limits links with description(to reduce working time of regexp)

	public static final String TEXT_PARSER_READY = "com.infm.readit.TEXTPARSERISREADY";
	public static final String TEXT_PARSER_NOT_READY = "com.infm.readit.TEXTPARSERISNOTREADY";

	public static final int SECOND = 1000;

	public static final int MAX_CACHED_FILES_COUNT = 100;

	public static final int READER_START_OFFSET = 10;

	public static final int DB_OPERATION_INSERT = 0;
	public static final int DB_OPERATION_DELETE = 1;
}
