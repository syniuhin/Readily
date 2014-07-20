package com.infmme.readily.readable;

import android.content.Context;
import android.text.TextUtils;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by infm on 7/2/14. Enjoy ;)
 */
public class EpubFileStorable extends FileStorable {

	public EpubFileStorable(String path){
		type = TYPE_EPUB;
		this.path = path;
	}

	public void process(Context context){
		try {
			path = FileStorable.takePath(context, path);
			if (path == null){
				return;
			}
			Book book = (new EpubReader()).readEpub(new FileInputStream(path));
			for (Resource res : book.getContents()){ text.append(new String(res.getData())); }
			text = new StringBuilder(parseEpub(text.toString())); //NullPointerEx can be thrown

			createRowData(context);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String parseEpub(String text){
		try {
			Document doc = Jsoup.parse(text);
			title = doc.title();
			return doc.select("p").text();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	protected void makeHeader(){
		if (TextUtils.isEmpty(title) || title.equals("Cover")){ super.makeHeader(); } else { header = title; }
	}
}
