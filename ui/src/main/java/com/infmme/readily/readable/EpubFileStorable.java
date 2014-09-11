package com.infmme.readily.readable;

import android.content.Context;
import android.text.TextUtils;
import com.infmme.readily.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * Created by infm on 7/2/14. Enjoy ;)
 */
public class EpubFileStorable extends FileStorable {

	private Book book;
	private List<Resource> resources;
	private int index;
	public static final int BUFFER_SIZE = 1024;

	public EpubFileStorable(String path){
		type = TYPE_EPUB;
		this.path = path;
	}

	public EpubFileStorable(EpubFileStorable that){
		super(that);
		type = TYPE_EPUB;
		book = that.getBook();
		List<Resource> oldResources = that.getResources();
		int oldIndex = that.getIndex();
		resources = oldResources.subList(oldIndex, oldResources.size());
		index = 0;
		inputDataLength = 0;
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
			File file = new File(path);
			fileSize = file.length();
			encoding = Constants.DEFAULT_ENCODING;

			book = (new EpubReader()).readEpubLazy(path, encoding);
			resources = book.getContents();

			createRowData(context);
			if (bytePosition > 0){
				long passed = 0;
				while (index < resources.size() && passed < bytePosition)
					passed += resources.get(index++).getSize();
			}
			processed = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void readData(){
		int cycleCount = 0;
		while (cycleCount < 5){
			setText("");
			try {
				while (text.length() < BUFFER_SIZE && index < resources.size()){
					Resource currentResource = resources.get(index++);
					inputDataLength += currentResource.getSize();
					text.append(new String(currentResource.getData()));
				}
			} catch (IOException e){
				e.printStackTrace();
			}
			text = new StringBuilder(parseEpub(text.toString()));
			if (index >= resources.size() || (!TextUtils.isEmpty(text) && doesHaveLetters(text))){
				break;
			}
			cycleCount++;
		}
	}

	@Override
	public Readable getNext(){
		return prepareNext(new EpubFileStorable(this));
	}

	private String parseEpub(String text){
		try {
			Document doc = Jsoup.parse(text);
			if (TextUtils.isEmpty(header)){ header = doc.select("book-title").text(); }
			return doc.select("p").text();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	protected void makeHeader(){
		if (TextUtils.isEmpty(title)){ super.makeHeader(); } else { header = title; }
	}
}
