package com.infmme.readilyapp.provider.cachedbookinfo;

import android.support.annotation.Nullable;
import com.infmme.readilyapp.provider.base.BaseModel;

/**
 * Extended information about a cached book.
 */
public interface CachedBookInfoModel extends BaseModel {

  /**
   * Author of a book or net article.
   * Can be {@code null}.
   */
  @Nullable
  String getAuthor();

  /**
   * Genre or category of a book or net article.
   * Can be {@code null}.
   */
  @Nullable
  String getGenre();

  /**
   * Language of a book.
   * Can be {@code null}.
   */
  @Nullable
  String getLanguage();

  /**
   * Current part title of a book.
   * Can be {@code null}.
   */
  @Nullable
  String getCurrentPartTitle();

  /**
   * Description of a book or net article.
   * Can be {@code null}.
   */
  @Nullable
  String getDescription();
}
