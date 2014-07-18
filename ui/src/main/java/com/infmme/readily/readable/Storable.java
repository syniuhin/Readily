package com.infmme.readily.readable;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.infmme.readily.Constants;
import com.infmme.readily.database.DataBundle;
import com.infmme.readily.database.LastReadContentProvider;
import com.infmme.readily.database.LastReadDBHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by infm on 6/30/14. Enjoy ;)
 */
abstract public class Storable extends Readable {


	protected String extension;
	protected String title;

	public static DataBundle getRowData(Cursor cursor, String path){
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
		return rowData;
	}

	public static void createStorageFile(Context context, String path, String text){
		if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.Preferences.STORAGE,
																			  true)){
			File storageFile = new File(path);
			try {
				storageFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(storageFile);
				fos.write(text.getBytes());
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected DataBundle takeRowData(Context context){
		return Storable.getRowData(context.getContentResolver().query(LastReadContentProvider.CONTENT_URI,
																	  null, null, null, null),
								   path); //looks weird, actually. upd: it will be in separate thread, so ok.
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
		if (TextUtils.isEmpty(header)){ header = text.toString().substring(0, Math.min(text.length(), 40)); }
	}

	protected String cleanFileName(String s){
		String q = s.replace(' ', '_');
		q = q.replace('/', '|');
		return q;
	}
}
