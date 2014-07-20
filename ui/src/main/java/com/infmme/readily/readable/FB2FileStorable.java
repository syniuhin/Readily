package com.infmme.readily.readable;

import android.content.Context;
import android.text.TextUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

/**
 * created on 7/20/14 by infm. Enjoy ;)
 */
public class FB2FileStorable extends FileStorable {

	public FB2FileStorable(String path){
		type = TYPE_FB2;
		this.path = path;
	}

	@Override
	public void process(Context context){
		path = FileStorable.takePath(context, path);
		if (path == null){
			return;
		}
		text = new StringBuilder(parseFB2(path));
		createRowData(context);
	}

	public String parseFB2(String path){
		try {
			File file = new File(path);
			Document doc = Jsoup.parse(file, "UTF-8");
			title = Jsoup.parse(doc.title()).select("p").text();
			if (TextUtils.isEmpty(title)){ title = doc.title(); }
			return doc.select("p").text();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	protected void makeHeader(){
		if (title.isEmpty() || title.equals("Cover")){ super.makeHeader(); } else { header = title; }
	}
}
