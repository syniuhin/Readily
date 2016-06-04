package com.infmme.readilyapp.database;

import android.content.ContentValues;
import android.content.Intent;
import com.infmme.readilyapp.Constants;

import java.io.Serializable;

/**
 * Created by infm on 6/24/14. Enjoy ;)
 */
public class DataBundle implements Serializable {

  private String header;
  private String path;
  private int position;
  private long bytePosition;
  private String percent;
  private int rowId;

  public DataBundle() {}

  public DataBundle(String header, String path, int position, String percent) {
    this.header = header;
    this.path = path;
    this.position = position;
    this.percent = percent;
  }

  public DataBundle(String header, String path, int position, long bytePosition,
                    String percent) {
    this.header = header;
    this.path = path;
    this.position = position;
    this.bytePosition = bytePosition;
    this.percent = percent;
  }

  public DataBundle(int rowId, String header, String path, int position,
                    long bytePosition, String percent) {
    this.rowId = rowId;
    this.header = header;
    this.path = path;
    this.position = position;
    this.bytePosition = bytePosition;
    this.percent = percent;
  }

  public static DataBundle createElementFromIntent(Intent intent) {
    return new DataBundle(intent.getStringExtra(Constants.EXTRA_HEADER),
                          intent.getStringExtra(Constants.EXTRA_PATH),
                          intent.getIntExtra(Constants.EXTRA_POSITION, 0),
                          intent.getLongExtra(Constants.EXTRA_BYTE_POSITION, 0),
                          intent.getStringExtra(Constants.EXTRA_PERCENT));
  }

  public static ContentValues getInsertContentValues(DataBundle dataBundle) {
    ContentValues values = new ContentValues();
    values.put(LastReadDBHelper.KEY_HEADER, dataBundle.getHeader());
    values.put(LastReadDBHelper.KEY_PATH, dataBundle.getPath());
    values.put(LastReadDBHelper.KEY_POSITION, dataBundle.getPosition());
    values.put(LastReadDBHelper.KEY_PERCENT, dataBundle.getPercent());
    values.put(LastReadDBHelper.KEY_BYTE_POSITION,
               dataBundle.getBytePosition());
    return values;
  }

  public static ContentValues getUpdateContentValues(DataBundle dataBundle) {
    ContentValues values = new ContentValues();
    values.put(LastReadDBHelper.KEY_POSITION, dataBundle.getPosition());
    values.put(LastReadDBHelper.KEY_PERCENT, dataBundle.getPercent());
    values.put(LastReadDBHelper.KEY_HEADER, dataBundle.getHeader());
    values.put(LastReadDBHelper.KEY_BYTE_POSITION,
               dataBundle.getBytePosition());
    return values;
  }

  public int getRowId() {
    return rowId;
  }

  public void setRowId(int rowId) {
    this.rowId = rowId;
  }

  public String getHeader() {
    return header;
  }

  public void setHeader(String header) {
    this.header = header;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public long getBytePosition() {
    return bytePosition;
  }

  public String getPercent() {
    return percent;
  }

  public void setPercent(String percent) {
    this.percent = percent;
  }

  @Override
  public String toString() {
    return "rowId: + " + rowId +
        "; header: " + header +
        "; path: " + path +
        "; position: " + position +
        "; bytePosition: " + bytePosition +
        "; percent: " + percent;
  }
}
