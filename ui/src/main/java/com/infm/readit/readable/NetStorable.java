package com.infm.readit.readable;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import de.jetwick.snacktory.HtmlFetcher;
import de.jetwick.snacktory.JResult;

/**
 * Created by infm on 6/13/14. Enjoy ;)
 */
public class NetStorable extends Storable {

	private static final String LOGTAG = "NetStorable";
	private String link;

	public NetStorable(String link){
		super();
		this.link = link;
		type = TYPE_NET;
	}

	public String getLink(){ return link; }

	public void setLink(String link){ this.link = link; }

	@Override
	public void process(Context context){
		if (!TextUtils.isEmpty(link)){
			if (isNetworkAvailable(context)){
				text = new StringBuilder(parseArticle(link));
			} else {
				processFailed = true;
				return;
			}
		} else {
			processFailed = true;
			return;
		}
		path = context.getFilesDir() + "/" + cleanFileName(title) + ".txt";
		rowData = takeRowData(context);
		if (rowData != null)
			position = rowData.getPosition();
		else
			createStorageFile(context, path, text.toString());
	}

	private String parseArticle(String url){
		HtmlFetcher fetcher = new HtmlFetcher();
		JResult res = null;
		try {
			res = fetcher.fetchAndExtract(url, 10000, true); //I don't know what it means, need to read docs/source
			title = res.getTitle();
			return title + " | " + res.getText();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	protected void makeHeader(){ header = title; }

	private boolean isNetworkAvailable(Context context){
		ConnectivityManager connectivityManager
				= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
