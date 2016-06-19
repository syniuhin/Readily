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

  public Fb2BookSelection fullyProcessed(boolean value) {
    addEquals(Fb2BookColumns.FULLY_PROCESSED, toObjectArray(value));
    return this;
  }

  public Fb2BookSelection orderByFullyProcessed(boolean desc) {
    orderBy(Fb2BookColumns.FULLY_PROCESSED, desc);
    return this;
  }

  public Fb2BookSelection orderByFullyProcessed() {
    orderBy(Fb2BookColumns.FULLY_PROCESSED, false);
    return this;
  }

  public Fb2BookSelection fullyProcessingSuccess(Boolean value) {
    addEquals(Fb2BookColumns.FULLY_PROCESSING_SUCCESS, toObjectArray(value));
    return this;
  }

  public Fb2BookSelection orderByFullyProcessingSuccess(boolean desc) {
    orderBy(Fb2BookColumns.FULLY_PROCESSING_SUCCESS, desc);
    return this;
  }

  public Fb2BookSelection orderByFullyProcessingSuccess() {
    orderBy(Fb2BookColumns.FULLY_PROCESSING_SUCCESS, false);
    return this;
  }

  public Fb2BookSelection bytePosition(int... value) {
    addEquals(Fb2BookColumns.BYTE_POSITION, toObjectArray(value));
    return this;
  }

  public Fb2BookSelection bytePositionNot(int... value) {
    addNotEquals(Fb2BookColumns.BYTE_POSITION, toObjectArray(value));
    return this;
  }

  public Fb2BookSelection bytePositionGt(int value) {
    addGreaterThan(Fb2BookColumns.BYTE_POSITION, value);
    return this;
  }

  public Fb2BookSelection bytePositionGtEq(int value) {
    addGreaterThanOrEquals(Fb2BookColumns.BYTE_POSITION, value);
    return this;
  }

  public Fb2BookSelection bytePositionLt(int value) {
    addLessThan(Fb2BookColumns.BYTE_POSITION, value);
    return this;
  }

  public Fb2BookSelection bytePositionLtEq(int value) {
    addLessThanOrEquals(Fb2BookColumns.BYTE_POSITION, value);
    return this;
  }

  public Fb2BookSelection orderByBytePosition(boolean desc) {
    orderBy(Fb2BookColumns.BYTE_POSITION, desc);
    return this;
  }

  public Fb2BookSelection orderByBytePosition() {
    orderBy(Fb2BookColumns.BYTE_POSITION, false);
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
}
