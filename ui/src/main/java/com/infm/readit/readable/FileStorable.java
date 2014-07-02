package com.infm.readit.readable;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.net.URISyntaxException;

/**
 * Created by infm on 6/13/14. Enjoy ;)
 */
abstract public class FileStorable extends Storable { //TODO: implement separate class for each extension

	private static final String LOGTAG = "FileStorable";

	public static FileStorable createFileStorable(String intentPath){
		FileStorable fileStorable;
		switch (getIntentType(intentPath)){
			case Readable.TYPE_TXT:
				fileStorable = new TxtFileStorable();
				break;
			case Readable.TYPE_EPUB:
				fileStorable = new EpubFileStorable();
				break;
			default:
				fileStorable = null;
		}
		return fileStorable;
	}

	public static String takePath(Context context, Uri uri) throws URISyntaxException{
		if ("content".equalsIgnoreCase(uri.getScheme())){
			String[] projection = {"_data"};
			Cursor cursor = null;
			try {
				cursor = context.getContentResolver().query(uri, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()){
					String toReturn = cursor.getString(column_index);
					cursor.close();
					return toReturn;
				} else cursor.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())){
			String path = uri.getPath();
			Log.d(LOGTAG, "path: " + path);
			return path;
		}
		return "";
	}

	//implement it properly
	public static String takePath(Context context, String s) throws URISyntaxException{
		if ("content".equals(s.substring(0, 7))){
			return FileStorable.takePath(context, Uri.parse(s));
		} else
			return s;
	}

	public static String getExtension(String path){
		String extension = MimeTypeMap.getFileExtensionFromUrl(path);
		if (TextUtils.isEmpty(extension)){
			int i = path.lastIndexOf('.');
			if (i > 0)
				extension = path.substring(i + 1);
		}
		return extension;
	}

	public static int getIntentType(String intentPath){
		String ext = getExtension(intentPath);
		if ("txt".equals(ext))
			return Readable.TYPE_TXT;
		if ("epub".equals(ext))
			return Readable.TYPE_EPUB;
		return -1;
	}

	public String getExtension(){ return extension; }

	@Override
	protected void makeHeader(){
		if (TextUtils.isEmpty(title))
			super.makeHeader();
		else
			header = title;
	}

	protected void createRowData(Context context){
		rowData = takeRowData(context);
		if (rowData != null)
			position = rowData.getPosition();
	}
}
