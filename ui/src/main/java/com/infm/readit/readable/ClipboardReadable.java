package com.infm.readit.readable;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.infm.readit.R;

/**
 * Created by infm on 6/12/14. Enjoy ;)
 */
public class ClipboardReadable extends Readable {

	private static final String LOGTAG = "ClipboardReadable";

	public ClipboardReadable(){
		super();
		type = TYPE_CLIPBOARD;
	}

	public void process(Context context){
		ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		if (processFailed = !clipboard.hasText()){
			Log.d(LOGTAG, "There's nothing in clipboard");
		} else {
			text.append(paste(clipboard));
		}
	}

	private String paste(ClipboardManager clipboard){
		ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
		CharSequence pasteData = item.getText();
		if (pasteData != null)
			return pasteData.toString();
		return null;
	}
}
