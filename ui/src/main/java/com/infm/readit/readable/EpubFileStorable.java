package com.infm.readit.readable;

import android.content.Context;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.FileInputStream;
import java.io.IOException;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

/**
 * Created by infm on 7/2/14. Enjoy ;)
 */
public class EpubFileStorable extends FileStorable {

	private static final String LOGTAG = "EpubFileStorable";

	public EpubFileStorable(String path){
		extension = "epub";
		type = TYPE_EPUB;
		this.path = path;
	}

	public void process(Context context){
		Log.d(LOGTAG, "process() is called, type: epub");
		try {
			path = FileStorable.takePath(context, path);
			if (path == null){
				Log.d(LOGTAG, "path is null");
				return;
			}
			Book book = (new EpubReader()).readEpub(new FileInputStream(path));
			for (Resource res : book.getContents())
				text.append(new String(res.getData()));
			text = new StringBuilder(parseEpub(text.toString())); //NullPointerEx can be thrown

			createRowData(context);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String parseEpub(String text){
		Document doc = null;
		try {
			doc = Jsoup.parse(text);
			title = doc.title();
			return doc.select("p").text();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	protected void makeHeader(){
		if (title.isEmpty() || title.equals("Cover"))
			super.makeHeader();
		else
			header = title;
	}
}
