package com.infm.readit.readable;

import android.content.Context;
import android.text.TextUtils;

import de.jetwick.snacktory.HtmlFetcher;
import de.jetwick.snacktory.JResult;

/**
 * Created by infm on 6/13/14. Enjoy ;)
 */
public class NetReadable extends Readable {

    private String link;

    public NetReadable(String link){
        super();
        this.link = link;
        setTextType("text/plain");
    }

    @Override
    public String getLink(){
        return link;
    }

    @Override
    public void setLink(String link){
        this.link = link;
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
        if (!TextUtils.isEmpty(link)){
            this.text = new StringBuilder(parseArticle(link));
        } else throw new IllegalArgumentException("Wrong Readable object created");
    }

    private String parseArticle(String url){
        HtmlFetcher fetcher = new HtmlFetcher();
        JResult res = null;
        try {
            res = fetcher.fetchAndExtract(url, 10000, true); //I don't know what it means, need to read docs/code
            return res.getTitle() + " . " + res.getText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
