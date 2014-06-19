package cmc.readit.rsvp_reader.ui;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by infm on 5/22/14. Enjoy ;)
 */
public class LastReadDBHelper extends SQLiteOpenHelper {
    static final String NAME = "last_read";
    static final String TABLE = "last_read_table";
    static final int VERSION = 1;
    static final String KEY_ROWID = "_id";
    static final String KEY_HEADER = "header";
    static final String KEY_PATH = "text";
    static final String KEY_POSITION = "position";
    static final String KEY_PERCENT = "percent_left";
    static final String KEY_TIME_MODIFIED = "time_modified";

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

    public Cursor getData(SQLiteDatabase db) {
        return db.query(TABLE, null,
                null, null, null, null, null);
    }
}
