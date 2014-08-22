package com.infmme.readily.readable;

import android.content.Context;

import java.io.FileReader;
import java.io.IOException;

/**
 * Created by infm on 7/2/14. Enjoy ;)
 */
public class TxtFileStorable extends FileStorable {

	private char[] inputData = new char[BUFFER_SIZE];

	public TxtFileStorable(String path){
		type = TYPE_TXT;
		this.path = path;
	}

	public TxtFileStorable(TxtFileStorable that){
		super(that);
	}

	public void process(Context context){
		try {
			path = FileStorable.takePath(context, path);
			if (path == null){
				return;
			}
			fileReader = new FileReader(path);
			createRowData(context);
			processed = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void readData(){
		try {
			int inputDataLength = fileReader.read(inputData);
			if (inputDataLength != -1){
				setText(new StringBuilder((new String(inputData)).substring(0, inputDataLength)));
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
		TxtFileStorable result = new TxtFileStorable(this);
		result.readData();
		result.cutLastWord();
		result.insertLastWord(lastWord);
		result.copyListSuffix(this);
		return result;
	}
}
