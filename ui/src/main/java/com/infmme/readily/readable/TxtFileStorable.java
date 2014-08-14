package com.infmme.readily.readable;

import android.content.Context;

import java.io.FileReader;
import java.io.IOException;

/**
 * Created by infm on 7/2/14. Enjoy ;)
 */
public class TxtFileStorable extends FileStorable {

	private char[] inputData = new char[BUFFER_SIZE];
	private FileReader fileReader;
	private String lastWord = "";

	public TxtFileStorable(String path){
		type = TYPE_TXT;
		this.path = path;
	}

	public TxtFileStorable(TxtFileStorable that){
		inputData = that.inputData;
		fileReader = that.fileReader;
		position = that.position;
		path = that.path;
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

	public void readData() throws IOException{
		if (fileReader.read(inputData) != -1){
			setText(new StringBuilder(new String(inputData)));
		} else {
			setText("");
		}
	}

	public void setText(StringBuilder nextText){
		text = nextText;
	}

	public TxtFileStorable getNext(){
		TxtFileStorable result = new TxtFileStorable(this);
		try {
			result.readData();
			result.cutLastWord();
			result.insertLastWord(lastWord);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * must be called before TextParser.process();
	 */
	public void cutLastWord(){
		String textString = text.toString();
		int index = textString.lastIndexOf(' ') + 1;
		lastWord = textString.substring(index);
		text = new StringBuilder(textString.substring(0, index));
	}

	public void insertLastWord(String lastWord){
		text = new StringBuilder(lastWord).append(text);
	}
}
