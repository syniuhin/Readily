package com.infmme.readilyapp.provider.cachedbook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.infmme.readilyapp.provider.base.AbstractCursor;
import com.infmme.readilyapp.provider.base.AbstractSelection;
import com.infmme.readilyapp.provider.cachedbookinfo.CachedBookInfoColumns;
import com.infmme.readilyapp.provider.cachedbookinfo.CachedBookInfoSelection;
import com.infmme.readilyapp.provider.epubbook.EpubBookColumns;
import com.infmme.readilyapp.provider.epubbook.EpubBookSelection;
import com.infmme.readilyapp.provider.fb2book.Fb2BookColumns;
import com.infmme.readilyapp.provider.fb2book.Fb2BookSelection;
import com.infmme.readilyapp.provider.txtbook.TxtBookColumns;
import com.infmme.readilyapp.provider.txtbook.TxtBookSelection;
import com.infmme.readilyapp.readable.type.ReadableType;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Cursor wrapper for the {@code cached_book} table.
 */
public class CachedBookCursor extends AbstractCursor
    implements CachedBookModel {
  public CachedBookCursor(Cursor cursor) {
    super(cursor);
  }

  public static Observable<CachedBookCursor> findCachedBook(
      final Context context, long id) {
    Observable<CachedBookCursor> res = Observable.create(subscriber -> {
      CachedBookSelection where = new CachedBookSelection();
      where.id(id);
      // To be closed in subscribed thread.
      @SuppressLint("Recycle")
      Cursor c = context.getContentResolver().query(
          CachedBookColumns.CONTENT_URI,
          CachedBookColumns.ALL_COLUMNS_FULL_JOIN,
          where.sel(), where.args(), null);
      CachedBookCursor bookCursor = new CachedBookCursor(c);
      if (bookCursor.moveToFirst()) {
        subscriber.onNext(bookCursor);
        subscriber.onCompleted();
      } else {
        subscriber.onError(new IllegalArgumentException(
            String.format("CachedBook with a given id: %d not found.", id)));
      }
    });
    res.subscribeOn(Schedulers.io());
    return res;
  }

  public static Observable<Boolean> removeCachedBook(
      final Context context, long id) {
    return findCachedBook(context, id).map(bookCursor -> {
      AbstractSelection where = null;
      Uri uri = null;
      if (bookCursor.getEpubBookId() != null) {
        where = new EpubBookSelection();
        ((EpubBookSelection) where).id(bookCursor.getEpubBookId());
        uri = EpubBookColumns.CONTENT_URI;
      } else if (bookCursor.getFb2BookId() != null) {
        where = new Fb2BookSelection();
        ((Fb2BookSelection) where).id(bookCursor.getFb2BookId());
        uri = Fb2BookColumns.CONTENT_URI;
      } else if (bookCursor.getTxtBookId() != null) {
        where = new TxtBookSelection();
        ((TxtBookSelection) where).id(bookCursor.getTxtBookId());
        uri = TxtBookColumns.CONTENT_URI;
      } else {
        bookCursor.close();
        return false;
      }
      CachedBookInfoSelection infoWhere = null;
      Long infoId = bookCursor.getInfoId();
      bookCursor.close();
      if (infoId != null) {
        infoWhere = new CachedBookInfoSelection();
        infoWhere.id(infoId);
      }

      // Deletes a parent book record.
      context.getContentResolver().delete(uri, where.sel(), where.args());

      if (infoWhere != null) {
        context.getContentResolver()
               .delete(CachedBookInfoColumns.CONTENT_URI, infoWhere.sel(),
                       infoWhere.args());
      }
      // Notifies cached_book table since ReadilyProvider does it only for a
      // parent one.
      context.getContentResolver()
             .notifyChange(CachedBookColumns.CONTENT_URI, null);
      return true;
    }).subscribeOn(Schedulers.io());
  }

  public static ReadableType inferReadableType(
      final CachedBookCursor bookCursor) {
    if (bookCursor.getEpubBookId() != null) {
      return ReadableType.EPUB;
    } else if (bookCursor.getFb2BookId() != null) {
      return ReadableType.FB2;
    } else if (bookCursor.getTxtBookId() != null) {
      return ReadableType.TXT;
    }
    throw new IllegalStateException(
        "Integrity violation: can't infer ReadableType.");
  }

  /**
   * Uses uniqueness of a path to get epub_book_id from a cached_book table.
   *
   * @return epub_book_id for an mPath.
   */
  public static Long getFkEpubBookId(final Context context, final String path) {
    Long id = null;

    CachedBookSelection cachedWhere = new CachedBookSelection();
    cachedWhere.path(path);
    CachedBookCursor cachedBookCursor =
        new CachedBookCursor(context.getContentResolver().query(
            CachedBookColumns.CONTENT_URI,
            new String[] { CachedBookColumns.EPUB_BOOK_ID },
            cachedWhere.sel(), cachedWhere.args(), null));
    if (cachedBookCursor.moveToFirst()) {
      id = cachedBookCursor.getEpubBookId();
    }
    cachedBookCursor.close();
    return id;
  }

  /**
   * Uses uniqueness of a path to get fb2_book_id from a cached_book table.
   *
   * @return fb2_book_id for an mPath.
   */
  public static Long getFkFb2BookId(final Context context, final String path) {
    Long id = null;

    CachedBookSelection cachedWhere = new CachedBookSelection();
    cachedWhere.path(path);
    CachedBookCursor cachedBookCursor =
        new CachedBookCursor(context.getContentResolver().query(
            CachedBookColumns.CONTENT_URI,
            new String[] { CachedBookColumns.FB2_BOOK_ID },
            cachedWhere.sel(), cachedWhere.args(), null));
    if (cachedBookCursor.moveToFirst()) {
      id = cachedBookCursor.getFb2BookId();
    }
    cachedBookCursor.close();
    return id;
  }

  /**
   * Uses uniqueness of a path to get txt_book_id from a cached_book table.
   *
   * @return txt_book_id for an mPath.
   */
  public static Long getFkTxtBookId(final Context context, final String path) {
    Long id = null;

    CachedBookSelection cachedWhere = new CachedBookSelection();
    cachedWhere.path(path);
    CachedBookCursor cachedBookCursor =
        new CachedBookCursor(context.getContentResolver().query(
            CachedBookColumns.CONTENT_URI,
            new String[] { CachedBookColumns.TXT_BOOK_ID },
            cachedWhere.sel(), cachedWhere.args(), null));
    if (cachedBookCursor.moveToFirst()) {
      id = cachedBookCursor.getTxtBookId();
    }
    cachedBookCursor.close();
    return id;
  }

  /**
   * Uses uniqueness of a path to get fb2_book_id from a cached_book table.
   *
   * @return fb2_book_id for an mPath.
   */
  public static Long getFkInfoId(final Context context, final String path) {
    Long id = null;

    CachedBookSelection cachedWhere = new CachedBookSelection();
    cachedWhere.path(path);
    CachedBookCursor cachedBookCursor =
        new CachedBookCursor(context.getContentResolver().query(
            CachedBookColumns.CONTENT_URI,
            new String[] { CachedBookColumns.INFO_ID },
            cachedWhere.sel(), cachedWhere.args(), null));
    if (cachedBookCursor.moveToFirst()) {
      id = cachedBookCursor.getInfoId();
    }
    cachedBookCursor.close();
    return id;
  }

  /**
   * Primary key.
   */
  public long getId() {
    Long res = getLongOrNull(CachedBookColumns._ID);
    if (res == null)
      throw new NullPointerException(
          "The value of '_id' in the database was null, which is not allowed " +
              "according to the model definition");
    return res;
  }

  /**
   * Get the {@code title} value.
   * Cannot be {@code null}.
   */
  @NonNull
  public String getTitle() {
    String res = getStringOrNull(CachedBookColumns.TITLE);
    if (res == null)
      throw new NullPointerException(
          "The value of 'title' in the database was null, which is not " +
              "allowed according to the model definition");
    return res;
  }

  /**
   * Path in a storage to read from.
   * Cannot be {@code null}.
   */
  @NonNull
  public String getPath() {
    String res = getStringOrNull(CachedBookColumns.PATH);
    if (res == null)
      throw new NullPointerException(
          "The value of 'path' in the database was null, which is not allowed" +
              " according to the model definition");
    return res;
  }

  /**
   * Position in word list.
   */
  public int getTextPosition() {
    Integer res = getIntegerOrNull(CachedBookColumns.TEXT_POSITION);
    if (res == null)
      throw new NullPointerException(
          "The value of 'text_position' in the database was null, which is " +
              "not allowed according to the model definition");
    return res;
  }

  /**
   * Amount of book that is already read, percent.
   */
  public double getPercentile() {
    Double res = getDoubleOrNull(CachedBookColumns.PERCENTILE);
    if (res == null)
      throw new NullPointerException(
          "The value of 'percentile' in the database was null, which is not " +
              "allowed according to the model definition");
    return res;
  }

  /**
   * Last open time, joda standard datetime format.
   * Cannot be {@code null}.
   */
  @NonNull
  public String getTimeOpened() {
    String res = getStringOrNull(CachedBookColumns.TIME_OPENED);
    if (res == null)
      throw new NullPointerException(
          "The value of 'time_opened' in the database was null, which is not " +
              "allowed according to the model definition");
    return res;
  }

  /**
   * Uri of the cover image.
   * Can be {@code null}.
   */
  @Nullable
  public String getCoverImageUri() {
    String res = getStringOrNull(CachedBookColumns.COVER_IMAGE_URI);
    return res;
  }

  /**
   * Mean color of the cover image in form of argb.
   * Can be {@code null}.
   */
  @Nullable
  public Integer getCoverImageMean() {
    Integer res = getIntegerOrNull(CachedBookColumns.COVER_IMAGE_MEAN);
    return res;
  }

  /**
   * Optional link to epub_book.
   * Can be {@code null}.
   */
  @Nullable
  public Long getEpubBookId() {
    Long res = getLongOrNull(CachedBookColumns.EPUB_BOOK_ID);
    return res;
  }

  /**
   * Id of a resource, from which last read was made.
   * Can be {@code null}.
   */
  @Nullable
  public String getEpubBookCurrentResourceId() {
    String res = getStringOrNull(EpubBookColumns.CURRENT_RESOURCE_ID);
    return res;
  }

  /**
   * Optional link to fb2_book.
   * Can be {@code null}.
   */
  @Nullable
  public Long getFb2BookId() {
    Long res = getLongOrNull(CachedBookColumns.FB2_BOOK_ID);
    return res;
  }

  /**
   * Tells if this fb2 was fully processed (i.e. table of contents, cover
   * image).
   * Can be {@code null}.
   */
  @Nullable
  public Boolean getFb2BookFullyProcessed() {
    Boolean res = getBooleanOrNull(Fb2BookColumns.FULLY_PROCESSED);
    return res;
  }

  /**
   * Tells if processing of this record was failed.
   * Can be {@code null}.
   */
  @Nullable
  public Boolean getFb2BookFullyProcessingSuccess() {
    Boolean res = getBooleanOrNull(Fb2BookColumns.FULLY_PROCESSING_SUCCESS);
    return res;
  }

  /**
   * Byte position of block in a file, either FB2Part or simple chunk read
   * continuously.
   * Can be {@code null}.
   */
  @Nullable
  public Integer getFb2BookBytePosition() {
    Integer res = getIntegerOrNull(Fb2BookColumns.BYTE_POSITION);
    return res;
  }

  /**
   * Id of a fb2part, from which last read was made.
   * Can be {@code null}.
   */
  @Nullable
  public String getFb2BookCurrentPartId() {
    String res = getStringOrNull(Fb2BookColumns.CURRENT_PART_ID);
    return res;
  }

  /**
   * Path to .json cache of a table of contents.
   * Can be {@code null}.
   */
  @Nullable
  public String getFb2BookPathToc() {
    String res = getStringOrNull(Fb2BookColumns.PATH_TOC);
    return res;
  }

  /**
   * Optional link to txt_book.
   * Can be {@code null}.
   */
  @Nullable
  public Long getTxtBookId() {
    Long res = getLongOrNull(CachedBookColumns.TXT_BOOK_ID);
    return res;
  }

  /**
   * Byte position of block in a file, either FB2Part or simple chunk read
   * continuously.
   * Can be {@code null}.
   */
  @Nullable
  public Integer getTxtBookBytePosition() {
    Integer res = getIntegerOrNull(TxtBookColumns.BYTE_POSITION);
    return res;
  }

  /**
   * Link to an extended information record.
   * Can be {@code null}.
   */
  @Nullable
  public Long getInfoId() {
    Long res = getLongOrNull(CachedBookColumns.INFO_ID);
    return res;
  }

  /**
   * Author of a book or net article.
   * Can be {@code null}.
   */
  @Nullable
  public String getCachedBookInfoAuthor() {
    String res = getStringOrNull(CachedBookInfoColumns.AUTHOR);
    return res;
  }

  /**
   * Genre or category of a book or net article.
   * Can be {@code null}.
   */
  @Nullable
  public String getCachedBookInfoGenre() {
    String res = getStringOrNull(CachedBookInfoColumns.GENRE);
    return res;
  }

  /**
   * Language of a book.
   * Can be {@code null}.
   */
  @Nullable
  public String getCachedBookInfoLanguage() {
    String res = getStringOrNull(CachedBookInfoColumns.LANGUAGE);
    return res;
  }

  /**
   * Current part title of a book.
   * Can be {@code null}.
   */
  @Nullable
  public String getCachedBookInfoCurrentPartTitle() {
    String res = getStringOrNull(CachedBookInfoColumns.CURRENT_PART_TITLE);
    return res;
  }

  /**
   * Description of a book or net article.
   * Can be {@code null}.
   */
  @Nullable
  public String getCachedBookInfoDescription() {
    String res = getStringOrNull(CachedBookInfoColumns.DESCRIPTION);
    return res;
  }
}
