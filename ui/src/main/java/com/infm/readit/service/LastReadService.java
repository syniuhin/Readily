package com.infm.readit.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import com.infm.readit.Constants;
import com.infm.readit.database.DataBundle;
import com.infm.readit.database.LastReadContentProvider;
import com.infm.readit.readable.Readable;

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
        Boolean isCompleted = intent.getBooleanExtra(Constants.EXTRA_READER_STATUS, false);
        DataBundle rowData = Readable.getRowData(contentResolver.query(LastReadContentProvider.CONTENT_URI,
                null, null, null, null), dataBundle.getPath());

        if (isCompleted){
            if (rowData != null)
                contentResolver.delete(
                        ContentUris.withAppendedId(LastReadContentProvider.CONTENT_URI, rowData.getRowId()),
                        null, null);
        } else {
            ContentValues contentValues = null;
            contentValues = Readable.getContentValues(dataBundle);

            if (rowData == null){
                contentResolver.insert(LastReadContentProvider.CONTENT_URI, contentValues);
            } else {
                contentResolver.update(ContentUris.withAppendedId(
                        LastReadContentProvider.CONTENT_URI, rowData.getRowId()
                ), contentValues, null, null);
            }
        }
    }
}
