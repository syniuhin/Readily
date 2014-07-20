package com.infmme.readily.readable;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by infm on 7/2/14. Enjoy ;)
 */
public class TxtFileStorable extends FileStorable {

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
			FileReader fileReader = new FileReader(path);
			BufferedReader br = new BufferedReader(fileReader);
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null){ text.append(sCurrentLine).append('\n'); }
			br.close();

			createRowData(context);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
