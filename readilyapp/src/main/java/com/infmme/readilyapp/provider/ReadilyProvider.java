package com.infmme.readilyapp.provider;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import com.infmme.readilyapp.BuildConfig;
import com.infmme.readilyapp.provider.base.BaseContentProvider;
import com.infmme.readilyapp.provider.cachedbook.CachedBookColumns;
import com.infmme.readilyapp.provider.epubbook.EpubBookColumns;
import com.infmme.readilyapp.provider.fb2book.Fb2BookColumns;
import com.infmme.readilyapp.provider.txtbook.TxtBookColumns;

import java.util.Arrays;

public class ReadilyProvider extends BaseContentProvider {
  public static final String AUTHORITY = "com.infmme.readilyapp.provider";
  public static final String CONTENT_URI_BASE = "content://" + AUTHORITY;
  private static final String TAG = ReadilyProvider.class.getSimpleName();
  private static final boolean DEBUG = BuildConfig.DEBUG;
  private static final String TYPE_CURSOR_ITEM = "vnd.android.cursor.item/";
  private static final String TYPE_CURSOR_DIR = "vnd.android.cursor.dir/";
  private static final int URI_TYPE_CACHED_BOOK = 0;
  private static final int URI_TYPE_CACHED_BOOK_ID = 1;

  private static final int URI_TYPE_EPUB_BOOK = 2;
  private static final int URI_TYPE_EPUB_BOOK_ID = 3;

  private static final int URI_TYPE_FB2_BOOK = 4;
  private static final int URI_TYPE_FB2_BOOK_ID = 5;

  private static final int URI_TYPE_TXT_BOOK = 6;
  private static final int URI_TYPE_TXT_BOOK_ID = 7;


  private static final UriMatcher URI_MATCHER = new UriMatcher(
      UriMatcher.NO_MATCH);

  static {
    URI_MATCHER.addURI(AUTHORITY, CachedBookColumns.TABLE_NAME,
                       URI_TYPE_CACHED_BOOK);
    URI_MATCHER.addURI(AUTHORITY, CachedBookColumns.TABLE_NAME + "/#",
                       URI_TYPE_CACHED_BOOK_ID);
    URI_MATCHER.addURI(AUTHORITY, EpubBookColumns.TABLE_NAME,
                       URI_TYPE_EPUB_BOOK);
    URI_MATCHER.addURI(AUTHORITY, EpubBookColumns.TABLE_NAME + "/#",
                       URI_TYPE_EPUB_BOOK_ID);
    URI_MATCHER.addURI(AUTHORITY, Fb2BookColumns.TABLE_NAME, URI_TYPE_FB2_BOOK);
    URI_MATCHER.addURI(AUTHORITY, Fb2BookColumns.TABLE_NAME + "/#",
                       URI_TYPE_FB2_BOOK_ID);
    URI_MATCHER.addURI(AUTHORITY, TxtBookColumns.TABLE_NAME, URI_TYPE_TXT_BOOK);
    URI_MATCHER.addURI(AUTHORITY, TxtBookColumns.TABLE_NAME + "/#",
                       URI_TYPE_TXT_BOOK_ID);
  }

  @Override
  protected SQLiteOpenHelper createSqLiteOpenHelper() {
    return ReadilySQLiteOpenHelper.getInstance(getContext());
  }

  @Override
  protected boolean hasDebug() {
    return DEBUG;
  }

  @Override
  public String getType(Uri uri) {
    int match = URI_MATCHER.match(uri);
    switch (match) {
      case URI_TYPE_CACHED_BOOK:
        return TYPE_CURSOR_DIR + CachedBookColumns.TABLE_NAME;
      case URI_TYPE_CACHED_BOOK_ID:
        return TYPE_CURSOR_ITEM + CachedBookColumns.TABLE_NAME;

      case URI_TYPE_EPUB_BOOK:
        return TYPE_CURSOR_DIR + EpubBookColumns.TABLE_NAME;
      case URI_TYPE_EPUB_BOOK_ID:
        return TYPE_CURSOR_ITEM + EpubBookColumns.TABLE_NAME;

      case URI_TYPE_FB2_BOOK:
        return TYPE_CURSOR_DIR + Fb2BookColumns.TABLE_NAME;
      case URI_TYPE_FB2_BOOK_ID:
        return TYPE_CURSOR_ITEM + Fb2BookColumns.TABLE_NAME;

      case URI_TYPE_TXT_BOOK:
        return TYPE_CURSOR_DIR + TxtBookColumns.TABLE_NAME;
      case URI_TYPE_TXT_BOOK_ID:
        return TYPE_CURSOR_ITEM + TxtBookColumns.TABLE_NAME;

    }
    return null;
  }

  @Override
  public Uri insert(Uri uri, ContentValues values) {
    if (DEBUG) Log.d(TAG, "insert uri=" + uri + " values=" + values);
    return super.insert(uri, values);
  }

  @Override
  public int bulkInsert(Uri uri, ContentValues[] values) {
    if (DEBUG)
      Log.d(TAG, "bulkInsert uri=" + uri + " values.length=" + values.length);
    return super.bulkInsert(uri, values);
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection,
                    String[] selectionArgs) {
    if (DEBUG) Log.d(TAG,
                     "update uri=" + uri + " values=" + values + " " +
                         "selection=" + selection + " selectionArgs=" + Arrays
                         .toString(selectionArgs));
    return super.update(uri, values, selection, selectionArgs);
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    if (DEBUG) Log.d(TAG,
                     "delete uri=" + uri + " selection=" + selection + " " +
                         "selectionArgs=" + Arrays
                         .toString(selectionArgs));
    return super.delete(uri, selection, selectionArgs);
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection,
                      String[] selectionArgs, String sortOrder) {
    if (DEBUG)
      Log.d(TAG,
            "query uri=" + uri + " selection=" + selection + " " +
                "selectionArgs=" + Arrays
                .toString(selectionArgs) + " sortOrder=" + sortOrder
                + " groupBy=" + uri.getQueryParameter(
                QUERY_GROUP_BY) + " having=" + uri.getQueryParameter(
                QUERY_HAVING) + " limit=" + uri.getQueryParameter(QUERY_LIMIT));
    return super.query(uri, projection, selection, selectionArgs, sortOrder);
  }

  @Override
  protected QueryParams getQueryParams(Uri uri, String selection,
                                       String[] projection) {
    QueryParams res = new QueryParams();
    String id = null;
    int matchedId = URI_MATCHER.match(uri);
    switch (matchedId) {
      case URI_TYPE_CACHED_BOOK:
      case URI_TYPE_CACHED_BOOK_ID:
        res.table = CachedBookColumns.TABLE_NAME;
        res.idColumn = CachedBookColumns._ID;
        res.tablesWithJoins = CachedBookColumns.TABLE_NAME;
        if (EpubBookColumns.hasColumns(projection)) {
          res.tablesWithJoins += " LEFT OUTER JOIN " + EpubBookColumns
              .TABLE_NAME + " AS " + CachedBookColumns.PREFIX_EPUB_BOOK + " " +
              "ON " + CachedBookColumns.TABLE_NAME + "." + CachedBookColumns
              .EPUB_BOOK_ID + "=" + CachedBookColumns.PREFIX_EPUB_BOOK + "."
              + EpubBookColumns._ID;
        }
        if (Fb2BookColumns.hasColumns(projection)) {
          res.tablesWithJoins += " LEFT OUTER JOIN " + Fb2BookColumns
              .TABLE_NAME + " AS " + CachedBookColumns.PREFIX_FB2_BOOK + " ON" +
              " " + CachedBookColumns.TABLE_NAME + "." + CachedBookColumns
              .FB2_BOOK_ID + "=" + CachedBookColumns.PREFIX_FB2_BOOK + "." +
              Fb2BookColumns._ID;
        }
        if (TxtBookColumns.hasColumns(projection)) {
          res.tablesWithJoins += " LEFT OUTER JOIN " + TxtBookColumns
              .TABLE_NAME + " AS " + CachedBookColumns.PREFIX_TXT_BOOK + " ON" +
              " " + CachedBookColumns.TABLE_NAME + "." + CachedBookColumns
              .TXT_BOOK_ID + "=" + CachedBookColumns.PREFIX_TXT_BOOK + "." +
              TxtBookColumns._ID;
        }
        res.orderBy = CachedBookColumns.DEFAULT_ORDER;
        break;

      case URI_TYPE_EPUB_BOOK:
      case URI_TYPE_EPUB_BOOK_ID:
        res.table = EpubBookColumns.TABLE_NAME;
        res.idColumn = EpubBookColumns._ID;
        res.tablesWithJoins = EpubBookColumns.TABLE_NAME;
        res.orderBy = EpubBookColumns.DEFAULT_ORDER;
        break;

      case URI_TYPE_FB2_BOOK:
      case URI_TYPE_FB2_BOOK_ID:
        res.table = Fb2BookColumns.TABLE_NAME;
        res.idColumn = Fb2BookColumns._ID;
        res.tablesWithJoins = Fb2BookColumns.TABLE_NAME;
        res.orderBy = Fb2BookColumns.DEFAULT_ORDER;
        break;

      case URI_TYPE_TXT_BOOK:
      case URI_TYPE_TXT_BOOK_ID:
        res.table = TxtBookColumns.TABLE_NAME;
        res.idColumn = TxtBookColumns._ID;
        res.tablesWithJoins = TxtBookColumns.TABLE_NAME;
        res.orderBy = TxtBookColumns.DEFAULT_ORDER;
        break;

      default:
        throw new IllegalArgumentException(
            "The uri '" + uri + "' is not supported by this ContentProvider");
    }

    switch (matchedId) {
      case URI_TYPE_CACHED_BOOK_ID:
      case URI_TYPE_EPUB_BOOK_ID:
      case URI_TYPE_FB2_BOOK_ID:
      case URI_TYPE_TXT_BOOK_ID:
        id = uri.getLastPathSegment();
    }
    if (id != null) {
      if (selection != null) {
        res.selection = res.table + "." + res.idColumn + "=" + id + " and ("
            + selection + ")";
      } else {
        res.selection = res.table + "." + res.idColumn + "=" + id;
      }
    } else {
      res.selection = selection;
    }
    return res;
  }
}
