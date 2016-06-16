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

  public CachedBookSelection title(String... value) {
    addEquals(CachedBookColumns.TITLE, value);
    return this;
  }

  public CachedBookSelection titleNot(String... value) {
    addNotEquals(CachedBookColumns.TITLE, value);
    return this;
  }

  public CachedBookSelection titleLike(String... value) {
    addLike(CachedBookColumns.TITLE, value);
    return this;
  }

  public CachedBookSelection titleContains(String... value) {
    addContains(CachedBookColumns.TITLE, value);
    return this;
  }

  public CachedBookSelection titleStartsWith(String... value) {
    addStartsWith(CachedBookColumns.TITLE, value);
    return this;
  }

  public CachedBookSelection titleEndsWith(String... value) {
    addEndsWith(CachedBookColumns.TITLE, value);
    return this;
  }

  public CachedBookSelection orderByTitle(boolean desc) {
    orderBy(CachedBookColumns.TITLE, desc);
    return this;
  }

  public CachedBookSelection orderByTitle() {
    orderBy(CachedBookColumns.TITLE, false);
    return this;
  }

  public CachedBookSelection path(String... value) {
    addEquals(CachedBookColumns.PATH, value);
    return this;
  }

  public CachedBookSelection pathNot(String... value) {
    addNotEquals(CachedBookColumns.PATH, value);
    return this;
  }

  public CachedBookSelection pathLike(String... value) {
    addLike(CachedBookColumns.PATH, value);
    return this;
  }

  public CachedBookSelection pathContains(String... value) {
    addContains(CachedBookColumns.PATH, value);
    return this;
  }

  public CachedBookSelection pathStartsWith(String... value) {
    addStartsWith(CachedBookColumns.PATH, value);
    return this;
  }

  public CachedBookSelection pathEndsWith(String... value) {
    addEndsWith(CachedBookColumns.PATH, value);
    return this;
  }

  public CachedBookSelection orderByPath(boolean desc) {
    orderBy(CachedBookColumns.PATH, desc);
    return this;
  }

  public CachedBookSelection orderByPath() {
    orderBy(CachedBookColumns.PATH, false);
    return this;
  }

  public CachedBookSelection percentile(double... value) {
    addEquals(CachedBookColumns.PERCENTILE, toObjectArray(value));
    return this;
  }

  public CachedBookSelection percentileNot(double... value) {
    addNotEquals(CachedBookColumns.PERCENTILE, toObjectArray(value));
    return this;
  }

  public CachedBookSelection percentileGt(double value) {
    addGreaterThan(CachedBookColumns.PERCENTILE, value);
    return this;
  }

  public CachedBookSelection percentileGtEq(double value) {
    addGreaterThanOrEquals(CachedBookColumns.PERCENTILE, value);
    return this;
  }

  public CachedBookSelection percentileLt(double value) {
    addLessThan(CachedBookColumns.PERCENTILE, value);
    return this;
  }

  public CachedBookSelection percentileLtEq(double value) {
    addLessThanOrEquals(CachedBookColumns.PERCENTILE, value);
    return this;
  }

  public CachedBookSelection orderByPercentile(boolean desc) {
    orderBy(CachedBookColumns.PERCENTILE, desc);
    return this;
  }

  public CachedBookSelection orderByPercentile() {
    orderBy(CachedBookColumns.PERCENTILE, false);
    return this;
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

  public CachedBookSelection coverImageUri(String... value) {
    addEquals(CachedBookColumns.COVER_IMAGE_URI, value);
    return this;
  }

  public CachedBookSelection coverImageUriNot(String... value) {
    addNotEquals(CachedBookColumns.COVER_IMAGE_URI, value);
    return this;
  }

  public CachedBookSelection coverImageUriLike(String... value) {
    addLike(CachedBookColumns.COVER_IMAGE_URI, value);
    return this;
  }

  public CachedBookSelection coverImageUriContains(String... value) {
    addContains(CachedBookColumns.COVER_IMAGE_URI, value);
    return this;
  }

  public CachedBookSelection coverImageUriStartsWith(String... value) {
    addStartsWith(CachedBookColumns.COVER_IMAGE_URI, value);
    return this;
  }

  public CachedBookSelection coverImageUriEndsWith(String... value) {
    addEndsWith(CachedBookColumns.COVER_IMAGE_URI, value);
    return this;
  }

  public CachedBookSelection orderByCoverImageUri(boolean desc) {
    orderBy(CachedBookColumns.COVER_IMAGE_URI, desc);
    return this;
  }

  public CachedBookSelection orderByCoverImageUri() {
    orderBy(CachedBookColumns.COVER_IMAGE_URI, false);
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

  public CachedBookSelection fb2BookFullyProcessed(Boolean value) {
    addEquals(Fb2BookColumns.FULLY_PROCESSED, toObjectArray(value));
    return this;
  }

  public CachedBookSelection orderByFb2BookFullyProcessed(boolean desc) {
    orderBy(Fb2BookColumns.FULLY_PROCESSED, desc);
    return this;
  }

  public CachedBookSelection orderByFb2BookFullyProcessed() {
    orderBy(Fb2BookColumns.FULLY_PROCESSED, false);
    return this;
  }

  public CachedBookSelection fb2BookBytePosition(Integer... value) {
    addEquals(Fb2BookColumns.BYTE_POSITION, value);
    return this;
  }

  public CachedBookSelection fb2BookBytePositionNot(Integer... value) {
    addNotEquals(Fb2BookColumns.BYTE_POSITION, value);
    return this;
  }

  public CachedBookSelection fb2BookBytePositionGt(int value) {
    addGreaterThan(Fb2BookColumns.BYTE_POSITION, value);
    return this;
  }

  public CachedBookSelection fb2BookBytePositionGtEq(int value) {
    addGreaterThanOrEquals(Fb2BookColumns.BYTE_POSITION, value);
    return this;
  }

  public CachedBookSelection fb2BookBytePositionLt(int value) {
    addLessThan(Fb2BookColumns.BYTE_POSITION, value);
    return this;
  }

  public CachedBookSelection fb2BookBytePositionLtEq(int value) {
    addLessThanOrEquals(Fb2BookColumns.BYTE_POSITION, value);
    return this;
  }

  public CachedBookSelection orderByFb2BookBytePosition(boolean desc) {
    orderBy(Fb2BookColumns.BYTE_POSITION, desc);
    return this;
  }

  public CachedBookSelection orderByFb2BookBytePosition() {
    orderBy(Fb2BookColumns.BYTE_POSITION, false);
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

  public CachedBookSelection txtBookBytePosition(Integer... value) {
    addEquals(TxtBookColumns.BYTE_POSITION, value);
    return this;
  }

  public CachedBookSelection txtBookBytePositionNot(Integer... value) {
    addNotEquals(TxtBookColumns.BYTE_POSITION, value);
    return this;
  }

  public CachedBookSelection txtBookBytePositionGt(int value) {
    addGreaterThan(TxtBookColumns.BYTE_POSITION, value);
    return this;
  }

  public CachedBookSelection txtBookBytePositionGtEq(int value) {
    addGreaterThanOrEquals(TxtBookColumns.BYTE_POSITION, value);
    return this;
  }

  public CachedBookSelection txtBookBytePositionLt(int value) {
    addLessThan(TxtBookColumns.BYTE_POSITION, value);
    return this;
  }

  public CachedBookSelection txtBookBytePositionLtEq(int value) {
    addLessThanOrEquals(TxtBookColumns.BYTE_POSITION, value);
    return this;
  }

  public CachedBookSelection orderByTxtBookBytePosition(boolean desc) {
    orderBy(TxtBookColumns.BYTE_POSITION, desc);
    return this;
  }

  public CachedBookSelection orderByTxtBookBytePosition() {
    orderBy(TxtBookColumns.BYTE_POSITION, false);
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
}
