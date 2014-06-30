package com.infm.readit.readable;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.infm.readit.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.jetwick.snacktory.HtmlFetcher;
import de.jetwick.snacktory.JResult;

/**
 * Created by infm on 6/13/14. Enjoy ;)
 */
public class NetReadable extends Readable {

    private static final String LOGTAG = "NetReadable";
    private String link;

    public NetReadable(String link){
        super();
        this.link = link;
        setTextType("text/plain");
    }

    public String getLink(){ return link; }

    public void setLink(String link){ this.link = link; }

    @Override
    public void process(Context context){
        if (!TextUtils.isEmpty(link)){
            text = new StringBuilder(parseArticle(link));
        } else {
            throw new IllegalArgumentException("Wrong Readable object created");
        }
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.PREF_CACHE, true))
            cacheText(context);
    }

    private void cacheText(Context context){
        File cacheFile;
        String cacheDir = context.getCacheDir().toString();
        do {
            int uniqueId = (int) (Math.random() * Constants.MAX_CACHED_FILES_COUNT);
            cacheFile = new File(cacheDir, uniqueId + ".txt");
        }
        while (cacheFile.exists());
        try {
            FileOutputStream fos = new FileOutputStream(cacheFile);
            fos.write(text.toString().getBytes());
            fos.close();
            path = cacheFile.getPath();
            Log.d(LOGTAG, "caching performed successfully, path: " + path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String parseArticle(String url){
        HtmlFetcher fetcher = new HtmlFetcher();
        JResult res = null;
        try {
            res = fetcher.fetchAndExtract(url, 10000, true); //I don't know what it means, need to read docs/source
            return res.getTitle() + " # " + res.getText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
