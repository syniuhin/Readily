package cmc.readit.rsvp_reader.ui.readable;

import android.content.Context;

import cmc.readit.rsvp_reader.ui.R;

/**
 * Created by infm on 6/13/14. Enjoy ;)
 */
public class TestSettingsText extends cmc.readit.rsvp_reader.ui.readable.Readable {
    public TestSettingsText(Context context) {
        setText(context.getResources().getString(R.string.sample_text));
        setTextType("text/plain");
        setPath(null);
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
