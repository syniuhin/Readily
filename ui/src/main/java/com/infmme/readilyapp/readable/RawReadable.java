package com.infmme.readilyapp.readable;

import android.content.Context;

/**
 * Created by infm on 7/3/14. Enjoy ;)
 */
public class RawReadable extends Storable {

	private boolean reallyStorable;

	public RawReadable(String text, boolean reallyStorable){
		this.text = new StringBuilder(text);
		this.reallyStorable = reallyStorable;
		type = TYPE_RAW;
	}

	public RawReadable(RawReadable that){
		super(that);
		reallyStorable = that.isReallyStorable();
	}

	public boolean isReallyStorable(){
		return reallyStorable;
	}

	@Override
	public void process(Context context){
		if (reallyStorable){
			makeHeader();
			path = context.getFilesDir() + "/" + cleanFileName(header) + ".txt";
			rowData = takeRowData(context);
			if (rowData != null){
				position = rowData.getPosition();
			} else {
				createInternalStorageFile(context, path, text.toString());
			}
		}
		processed = true;
	}

	@Override
	public void readData(){
		//Well, actually all data is already read
	}

	@Override
	public Readable getNext(){
		RawReadable result = new RawReadable(this);
		result.setText("");
		return result;
	}
}
