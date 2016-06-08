package com.infmme.readilyapp.provider.fb2book;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.infmme.readilyapp.provider.base.AbstractSelection;

/**
 * Selection for the {@code fb2_book} table.
 */
public class Fb2BookSelection extends AbstractSelection<Fb2BookSelection> {
  @Override
  protected Uri baseUri() {
    return Fb2BookColumns.CONTENT_URI;
  }

  /**
   * Query the given content resolver using this selection.
   *
   * @param contentResolver The content resolver to query.
   * @param projection      A list of which columns to return. Passing null
   *                        will return all columns, which is inefficient.
   * @return A {@code Fb2BookCursor} object, which is positioned before the
   * first entry, or null.
   */
  public Fb2BookCursor query(ContentResolver contentResolver,
                             String[] projection) {
    Cursor cursor = contentResolver.query(uri(), projection, sel(), args(),
                                          order());
    if (cursor == null) return null;
    return new Fb2BookCursor(cursor);
  }

  /**
   * Equivalent of calling {@code query(contentResolver, null)}.
   */
  public Fb2BookCursor query(ContentResolver contentResolver) {
    return query(contentResolver, null);
  }

  /**
   * Query the given content resolver using this selection.
   *
   * @param context    The context to use for the query.
   * @param projection A list of which columns to return. Passing null will
   *                   return all columns, which is inefficient.
   * @return A {@code Fb2BookCursor} object, which is positioned before the
   * first entry, or null.
   */
  public Fb2BookCursor query(Context context, String[] projection) {
    Cursor cursor = context.getContentResolver()
                           .query(uri(), projection, sel(), args(), order());
    if (cursor == null) return null;
    return new Fb2BookCursor(cursor);
  }

  /**
   * Equivalent of calling {@code query(context, null)}.
   */
  public Fb2BookCursor query(Context context) {
    return query(context, null);
  }


  public Fb2BookSelection id(long... value) {
    addEquals("fb2_book." + Fb2BookColumns._ID, toObjectArray(value));
    return this;
  }

  public Fb2BookSelection idNot(long... value) {
    addNotEquals("fb2_book." + Fb2BookColumns._ID, toObjectArray(value));
    return this;
  }

  public Fb2BookSelection orderById(boolean desc) {
    orderBy("fb2_book." + Fb2BookColumns._ID, desc);
    return this;
  }

  public Fb2BookSelection orderById() {
    return orderById(false);
  }

  public Fb2BookSelection title(String... value) {
    addEquals(Fb2BookColumns.TITLE, value);
    return this;
  }

  public Fb2BookSelection titleNot(String... value) {
    addNotEquals(Fb2BookColumns.TITLE, value);
    return this;
  }

  public Fb2BookSelection titleLike(String... value) {
    addLike(Fb2BookColumns.TITLE, value);
    return this;
  }

  public Fb2BookSelection titleContains(String... value) {
    addContains(Fb2BookColumns.TITLE, value);
    return this;
  }

  public Fb2BookSelection titleStartsWith(String... value) {
    addStartsWith(Fb2BookColumns.TITLE, value);
    return this;
  }

  public Fb2BookSelection titleEndsWith(String... value) {
    addEndsWith(Fb2BookColumns.TITLE, value);
    return this;
  }

  public Fb2BookSelection orderByTitle(boolean desc) {
    orderBy(Fb2BookColumns.TITLE, desc);
    return this;
  }

  public Fb2BookSelection orderByTitle() {
    orderBy(Fb2BookColumns.TITLE, false);
    return this;
  }

  public Fb2BookSelection path(String... value) {
    addEquals(Fb2BookColumns.PATH, value);
    return this;
  }

  public Fb2BookSelection pathNot(String... value) {
    addNotEquals(Fb2BookColumns.PATH, value);
    return this;
  }

  public Fb2BookSelection pathLike(String... value) {
    addLike(Fb2BookColumns.PATH, value);
    return this;
  }

  public Fb2BookSelection pathContains(String... value) {
    addContains(Fb2BookColumns.PATH, value);
    return this;
  }

  public Fb2BookSelection pathStartsWith(String... value) {
    addStartsWith(Fb2BookColumns.PATH, value);
    return this;
  }

  public Fb2BookSelection pathEndsWith(String... value) {
    addEndsWith(Fb2BookColumns.PATH, value);
    return this;
  }

  public Fb2BookSelection orderByPath(boolean desc) {
    orderBy(Fb2BookColumns.PATH, desc);
    return this;
  }

  public Fb2BookSelection orderByPath() {
    orderBy(Fb2BookColumns.PATH, false);
    return this;
  }

  public Fb2BookSelection currentPartId(String... value) {
    addEquals(Fb2BookColumns.CURRENT_PART_ID, value);
    return this;
  }

  public Fb2BookSelection currentPartIdNot(String... value) {
    addNotEquals(Fb2BookColumns.CURRENT_PART_ID, value);
    return this;
  }

  public Fb2BookSelection currentPartIdLike(String... value) {
    addLike(Fb2BookColumns.CURRENT_PART_ID, value);
    return this;
  }

  public Fb2BookSelection currentPartIdContains(String... value) {
    addContains(Fb2BookColumns.CURRENT_PART_ID, value);
    return this;
  }

  public Fb2BookSelection currentPartIdStartsWith(String... value) {
    addStartsWith(Fb2BookColumns.CURRENT_PART_ID, value);
    return this;
  }

  public Fb2BookSelection currentPartIdEndsWith(String... value) {
    addEndsWith(Fb2BookColumns.CURRENT_PART_ID, value);
    return this;
  }

  public Fb2BookSelection orderByCurrentPartId(boolean desc) {
    orderBy(Fb2BookColumns.CURRENT_PART_ID, desc);
    return this;
  }

  public Fb2BookSelection orderByCurrentPartId() {
    orderBy(Fb2BookColumns.CURRENT_PART_ID, false);
    return this;
  }

  public Fb2BookSelection textPosition(int... value) {
    addEquals(Fb2BookColumns.TEXT_POSITION, toObjectArray(value));
    return this;
  }

  public Fb2BookSelection textPositionNot(int... value) {
    addNotEquals(Fb2BookColumns.TEXT_POSITION, toObjectArray(value));
    return this;
  }

  public Fb2BookSelection textPositionGt(int value) {
    addGreaterThan(Fb2BookColumns.TEXT_POSITION, value);
    return this;
  }

  public Fb2BookSelection textPositionGtEq(int value) {
    addGreaterThanOrEquals(Fb2BookColumns.TEXT_POSITION, value);
    return this;
  }

  public Fb2BookSelection textPositionLt(int value) {
    addLessThan(Fb2BookColumns.TEXT_POSITION, value);
    return this;
  }

  public Fb2BookSelection textPositionLtEq(int value) {
    addLessThanOrEquals(Fb2BookColumns.TEXT_POSITION, value);
    return this;
  }

  public Fb2BookSelection orderByTextPosition(boolean desc) {
    orderBy(Fb2BookColumns.TEXT_POSITION, desc);
    return this;
  }

  public Fb2BookSelection orderByTextPosition() {
    orderBy(Fb2BookColumns.TEXT_POSITION, false);
    return this;
  }

  public Fb2BookSelection percentile(double... value) {
    addEquals(Fb2BookColumns.PERCENTILE, toObjectArray(value));
    return this;
  }

  public Fb2BookSelection percentileNot(double... value) {
    addNotEquals(Fb2BookColumns.PERCENTILE, toObjectArray(value));
    return this;
  }

  public Fb2BookSelection percentileGt(double value) {
    addGreaterThan(Fb2BookColumns.PERCENTILE, value);
    return this;
  }

  public Fb2BookSelection percentileGtEq(double value) {
    addGreaterThanOrEquals(Fb2BookColumns.PERCENTILE, value);
    return this;
  }

  public Fb2BookSelection percentileLt(double value) {
    addLessThan(Fb2BookColumns.PERCENTILE, value);
    return this;
  }

  public Fb2BookSelection percentileLtEq(double value) {
    addLessThanOrEquals(Fb2BookColumns.PERCENTILE, value);
    return this;
  }

  public Fb2BookSelection orderByPercentile(boolean desc) {
    orderBy(Fb2BookColumns.PERCENTILE, desc);
    return this;
  }

  public Fb2BookSelection orderByPercentile() {
    orderBy(Fb2BookColumns.PERCENTILE, false);
    return this;
  }

  public Fb2BookSelection pathToc(String... value) {
    addEquals(Fb2BookColumns.PATH_TOC, value);
    return this;
  }

  public Fb2BookSelection pathTocNot(String... value) {
    addNotEquals(Fb2BookColumns.PATH_TOC, value);
    return this;
  }

  public Fb2BookSelection pathTocLike(String... value) {
    addLike(Fb2BookColumns.PATH_TOC, value);
    return this;
  }

  public Fb2BookSelection pathTocContains(String... value) {
    addContains(Fb2BookColumns.PATH_TOC, value);
    return this;
  }

  public Fb2BookSelection pathTocStartsWith(String... value) {
    addStartsWith(Fb2BookColumns.PATH_TOC, value);
    return this;
  }

  public Fb2BookSelection pathTocEndsWith(String... value) {
    addEndsWith(Fb2BookColumns.PATH_TOC, value);
    return this;
  }

  public Fb2BookSelection orderByPathToc(boolean desc) {
    orderBy(Fb2BookColumns.PATH_TOC, desc);
    return this;
  }

  public Fb2BookSelection orderByPathToc() {
    orderBy(Fb2BookColumns.PATH_TOC, false);
    return this;
  }

  public Fb2BookSelection timeOpened(String... value) {
    addEquals(Fb2BookColumns.TIME_OPENED, value);
    return this;
  }

  public Fb2BookSelection timeOpenedNot(String... value) {
    addNotEquals(Fb2BookColumns.TIME_OPENED, value);
    return this;
  }

  public Fb2BookSelection timeOpenedLike(String... value) {
    addLike(Fb2BookColumns.TIME_OPENED, value);
    return this;
  }

  public Fb2BookSelection timeOpenedContains(String... value) {
    addContains(Fb2BookColumns.TIME_OPENED, value);
    return this;
  }

  public Fb2BookSelection timeOpenedStartsWith(String... value) {
    addStartsWith(Fb2BookColumns.TIME_OPENED, value);
    return this;
  }

  public Fb2BookSelection timeOpenedEndsWith(String... value) {
    addEndsWith(Fb2BookColumns.TIME_OPENED, value);
    return this;
  }

  public Fb2BookSelection orderByTimeOpened(boolean desc) {
    orderBy(Fb2BookColumns.TIME_OPENED, desc);
    return this;
  }

  public Fb2BookSelection orderByTimeOpened() {
    orderBy(Fb2BookColumns.TIME_OPENED, false);
    return this;
  }
}
