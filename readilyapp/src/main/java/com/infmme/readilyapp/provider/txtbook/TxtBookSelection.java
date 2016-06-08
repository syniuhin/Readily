package com.infmme.readilyapp.provider.txtbook;

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

  public TxtBookSelection title(String... value) {
    addEquals(TxtBookColumns.TITLE, value);
    return this;
  }

  public TxtBookSelection titleNot(String... value) {
    addNotEquals(TxtBookColumns.TITLE, value);
    return this;
  }

  public TxtBookSelection titleLike(String... value) {
    addLike(TxtBookColumns.TITLE, value);
    return this;
  }

  public TxtBookSelection titleContains(String... value) {
    addContains(TxtBookColumns.TITLE, value);
    return this;
  }

  public TxtBookSelection titleStartsWith(String... value) {
    addStartsWith(TxtBookColumns.TITLE, value);
    return this;
  }

  public TxtBookSelection titleEndsWith(String... value) {
    addEndsWith(TxtBookColumns.TITLE, value);
    return this;
  }

  public TxtBookSelection orderByTitle(boolean desc) {
    orderBy(TxtBookColumns.TITLE, desc);
    return this;
  }

  public TxtBookSelection orderByTitle() {
    orderBy(TxtBookColumns.TITLE, false);
    return this;
  }

  public TxtBookSelection path(String... value) {
    addEquals(TxtBookColumns.PATH, value);
    return this;
  }

  public TxtBookSelection pathNot(String... value) {
    addNotEquals(TxtBookColumns.PATH, value);
    return this;
  }

  public TxtBookSelection pathLike(String... value) {
    addLike(TxtBookColumns.PATH, value);
    return this;
  }

  public TxtBookSelection pathContains(String... value) {
    addContains(TxtBookColumns.PATH, value);
    return this;
  }

  public TxtBookSelection pathStartsWith(String... value) {
    addStartsWith(TxtBookColumns.PATH, value);
    return this;
  }

  public TxtBookSelection pathEndsWith(String... value) {
    addEndsWith(TxtBookColumns.PATH, value);
    return this;
  }

  public TxtBookSelection orderByPath(boolean desc) {
    orderBy(TxtBookColumns.PATH, desc);
    return this;
  }

  public TxtBookSelection orderByPath() {
    orderBy(TxtBookColumns.PATH, false);
    return this;
  }

  public TxtBookSelection textPosition(int... value) {
    addEquals(TxtBookColumns.TEXT_POSITION, toObjectArray(value));
    return this;
  }

  public TxtBookSelection textPositionNot(int... value) {
    addNotEquals(TxtBookColumns.TEXT_POSITION, toObjectArray(value));
    return this;
  }

  public TxtBookSelection textPositionGt(int value) {
    addGreaterThan(TxtBookColumns.TEXT_POSITION, value);
    return this;
  }

  public TxtBookSelection textPositionGtEq(int value) {
    addGreaterThanOrEquals(TxtBookColumns.TEXT_POSITION, value);
    return this;
  }

  public TxtBookSelection textPositionLt(int value) {
    addLessThan(TxtBookColumns.TEXT_POSITION, value);
    return this;
  }

  public TxtBookSelection textPositionLtEq(int value) {
    addLessThanOrEquals(TxtBookColumns.TEXT_POSITION, value);
    return this;
  }

  public TxtBookSelection orderByTextPosition(boolean desc) {
    orderBy(TxtBookColumns.TEXT_POSITION, desc);
    return this;
  }

  public TxtBookSelection orderByTextPosition() {
    orderBy(TxtBookColumns.TEXT_POSITION, false);
    return this;
  }

  public TxtBookSelection percentile(double... value) {
    addEquals(TxtBookColumns.PERCENTILE, toObjectArray(value));
    return this;
  }

  public TxtBookSelection percentileNot(double... value) {
    addNotEquals(TxtBookColumns.PERCENTILE, toObjectArray(value));
    return this;
  }

  public TxtBookSelection percentileGt(double value) {
    addGreaterThan(TxtBookColumns.PERCENTILE, value);
    return this;
  }

  public TxtBookSelection percentileGtEq(double value) {
    addGreaterThanOrEquals(TxtBookColumns.PERCENTILE, value);
    return this;
  }

  public TxtBookSelection percentileLt(double value) {
    addLessThan(TxtBookColumns.PERCENTILE, value);
    return this;
  }

  public TxtBookSelection percentileLtEq(double value) {
    addLessThanOrEquals(TxtBookColumns.PERCENTILE, value);
    return this;
  }

  public TxtBookSelection orderByPercentile(boolean desc) {
    orderBy(TxtBookColumns.PERCENTILE, desc);
    return this;
  }

  public TxtBookSelection orderByPercentile() {
    orderBy(TxtBookColumns.PERCENTILE, false);
    return this;
  }

  public TxtBookSelection timeOpened(String... value) {
    addEquals(TxtBookColumns.TIME_OPENED, value);
    return this;
  }

  public TxtBookSelection timeOpenedNot(String... value) {
    addNotEquals(TxtBookColumns.TIME_OPENED, value);
    return this;
  }

  public TxtBookSelection timeOpenedLike(String... value) {
    addLike(TxtBookColumns.TIME_OPENED, value);
    return this;
  }

  public TxtBookSelection timeOpenedContains(String... value) {
    addContains(TxtBookColumns.TIME_OPENED, value);
    return this;
  }

  public TxtBookSelection timeOpenedStartsWith(String... value) {
    addStartsWith(TxtBookColumns.TIME_OPENED, value);
    return this;
  }

  public TxtBookSelection timeOpenedEndsWith(String... value) {
    addEndsWith(TxtBookColumns.TIME_OPENED, value);
    return this;
  }

  public TxtBookSelection orderByTimeOpened(boolean desc) {
    orderBy(TxtBookColumns.TIME_OPENED, desc);
    return this;
  }

  public TxtBookSelection orderByTimeOpened() {
    orderBy(TxtBookColumns.TIME_OPENED, false);
    return this;
  }
}
