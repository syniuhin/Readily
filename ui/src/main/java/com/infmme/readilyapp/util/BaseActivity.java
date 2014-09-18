package com.infmme.readilyapp.util;

import android.support.v7.app.ActionBarActivity;
import com.flurry.android.FlurryAgent;

/**
 * created on 7/15/14 by infm. Enjoy ;)
 */
public class BaseActivity extends ActionBarActivity {
	@Override
	protected void onStart(){
		super.onStart();
		FlurryAgent.onStartSession(this, "6CNDCMYSWHYDFYDVKDMD");
	}

	@Override
	protected void onStop(){
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
}
