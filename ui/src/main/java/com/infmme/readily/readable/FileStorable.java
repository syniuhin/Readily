package com.infmme.readily.readable;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import com.infmme.readily.Constants;
import com.ipaulpro.afilechooser.utils.FileUtils;

/**
 * Created by infm on 6/13/14. Enjoy ;)
 */
abstract public class FileStorable extends Storable {

	public static FileStorable createFileStorable(String intentPath){
		FileStorable fileStorable;
		switch (getIntentType(intentPath)){
			case Readable.TYPE_TXT:
				fileStorable = new TxtFileStorable(intentPath);
				break;
			case Readable.TYPE_EPUB:
				fileStorable = new EpubFileStorable(intentPath);
				break;
			default:
				fileStorable = null;
		}
		return fileStorable;
	}

	public static String takePath(Context context, String s){
		String candidate = FileUtils.getPath(context, Uri.parse(s));
		if (TextUtils.isEmpty(candidate)){ return s; }
		return candidate;
	}

	public static int getIntentType(String intentPath){
		String ext = FileUtils.getExtension(intentPath);
		if (Constants.EXTENSION_TXT.equals(ext)){ return Readable.TYPE_TXT; }
		if (Constants.EXTENSION_EPUB.equals(ext)){ return Readable.TYPE_EPUB; }
		return -1;
	}

	public static boolean isExtensionValid(String extension){
		return Constants.EXTENSION_TXT.equals(extension) ||
				Constants.EXTENSION_EPUB.equals(extension); //to be continued...
	}

	public String getExtension(){ return extension; }

	protected void createRowData(Context context){
		rowData = takeRowData(context);
		if (rowData != null){ position = rowData.getPosition(); }
	}
}
