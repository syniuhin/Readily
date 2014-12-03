package com.infmme.readilyapp.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import com.infmme.readilyapp.database.LastReadContentProvider;
import com.infmme.readilyapp.database.LastReadDBHelper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by infm on 7/1/14. Enjoy ;)
 */
public class StorageCheckerService extends IntentService {

	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 *
	 * @param name Used to name the worker thread, important only for debugging.
	 */
	public StorageCheckerService(String name){
		super(name);
	}

	public StorageCheckerService(){
		super("StorageCheckerService");
	}

	@Override
	protected void onHandleIntent(Intent intent){
		ContentResolver contentResolver = getContentResolver();
		processFolder(getBaseData(contentResolver), contentResolver);
	}

	private Map<String, Integer> getBaseData(ContentResolver contentResolver){
		Map<String, Integer> result = new HashMap<String, Integer>();
		Cursor cursor = contentResolver.query(LastReadContentProvider.CONTENT_URI,
											  new String[]{LastReadDBHelper.KEY_ROWID, LastReadDBHelper.KEY_PATH},
											  null, null, null);
		while (cursor.moveToNext())
			result.put(cursor.getString(1), cursor.getInt(0));
		cursor.close();
		return result;
	}

	private void processFolder(Map<String, Integer> baseData, ContentResolver contentResolver){
		for (File file : getFilesDir().listFiles())
			if (file.exists() && !baseData.containsKey(file.getAbsolutePath()))
				file.delete();
		for (Map.Entry<String, Integer> entry : baseData.entrySet()){
			if (!(new File(entry.getKey())).exists()){
				contentResolver.delete(
						ContentUris.withAppendedId(LastReadContentProvider.CONTENT_URI, entry.getValue()),
						null, null);
			}
		}
	}
}
