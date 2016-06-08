package com.infmme.readilyapp.provider.cachedbook;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.infmme.readilyapp.provider.base.AbstractSelection;
import com.infmme.readilyapp.provider.epubbook.EpubBookColumns;
import com.infmme.readilyapp.provider.fb2book.Fb2BookColumns;
import com.infmme.readilyapp.provider.txtbook.TxtBookColumns;

/**
 * Selection for the {@code cached_book} table.
 */
public class CachedBookSelection
    extends AbstractSelection<CachedBookSelection> {
  @Override
  protected Uri baseUri() {
    return CachedBookColumns.CONTENT_URI;
  }

  /**
   * Query the given content resolver using this selection.
   *
   * @param contentResolver The content resolver to query.
   * @param projection      A list of which columns to return. Passing null
   *                        will return all columns, which is inefficient.
   * @return A {@code CachedBookCursor} object, which is positioned before
   * the first entry, or null.
   */
  public CachedBookCursor query(ContentResolver contentResolver,
                                String[] projection) {
    Cursor cursor = contentResolver.query(uri(), projection, sel(), args(),
                                          order());
    if (cursor == null) return null;
    return new CachedBookCursor(cursor);
  }

  /**
   * Equivalent of calling {@code query(contentResolver, null)}.
   */
  public CachedBookCursor query(ContentResolver contentResolver) {
    return query(contentResolver, null);
  }

  /**
   * Query the given content resolver using this selection.
   *
   * @param context    The context to use for the query.
   * @param projection A list of which columns to return. Passing null will
   *                   return all columns, which is inefficient.
   * @return A {@code CachedBookCursor} object, which is positioned before
   * the first entry, or null.
   */
  public CachedBookCursor query(Context context, String[] projection) {
    Cursor cursor = context.getContentResolver()
                           .query(uri(), projection, sel(), args(), order());
    if (cursor == null) return null;
    return new CachedBookCursor(cursor);
  }

  /**
   * Equivalent of calling {@code query(context, null)}.
   */
  public CachedBookCursor query(Context context) {
    return query(context, null);
  }


  public CachedBookSelection id(long... value) {
    addEquals("cached_book." + CachedBookColumns._ID, toObjectArray(value));
    return this;
  }

  public CachedBookSelection idNot(long... value) {
    addNotEquals("cached_book." + CachedBookColumns._ID, toObjectArray(value));
    return this;
  }

  public CachedBookSelection orderById(boolean desc) {
    orderBy("cached_book." + CachedBookColumns._ID, desc);
    return this;
  }

  public CachedBookSelection orderById() {
    return orderById(false);
  }

  public CachedBookSelection timeOpened(String... value) {
    addEquals(CachedBookColumns.TIME_OPENED, value);
    return this;
  }

  public CachedBookSelection timeOpenedNot(String... value) {
    addNotEquals(CachedBookColumns.TIME_OPENED, value);
    return this;
  }

  public CachedBookSelection timeOpenedLike(String... value) {
    addLike(CachedBookColumns.TIME_OPENED, value);
    return this;
  }

  public CachedBookSelection timeOpenedContains(String... value) {
    addContains(CachedBookColumns.TIME_OPENED, value);
    return this;
  }

  public CachedBookSelection timeOpenedStartsWith(String... value) {
    addStartsWith(CachedBookColumns.TIME_OPENED, value);
    return this;
  }

  public CachedBookSelection timeOpenedEndsWith(String... value) {
    addEndsWith(CachedBookColumns.TIME_OPENED, value);
    return this;
  }

  public CachedBookSelection orderByTimeOpened(boolean desc) {
    orderBy(CachedBookColumns.TIME_OPENED, desc);
    return this;
  }

  public CachedBookSelection orderByTimeOpened() {
    orderBy(CachedBookColumns.TIME_OPENED, false);
    return this;
  }

  public CachedBookSelection epubBookId(Long... value) {
    addEquals(CachedBookColumns.EPUB_BOOK_ID, value);
    return this;
  }

  public CachedBookSelection epubBookIdNot(Long... value) {
    addNotEquals(CachedBookColumns.EPUB_BOOK_ID, value);
    return this;
  }

  public CachedBookSelection epubBookIdGt(long value) {
    addGreaterThan(CachedBookColumns.EPUB_BOOK_ID, value);
    return this;
  }

  public CachedBookSelection epubBookIdGtEq(long value) {
    addGreaterThanOrEquals(CachedBookColumns.EPUB_BOOK_ID, value);
    return this;
  }

  public CachedBookSelection epubBookIdLt(long value) {
    addLessThan(CachedBookColumns.EPUB_BOOK_ID, value);
    return this;
  }

  public CachedBookSelection epubBookIdLtEq(long value) {
    addLessThanOrEquals(CachedBookColumns.EPUB_BOOK_ID, value);
    return this;
  }

  public CachedBookSelection orderByEpubBookId(boolean desc) {
    orderBy(CachedBookColumns.EPUB_BOOK_ID, desc);
    return this;
  }

  public CachedBookSelection orderByEpubBookId() {
    orderBy(CachedBookColumns.EPUB_BOOK_ID, false);
    return this;
  }

  public CachedBookSelection epubBookTitle(String... value) {
    addEquals(EpubBookColumns.TITLE, value);
    return this;
  }

  public CachedBookSelection epubBookTitleNot(String... value) {
    addNotEquals(EpubBookColumns.TITLE, value);
    return this;
  }

  public CachedBookSelection epubBookTitleLike(String... value) {
    addLike(EpubBookColumns.TITLE, value);
    return this;
  }

  public CachedBookSelection epubBookTitleContains(String... value) {
    addContains(EpubBookColumns.TITLE, value);
    return this;
  }

  public CachedBookSelection epubBookTitleStartsWith(String... value) {
    addStartsWith(EpubBookColumns.TITLE, value);
    return this;
  }

  public CachedBookSelection epubBookTitleEndsWith(String... value) {
    addEndsWith(EpubBookColumns.TITLE, value);
    return this;
  }

  public CachedBookSelection orderByEpubBookTitle(boolean desc) {
    orderBy(EpubBookColumns.TITLE, desc);
    return this;
  }

  public CachedBookSelection orderByEpubBookTitle() {
    orderBy(EpubBookColumns.TITLE, false);
    return this;
  }

  public CachedBookSelection epubBookPath(String... value) {
    addEquals(EpubBookColumns.PATH, value);
    return this;
  }

  public CachedBookSelection epubBookPathNot(String... value) {
    addNotEquals(EpubBookColumns.PATH, value);
    return this;
  }

  public CachedBookSelection epubBookPathLike(String... value) {
    addLike(EpubBookColumns.PATH, value);
    return this;
  }

  public CachedBookSelection epubBookPathContains(String... value) {
    addContains(EpubBookColumns.PATH, value);
    return this;
  }

  public CachedBookSelection epubBookPathStartsWith(String... value) {
    addStartsWith(EpubBookColumns.PATH, value);
    return this;
  }

  public CachedBookSelection epubBookPathEndsWith(String... value) {
    addEndsWith(EpubBookColumns.PATH, value);
    return this;
  }

  public CachedBookSelection orderByEpubBookPath(boolean desc) {
    orderBy(EpubBookColumns.PATH, desc);
    return this;
  }

  public CachedBookSelection orderByEpubBookPath() {
    orderBy(EpubBookColumns.PATH, false);
    return this;
  }

  public CachedBookSelection epubBookCurrentResourceId(String... value) {
    addEquals(EpubBookColumns.CURRENT_RESOURCE_ID, value);
    return this;
  }

  public CachedBookSelection epubBookCurrentResourceIdNot(String... value) {
    addNotEquals(EpubBookColumns.CURRENT_RESOURCE_ID, value);
    return this;
  }

  public CachedBookSelection epubBookCurrentResourceIdLike(String... value) {
    addLike(EpubBookColumns.CURRENT_RESOURCE_ID, value);
    return this;
  }

  public CachedBookSelection epubBookCurrentResourceIdContains(
      String... value) {
    addContains(EpubBookColumns.CURRENT_RESOURCE_ID, value);
    return this;
  }

  public CachedBookSelection epubBookCurrentResourceIdStartsWith(
      String... value) {
    addStartsWith(EpubBookColumns.CURRENT_RESOURCE_ID, value);
    return this;
  }

  public CachedBookSelection epubBookCurrentResourceIdEndsWith(
      String... value) {
    addEndsWith(EpubBookColumns.CURRENT_RESOURCE_ID, value);
    return this;
  }

  public CachedBookSelection orderByEpubBookCurrentResourceId(boolean desc) {
    orderBy(EpubBookColumns.CURRENT_RESOURCE_ID, desc);
    return this;
  }

  public CachedBookSelection orderByEpubBookCurrentResourceId() {
    orderBy(EpubBookColumns.CURRENT_RESOURCE_ID, false);
    return this;
  }

  public CachedBookSelection epubBookTextPosition(Integer... value) {
    addEquals(EpubBookColumns.TEXT_POSITION, value);
    return this;
  }

  public CachedBookSelection epubBookTextPositionNot(Integer... value) {
    addNotEquals(EpubBookColumns.TEXT_POSITION, value);
    return this;
  }

  public CachedBookSelection epubBookTextPositionGt(int value) {
    addGreaterThan(EpubBookColumns.TEXT_POSITION, value);
    return this;
  }

  public CachedBookSelection epubBookTextPositionGtEq(int value) {
    addGreaterThanOrEquals(EpubBookColumns.TEXT_POSITION, value);
    return this;
  }

  public CachedBookSelection epubBookTextPositionLt(int value) {
    addLessThan(EpubBookColumns.TEXT_POSITION, value);
    return this;
  }

  public CachedBookSelection epubBookTextPositionLtEq(int value) {
    addLessThanOrEquals(EpubBookColumns.TEXT_POSITION, value);
    return this;
  }

  public CachedBookSelection orderByEpubBookTextPosition(boolean desc) {
    orderBy(EpubBookColumns.TEXT_POSITION, desc);
    return this;
  }

  public CachedBookSelection orderByEpubBookTextPosition() {
    orderBy(EpubBookColumns.TEXT_POSITION, false);
    return this;
  }

  public CachedBookSelection epubBookPercentile(Double... value) {
    addEquals(EpubBookColumns.PERCENTILE, value);
    return this;
  }

  public CachedBookSelection epubBookPercentileNot(Double... value) {
    addNotEquals(EpubBookColumns.PERCENTILE, value);
    return this;
  }

  public CachedBookSelection epubBookPercentileGt(double value) {
    addGreaterThan(EpubBookColumns.PERCENTILE, value);
    return this;
  }

  public CachedBookSelection epubBookPercentileGtEq(double value) {
    addGreaterThanOrEquals(EpubBookColumns.PERCENTILE, value);
    return this;
  }

  public CachedBookSelection epubBookPercentileLt(double value) {
    addLessThan(EpubBookColumns.PERCENTILE, value);
    return this;
  }

  public CachedBookSelection epubBookPercentileLtEq(double value) {
    addLessThanOrEquals(EpubBookColumns.PERCENTILE, value);
    return this;
  }

  public CachedBookSelection orderByEpubBookPercentile(boolean desc) {
    orderBy(EpubBookColumns.PERCENTILE, desc);
    return this;
  }

  public CachedBookSelection orderByEpubBookPercentile() {
    orderBy(EpubBookColumns.PERCENTILE, false);
    return this;
  }

  public CachedBookSelection epubBookTimeOpened(String... value) {
    addEquals(EpubBookColumns.TIME_OPENED, value);
    return this;
  }

  public CachedBookSelection epubBookTimeOpenedNot(String... value) {
    addNotEquals(EpubBookColumns.TIME_OPENED, value);
    return this;
  }

  public CachedBookSelection epubBookTimeOpenedLike(String... value) {
    addLike(EpubBookColumns.TIME_OPENED, value);
    return this;
  }

  public CachedBookSelection epubBookTimeOpenedContains(String... value) {
    addContains(EpubBookColumns.TIME_OPENED, value);
    return this;
  }

  public CachedBookSelection epubBookTimeOpenedStartsWith(String... value) {
    addStartsWith(EpubBookColumns.TIME_OPENED, value);
    return this;
  }

  public CachedBookSelection epubBookTimeOpenedEndsWith(String... value) {
    addEndsWith(EpubBookColumns.TIME_OPENED, value);
    return this;
  }

  public CachedBookSelection orderByEpubBookTimeOpened(boolean desc) {
    orderBy(EpubBookColumns.TIME_OPENED, desc);
    return this;
  }

  public CachedBookSelection orderByEpubBookTimeOpened() {
    orderBy(EpubBookColumns.TIME_OPENED, false);
    return this;
  }

  public CachedBookSelection fb2BookId(Long... value) {
    addEquals(CachedBookColumns.FB2_BOOK_ID, value);
    return this;
  }

  public CachedBookSelection fb2BookIdNot(Long... value) {
    addNotEquals(CachedBookColumns.FB2_BOOK_ID, value);
    return this;
  }

  public CachedBookSelection fb2BookIdGt(long value) {
    addGreaterThan(CachedBookColumns.FB2_BOOK_ID, value);
    return this;
  }

  public CachedBookSelection fb2BookIdGtEq(long value) {
    addGreaterThanOrEquals(CachedBookColumns.FB2_BOOK_ID, value);
    return this;
  }

  public CachedBookSelection fb2BookIdLt(long value) {
    addLessThan(CachedBookColumns.FB2_BOOK_ID, value);
    return this;
  }

  public CachedBookSelection fb2BookIdLtEq(long value) {
    addLessThanOrEquals(CachedBookColumns.FB2_BOOK_ID, value);
    return this;
  }

  public CachedBookSelection orderByFb2BookId(boolean desc) {
    orderBy(CachedBookColumns.FB2_BOOK_ID, desc);
    return this;
  }

  public CachedBookSelection orderByFb2BookId() {
    orderBy(CachedBookColumns.FB2_BOOK_ID, false);
    return this;
  }

  public CachedBookSelection fb2BookTitle(String... value) {
    addEquals(Fb2BookColumns.TITLE, value);
    return this;
  }

  public CachedBookSelection fb2BookTitleNot(String... value) {
    addNotEquals(Fb2BookColumns.TITLE, value);
    return this;
  }

  public CachedBookSelection fb2BookTitleLike(String... value) {
    addLike(Fb2BookColumns.TITLE, value);
    return this;
  }

  public CachedBookSelection fb2BookTitleContains(String... value) {
    addContains(Fb2BookColumns.TITLE, value);
    return this;
  }

  public CachedBookSelection fb2BookTitleStartsWith(String... value) {
    addStartsWith(Fb2BookColumns.TITLE, value);
    return this;
  }

  public CachedBookSelection fb2BookTitleEndsWith(String... value) {
    addEndsWith(Fb2BookColumns.TITLE, value);
    return this;
  }

  public CachedBookSelection orderByFb2BookTitle(boolean desc) {
    orderBy(Fb2BookColumns.TITLE, desc);
    return this;
  }

  public CachedBookSelection orderByFb2BookTitle() {
    orderBy(Fb2BookColumns.TITLE, false);
    return this;
  }

  public CachedBookSelection fb2BookPath(String... value) {
    addEquals(Fb2BookColumns.PATH, value);
    return this;
  }

  public CachedBookSelection fb2BookPathNot(String... value) {
    addNotEquals(Fb2BookColumns.PATH, value);
    return this;
  }

  public CachedBookSelection fb2BookPathLike(String... value) {
    addLike(Fb2BookColumns.PATH, value);
    return this;
  }

  public CachedBookSelection fb2BookPathContains(String... value) {
    addContains(Fb2BookColumns.PATH, value);
    return this;
  }

  public CachedBookSelection fb2BookPathStartsWith(String... value) {
    addStartsWith(Fb2BookColumns.PATH, value);
    return this;
  }

  public CachedBookSelection fb2BookPathEndsWith(String... value) {
    addEndsWith(Fb2BookColumns.PATH, value);
    return this;
  }

  public CachedBookSelection orderByFb2BookPath(boolean desc) {
    orderBy(Fb2BookColumns.PATH, desc);
    return this;
  }

  public CachedBookSelection orderByFb2BookPath() {
    orderBy(Fb2BookColumns.PATH, false);
    return this;
  }

  public CachedBookSelection fb2BookCurrentPartId(String... value) {
    addEquals(Fb2BookColumns.CURRENT_PART_ID, value);
    return this;
  }

  public CachedBookSelection fb2BookCurrentPartIdNot(String... value) {
    addNotEquals(Fb2BookColumns.CURRENT_PART_ID, value);
    return this;
  }

  public CachedBookSelection fb2BookCurrentPartIdLike(String... value) {
    addLike(Fb2BookColumns.CURRENT_PART_ID, value);
    return this;
  }

  public CachedBookSelection fb2BookCurrentPartIdContains(String... value) {
    addContains(Fb2BookColumns.CURRENT_PART_ID, value);
    return this;
  }

  public CachedBookSelection fb2BookCurrentPartIdStartsWith(String... value) {
    addStartsWith(Fb2BookColumns.CURRENT_PART_ID, value);
    return this;
  }

  public CachedBookSelection fb2BookCurrentPartIdEndsWith(String... value) {
    addEndsWith(Fb2BookColumns.CURRENT_PART_ID, value);
    return this;
  }

  public CachedBookSelection orderByFb2BookCurrentPartId(boolean desc) {
    orderBy(Fb2BookColumns.CURRENT_PART_ID, desc);
    return this;
  }

  public CachedBookSelection orderByFb2BookCurrentPartId() {
    orderBy(Fb2BookColumns.CURRENT_PART_ID, false);
    return this;
  }

  public CachedBookSelection fb2BookTextPosition(Integer... value) {
    addEquals(Fb2BookColumns.TEXT_POSITION, value);
    return this;
  }

  public CachedBookSelection fb2BookTextPositionNot(Integer... value) {
    addNotEquals(Fb2BookColumns.TEXT_POSITION, value);
    return this;
  }

  public CachedBookSelection fb2BookTextPositionGt(int value) {
    addGreaterThan(Fb2BookColumns.TEXT_POSITION, value);
    return this;
  }

  public CachedBookSelection fb2BookTextPositionGtEq(int value) {
    addGreaterThanOrEquals(Fb2BookColumns.TEXT_POSITION, value);
    return this;
  }

  public CachedBookSelection fb2BookTextPositionLt(int value) {
    addLessThan(Fb2BookColumns.TEXT_POSITION, value);
    return this;
  }

  public CachedBookSelection fb2BookTextPositionLtEq(int value) {
    addLessThanOrEquals(Fb2BookColumns.TEXT_POSITION, value);
    return this;
  }

  public CachedBookSelection orderByFb2BookTextPosition(boolean desc) {
    orderBy(Fb2BookColumns.TEXT_POSITION, desc);
    return this;
  }

  public CachedBookSelection orderByFb2BookTextPosition() {
    orderBy(Fb2BookColumns.TEXT_POSITION, false);
    return this;
  }

  public CachedBookSelection fb2BookPercentile(Double... value) {
    addEquals(Fb2BookColumns.PERCENTILE, value);
    return this;
  }

  public CachedBookSelection fb2BookPercentileNot(Double... value) {
    addNotEquals(Fb2BookColumns.PERCENTILE, value);
    return this;
  }

  public CachedBookSelection fb2BookPercentileGt(double value) {
    addGreaterThan(Fb2BookColumns.PERCENTILE, value);
    return this;
  }

  public CachedBookSelection fb2BookPercentileGtEq(double value) {
    addGreaterThanOrEquals(Fb2BookColumns.PERCENTILE, value);
    return this;
  }

  public CachedBookSelection fb2BookPercentileLt(double value) {
    addLessThan(Fb2BookColumns.PERCENTILE, value);
    return this;
  }

  public CachedBookSelection fb2BookPercentileLtEq(double value) {
    addLessThanOrEquals(Fb2BookColumns.PERCENTILE, value);
    return this;
  }

  public CachedBookSelection orderByFb2BookPercentile(boolean desc) {
    orderBy(Fb2BookColumns.PERCENTILE, desc);
    return this;
  }

  public CachedBookSelection orderByFb2BookPercentile() {
    orderBy(Fb2BookColumns.PERCENTILE, false);
    return this;
  }

  public CachedBookSelection fb2BookPathToc(String... value) {
    addEquals(Fb2BookColumns.PATH_TOC, value);
    return this;
  }

  public CachedBookSelection fb2BookPathTocNot(String... value) {
    addNotEquals(Fb2BookColumns.PATH_TOC, value);
    return this;
  }

  public CachedBookSelection fb2BookPathTocLike(String... value) {
    addLike(Fb2BookColumns.PATH_TOC, value);
    return this;
  }

  public CachedBookSelection fb2BookPathTocContains(String... value) {
    addContains(Fb2BookColumns.PATH_TOC, value);
    return this;
  }

  public CachedBookSelection fb2BookPathTocStartsWith(String... value) {
    addStartsWith(Fb2BookColumns.PATH_TOC, value);
    return this;
  }

  public CachedBookSelection fb2BookPathTocEndsWith(String... value) {
    addEndsWith(Fb2BookColumns.PATH_TOC, value);
    return this;
  }

  public CachedBookSelection orderByFb2BookPathToc(boolean desc) {
    orderBy(Fb2BookColumns.PATH_TOC, desc);
    return this;
  }

  public CachedBookSelection orderByFb2BookPathToc() {
    orderBy(Fb2BookColumns.PATH_TOC, false);
    return this;
  }

  public CachedBookSelection fb2BookTimeOpened(String... value) {
    addEquals(Fb2BookColumns.TIME_OPENED, value);
    return this;
  }

  public CachedBookSelection fb2BookTimeOpenedNot(String... value) {
    addNotEquals(Fb2BookColumns.TIME_OPENED, value);
    return this;
  }

  public CachedBookSelection fb2BookTimeOpenedLike(String... value) {
    addLike(Fb2BookColumns.TIME_OPENED, value);
    return this;
  }

  public CachedBookSelection fb2BookTimeOpenedContains(String... value) {
    addContains(Fb2BookColumns.TIME_OPENED, value);
    return this;
  }

  public CachedBookSelection fb2BookTimeOpenedStartsWith(String... value) {
    addStartsWith(Fb2BookColumns.TIME_OPENED, value);
    return this;
  }

  public CachedBookSelection fb2BookTimeOpenedEndsWith(String... value) {
    addEndsWith(Fb2BookColumns.TIME_OPENED, value);
    return this;
  }

  public CachedBookSelection orderByFb2BookTimeOpened(boolean desc) {
    orderBy(Fb2BookColumns.TIME_OPENED, desc);
    return this;
  }

  public CachedBookSelection orderByFb2BookTimeOpened() {
    orderBy(Fb2BookColumns.TIME_OPENED, false);
    return this;
  }

  public CachedBookSelection txtBookId(Long... value) {
    addEquals(CachedBookColumns.TXT_BOOK_ID, value);
    return this;
  }

  public CachedBookSelection txtBookIdNot(Long... value) {
    addNotEquals(CachedBookColumns.TXT_BOOK_ID, value);
    return this;
  }

  public CachedBookSelection txtBookIdGt(long value) {
    addGreaterThan(CachedBookColumns.TXT_BOOK_ID, value);
    return this;
  }

  public CachedBookSelection txtBookIdGtEq(long value) {
    addGreaterThanOrEquals(CachedBookColumns.TXT_BOOK_ID, value);
    return this;
  }

  public CachedBookSelection txtBookIdLt(long value) {
    addLessThan(CachedBookColumns.TXT_BOOK_ID, value);
    return this;
  }

  public CachedBookSelection txtBookIdLtEq(long value) {
    addLessThanOrEquals(CachedBookColumns.TXT_BOOK_ID, value);
    return this;
  }

  public CachedBookSelection orderByTxtBookId(boolean desc) {
    orderBy(CachedBookColumns.TXT_BOOK_ID, desc);
    return this;
  }

  public CachedBookSelection orderByTxtBookId() {
    orderBy(CachedBookColumns.TXT_BOOK_ID, false);
    return this;
  }

  public CachedBookSelection txtBookTitle(String... value) {
    addEquals(TxtBookColumns.TITLE, value);
    return this;
  }

  public CachedBookSelection txtBookTitleNot(String... value) {
    addNotEquals(TxtBookColumns.TITLE, value);
    return this;
  }

  public CachedBookSelection txtBookTitleLike(String... value) {
    addLike(TxtBookColumns.TITLE, value);
    return this;
  }

  public CachedBookSelection txtBookTitleContains(String... value) {
    addContains(TxtBookColumns.TITLE, value);
    return this;
  }

  public CachedBookSelection txtBookTitleStartsWith(String... value) {
    addStartsWith(TxtBookColumns.TITLE, value);
    return this;
  }

  public CachedBookSelection txtBookTitleEndsWith(String... value) {
    addEndsWith(TxtBookColumns.TITLE, value);
    return this;
  }

  public CachedBookSelection orderByTxtBookTitle(boolean desc) {
    orderBy(TxtBookColumns.TITLE, desc);
    return this;
  }

  public CachedBookSelection orderByTxtBookTitle() {
    orderBy(TxtBookColumns.TITLE, false);
    return this;
  }

  public CachedBookSelection txtBookPath(String... value) {
    addEquals(TxtBookColumns.PATH, value);
    return this;
  }

  public CachedBookSelection txtBookPathNot(String... value) {
    addNotEquals(TxtBookColumns.PATH, value);
    return this;
  }

  public CachedBookSelection txtBookPathLike(String... value) {
    addLike(TxtBookColumns.PATH, value);
    return this;
  }

  public CachedBookSelection txtBookPathContains(String... value) {
    addContains(TxtBookColumns.PATH, value);
    return this;
  }

  public CachedBookSelection txtBookPathStartsWith(String... value) {
    addStartsWith(TxtBookColumns.PATH, value);
    return this;
  }

  public CachedBookSelection txtBookPathEndsWith(String... value) {
    addEndsWith(TxtBookColumns.PATH, value);
    return this;
  }

  public CachedBookSelection orderByTxtBookPath(boolean desc) {
    orderBy(TxtBookColumns.PATH, desc);
    return this;
  }

  public CachedBookSelection orderByTxtBookPath() {
    orderBy(TxtBookColumns.PATH, false);
    return this;
  }

  public CachedBookSelection txtBookTextPosition(Integer... value) {
    addEquals(TxtBookColumns.TEXT_POSITION, value);
    return this;
  }

  public CachedBookSelection txtBookTextPositionNot(Integer... value) {
    addNotEquals(TxtBookColumns.TEXT_POSITION, value);
    return this;
  }

  public CachedBookSelection txtBookTextPositionGt(int value) {
    addGreaterThan(TxtBookColumns.TEXT_POSITION, value);
    return this;
  }

  public CachedBookSelection txtBookTextPositionGtEq(int value) {
    addGreaterThanOrEquals(TxtBookColumns.TEXT_POSITION, value);
    return this;
  }

  public CachedBookSelection txtBookTextPositionLt(int value) {
    addLessThan(TxtBookColumns.TEXT_POSITION, value);
    return this;
  }

  public CachedBookSelection txtBookTextPositionLtEq(int value) {
    addLessThanOrEquals(TxtBookColumns.TEXT_POSITION, value);
    return this;
  }

  public CachedBookSelection orderByTxtBookTextPosition(boolean desc) {
    orderBy(TxtBookColumns.TEXT_POSITION, desc);
    return this;
  }

  public CachedBookSelection orderByTxtBookTextPosition() {
    orderBy(TxtBookColumns.TEXT_POSITION, false);
    return this;
  }

  public CachedBookSelection txtBookPercentile(Double... value) {
    addEquals(TxtBookColumns.PERCENTILE, value);
    return this;
  }

  public CachedBookSelection txtBookPercentileNot(Double... value) {
    addNotEquals(TxtBookColumns.PERCENTILE, value);
    return this;
  }

  public CachedBookSelection txtBookPercentileGt(double value) {
    addGreaterThan(TxtBookColumns.PERCENTILE, value);
    return this;
  }

  public CachedBookSelection txtBookPercentileGtEq(double value) {
    addGreaterThanOrEquals(TxtBookColumns.PERCENTILE, value);
    return this;
  }

  public CachedBookSelection txtBookPercentileLt(double value) {
    addLessThan(TxtBookColumns.PERCENTILE, value);
    return this;
  }

  public CachedBookSelection txtBookPercentileLtEq(double value) {
    addLessThanOrEquals(TxtBookColumns.PERCENTILE, value);
    return this;
  }

  public CachedBookSelection orderByTxtBookPercentile(boolean desc) {
    orderBy(TxtBookColumns.PERCENTILE, desc);
    return this;
  }

  public CachedBookSelection orderByTxtBookPercentile() {
    orderBy(TxtBookColumns.PERCENTILE, false);
    return this;
  }

  public CachedBookSelection txtBookTimeOpened(String... value) {
    addEquals(TxtBookColumns.TIME_OPENED, value);
    return this;
  }

  public CachedBookSelection txtBookTimeOpenedNot(String... value) {
    addNotEquals(TxtBookColumns.TIME_OPENED, value);
    return this;
  }

  public CachedBookSelection txtBookTimeOpenedLike(String... value) {
    addLike(TxtBookColumns.TIME_OPENED, value);
    return this;
  }

  public CachedBookSelection txtBookTimeOpenedContains(String... value) {
    addContains(TxtBookColumns.TIME_OPENED, value);
    return this;
  }

  public CachedBookSelection txtBookTimeOpenedStartsWith(String... value) {
    addStartsWith(TxtBookColumns.TIME_OPENED, value);
    return this;
  }

  public CachedBookSelection txtBookTimeOpenedEndsWith(String... value) {
    addEndsWith(TxtBookColumns.TIME_OPENED, value);
    return this;
  }

  public CachedBookSelection orderByTxtBookTimeOpened(boolean desc) {
    orderBy(TxtBookColumns.TIME_OPENED, desc);
    return this;
  }

  public CachedBookSelection orderByTxtBookTimeOpened() {
    orderBy(TxtBookColumns.TIME_OPENED, false);
    return this;
  }
}
