package com.infmme.readilyapp.view.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.infmme.readilyapp.R;
import com.infmme.readilyapp.provider.cachedbook.CachedBookCursor;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Created with love, by infm dated on 6/13/16.
 */

public class CachedBooksAdapter
    extends CursorRecyclerAdapter<CachedBooksAdapter.CachedBookHolder> {

  private CachedBookHolder.ItemClickCallback mCallback;

  public CachedBooksAdapter(Cursor cursor,
                            CachedBookHolder.ItemClickCallback callback) {
    super(cursor);
    mCallback = callback;
  }

  @Override
  public void onBindViewHolderCursor(CachedBookHolder holder,
                                     Cursor cursor) {
    CachedBookCursor bookCursor = new CachedBookCursor(cursor);
    holder.mTitleView.setText(bookCursor.getTitle());
    LocalDateTime timeOpened = LocalDateTime.parse(bookCursor.getTimeOpened());
    // TODO: Add options for 'Today', 'Yesterday' etc.
    String strTimeOpened = timeOpened.toString(
        DateTimeFormat.forPattern("d MMMM, HH:mm"));
    holder.mTimeOpenedView.setText(strTimeOpened);
    // TODO: Replace with a real value
    holder.mProgressView.setProgress(50);

    holder.mId = bookCursor.getId();
  }

  @Override
  public CachedBookHolder onCreateViewHolder(
      ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext())
                           .inflate(R.layout.file_list_card_image, parent,
                                    false);
    CachedBookHolder holder = new CachedBookHolder(v, 0, mCallback);
    holder.mImageView.setOnClickListener(holder);
    return holder;
  }

  public static class CachedBookHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener {
    ImageView mImageView;
    TextView mTitleView;
    TextView mTimeOpenedView;
    ProgressBar mProgressView;

    long mId;
    ItemClickCallback mCallback;

    public CachedBookHolder(View v, long id, ItemClickCallback callback) {
      super(v);
      mImageView = (ImageView) v.findViewById(R.id.file_list_card_image);
      mTitleView = (TextView) v.findViewById(R.id.file_list_card_title);
      mTimeOpenedView = (TextView) v.findViewById(
          R.id.file_list_card_time_opened);
      mProgressView = (ProgressBar) v.findViewById(
          R.id.file_list_card_progress);

      mId = id;
      mCallback = callback;
    }

    @Override
    public void onClick(View v) {
      mCallback.onItem(mId);
    }

    public interface ItemClickCallback {
      void onItem(long id);
    }
  }
}
