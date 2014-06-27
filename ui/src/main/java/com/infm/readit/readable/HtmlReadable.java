package com.infm.readit.readable;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.infm.readit.Constants;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.concurrent.ExecutionException;

import de.jetwick.snacktory.HtmlFetcher;
import de.jetwick.snacktory.JResult;

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
        String text = this.text.toString();
        if (!TextUtils.isEmpty(link))
            try {
                text = new ArticleHtmlParser().execute(link).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        else if ("text/html".equals(textType)) {
            try {
                Document doc = new InnerHtmlParser().execute(text).get();
                text = doc.title() + doc.select("p").text();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else if (!"text/plain".equals(textType)) {
            Log.e(LOGTAG, "Wrong text type");
            return;
        }

        this.text = new StringBuilder(text);
    }

    private class InnerHtmlParser extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... params) {
            Document doc = null;
            try {
                doc = Jsoup.parse(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return doc;
        }
    }

    private class ArticleHtmlParser extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            HtmlFetcher fetcher = new HtmlFetcher();
            JResult res = null;
            try {
                res = fetcher.fetchAndExtract(url, 10000, true);
                return res.getTitle() + " . " + res.getText();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }
    }
}
