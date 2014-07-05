package com.infm.readit.service;

import android.app.IntentService;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.infm.readit.Constants;
import com.infm.readit.essential.TextParser;
import com.infm.readit.readable.Readable;
import com.infm.readit.settings.SettingsBundle;

/**
 * Created by infm on 6/26/14. Enjoy ;)
 */
public class TextParserService extends IntentService {

	private static final String LOGTAG = "TextParserService";

	public static final int RESULT_CODE_OK = 0;
	public static final int RESULT_CODE_EMPTY_CLIPBOARD = 1;
	public static final int RESULT_CODE_WRONG_EXT = 2;
	public static final int RESULT_CODE_WTF = 3;
	public static final int RESULT_CODE_CANT_FETCH = 4;

	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 *
	 * @param name Used to name the worker thread, important only for debugging.
	 */
	public TextParserService(String name){
		super(name);
	}

	public TextParserService(){
		super("TextParserService");
	}

	@Override
	protected void onHandleIntent(Intent intent){
		Log.d(LOGTAG, "onHandleIntent() called, incoming extras: " +
				intent.getStringExtra(Constants.EXTRA_PATH) + " " +
				intent.getLongExtra(Constants.EXTRA_UNIQUE_ID, 0));
		Readable readable = Readable.createReadable(this,
				intent.getExtras());
		readable.process(this); //don't sure that context is necessary, esp. considering level of abstraction..
		TextParser textParser = TextParser.newInstance(readable,
				new SettingsBundle(PreferenceManager.getDefaultSharedPreferences(this)));
		Intent toSend = new Intent(Constants.TEXT_PARSER_READY).
				putExtra(Constants.EXTRA_PARSER, textParser.toString()).
				putExtra(Constants.EXTRA_PARSER_RESULT_CODE, checkResult(textParser)).
				putExtra(Constants.EXTRA_UNIQUE_ID, intent.getLongExtra(Constants.EXTRA_UNIQUE_ID, 0));
		LocalBroadcastManager.getInstance(this).sendBroadcast(toSend);
		Log.d(LOGTAG, "broadcast sent, id: " +
				toSend.getLongExtra(Constants.EXTRA_UNIQUE_ID, 0));
	}

	private int checkResult(TextParser textParser){
		int toReturn;
		if (textParser != null && textParser.getReadable() != null){
			if (TextUtils.isEmpty(textParser.getReadable().getText()) ||
					textParser.getReadable().getWordList().isEmpty() ||
					textParser.getReadable().getWordList().size() < 2 ||
					textParser.getReadable().getProcessFailed()){
				switch (textParser.getReadable().getType()){
					case Readable.TYPE_CLIPBOARD:
						Log.v(LOGTAG, "checkResult(), clipboard");
						toReturn = RESULT_CODE_EMPTY_CLIPBOARD;
						break;
					case Readable.TYPE_FILE:
						Log.v(LOGTAG, "checkResult(), file");
						toReturn = RESULT_CODE_WRONG_EXT;
						break;
					case Readable.TYPE_TXT:
						Log.v(LOGTAG, "checkResult(), txt");
						toReturn = RESULT_CODE_WRONG_EXT;
						break;
					case Readable.TYPE_EPUB:
						Log.v(LOGTAG, "checkResult(), epub");
						toReturn = RESULT_CODE_WRONG_EXT;
						break;
					case Readable.TYPE_NET:
						Log.v(LOGTAG, "checkResult(), net");
						toReturn = RESULT_CODE_CANT_FETCH;
						break;
					default:
						Log.v(LOGTAG, "checkResult(), default");
						toReturn = RESULT_CODE_WTF;
						break;
				}
			} else {
				Log.v(LOGTAG, "checkResult(), not null");
				toReturn = RESULT_CODE_OK;
			}
		} else {
			Log.w(LOGTAG, "checkResult(), null");
			toReturn = RESULT_CODE_WTF;
		}
		return toReturn;
	}
}
