package com.infm.readit.readable;

import android.content.Context;
import android.preference.PreferenceManager;

import com.infm.readit.Constants;

/**
 * Created by infm on 6/13/14. Enjoy ;)
 */
public class HtmlReadable extends Readable {

    private String link;

    public HtmlReadable(String link) {
        super();
        this.link = link;
        setTextType("text/plain");
    }

    @Override
    public String getLink() {
        return link;
    }

    @Override
    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public ChunkData getChunkData() {
        return null;
    }

    @Override
    public void setChunkData(ChunkData data) {

    }

    @Override
    public void process(Context context) {

    }
}
