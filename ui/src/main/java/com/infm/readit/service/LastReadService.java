package com.infm.readit.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.util.Log;

import com.infm.readit.Constants;
import com.infm.readit.database.DataBundle;
import com.infm.readit.database.LastReadContentProvider;
import com.infm.readit.readable.Storable;

public class LastReadService extends IntentService {

	private static final String LOGTAG = "LastReadService";

	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 *
	 * @param name Used to name the worker thread, important only for debugging.
	 */
	public LastReadService(String name){
		super(name);
	}

	public LastReadService(){
		super("LastReadService");
	}

	@Override
	protected void onHandleIntent(Intent intent){
		DataBundle dataBundle = DataBundle.createFromIntent(intent);
		Log.d(LOGTAG, "DataBundle received: " + dataBundle.toString());

		ContentResolver contentResolver = getContentResolver();
		Integer operation = intent.getIntExtra(Constants.EXTRA_DB_OPERATION, -1);
		DataBundle rowData = Storable.getRowData(contentResolver.query(LastReadContentProvider.CONTENT_URI,
				null, null, null, null), dataBundle.getPath());
		switch (operation){
			case Constants.DB_OPERATION_INSERT:
				insertDataWithoutConflict(contentResolver, dataBundle, rowData);
				break;
			case Constants.DB_OPERATION_DELETE:
				deleteData(contentResolver, rowData);
				break;
			default:
				throw new IllegalArgumentException("DB operation hasn't been recognized");
		}
	}

	private void insertDataWithoutConflict(ContentResolver contentResolver, DataBundle dataBundle, DataBundle rowData){
		if (rowData == null){
			contentResolver.insert(LastReadContentProvider.CONTENT_URI, Storable.getInsertContentValues(dataBundle));
		} else {
			contentResolver.update(ContentUris.withAppendedId(
					LastReadContentProvider.CONTENT_URI, rowData.getRowId()
			), Storable.getUpdateContentValues(dataBundle), null, null);
		}
	}

	private void deleteData(ContentResolver contentResolver, DataBundle rowData){
		if (rowData != null)
			contentResolver.delete(
					ContentUris.withAppendedId(LastReadContentProvider.CONTENT_URI, rowData.getRowId()),
					null, null);
	}
}
