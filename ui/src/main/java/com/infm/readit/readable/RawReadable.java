package com.infm.readit.readable;

import android.content.Context;

/**
 * Created by infm on 7/3/14. Enjoy ;)
 */
public class RawReadable extends Storable {

	private boolean isReallyStorable;

	public RawReadable(String text, boolean isReallyStorable){
		this.text = new StringBuilder(text);
		this.isReallyStorable = isReallyStorable;
		type = TYPE_RAW;
	}

	@Override
	public void process(Context context){
		if (isReallyStorable){
			makeHeader();
			path = context.getFilesDir() + "/" + cleanFileName(header) + ".txt";
			rowData = takeRowData(context);
			if (rowData != null)
				position = rowData.getPosition();
			else
				createStorageFile(context, path, text.toString());
		}
	}
}
