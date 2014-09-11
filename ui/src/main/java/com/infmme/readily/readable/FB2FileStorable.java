package com.infmme.readily.readable;

import android.content.Context;
import android.text.TextUtils;
import com.infmme.readily.Constants;
import com.infmme.readily.xmlparser.XMLEvent;
import com.infmme.readily.xmlparser.XMLParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * created on 7/20/14 by infm. Enjoy ;)
 */
public class FB2FileStorable extends FileStorable {

	private XMLParser parser;

	public FB2FileStorable(String path){
		type = TYPE_FB2;
		this.path = path;
	}

	public FB2FileStorable(FB2FileStorable that){
		super(that);
		type = TYPE_FB2;
		parser = that.getParser();
	}

	public XMLParser getParser() {
		return parser;
	}

	@Override
	public void process(Context context){
		path = FileStorable.takePath(context, path);
		if (path == null){
			return;
		}
		try {
			File file = new File(path);
			FileInputStream encodingHelper = new FileInputStream(file);
			encoding = guessCharset(encodingHelper);
			encodingHelper.close();

			fileInputStream = new FileInputStream(file);
			fileSize = file.length();
			createRowData(context);
			if (bytePosition > 0)
				fileInputStream.skip(bytePosition);

			parser = new XMLParser();
			parser.setInput(fileInputStream, encoding);
			processed = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void readData(){
		setText("");
		try {
			if (parser == null) { return; }
			XMLEvent event = parser.next();
			int eventType = event.getType();
			boolean needTitle = TextUtils.isEmpty(title);

			while (eventType != XMLParser.DOCUMENT_CLOSE && text.length() < BUFFER_SIZE){
				if (eventType == XMLParser.CONTENT){
					String contentType = event.getContentType();
					if (!TextUtils.isEmpty(contentType)){
						if (needTitle && contentType.equals("book-title"))
							title = event.getContent();
						if (contentType.equals("p"))
							text.append(event.getContent());
					} else { //TODO: handle this situation carefully
						text.append(event.getContent());
					}
					text.append(" ");
				}
				event = parser.next();
				eventType = event.getType();
			}
			inputDataLength = event.getDomain().second;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Readable getNext(){
		return prepareNext(new FB2FileStorable(this));
	}

	@Override
	protected void makeHeader(){
		if (TextUtils.isEmpty(title)){
			super.makeHeader();
		} else {
			header = title;
		}
	}
}
