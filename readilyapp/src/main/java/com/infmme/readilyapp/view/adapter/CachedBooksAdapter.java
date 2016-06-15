package com.infmme.readilyapp.view.adapter;

import android.database.Cursor;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
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
    String coverImageUri = bookCursor.getCoverImageUri();
    if (coverImageUri != null && URLUtil.isValidUrl(coverImageUri)) {
      bindWithImage(holder, bookCursor);
    } else {
      bindWithoutImage(holder, bookCursor);
    }
    holder.mNavigateButton.setVisibility((supportsNavigation(bookCursor))
                                             ? View.VISIBLE
                                             : View.GONE);

    LocalDateTime timeOpened = LocalDateTime.parse(bookCursor.getTimeOpened());
    // TODO: Add options for 'Today', 'Yesterday' etc.
    String strTimeOpened = timeOpened.toString(
        DateTimeFormat.forPattern("d MMMM, HH:mm"));
    holder.mTimeOpenedView.setText(strTimeOpened);
    // TODO: Replace with a real value
    holder.mProgressView.setProgress((int) (bookCursor.getPercentile() * 100));

    holder.mId = bookCursor.getId();
  }

  private void bindWithImage(final CachedBookHolder holder,
                             final CachedBookCursor bookCursor) {
    holder.mTitleBelowView.setVisibility(View.GONE);

    holder.mTitleAboveView.setVisibility(View.VISIBLE);
    holder.mTitleAboveView.setText(bookCursor.getTitle());

    holder.mImageView.setVisibility(View.VISIBLE);
    // Picasso load blah blah blah
  }

  private void bindWithoutImage(final CachedBookHolder holder,
                                final CachedBookCursor bookCursor) {
    holder.mTitleAboveView.setVisibility(View.GONE);
    holder.mImageView.setVisibility(View.GONE);

    holder.mTitleBelowView.setVisibility(View.VISIBLE);
    holder.mTitleBelowView.setText(bookCursor.getTitle());
  }

  private boolean supportsNavigation(final CachedBookCursor bookCursor) {
    return bookCursor.getEpubBookId() != null || bookCursor.getFb2BookId() !=
        null;
  }

  @Override
  public CachedBookHolder onCreateViewHolder(
      ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext())
                           .inflate(R.layout.file_list_card, parent,
                                    false);
    CachedBookHolder holder = new CachedBookHolder(v, 0, mCallback);
    holder.mActionView.setOnClickListener(holder);
    return holder;
  }

  public static class CachedBookHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener {
    PercentRelativeLayout mActionView;

    ImageView mImageView;
    TextView mTitleAboveView;
    TextView mTitleBelowView;
    TextView mTimeOpenedView;
    ProgressBar mProgressView;
    Button mNavigateButton;
    // TODO: Add master-detail flow for 'About' button

    long mId;
    ItemClickCallback mCallback;

    public CachedBookHolder(View v, long id, ItemClickCallback callback) {
      super(v);
      mActionView = (PercentRelativeLayout) v.findViewById(
          R.id.file_list_card_child);

      mImageView = (ImageView) v.findViewById(R.id.file_list_card_image);
      mTitleAboveView = (TextView) v.findViewById(
          R.id.file_list_card_title_above);
      mTitleBelowView = (TextView) v.findViewById(
          R.id.file_list_card_title_below);
      mTimeOpenedView = (TextView) v.findViewById(
          R.id.file_list_card_time_opened);
      mProgressView = (ProgressBar) v.findViewById(
          R.id.file_list_card_progress);
      mNavigateButton = (Button) v.findViewById(R.id.file_list_card_button_toc);
      setupButtons();

      mId = id;
      mCallback = callback;
    }

    @Override
    public void onClick(View v) {
      mCallback.onItem(mId);
    }

    private void setupButtons() {
      mNavigateButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          mCallback.onNavigateButton(mId);
        }
      });
    }

    public interface ItemClickCallback {
      void onItem(long id);

      void onNavigateButton(long id);
    }
  }
}
