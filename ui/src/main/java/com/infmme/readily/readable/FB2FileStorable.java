package com.infmme.readily.readable;

import android.content.Context;
import android.text.TextUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * created on 7/20/14 by infm. Enjoy ;)
 */
public class FB2FileStorable extends FileStorable {

	private XmlPullParser xmlParser;
	private int openedTags = 0;

	public FB2FileStorable(String path){
		type = TYPE_FB2;
		this.path = path;
	}

	public FB2FileStorable(FB2FileStorable that){
		super(that);
		xmlParser = that.getXmlParser();
		openedTags = that.getOpenedTags();
	}

	public XmlPullParser getXmlParser(){
		return xmlParser;
	}

	public int getOpenedTags(){
		return openedTags;
	}

	@Override
	public void process(Context context){
		path = FileStorable.takePath(context, path);
		if (path == null){
			return;
		}
		try {
			fileReader = new FileReader(path);

			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			xmlParser = factory.newPullParser();
			xmlParser.setInput(fileReader);

			createRowData(context);
			processed = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void readData(){
		setText("");
		try {
			if (xmlParser == null){ return; }
			int eventType = xmlParser.getEventType();
			boolean nextTitle = false;
			boolean needTitle = TextUtils.isEmpty(title);

			while (eventType != XmlPullParser.END_DOCUMENT && text.length() < BUFFER_SIZE){
				String eventName = xmlParser.getName();
				if (!TextUtils.isEmpty(eventName)){
					if ("p".equals(eventName)){
						switch (eventType){
							case XmlPullParser.START_TAG:
								openedTags++;
								break;
							case XmlPullParser.END_TAG:
								openedTags--;
								break;
						}
					} else if (needTitle && "title".equals(eventName)){
						needTitle = false;
						nextTitle = true;
					}
				} else if (eventType == XmlPullParser.TEXT){
					if (nextTitle){
						title = xmlParser.getText();
						nextTitle = false;
					}
					if (openedTags > 0)
						text.append(xmlParser.getText()).append(" ");
				}
				eventType = xmlParser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Readable getNext(){
		FB2FileStorable result = new FB2FileStorable(this);
		result.readData();
		result.cutLastWord();
		result.insertLastWord(lastWord);
		return result;
	}

	@Override
	protected void makeHeader(){
		if (TextUtils.isEmpty(title) || title.equals("Cover")){
			super.makeHeader();
		} else {
			header = title;
		}
	}
}
