package com.infmme.readilyapp.readable.old;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.infmme.readilyapp.util.Constants;
import com.infmme.readilyapp.database.DataBundle;
import com.infmme.readilyapp.database.LastReadContentProvider;
import com.infmme.readilyapp.database.LastReadDBHelper;
import com.infmme.readilyapp.service.LastReadService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.*;

/**
 * Created by infm on 6/30/14. Enjoy ;)
 */
abstract public class Storable extends Readable {

  protected String title;
  protected long bytePosition;
  protected long approxCharCount;

  public Storable() {}

  public Storable(Storable that) {
    super(that);
    title = that.getTitle();
    bytePosition = that.getBytePosition();
    approxCharCount = that.getApproxCharCount();
  }

  public static DataBundle getRowData(Cursor cursor, String path) {
    DataBundle rowData = null;
    if (!TextUtils.isEmpty(path)) {
      while (cursor.moveToNext() && rowData == null) {
        if (path.equals(cursor.getString(LastReadDBHelper.COLUMN_PATH))) {
          rowData = new DataBundle(
              cursor.getInt(LastReadDBHelper.COLUMN_ROWID),
              cursor.getString(LastReadDBHelper.COLUMN_HEADER),
              path,
              cursor.getInt(LastReadDBHelper.COLUMN_POSITION),
              cursor.getLong(LastReadDBHelper.COLUMN_BYTE_POSITION),
              cursor.getString(LastReadDBHelper.COLUMN_PERCENT)
          );
        }
      }
    }
    cursor.close();
    return rowData;
  }

  public static void createInternalStorageFile(Context context, String path,
                                               String text) {
    if (PreferenceManager.getDefaultSharedPreferences(context)
                         .getBoolean(Constants.Preferences.STORAGE,
                                     true)) {
      File storageFile = new File(path);
      try {
        //TODO: implement check if path pointing to internal storage
        if (storageFile.createNewFile()) {
          FileOutputStream fos = new FileOutputStream(storageFile);
          fos.write(text.getBytes());
          fos.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public String getTitle() { return title; }

  public long getBytePosition() { return bytePosition; }

  public void setBytePosition(
      long bytePosition) { this.bytePosition = bytePosition; }

  public long getApproxCharCount() {
    return approxCharCount;
  }

  public void setApproxCharCount(long approxCharCount) {
    this.approxCharCount = approxCharCount;
  }

  protected DataBundle takeRowData(Context context) {
    return Storable.getRowData(
        context.getContentResolver().query(LastReadContentProvider.CONTENT_URI,
                                           null, null, null, null),
        path); //looks weird, actually. upd: it will be in separate thread,
    // so ok.
  }

  /**
   * @param intent: intent to put
   */
  public Intent putInsertionDataInIntent(Intent intent) {
    makeHeader();
    return intent.putExtra(Constants.EXTRA_HEADER, header).
        putExtra(Constants.EXTRA_PATH, path).
                     putExtra(Constants.EXTRA_POSITION, 0).
                     putExtra(Constants.EXTRA_BYTE_POSITION, approxCharCount).
                     putExtra(Constants.EXTRA_PERCENT,
                              (100 - calcProgress(position,
                                                  approxCharCount)) + "%").
                     putExtra(Constants.EXTRA_DB_OPERATION,
                              Constants.DB_OPERATION_INSERT);
  }

  public Intent putDeletionDataInIntent(Intent intent) {
    return intent.putExtra(Constants.EXTRA_PATH, path).
        putExtra(Constants.EXTRA_DB_OPERATION, Constants.DB_OPERATION_DELETE);
  }

  public void onClose(Context context, boolean isCompleted,
                      boolean storeComplete) {
    if (storeComplete)
      save(context);
    else if (isCompleted)
      delete(context);
    else
      save(context);
    createInternalStorageFile(context, path, text.toString());
  }

  public void save(Context context) {
    context.startService(
        putInsertionDataInIntent(new Intent(context, LastReadService.class)));
  }

  public void delete(Context context) {
    context.startService(
        putDeletionDataInIntent(new Intent(context, LastReadService.class)));
  }

  protected void makeHeader() {
    if (TextUtils.isEmpty(header))
      header = text.toString().substring(0, Math.min(text.length(), 40));
  }

  protected String cleanFileName(String s) {
    return ((TextUtils.isEmpty(s))
        ? System.currentTimeMillis() + "t"
        : s.replace(' ', '_')).replace('/', '|');
  }
}
