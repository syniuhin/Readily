package cmc.readit.rsvp_reader.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;

/**
 * infm : 17/05/14. Enjoy it ;)
 */
public class Reader implements Runnable, SharedPreferences.OnSharedPreferenceChangeListener {
    private View mReaderLayout;
    private PrepareForView mPrep;
    private Handler mHandler;
    private Activity mActivity;
    private int cancelled;
    private int mPos = 0;
    private double SPM;
    private boolean completed = false;

    public Reader(Handler handler, View readerLayout, Activity activity, PrepareForView prep, int pos) {
        mHandler = handler;
        mReaderLayout = readerLayout;
        mActivity = activity;
        mPrep = prep;
        mPos = pos;

        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(mActivity);
        PreferenceManager.getDefaultSharedPreferences(mActivity).registerOnSharedPreferenceChangeListener(this);

        final int WPM = Integer.parseInt(sPref.getString(SettingsActivity.PREF_WPM, "250"));
        SPM = 60 * 1f / WPM;
    }

    public int getPosition() {
        return mPos;
    }

    public void setPosition(int _pos) {
        mPos = _pos;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isCancelled() {
        return cancelled % 2 == 1;
    }

    public void incCancelled() {
        cancelled++;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (SettingsActivity.PREF_WPM.equals(key))
            SPM = 60f / Integer.parseInt(sharedPreferences.getString(key, "250"));
    }

    @Override
    public void run() {
        int wlen = mPrep.getParser().getReadable().getWordList().size();
        int i = mPos;
        if (i < wlen) {
            completed = false;
            if (!isCancelled()) {
                mPrep.updateView(mReaderLayout, i % wlen);
                mHandler.postDelayed(this, mPrep.getParser().getReadable().getDelayList().get(mPos++ % wlen) * Math.round(100 * SPM));
            } else {
                mHandler.postDelayed(this, 500);
            }
        } else {
            completed = true;
            cancelled = 1;
        }
    }
}
