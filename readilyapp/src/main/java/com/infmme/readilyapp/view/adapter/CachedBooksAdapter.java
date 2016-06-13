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

/**
 * Created with love, by infm dated on 6/13/16.
 */

public class CachedBooksAdapter
    extends CursorRecyclerAdapter<CachedBooksAdapter.CachedBookHolder> {

  public CachedBooksAdapter(Cursor cursor) {
    super(cursor);
  }

  @Override
  public void onBindViewHolderCursor(CachedBookHolder holder,
                                     Cursor cursor) {
    CachedBookCursor bookCursor = new CachedBookCursor(cursor);
    holder.mTitleView.setText(bookCursor.getTitle());
    holder.mTimeOpenedView.setText(bookCursor.getTimeOpened());
    // TODO: Replace with a real value
    holder.mProgressView.setProgress(50);
  }

  @Override
  public CachedBookHolder onCreateViewHolder(
      ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext())
                           .inflate(R.layout.file_list_card, parent, false);
    CachedBookHolder holder = new CachedBookHolder(v);
    return holder;
  }

  public static class CachedBookHolder extends RecyclerView.ViewHolder {
    ImageView mImageView;
    TextView mTitleView;
    TextView mTimeOpenedView;
    ProgressBar mProgressView;

    public CachedBookHolder(View v) {
      super(v);
      mImageView = (ImageView) v.findViewById(R.id.file_list_card_image);
      mTitleView = (TextView) v.findViewById(R.id.file_list_card_title);
      mTimeOpenedView = (TextView) v.findViewById(
          R.id.file_list_card_time_opened);
      mProgressView = (ProgressBar) v.findViewById(
          R.id.file_list_card_progress);
    }
  }
}
