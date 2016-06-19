package com.infmme.readilyapp.provider;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;
import com.infmme.readilyapp.BuildConfig;
import com.infmme.readilyapp.provider.cachedbook.CachedBookColumns;
import com.infmme.readilyapp.provider.cachedbookinfo.CachedBookInfoColumns;
import com.infmme.readilyapp.provider.epubbook.EpubBookColumns;
import com.infmme.readilyapp.provider.fb2book.Fb2BookColumns;
import com.infmme.readilyapp.provider.txtbook.TxtBookColumns;

public class ReadilySQLiteOpenHelper extends SQLiteOpenHelper {
  public static final String DATABASE_FILE_NAME = "cached_book.db";
  // @formatter:off
  public static final String SQL_CREATE_TABLE_CACHED_BOOK = "CREATE TABLE IF " +
      "NOT EXISTS "
      + CachedBookColumns.TABLE_NAME + " ( "
      + CachedBookColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
      + CachedBookColumns.TITLE + " TEXT NOT NULL, "
      + CachedBookColumns.PATH + " TEXT NOT NULL, "
      + CachedBookColumns.TEXT_POSITION + " INTEGER NOT NULL, "
      + CachedBookColumns.PERCENTILE + " REAL NOT NULL, "
      + CachedBookColumns.TIME_OPENED + " TEXT NOT NULL, "
      + CachedBookColumns.COVER_IMAGE_URI + " TEXT, "
      + CachedBookColumns.COVER_IMAGE_MEAN + " INTEGER, "
      + CachedBookColumns.EPUB_BOOK_ID + " INTEGER, "
      + CachedBookColumns.FB2_BOOK_ID + " INTEGER, "
      + CachedBookColumns.TXT_BOOK_ID + " INTEGER, "
      + CachedBookColumns.INFO_ID + " INTEGER "
      + ", CONSTRAINT fk_epub_book_id FOREIGN KEY (" + CachedBookColumns
      .EPUB_BOOK_ID + ") REFERENCES epub_book (_id) ON DELETE CASCADE"
      + ", CONSTRAINT fk_fb2_book_id FOREIGN KEY (" + CachedBookColumns
      .FB2_BOOK_ID + ") REFERENCES fb2_book (_id) ON DELETE CASCADE"
      + ", CONSTRAINT fk_txt_book_id FOREIGN KEY (" + CachedBookColumns
      .TXT_BOOK_ID + ") REFERENCES txt_book (_id) ON DELETE CASCADE"
      + ", CONSTRAINT fk_info_id FOREIGN KEY (" + CachedBookColumns.INFO_ID +
      ") REFERENCES cached_book_info (_id) ON DELETE RESTRICT"
      + ", CONSTRAINT unique_path UNIQUE (path)"
      + ", CONSTRAINT unique_epub_book UNIQUE (epub_book_id)"
      + ", CONSTRAINT unique_fb2_book UNIQUE (fb2_book_id)"
      + ", CONSTRAINT unique_txt_book UNIQUE (txt_book_id)"
      + " );";
  public static final String SQL_CREATE_TABLE_CACHED_BOOK_INFO = "CREATE " +
      "TABLE IF NOT EXISTS "
      + CachedBookInfoColumns.TABLE_NAME + " ( "
      + CachedBookInfoColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
      + CachedBookInfoColumns.AUTHOR + " TEXT, "
      + CachedBookInfoColumns.GENRE + " TEXT, "
      + CachedBookInfoColumns.LANGUAGE + " TEXT, "
      + CachedBookInfoColumns.CURRENT_PART_TITLE + " TEXT, "
      + CachedBookInfoColumns.DESCRIPTION + " TEXT "
      + " );";
  public static final String SQL_CREATE_TABLE_EPUB_BOOK = "CREATE TABLE IF " +
      "NOT EXISTS "
      + EpubBookColumns.TABLE_NAME + " ( "
      + EpubBookColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
      + EpubBookColumns.CURRENT_RESOURCE_ID + " TEXT NOT NULL "
      + " );";
  public static final String SQL_CREATE_TABLE_FB2_BOOK = "CREATE TABLE IF NOT" +
      " EXISTS "
      + Fb2BookColumns.TABLE_NAME + " ( "
      + Fb2BookColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
      + Fb2BookColumns.FULLY_PROCESSED + " INTEGER NOT NULL, "
      + Fb2BookColumns.FULLY_PROCESSING_SUCCESS + " INTEGER, "
      + Fb2BookColumns.BYTE_POSITION + " INTEGER NOT NULL, "
      + Fb2BookColumns.CURRENT_PART_ID + " TEXT NOT NULL, "
      + Fb2BookColumns.PATH_TOC + " TEXT "
      + " );";
  public static final String SQL_CREATE_TABLE_TXT_BOOK = "CREATE TABLE IF NOT" +
      " EXISTS "
      + TxtBookColumns.TABLE_NAME + " ( "
      + TxtBookColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
      + TxtBookColumns.BYTE_POSITION + " INTEGER NOT NULL "
      + " );";
  private static final String TAG = ReadilySQLiteOpenHelper.class
      .getSimpleName();
  private static final int DATABASE_VERSION = 3;
  private static ReadilySQLiteOpenHelper sInstance;
  private final Context mContext;
  private final ReadilySQLiteOpenHelperCallbacks mOpenHelperCallbacks;

  // @formatter:on

  private ReadilySQLiteOpenHelper(Context context) {
    super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
    mContext = context;
    mOpenHelperCallbacks = new ReadilySQLiteOpenHelperCallbacks();
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private ReadilySQLiteOpenHelper(Context context,
                                  DatabaseErrorHandler errorHandler) {
    super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION, errorHandler);
    mContext = context;
    mOpenHelperCallbacks = new ReadilySQLiteOpenHelperCallbacks();
  }

  public static ReadilySQLiteOpenHelper getInstance(Context context) {
    // Use the application context, which will ensure that you
    // don't accidentally leak an Activity's context.
    // See this article for more information: http://bit.ly/6LRzfx
    if (sInstance == null) {
      sInstance = newInstance(context.getApplicationContext());
    }
    return sInstance;
  }

  private static ReadilySQLiteOpenHelper newInstance(Context context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
      return newInstancePreHoneycomb(context);
    }
    return newInstancePostHoneycomb(context);
  }

  /*
   * Pre Honeycomb.
   */
  private static ReadilySQLiteOpenHelper newInstancePreHoneycomb(
      Context context) {
    return new ReadilySQLiteOpenHelper(context);
  }

  /*
   * Post Honeycomb.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private static ReadilySQLiteOpenHelper newInstancePostHoneycomb(
      Context context) {
    return new ReadilySQLiteOpenHelper(context,
                                       new DefaultDatabaseErrorHandler());
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    if (BuildConfig.DEBUG) Log.d(TAG, "onCreate");
    mOpenHelperCallbacks.onPreCreate(mContext, db);
    db.execSQL(SQL_CREATE_TABLE_CACHED_BOOK);
    db.execSQL(SQL_CREATE_TABLE_CACHED_BOOK_INFO);
    db.execSQL(SQL_CREATE_TABLE_EPUB_BOOK);
    db.execSQL(SQL_CREATE_TABLE_FB2_BOOK);
    db.execSQL(SQL_CREATE_TABLE_TXT_BOOK);
    mOpenHelperCallbacks.onPostCreate(mContext, db);
  }

  @Override
  public void onOpen(SQLiteDatabase db) {
    super.onOpen(db);
    if (!db.isReadOnly()) {
      setForeignKeyConstraintsEnabled(db);
    }
    mOpenHelperCallbacks.onOpen(mContext, db);
  }

  private void setForeignKeyConstraintsEnabled(SQLiteDatabase db) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
      setForeignKeyConstraintsEnabledPreJellyBean(db);
    } else {
      setForeignKeyConstraintsEnabledPostJellyBean(db);
    }
  }

  private void setForeignKeyConstraintsEnabledPreJellyBean(SQLiteDatabase db) {
    db.execSQL("PRAGMA foreign_keys=ON;");
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  private void setForeignKeyConstraintsEnabledPostJellyBean(SQLiteDatabase db) {
    db.setForeignKeyConstraintsEnabled(true);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    mOpenHelperCallbacks.onUpgrade(mContext, db, oldVersion, newVersion);
  }
}
