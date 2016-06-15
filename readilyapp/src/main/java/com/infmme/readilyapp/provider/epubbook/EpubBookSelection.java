package com.infmme.readilyapp.provider.epubbook;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.infmme.readilyapp.provider.base.AbstractSelection;

/**
 * Selection for the {@code epub_book} table.
 */
public class EpubBookSelection extends AbstractSelection<EpubBookSelection> {
  @Override
  protected Uri baseUri() {
    return EpubBookColumns.CONTENT_URI;
  }

  /**
   * Query the given content resolver using this selection.
   *
   * @param contentResolver The content resolver to query.
   * @param projection      A list of which columns to return. Passing null
   *                        will return all columns, which is inefficient.
   * @return A {@code EpubBookCursor} object, which is positioned before
   * the first entry, or null.
   */
  public EpubBookCursor query(ContentResolver contentResolver,
                              String[] projection) {
    Cursor cursor = contentResolver.query(uri(), projection, sel(), args(),
                                          order());
    if (cursor == null) return null;
    return new EpubBookCursor(cursor);
  }

  /**
   * Equivalent of calling {@code query(contentResolver, null)}.
   */
  public EpubBookCursor query(ContentResolver contentResolver) {
    return query(contentResolver, null);
  }

  /**
   * Query the given content resolver using this selection.
   *
   * @param context    The context to use for the query.
   * @param projection A list of which columns to return. Passing null will
   *                   return all columns, which is inefficient.
   * @return A {@code EpubBookCursor} object, which is positioned before the
   * first entry, or null.
   */
  public EpubBookCursor query(Context context, String[] projection) {
    Cursor cursor = context.getContentResolver()
                           .query(uri(), projection, sel(), args(), order());
    if (cursor == null) return null;
    return new EpubBookCursor(cursor);
  }

  /**
   * Equivalent of calling {@code query(context, null)}.
   */
  public EpubBookCursor query(Context context) {
    return query(context, null);
  }


  public EpubBookSelection id(long... value) {
    addEquals("epub_book." + EpubBookColumns._ID, toObjectArray(value));
    return this;
  }

  public EpubBookSelection idNot(long... value) {
    addNotEquals("epub_book." + EpubBookColumns._ID, toObjectArray(value));
    return this;
  }

  public EpubBookSelection orderById(boolean desc) {
    orderBy("epub_book." + EpubBookColumns._ID, desc);
    return this;
  }

  public EpubBookSelection orderById() {
    return orderById(false);
  }

  public EpubBookSelection currentResourceId(String... value) {
    addEquals(EpubBookColumns.CURRENT_RESOURCE_ID, value);
    return this;
  }

  public EpubBookSelection currentResourceIdNot(String... value) {
    addNotEquals(EpubBookColumns.CURRENT_RESOURCE_ID, value);
    return this;
  }

  public EpubBookSelection currentResourceIdLike(String... value) {
    addLike(EpubBookColumns.CURRENT_RESOURCE_ID, value);
    return this;
  }

  public EpubBookSelection currentResourceIdContains(String... value) {
    addContains(EpubBookColumns.CURRENT_RESOURCE_ID, value);
    return this;
  }

  public EpubBookSelection currentResourceIdStartsWith(String... value) {
    addStartsWith(EpubBookColumns.CURRENT_RESOURCE_ID, value);
    return this;
  }

  public EpubBookSelection currentResourceIdEndsWith(String... value) {
    addEndsWith(EpubBookColumns.CURRENT_RESOURCE_ID, value);
    return this;
  }

  public EpubBookSelection orderByCurrentResourceId(boolean desc) {
    orderBy(EpubBookColumns.CURRENT_RESOURCE_ID, desc);
    return this;
  }

  public EpubBookSelection orderByCurrentResourceId() {
    orderBy(EpubBookColumns.CURRENT_RESOURCE_ID, false);
    return this;
  }

  public EpubBookSelection textPosition(int... value) {
    addEquals(EpubBookColumns.TEXT_POSITION, toObjectArray(value));
    return this;
  }

  public EpubBookSelection textPositionNot(int... value) {
    addNotEquals(EpubBookColumns.TEXT_POSITION, toObjectArray(value));
    return this;
  }

  public EpubBookSelection textPositionGt(int value) {
    addGreaterThan(EpubBookColumns.TEXT_POSITION, value);
    return this;
  }

  public EpubBookSelection textPositionGtEq(int value) {
    addGreaterThanOrEquals(EpubBookColumns.TEXT_POSITION, value);
    return this;
  }

  public EpubBookSelection textPositionLt(int value) {
    addLessThan(EpubBookColumns.TEXT_POSITION, value);
    return this;
  }

  public EpubBookSelection textPositionLtEq(int value) {
    addLessThanOrEquals(EpubBookColumns.TEXT_POSITION, value);
    return this;
  }

  public EpubBookSelection orderByTextPosition(boolean desc) {
    orderBy(EpubBookColumns.TEXT_POSITION, desc);
    return this;
  }

  public EpubBookSelection orderByTextPosition() {
    orderBy(EpubBookColumns.TEXT_POSITION, false);
    return this;
  }

  public EpubBookSelection coverImageUri(String... value) {
    addEquals(EpubBookColumns.COVER_IMAGE_URI, value);
    return this;
  }

  public EpubBookSelection coverImageUriNot(String... value) {
    addNotEquals(EpubBookColumns.COVER_IMAGE_URI, value);
    return this;
  }

  public EpubBookSelection coverImageUriLike(String... value) {
    addLike(EpubBookColumns.COVER_IMAGE_URI, value);
    return this;
  }

  public EpubBookSelection coverImageUriContains(String... value) {
    addContains(EpubBookColumns.COVER_IMAGE_URI, value);
    return this;
  }

  public EpubBookSelection coverImageUriStartsWith(String... value) {
    addStartsWith(EpubBookColumns.COVER_IMAGE_URI, value);
    return this;
  }

  public EpubBookSelection coverImageUriEndsWith(String... value) {
    addEndsWith(EpubBookColumns.COVER_IMAGE_URI, value);
    return this;
  }

  public EpubBookSelection orderByCoverImageUri(boolean desc) {
    orderBy(EpubBookColumns.COVER_IMAGE_URI, desc);
    return this;
  }

  public EpubBookSelection orderByCoverImageUri() {
    orderBy(EpubBookColumns.COVER_IMAGE_URI, false);
    return this;
  }
}
