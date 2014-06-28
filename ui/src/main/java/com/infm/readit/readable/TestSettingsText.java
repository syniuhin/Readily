package com.infm.readit.readable;

import android.content.Context;

/**
 * Created by infm on 6/13/14. Enjoy ;)
 */
public class TestSettingsText extends Readable {

    public TestSettingsText(){
        super();
        setTextType("text/plain");
        setPath(null);
    }

    @Override
    public String getLink(){
        return null;
    }

    @Override
    public void setLink(String link){

    }

    @Override
    public ChunkData getChunkData(){
        return null;
    }

    @Override
    public void setChunkData(ChunkData data){

    }

    @Override
    public void process(Context context){

    }
}
