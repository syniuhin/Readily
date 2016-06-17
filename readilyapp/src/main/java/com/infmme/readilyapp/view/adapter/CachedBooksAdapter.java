package com.infmme.readilyapp.view.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.infmme.readilyapp.R;
import com.infmme.readilyapp.provider.cachedbook.CachedBookCursor;
import com.squareup.picasso.Picasso;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Created with love, by infm dated on 6/13/16.
 */

public class CachedBooksAdapter
    extends CursorRecyclerAdapter<CachedBooksAdapter.CachedBookHolder> {

  private CachedBookHolder.ItemClickCallback mCallback;
  private transient Context mContext;

  public CachedBooksAdapter(final Context context, Cursor cursor,
                            CachedBookHolder.ItemClickCallback callback) {
    super(cursor);
    mCallback = callback;
    mContext = context;
  }

  @Override
  public void onBindViewHolderCursor(CachedBookHolder holder,
                                     Cursor cursor) {
    CachedBookCursor bookCursor = new CachedBookCursor(cursor);
    String coverImageUri = bookCursor.getCoverImageUri();
    // TODO: Validate coverImageUri.
    if (coverImageUri != null) {
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
    holder.mCardView.setBackgroundColor(bookCursor.getCoverImageMean());

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      holder.mTitleView.setTextAppearance(
          android.R.style.TextAppearance_Material_Large_Inverse);
      holder.mTimeOpenedView.setTextAppearance(
          android.R.style.TextAppearance_Material_Small_Inverse);
      holder.mNavigateButton.setTextColor(
          mContext.getResources()
                  .getColor(android.R.color.primary_text_dark,
                            mContext.getTheme()));
      holder.mMoreButton.setTextColor(
          mContext.getResources()
                  .getColor(android.R.color.primary_text_dark,
                            mContext.getTheme()));
    } else {
      holder.mNavigateButton.setTextColor(
          mContext.getResources()
                  .getColor(android.R.color.primary_text_dark));
      holder.mMoreButton.setTextColor(
          mContext.getResources()
                  .getColor(android.R.color.primary_text_dark));
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES
          .ICE_CREAM_SANDWICH) {
        holder.mTitleView.setTextAppearance(
            mContext,
            android.R.style.TextAppearance_DeviceDefault_Large_Inverse);
        holder.mTimeOpenedView.setTextAppearance(
            mContext,
            android.R.style.TextAppearance_DeviceDefault_Small_Inverse);
      } else {
        holder.mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        holder.mTitleView.setTextColor(
            mContext.getResources()
                    .getColor(android.R.color.primary_text_dark));
        holder.mTimeOpenedView.setTextColor(
            mContext.getResources()
                    .getColor(android.R.color.secondary_text_dark));
      }
    }
    holder.mTitleView.setText(bookCursor.getTitle());

    holder.mImageView.setVisibility(View.VISIBLE);
    Picasso.with(mContext)
           .load("file:" + bookCursor.getCoverImageUri())
           .centerCrop()
           .fit()
           .into(holder.mImageView);
  }

  private void bindWithoutImage(final CachedBookHolder holder,
                                final CachedBookCursor bookCursor) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      holder.mCardView.setBackgroundColor(
          mContext.getResources()
                  .getColor(R.color.cardview_light_background,
                            mContext.getTheme()));
      holder.mTitleView.setTextAppearance(
          android.R.style.TextAppearance_Material_Display1);
      holder.mTitleView.setTextColor(
          mContext.getResources()
                  .getColor(android.R.color.primary_text_light,
                            mContext.getTheme()));
      holder.mTimeOpenedView.setTextAppearance(
          android.R.style.TextAppearance_Material_Small);
      holder.mNavigateButton.setTextColor(
          mContext.getResources()
                  .getColor(R.color.accent, mContext.getTheme()));
      holder.mMoreButton.setTextColor(
          mContext.getResources()
                  .getColor(android.R.color.primary_text_light,
                            mContext.getTheme()));
    } else {
      holder.mCardView.setBackgroundColor(
          mContext.getResources()
                  .getColor(R.color.cardview_light_background));
      holder.mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
      holder.mTitleView.setTextColor(
          mContext.getResources().getColor(android.R.color.primary_text_light));
      holder.mTimeOpenedView.setTextColor(
          mContext.getResources()
                  .getColor(android.R.color.secondary_text_light));
      holder.mNavigateButton.setTextColor(
          mContext.getResources()
                  .getColor(R.color.accent));
      holder.mMoreButton.setTextColor(
          mContext.getResources()
                  .getColor(android.R.color.primary_text_light));
    }
    holder.mTitleView.setText(bookCursor.getTitle());

    holder.mImageView.setVisibility(View.GONE);
  }

  private boolean supportsNavigation(final CachedBookCursor bookCursor) {
    return bookCursor.getEpubBookId() != null || bookCursor.getFb2BookId() !=
        null;
  }

  @Override
  public CachedBookHolder onCreateViewHolder(
      ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext())
                           .inflate(R.layout.cache_list_card, parent,
                                    false);
    CachedBookHolder holder = new CachedBookHolder(v, 0, mCallback);
    holder.mActionView.setOnClickListener(holder);
    return holder;
  }

  public static class CachedBookHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener {
    CardView mCardView;
    PercentRelativeLayout mActionView;

    ImageView mImageView;
    TextView mTitleView;
    TextView mTimeOpenedView;
    ProgressBar mProgressView;
    Button mNavigateButton;
    // TODO: Add master-detail flow for 'About' button
    Button mMoreButton;

    long mId;
    ItemClickCallback mCallback;

    public CachedBookHolder(View v, long id, ItemClickCallback callback) {
      super(v);
      mCardView = (CardView) v.findViewById(R.id.cache_list_card);
      mActionView = (PercentRelativeLayout) v.findViewById(
          R.id.cache_list_card_child);

      mImageView = (ImageView) v.findViewById(R.id.cache_list_card_image);
      mTitleView = (TextView) v.findViewById(R.id.cache_list_card_title);
      mTimeOpenedView = (TextView) v.findViewById(
          R.id.cache_list_card_time_opened);
      mProgressView = (ProgressBar) v.findViewById(
          R.id.cache_list_card_progress);
      mNavigateButton = (Button) v.findViewById(
          R.id.cache_list_card_button_toc);
      mMoreButton = (Button) v.findViewById(R.id.cache_list_card_button_more);
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
