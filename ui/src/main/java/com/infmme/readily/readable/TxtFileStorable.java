package com.infmme.readily.readable;

import android.content.Context;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by infm on 7/2/14. Enjoy ;)
 */
public class TxtFileStorable extends FileStorable {

	private byte[] inputData = new byte[BUFFER_SIZE];
	private String encoding;

	public TxtFileStorable(String path, String encoding){
		type = TYPE_TXT;
		this.path = path;
		this.encoding = encoding;
	}

	public TxtFileStorable(TxtFileStorable that){
		super(that);
		type = TYPE_TXT;
	}

	public void process(Context context){
		try {
			path = FileStorable.takePath(context, path);
			if (path == null){
				return;
			}
			fileInputStream = new FileInputStream(path);
			createRowData(context);
			if (bytePosition > 0)
				fileInputStream.skip(bytePosition);
			processed = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void readData(){
		try {
			inputDataLength = fileInputStream.read(inputData);
			if (inputDataLength != -1){
				//TODO: review encoding
				setText(new StringBuilder((new String(inputData, encoding)).
						substring(0, (int) inputDataLength)));
			} else {
				setText("");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setText(StringBuilder nextText){
		text = nextText;
	}

	@Override
	public Readable getNext(){
		return prepareNext(new TxtFileStorable(this));
	}
}
