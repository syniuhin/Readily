package com.infm.readit.util;

import android.app.Activity;
import com.flurry.android.FlurryAgent;

/**
 * created on 7/15/14 by infm. Enjoy ;)
 */
public class BaseActivity extends Activity {
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
