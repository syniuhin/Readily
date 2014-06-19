package cmc.readit.rsvp_reader.ui;

import android.content.Context;
import android.preference.PreferenceManager;

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
    protected String getLink() {
        return null;
    }

    @Override
    protected void setLink(String link) {

    }

    @Override
    protected ChunkData getChunkData() {
        return null;
    }

    @Override
    protected void setChunkData(ChunkData data) {

    }
}
