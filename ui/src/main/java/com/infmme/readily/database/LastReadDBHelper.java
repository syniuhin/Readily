package com.infmme.readily.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by infm on 5/22/14. Enjoy ;)
 */
public class LastReadDBHelper extends SQLiteOpenHelper {

	public static final String NAME = "last_read";
	public static final String TABLE = "last_read_table";
	public static final int VERSION = 2;
	public static final String KEY_ROWID = "_id";
	public static final String KEY_HEADER = "header";
	public static final String KEY_PATH = "path";
	public static final String KEY_POSITION = "position";
	public static final String KEY_PERCENT = "percent_left";
	public static final String KEY_TIME_MODIFIED = "time_modified";
	public static final String KEY_LINK = "link";
	public static final String KEY_BYTE_POSITION = "byte_position";
	public static final int COLUMN_ROWID = 0;
	public static final int COLUMN_HEADER = 1;
	public static final int COLUMN_PATH = 2;
	public static final int COLUMN_POSITION = 5;
	public static final int COLUMN_PERCENT = 4;
	public static final int COLUMN_BYTE_POSITION = 7;
	static final String CREATE =
			"CREATE TABLE " + TABLE + " (" +
					KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					KEY_HEADER + " TEXT NOT NULL, " +
					KEY_PATH + " TEXT NOT NULL, " +
					KEY_TIME_MODIFIED + " INTEGER, " +
					KEY_PERCENT + " INTEGER, " +
					KEY_POSITION + " INTEGER, " +
					KEY_LINK + " TEXT, " +
					KEY_BYTE_POSITION + " INTEGER);";

	public LastReadDBHelper(Context context){
		super(context, NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		db.execSQL(CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		int upgradeTo = oldVersion;
		while (upgradeTo++ <= newVersion) {
			switch (upgradeTo) {
				case 2:
					db.execSQL("ALTER TABLE " + TABLE + " ADD COLUMN " + KEY_BYTE_POSITION + " INTEGER");
			}
		}
	}
}
