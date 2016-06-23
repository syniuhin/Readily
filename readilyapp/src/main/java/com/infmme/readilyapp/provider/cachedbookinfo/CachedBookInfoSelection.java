package com.infmme.readilyapp.provider.cachedbookinfo;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.infmme.readilyapp.provider.base.AbstractSelection;

/**
 * Selection for the {@code cached_book_info} table.
 */
public class CachedBookInfoSelection
    extends AbstractSelection<CachedBookInfoSelection> {
  @Override
  protected Uri baseUri() {
    return CachedBookInfoColumns.CONTENT_URI;
  }

  /**
   * Query the given content resolver using this selection.
   *
   * @param contentResolver The content resolver to query.
   * @param projection      A list of which columns to return. Passing null
   *                        will return all columns, which is inefficient.
   * @return A {@code CachedBookInfoCursor} object, which is positioned
   * before the first entry, or null.
   */
  public CachedBookInfoCursor query(ContentResolver contentResolver,
                                    String[] projection) {
    @SuppressLint("Recycle")
    Cursor cursor = contentResolver.query(uri(), projection, sel(), args(),
                                          order());
    if (cursor == null) return null;
    return new CachedBookInfoCursor(cursor);
  }

  /**
   * Equivalent of calling {@code query(contentResolver, null)}.
   */
  public CachedBookInfoCursor query(ContentResolver contentResolver) {
    return query(contentResolver, null);
  }

  /**
   * Query the given content resolver using this selection.
   *
   * @param context    The context to use for the query.
   * @param projection A list of which columns to return. Passing null will
   *                   return all columns, which is inefficient.
   * @return A {@code CachedBookInfoCursor} object, which is positioned
   * before the first entry, or null.
   */
  public CachedBookInfoCursor query(Context context, String[] projection) {
    @SuppressLint("Recycle")
    Cursor cursor = context.getContentResolver()
                           .query(uri(), projection, sel(), args(), order());
    if (cursor == null) return null;
    return new CachedBookInfoCursor(cursor);
  }

  /**
   * Equivalent of calling {@code query(context, null)}.
   */
  public CachedBookInfoCursor query(Context context) {
    return query(context, null);
  }


  public CachedBookInfoSelection id(long... value) {
    addEquals("cached_book_info." + CachedBookInfoColumns._ID,
              toObjectArray(value));
    return this;
  }

  public CachedBookInfoSelection idNot(long... value) {
    addNotEquals("cached_book_info." + CachedBookInfoColumns._ID,
                 toObjectArray(value));
    return this;
  }

  public CachedBookInfoSelection orderById(boolean desc) {
    orderBy("cached_book_info." + CachedBookInfoColumns._ID, desc);
    return this;
  }

  public CachedBookInfoSelection orderById() {
    return orderById(false);
  }

  public CachedBookInfoSelection author(String... value) {
    addEquals(CachedBookInfoColumns.AUTHOR, value);
    return this;
  }

  public CachedBookInfoSelection authorNot(String... value) {
    addNotEquals(CachedBookInfoColumns.AUTHOR, value);
    return this;
  }

  public CachedBookInfoSelection authorLike(String... value) {
    addLike(CachedBookInfoColumns.AUTHOR, value);
    return this;
  }

  public CachedBookInfoSelection authorContains(String... value) {
    addContains(CachedBookInfoColumns.AUTHOR, value);
    return this;
  }

  public CachedBookInfoSelection authorStartsWith(String... value) {
    addStartsWith(CachedBookInfoColumns.AUTHOR, value);
    return this;
  }

  public CachedBookInfoSelection authorEndsWith(String... value) {
    addEndsWith(CachedBookInfoColumns.AUTHOR, value);
    return this;
  }

  public CachedBookInfoSelection orderByAuthor(boolean desc) {
    orderBy(CachedBookInfoColumns.AUTHOR, desc);
    return this;
  }

  public CachedBookInfoSelection orderByAuthor() {
    orderBy(CachedBookInfoColumns.AUTHOR, false);
    return this;
  }

  public CachedBookInfoSelection genre(String... value) {
    addEquals(CachedBookInfoColumns.GENRE, value);
    return this;
  }

  public CachedBookInfoSelection genreNot(String... value) {
    addNotEquals(CachedBookInfoColumns.GENRE, value);
    return this;
  }

  public CachedBookInfoSelection genreLike(String... value) {
    addLike(CachedBookInfoColumns.GENRE, value);
    return this;
  }

  public CachedBookInfoSelection genreContains(String... value) {
    addContains(CachedBookInfoColumns.GENRE, value);
    return this;
  }

  public CachedBookInfoSelection genreStartsWith(String... value) {
    addStartsWith(CachedBookInfoColumns.GENRE, value);
    return this;
  }

  public CachedBookInfoSelection genreEndsWith(String... value) {
    addEndsWith(CachedBookInfoColumns.GENRE, value);
    return this;
  }

  public CachedBookInfoSelection orderByGenre(boolean desc) {
    orderBy(CachedBookInfoColumns.GENRE, desc);
    return this;
  }

  public CachedBookInfoSelection orderByGenre() {
    orderBy(CachedBookInfoColumns.GENRE, false);
    return this;
  }

  public CachedBookInfoSelection language(String... value) {
    addEquals(CachedBookInfoColumns.LANGUAGE, value);
    return this;
  }

  public CachedBookInfoSelection languageNot(String... value) {
    addNotEquals(CachedBookInfoColumns.LANGUAGE, value);
    return this;
  }

  public CachedBookInfoSelection languageLike(String... value) {
    addLike(CachedBookInfoColumns.LANGUAGE, value);
    return this;
  }

  public CachedBookInfoSelection languageContains(String... value) {
    addContains(CachedBookInfoColumns.LANGUAGE, value);
    return this;
  }

  public CachedBookInfoSelection languageStartsWith(String... value) {
    addStartsWith(CachedBookInfoColumns.LANGUAGE, value);
    return this;
  }

  public CachedBookInfoSelection languageEndsWith(String... value) {
    addEndsWith(CachedBookInfoColumns.LANGUAGE, value);
    return this;
  }

  public CachedBookInfoSelection orderByLanguage(boolean desc) {
    orderBy(CachedBookInfoColumns.LANGUAGE, desc);
    return this;
  }

  public CachedBookInfoSelection orderByLanguage() {
    orderBy(CachedBookInfoColumns.LANGUAGE, false);
    return this;
  }

  public CachedBookInfoSelection currentPartTitle(String... value) {
    addEquals(CachedBookInfoColumns.CURRENT_PART_TITLE, value);
    return this;
  }

  public CachedBookInfoSelection currentPartTitleNot(String... value) {
    addNotEquals(CachedBookInfoColumns.CURRENT_PART_TITLE, value);
    return this;
  }

  public CachedBookInfoSelection currentPartTitleLike(String... value) {
    addLike(CachedBookInfoColumns.CURRENT_PART_TITLE, value);
    return this;
  }

  public CachedBookInfoSelection currentPartTitleContains(String... value) {
    addContains(CachedBookInfoColumns.CURRENT_PART_TITLE, value);
    return this;
  }

  public CachedBookInfoSelection currentPartTitleStartsWith(String... value) {
    addStartsWith(CachedBookInfoColumns.CURRENT_PART_TITLE, value);
    return this;
  }

  public CachedBookInfoSelection currentPartTitleEndsWith(String... value) {
    addEndsWith(CachedBookInfoColumns.CURRENT_PART_TITLE, value);
    return this;
  }

  public CachedBookInfoSelection orderByCurrentPartTitle(boolean desc) {
    orderBy(CachedBookInfoColumns.CURRENT_PART_TITLE, desc);
    return this;
  }

  public CachedBookInfoSelection orderByCurrentPartTitle() {
    orderBy(CachedBookInfoColumns.CURRENT_PART_TITLE, false);
    return this;
  }

  public CachedBookInfoSelection description(String... value) {
    addEquals(CachedBookInfoColumns.DESCRIPTION, value);
    return this;
  }

  public CachedBookInfoSelection descriptionNot(String... value) {
    addNotEquals(CachedBookInfoColumns.DESCRIPTION, value);
    return this;
  }

  public CachedBookInfoSelection descriptionLike(String... value) {
    addLike(CachedBookInfoColumns.DESCRIPTION, value);
    return this;
  }

  public CachedBookInfoSelection descriptionContains(String... value) {
    addContains(CachedBookInfoColumns.DESCRIPTION, value);
    return this;
  }

  public CachedBookInfoSelection descriptionStartsWith(String... value) {
    addStartsWith(CachedBookInfoColumns.DESCRIPTION, value);
    return this;
  }

  public CachedBookInfoSelection descriptionEndsWith(String... value) {
    addEndsWith(CachedBookInfoColumns.DESCRIPTION, value);
    return this;
  }

  public CachedBookInfoSelection orderByDescription(boolean desc) {
    orderBy(CachedBookInfoColumns.DESCRIPTION, desc);
    return this;
  }

  public CachedBookInfoSelection orderByDescription() {
    orderBy(CachedBookInfoColumns.DESCRIPTION, false);
    return this;
  }
}
