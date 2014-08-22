package com.infmme.readily.readable;

import android.content.Context;
import android.text.TextUtils;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

/**
 * Created by infm on 7/2/14. Enjoy ;)
 */
public class EpubFileStorable extends FileStorable {

	private Book book;
	private List<Resource> resources;
	private int index;

	public EpubFileStorable(String path){
		type = TYPE_EPUB;
		this.path = path;
	}

	public EpubFileStorable(EpubFileStorable that){
		super(that);
		book = that.getBook();
		resources = that.getResources();
		index = that.getIndex();
	}

	public Book getBook(){
		return book;
	}

	public List<Resource> getResources(){
		return resources;
	}

	public int getIndex(){
		return index;
	}

	public void process(Context context){
		try {
			path = FileStorable.takePath(context, path);
			if (path == null){
				return;
			}
			book = (new EpubReader()).readEpubLazy(path, "UTF-8");
			resources = book.getContents();

			createRowData(context);
			processed = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void readData(){
		setText("");
		try {
			while (text.length() < BUFFER_SIZE && index < resources.size()){
				text.append(new String(resources.get(index++).getData()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		text = new StringBuilder(parseEpub(text.toString()));
		if (index < resources.size() && TextUtils.isEmpty(text)){ readData(); }
	}

	@Override
	public Readable getNext(){
		EpubFileStorable result = new EpubFileStorable(this);
		result.readData();
		result.cutLastWord();
		result.insertLastWord(lastWord);
		result.copyListSuffix(this);
		return result;
	}

	private String parseEpub(String text){
		try {
			Document doc = Jsoup.parse(text);
			if (TextUtils.isEmpty(header)){ header = doc.title(); }
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
