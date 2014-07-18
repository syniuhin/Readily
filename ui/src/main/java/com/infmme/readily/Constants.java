package com.infmme.readily;

/**
 * Created by infm on 6/25/14. Enjoy ;)
 */
public class Constants {

	public static final String EXTRA_PATH = "path";
	public static final String EXTRA_PATH_ARRAY = "path_array";
	public static final String EXTRA_POSITION = "position";
	public static final String EXTRA_TYPE = "source_type";
	public static final String EXTRA_HEADER = "header";
	public static final String EXTRA_PERCENT = "percent_left";
	public static final String EXTRA_DB_OPERATION = "db_operation";

	public static final String DEFAULT_WPM = "250";
	public static final int MAX_WPM = 1200;
	public static final int MIN_WPM = 50;
	public static final int WPM_STEP_PREFERENCES = 10;
	public static final int WPM_STEP_READER = 50;

	//defines a length, which limits links with description(to reduce working time of regexp)
	public static final int NON_LINK_LENGTH = 500;

	public static final int SECOND = 1000;

	public static final int READER_START_OFFSET = 10;

	public static final int DB_OPERATION_INSERT = 0;
	public static final int DB_OPERATION_DELETE = 1;

	public static final String EXTENSION_TXT = ".txt";
	public static final String EXTENSION_EPUB = ".epub";

	public static class Preferences {
		public static final String NEWCOMER = "is_anybody_out_there?";

		public static final String TEST = "pref_test";
		public static final String STORAGE = "pref_cache";
		public static final String WPM = "pref_wpm";
		public static final String SHOW_CONTEXT = "pref_showContext";
		public static final String SWIPE = "pref_swipe";
		public static final String TYPEFACE = "pref_typeface";
		public static final String PUNCTUATION_DIFFERS = "pref_punct";

		/*
				public static final String PUNCTUATION_DEFAULT = "pref_punctuationDefault";
				public static final String COMA_OR_LONG = "pref_comaOrLong";
				public static final String END_OF_SENTENCE = "pref_endOfSentence";
				public static final String DASH_OR_COLON = "pref_dashOrColon";
				public static final String BEGINNING_OF_PARAGRAPH = "pref_begOfPar";

				public static final String[] STR_PUNCTUATION_PREFS =
						{PUNCTUATION_DEFAULT, COMA_OR_LONG, END_OF_SENTENCE, DASH_OR_COLON,
								BEGINNING_OF_PARAGRAPH};
		*/
		public static final String[] STR_PUNCTUATION_DEFAULTS =
				{"10", "15", "20", "18", "20"};
	}
}
