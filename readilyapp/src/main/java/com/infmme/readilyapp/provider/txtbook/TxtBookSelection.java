package com.infmme.readilyapp.provider.txtbook;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.infmme.readilyapp.provider.base.AbstractSelection;

/**
 * Selection for the {@code txt_book} table.
 */
public class TxtBookSelection extends AbstractSelection<TxtBookSelection> {
  @Override
  protected Uri baseUri() {
    return TxtBookColumns.CONTENT_URI;
  }

  /**
   * Query the given content resolver using this selection.
   *
   * @param contentResolver The content resolver to query.
   * @param projection      A list of which columns to return. Passing null
   *                        will return all columns, which is inefficient.
   * @return A {@code TxtBookCursor} object, which is positioned before the
   * first entry, or null.
   */
  public TxtBookCursor query(ContentResolver contentResolver,
                             String[] projection) {
    @SuppressLint("Recycle")
    Cursor cursor = contentResolver.query(uri(), projection, sel(), args(),
                                          order());
    if (cursor == null) return null;
    return new TxtBookCursor(cursor);
  }

  /**
   * Equivalent of calling {@code query(contentResolver, null)}.
   */
  public TxtBookCursor query(ContentResolver contentResolver) {
    return query(contentResolver, null);
  }

  /**
   * Query the given content resolver using this selection.
   *
   * @param context    The context to use for the query.
   * @param projection A list of which columns to return. Passing null will
   *                   return all columns, which is inefficient.
   * @return A {@code TxtBookCursor} object, which is positioned before the
   * first entry, or null.
   */
  public TxtBookCursor query(Context context, String[] projection) {
    @SuppressLint("Recycle")
    Cursor cursor = context.getContentResolver()
                           .query(uri(), projection, sel(), args(), order());
    if (cursor == null) return null;
    return new TxtBookCursor(cursor);
  }

  /**
   * Equivalent of calling {@code query(context, null)}.
   */
  public TxtBookCursor query(Context context) {
    return query(context, null);
  }


  public TxtBookSelection id(long... value) {
    addEquals("txt_book." + TxtBookColumns._ID, toObjectArray(value));
    return this;
  }

  public TxtBookSelection idNot(long... value) {
    addNotEquals("txt_book." + TxtBookColumns._ID, toObjectArray(value));
    return this;
  }

  public TxtBookSelection orderById(boolean desc) {
    orderBy("txt_book." + TxtBookColumns._ID, desc);
    return this;
  }

  public TxtBookSelection orderById() {
    return orderById(false);
  }

  public TxtBookSelection bytePosition(int... value) {
    addEquals(TxtBookColumns.BYTE_POSITION, toObjectArray(value));
    return this;
  }

  public TxtBookSelection bytePositionNot(int... value) {
    addNotEquals(TxtBookColumns.BYTE_POSITION, toObjectArray(value));
    return this;
  }

  public TxtBookSelection bytePositionGt(int value) {
    addGreaterThan(TxtBookColumns.BYTE_POSITION, value);
    return this;
  }

  public TxtBookSelection bytePositionGtEq(int value) {
    addGreaterThanOrEquals(TxtBookColumns.BYTE_POSITION, value);
    return this;
  }

  public TxtBookSelection bytePositionLt(int value) {
    addLessThan(TxtBookColumns.BYTE_POSITION, value);
    return this;
  }

  public TxtBookSelection bytePositionLtEq(int value) {
    addLessThanOrEquals(TxtBookColumns.BYTE_POSITION, value);
    return this;
  }

  public TxtBookSelection orderByBytePosition(boolean desc) {
    orderBy(TxtBookColumns.BYTE_POSITION, desc);
    return this;
  }

  public TxtBookSelection orderByBytePosition() {
    orderBy(TxtBookColumns.BYTE_POSITION, false);
    return this;
  }
}
