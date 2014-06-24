package com.infm.readit.database;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;

public class LastReadService extends IntentService {

    public static final String LOGTAG = "LastReadService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public LastReadService(String name) {
        super(name);
    }

    public LastReadService() {
        super("LastReadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DataBundle dataBundle = new DataBundle(intent.getStringExtra(LastReadDBHelper.KEY_HEADER),
                intent.getStringExtra(LastReadDBHelper.KEY_PATH),
                intent.getIntExtra(LastReadDBHelper.KEY_POSITION, 0),
                intent.getStringExtra(LastReadDBHelper.KEY_PERCENT));

        Log.d(LOGTAG, "DataBundle received: " + dataBundle.mkString());

        ContentValues contentValues = com.infm.readit.readable.Readable.getContentValues(dataBundle);
        ContentResolver contentResolver = getContentResolver();
        Pair<Integer, Integer> existingData =
                com.infm.readit.readable.Readable.getRowData(contentResolver.query(LastReadContentProvider.CONTENT_URI,
                        null, null, null, null), dataBundle.getPath());
        if (existingData == null)
            contentResolver.insert(LastReadContentProvider.CONTENT_URI, contentValues);
        else
            contentResolver.update(ContentUris.withAppendedId(LastReadContentProvider.CONTENT_URI, existingData.first),
                    contentValues, null, null);
    }
}
