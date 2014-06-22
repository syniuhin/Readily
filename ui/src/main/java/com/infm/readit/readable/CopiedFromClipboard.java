package com.infm.readit.readable;

import android.content.Context;
import android.preference.PreferenceManager;

import com.infm.readit.SettingsActivity;

/**
 * Created by infm on 6/12/14. Enjoy ;)
 */
public class CopiedFromClipboard extends Readable {
    Context context;
    Boolean gonnaSaved;

    public CopiedFromClipboard(Context context) {
        this.context = context;
        setTextType("text/plain");
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SettingsActivity.PREF_SAVE_CLIPBOARD, false)) {
            gonnaSaved = true;
            //need to define the path to save
        }
    }

    @Override
    public String getLink() {
        return null;
    }

    @Override
    public void setLink(String link) {

    }

    @Override
    public ChunkData getChunkData() {
        return null;
    }

    @Override
    public void setChunkData(ChunkData data) {

    }
}
