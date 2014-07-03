package com.infm.readit.service;

import android.app.IntentService;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.infm.readit.Constants;
import com.infm.readit.essential.TextParser;
import com.infm.readit.readable.Readable;
import com.infm.readit.settings.SettingsBundle;

/**
 * Created by infm on 6/26/14. Enjoy ;)
 */
public class TextParserService extends
		IntentService/* implements Serializable */ { //I don't know why, but TextParser.toString() throws an error otherwise

	private static final String LOGTAG = "TextParserService";

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
		Log.d(LOGTAG, "onHandleIntent() called");
		Readable readable = Readable.createReadable(this,
				intent.getExtras());
		readable.process(this); //don't sure that context is necessary, esp. considering level of abstraction..
		TextParser textParser = TextParser.newInstance(readable,
				new SettingsBundle(PreferenceManager.getDefaultSharedPreferences(this)));
		Intent toSend = new Intent(Constants.TEXT_PARSER_READY).
				putExtra(Constants.EXTRA_PARSER, textParser.toString());
		LocalBroadcastManager.getInstance(this).sendBroadcast(toSend);
		Log.d(LOGTAG, "broadcast sent");
	}
}
