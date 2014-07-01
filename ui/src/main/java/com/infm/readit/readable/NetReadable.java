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
public class NetReadable extends Storable {

    private static final String LOGTAG = "NetReadable";
    private String link;

    public NetReadable(String link){
        super();
        this.link = link;
        type = TYPE_NET;
    }

    public static void createCacheFile(Context context, String path, String text){
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.PREF_CACHE, true)){
            File cacheFile = new File(path);
            try {
                cacheFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(cacheFile);
                fos.write(text.getBytes());
                fos.close();
                Log.d(LOGTAG, "caching performed successfully");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        path = context.getCacheDir() + "/" + cleanFileName(title) + ".txt";
        rowData = takeRowData(context);
        if (rowData != null)
            position = rowData.getPosition();
        else
            createCacheFile(context, path, text.toString());
    }

    private String parseArticle(String url){
        HtmlFetcher fetcher = new HtmlFetcher();
        JResult res = null;
        try {
            res = fetcher.fetchAndExtract(url, 10000, true); //I don't know what it means, need to read docs/source
            title = res.getTitle();
            return res.getText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void makeHeader(){ header = title; }
}
