package com.infm.readit.readable;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by infm on 7/2/14. Enjoy ;)
 */
public class TxtFileStorable extends FileStorable {

	private static final String LOGTAG = "TxtFileStorable";

	public TxtFileStorable(){
		extension = "txt";
		type = TYPE_TXT;
	}

	public void process(Context context){
		Log.d(LOGTAG, "type: txt");
		try {
			path = FileStorable.takePath(context, path);
			if (path == null){
				Log.d(LOGTAG, "path is null");
				return;
			}
			FileReader fileReader = new FileReader(path);
			BufferedReader br = new BufferedReader(fileReader);
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null)
				text.append(sCurrentLine).append('\n');
			br.close();

			createRowData(context);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
