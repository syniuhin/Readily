package com.infm.readit.readable;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.infm.readit.Constants;
import com.infm.readit.database.DataBundle;
import com.infm.readit.database.LastReadContentProvider;
import com.infm.readit.database.LastReadDBHelper;

/**
 * Created by infm on 6/30/14. Enjoy ;)
 */
abstract public class Storable extends Readable {

	private static final String LOGTAG = "Storable";

	protected String extension;
	protected String title;

	public static DataBundle getRowData(Cursor cursor, String path){
		Log.d(LOGTAG, "getRowData() called; cursor size: " + cursor.getCount() + "; path: " + path);
		DataBundle rowData = null;
		if (!TextUtils.isEmpty(path)){
			while (cursor.moveToNext() && rowData == null){
				if (path.equals(cursor.getString(LastReadDBHelper.COLUMN_PATH))){
					rowData = new DataBundle(
							cursor.getInt(LastReadDBHelper.COLUMN_ROWID),
							cursor.getString(LastReadDBHelper.COLUMN_HEADER),
							path,
							cursor.getInt(LastReadDBHelper.COLUMN_POSITION),
							cursor.getString(LastReadDBHelper.COLUMN_PERCENT)
					);
				}
			}
		}
		cursor.close();
		Log.d(LOGTAG, "getRowData() : " + ((rowData == null) ? "null" : rowData.toString()));
		return rowData;
	}

	public static ContentValues getInsertContentValues(DataBundle dataBundle){
		ContentValues values = new ContentValues();
		values.put(LastReadDBHelper.KEY_HEADER, dataBundle.getHeader());
		values.put(LastReadDBHelper.KEY_PATH, dataBundle.getPath());
		values.put(LastReadDBHelper.KEY_POSITION, dataBundle.getPosition());
		values.put(LastReadDBHelper.KEY_PERCENT, dataBundle.getPercent());
		return values;
	}

	public static ContentValues getUpdateContentValues(DataBundle dataBundle){
		ContentValues values = new ContentValues();
		values.put(LastReadDBHelper.KEY_POSITION, dataBundle.getPosition());
		values.put(LastReadDBHelper.KEY_PERCENT, dataBundle.getPercent());
		return values;
	}

	protected DataBundle takeRowData(Context context){
		return Storable.getRowData(context.getContentResolver().query(LastReadContentProvider.CONTENT_URI,
				null, null, null, null), path); //looks weird, actually. upd: it will be in separate thread, so ok.
	}

	/**
	 * @param intent: intent to put
	 */
	public void putDataInIntent(Intent intent){
		makeHeader();
		intent.putExtra(Constants.EXTRA_HEADER, header);
		intent.putExtra(Constants.EXTRA_PATH, path);
		intent.putExtra(Constants.EXTRA_POSITION, position);
		intent.putExtra(Constants.EXTRA_PERCENT, 100 - (int) (position * 100f / wordList.size() + .5f) + "%");
	}

	protected void makeHeader(){
		int charLen = 0;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < wordList.size() && charLen < 40; ++i){
			String word = wordList.get(i);
			sb.append(word).append(" ");
			charLen += word.length() + 1;
		}
		header = sb.toString();
	}

	protected String cleanFileName(String s){
		String q = s.replace(' ', '_');
		q = q.replace('/', '|');
		return q;
	}
}
