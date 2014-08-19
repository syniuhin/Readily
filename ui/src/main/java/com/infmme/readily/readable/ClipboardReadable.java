package com.infmme.readily.readable;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Looper;

/**
 * Created by infm on 6/12/14. Enjoy ;)
 */
public class ClipboardReadable extends Readable {

	private ClipboardManager clipboard;

	public ClipboardReadable(){
		type = TYPE_CLIPBOARD;
	}

	public ClipboardReadable(ClipboardReadable that){
		super(that);
		clipboard = that.getClipboard();
	}

	public ClipboardManager getClipboard(){
		return clipboard;
	}

	public void process(final Context context){
		Looper.prepare(); //TODO: review it CAREFULLY
		clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		processFailed = !clipboard.hasText();
		processed = true;
	}

	@Override
	public void readData(){
		text.append(paste(clipboard));
	}

	@Override
	public Readable getNext(){
		ClipboardReadable result = new ClipboardReadable(this);
		result.readData();
		return result;
	}

	private String paste(ClipboardManager clipboard){
		ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
		CharSequence pasteData = item.getText();
		if (pasteData != null){ return pasteData.toString(); }
		return null;
	}
}
