package com.infmme.readilyapp.readable;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import com.infmme.readilyapp.Constants;
import com.infmme.readilyapp.database.LastReadDBHelper;
import com.infmme.readilyapp.service.LastReadService;

/**
 * Created by infm on 7/11/14. Enjoy ;)
 */
public class MiniReadable extends Readable {

  String percent;

  public MiniReadable(String path, String header, String percent, int position) {
    this.path = path;
    this.header = header;
    this.percent = percent;
    this.position = position;
  }

  public static MiniReadable singletonFromCursor(Cursor cursor) {
    return new MiniReadable(
        cursor.getString(LastReadDBHelper.COLUMN_PATH),
        cursor.getString(LastReadDBHelper.COLUMN_HEADER),
        cursor.getString(LastReadDBHelper.COLUMN_PERCENT),
        cursor.getInt(LastReadDBHelper.COLUMN_POSITION)
    );
  }

  public static Intent createDBServiceIntent(Context context, MiniReadable readable) {
    Intent intent = new Intent(context, LastReadService.class);
    intent.putExtra(Constants.EXTRA_HEADER, readable.getHeader());
    intent.putExtra(Constants.EXTRA_PATH, readable.getPath());
    intent.putExtra(Constants.EXTRA_POSITION, readable.getPosition());
    intent.putExtra(Constants.EXTRA_PERCENT, readable.getPercent()); //dirty, dirty hack..
    return intent;
  }

  public String getPercent() {
    return percent;
  }

  @Override
  public void process(Context context) {}

  @Override
  public void readData() {}

  @Override
  public Readable getNext() { return null; }

  @Override
  public String toString() {
    return header + ", " + path + ", " + percent + ".";
  }
}
