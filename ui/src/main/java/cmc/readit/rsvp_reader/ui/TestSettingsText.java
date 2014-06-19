package cmc.readit.rsvp_reader.ui;

import android.content.Context;

/**
 * Created by infm on 6/13/14. Enjoy ;)
 */
public class TestSettingsText extends Readable {
    public TestSettingsText(Context context) {
        setText(context.getResources().getString(R.string.sample_text));
        setTextType("text/plain");
        setPath(null);
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
