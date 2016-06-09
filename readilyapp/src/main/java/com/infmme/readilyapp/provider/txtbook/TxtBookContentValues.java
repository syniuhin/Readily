package com.infmme.readilyapp.provider.txtbook;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import com.infmme.readilyapp.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code txt_book} table.
 */
public class TxtBookContentValues extends AbstractContentValues {
  @Override
  public Uri uri() {
    return TxtBookColumns.CONTENT_URI;
  }

  /**
   * Update row(s) using the values stored by this object and the given
   * selection.
   *
   * @param contentResolver The content resolver to use.
   * @param where           The selection to use (can be {@code null}).
   */
  public int update(ContentResolver contentResolver,
                    @Nullable TxtBookSelection where) {
    return contentResolver.update(uri(), values(),
                                  where == null ? null : where.sel(),
                                  where == null ? null : where.args());
  }

  /**
   * Update row(s) using the values stored by this object and the given
   * selection.
   *
   * @param contentResolver The content resolver to use.
   * @param where           The selection to use (can be {@code null}).
   */
  public int update(Context context, @Nullable TxtBookSelection where) {
    return context.getContentResolver()
                  .update(uri(), values(), where == null ? null : where.sel(),
                          where == null ? null : where.args());
  }

  /**
   * Position in a parsed string, on which read was finished.
   */
  public TxtBookContentValues putTextPosition(int value) {
    mContentValues.put(TxtBookColumns.TEXT_POSITION, value);
    return this;
  }

}
