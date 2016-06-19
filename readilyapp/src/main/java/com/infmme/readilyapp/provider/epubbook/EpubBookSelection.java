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

  public EpubBookSelection currentResourceTitle(String... value) {
    addEquals(EpubBookColumns.CURRENT_RESOURCE_TITLE, value);
    return this;
  }

  public EpubBookSelection currentResourceTitleNot(String... value) {
    addNotEquals(EpubBookColumns.CURRENT_RESOURCE_TITLE, value);
    return this;
  }

  public EpubBookSelection currentResourceTitleLike(String... value) {
    addLike(EpubBookColumns.CURRENT_RESOURCE_TITLE, value);
    return this;
  }

  public EpubBookSelection currentResourceTitleContains(String... value) {
    addContains(EpubBookColumns.CURRENT_RESOURCE_TITLE, value);
    return this;
  }

  public EpubBookSelection currentResourceTitleStartsWith(String... value) {
    addStartsWith(EpubBookColumns.CURRENT_RESOURCE_TITLE, value);
    return this;
  }

  public EpubBookSelection currentResourceTitleEndsWith(String... value) {
    addEndsWith(EpubBookColumns.CURRENT_RESOURCE_TITLE, value);
    return this;
  }

  public EpubBookSelection orderByCurrentResourceTitle(boolean desc) {
    orderBy(EpubBookColumns.CURRENT_RESOURCE_TITLE, desc);
    return this;
  }

  public EpubBookSelection orderByCurrentResourceTitle() {
    orderBy(EpubBookColumns.CURRENT_RESOURCE_TITLE, false);
    return this;
  }
}
