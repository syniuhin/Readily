package com.infm.readit.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.infm.readit.Constants;
import com.infm.readit.database.DataBundle;
import com.infm.readit.database.LastReadContentProvider;
import com.infm.readit.database.LastReadDBHelper;
import com.infm.readit.readable.Storable;

public class LastReadService extends IntentService {

    private static final String LOGTAG = "LastReadService";

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

    private static Intent createIntent(Context context, Storable storable, int operation) {
        Intent intent = new Intent(context, LastReadService.class);
        storable.putDataInIntent(intent);
        intent.putExtra(Constants.EXTRA_DB_OPERATION, operation);
        return intent;
    }

    private static Intent createIntent(Context context, String path, int operation) {
        if (operation == Constants.DB_OPERATION_DELETE) {
            Intent intent = new Intent(context, LastReadService.class);
            intent.putExtra(Constants.EXTRA_PATH, path);
            intent.putExtra(Constants.EXTRA_DB_OPERATION, operation);
            return intent;
        } else {
            throw new IllegalArgumentException("operation != DELETE");
        }
    }

    public static void start(Context context, Storable storable, int operation) {
        context.startService(createIntent(context, storable, operation));
    }

    public static void start(Context context, String path, int operation){
        context.startService(createIntent(context, path, operation));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ContentResolver contentResolver = getContentResolver();
        switch (intent.getIntExtra(Constants.EXTRA_DB_OPERATION, -1)) {
            case Constants.DB_OPERATION_INSERT:
                DataBundle dataBundle = DataBundle.createElementFromIntent(intent);
                Log.d(LOGTAG, "DataBundle received: " + dataBundle.toString());
                DataBundle rowData = Storable.getRowData(contentResolver.query(LastReadContentProvider.CONTENT_URI,
                        null, null, null, null), dataBundle.getPath());

                insertDataWithoutConflict(contentResolver, dataBundle, rowData);
                break;
            case Constants.DB_OPERATION_DELETE:
                deleteData(contentResolver, getPaths(intent));
                break;
            default:
                throw new IllegalArgumentException("DB operation hasn't been recognized");
        }
    }

    private void insertDataWithoutConflict(ContentResolver contentResolver, DataBundle dataBundle, DataBundle rowData) {
        if (rowData == null) {
            contentResolver.insert(LastReadContentProvider.CONTENT_URI, DataBundle.getInsertContentValues(dataBundle));
        } else {
            contentResolver.update(ContentUris.withAppendedId(
                    LastReadContentProvider.CONTENT_URI, rowData.getRowId()
            ), DataBundle.getUpdateContentValues(dataBundle), null, null);
        }
    }

    private String[] getPaths(Intent intent) {
        String[] paths = intent.getStringArrayExtra(Constants.EXTRA_PATH_ARRAY);
        if (paths == null)
            paths = new String[]{intent.getStringExtra(Constants.EXTRA_PATH)};
        return paths;
    }

    private void deleteData(ContentResolver contentResolver, String[] paths) {
        contentResolver.delete(LastReadContentProvider.CONTENT_URI,
                LastReadDBHelper.KEY_PATH + "=?", paths);
    }
}
