package com.infm.readit.readable;

import android.content.Context;
import android.database.Cursor;
import com.infm.readit.database.LastReadDBHelper;

import java.util.ArrayList;

/**
 * Created by infm on 7/11/14. Enjoy ;)
 */
public class MiniReadable extends Readable {

	String percent;

	public MiniReadable(){}

	public MiniReadable(String path, String header, String percent){
		this.path = path;
		this.header = header;
		this.percent = percent;
	}

	public static ArrayList<MiniReadable> getFromCursor(Cursor cursor){
		ArrayList<MiniReadable> result = new ArrayList<MiniReadable>();
		if (cursor != null){
			while (cursor.moveToNext())
				result.add(new MiniReadable(
						cursor.getString(LastReadDBHelper.COLUMN_PATH),
						cursor.getString(LastReadDBHelper.COLUMN_HEADER),
						cursor.getString(LastReadDBHelper.COLUMN_PERCENT)
				));
		}
		return result;
	}

	public String getPercent(){
		return percent;
	}

	public void setPercent(String percent){
		this.percent = percent;
	}

	@Override
	public void process(Context context){}

    @Override
    public String toString() {
        return header + ", " + path + ", " + percent + ".";
    }
}
