package com.infm.readit.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by infm on 5/22/14. Enjoy ;)
 */
public class LastReadDBHelper extends SQLiteOpenHelper {
    public static final String NAME = "last_read";
    public static final String TABLE = "last_read_table";
    public static final int VERSION = 1;
    public static final String KEY_ROWID = "_id";
    public static final String KEY_HEADER = "header";
    public static final String KEY_PATH = "path";
    public static final String KEY_POSITION = "position";
    public static final String KEY_PERCENT = "percent_left";
    public static final String KEY_TIME_MODIFIED = "time_modified";

    public static final Integer N_KEY_ROWID = 0;
    public static final Integer N_KEY_HEADER = 1;
    public static final Integer N_KEY_PATH = 2;
    public static final Integer N_KEY_POSITION = 5;
    public static final Integer N_KEY_PERCENT = 4;
    public static final Integer N_KEY_TIME_MODIFIED = 3;
    
    static final String CREATE =
            "CREATE TABLE " + TABLE + " (" +
                    KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_HEADER + " TEXT NOT NULL, " +
                    KEY_PATH + " TEXT NOT NULL, " +
                    KEY_TIME_MODIFIED + " INTEGER, " +
                    KEY_PERCENT + " INTEGER, " +
                    KEY_POSITION + " INTEGER" + ");";

    public LastReadDBHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DBHelper", "helper updates db??");
    }
}
