package com.infmme.readily.readable;

import android.content.Context;

import java.io.FileReader;
import java.io.IOException;

/**
 * Created by infm on 7/2/14. Enjoy ;)
 */
public class TxtFileStorable extends FileStorable {

	private FileReader fileReader;
	char[] inputData = new char[BUFFER_SIZE];
	private StringBuilder nextText;

	public TxtFileStorable(String path){
		type = TYPE_TXT;
		this.path = path;
	}

	public void process(Context context){
		try {
			path = FileStorable.takePath(context, path);
			if (path == null){
				return;
			}
			fileReader = new FileReader(path);
/*
			BufferedReader br = new BufferedReader(fileReader);
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null){ text.append(sCurrentLine).append('\n'); }
			br.close();
*/
			createRowData(context);
			processed = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean readNext() throws IOException{
		if (fileReader.read(inputData) != -1){
			nextText = new StringBuilder(new String(inputData));
			return true;
		}
		return false;
	}

	public boolean switchToNext(){
		if (nextText != null){
			text = nextText;
			return true;
		}
		return false;
	}
}
