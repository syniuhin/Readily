package com.infmme.readilyapp.view.adapter;

import android.annotation.TargetApi;
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
    holder.mId = bookCursor.getId();
    holder.mCoverImageUri = bookCursor.getCoverImageUri();

    holder.mTitleView.setText(bookCursor.getTitle());
    if (holder.mCoverImageUri != null) {
      bindWithImage(holder, bookCursor);
    } else {
      bindWithoutImage(holder);
    }

    if (supportsNavigation(bookCursor)) {
      String subtitle = bookCursor.getEpubBookCurrentResourceTitle();
      if (subtitle == null) {
        subtitle = bookCursor.getFb2BookCurrentPartTitle();
      }
      if (subtitle != null) {
        holder.mSubtitleView.setVisibility(View.VISIBLE);
        holder.mSubtitleView.setText(subtitle);
      } else {
        holder.mSubtitleView.setVisibility(View.GONE);
      }
      holder.mNavigateButton.setVisibility(View.VISIBLE);
    } else {
      holder.mSubtitleView.setVisibility(View.GONE);
      holder.mNavigateButton.setVisibility(View.GONE);
    }

    // TODO: Figure out jodatime issue.
    LocalDateTime timeOpened = LocalDateTime.parse(bookCursor.getTimeOpened());
    // TODO: Add options for 'Today', 'Yesterday' etc.
    String strTimeOpened = timeOpened.toString(
        DateTimeFormat.forPattern("d MMMM, HH:mm"));
    holder.mTimeOpenedView.setText(strTimeOpened);
    // TODO: Replace with a real value
    holder.mProgressView.setProgress((int) (bookCursor.getPercentile() * 100));
  }

  private void bindWithImage(final CachedBookHolder holder,
                             final CachedBookCursor bookCursor) {
    holder.hasImage(mContext);
    holder.mCardView.setBackgroundColor(bookCursor.getCoverImageMean());
    Picasso.with(mContext)
           .load("file:" + bookCursor.getCoverImageUri())
           .centerCrop()
           .fit()
           .into(holder.mImageView);
  }

  private void bindWithoutImage(final CachedBookHolder holder) {
    holder.hasNoImage(mContext);
  }

  private boolean supportsNavigation(final CachedBookCursor bookCursor) {
    return bookCursor.getEpubBookId() != null ||
        bookCursor.getFb2BookId() != null;
  }

  @Override
  public CachedBookHolder onCreateViewHolder(
      ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext())
                           .inflate(R.layout.cache_list_card, parent,
                                    false);
    CachedBookHolder holder = new CachedBookHolder(v, 0, null, mCallback);
    holder.mActionView.setOnClickListener(holder);
    return holder;
  }

  public static class CachedBookHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener {
    CardView mCardView;
    PercentRelativeLayout mActionView;

    ImageView mImageView;
    TextView mTitleView;
    TextView mSubtitleView;
    TextView mTimeOpenedView;
    ProgressBar mProgressView;
    Button mNavigateButton;
    // TODO: Add master-detail flow for 'About' button
    Button mMoreButton;

    long mId;
    String mCoverImageUri;
    ItemClickCallback mCallback;

    public CachedBookHolder(View v, long id, String coverImageUri,
                            ItemClickCallback callback) {
      super(v);
      mCardView = (CardView) v.findViewById(R.id.cache_list_card);
      mActionView = (PercentRelativeLayout) v.findViewById(
          R.id.cache_list_card_child);

      mImageView = (ImageView) v.findViewById(R.id.cache_list_card_image);
      mTitleView = (TextView) v.findViewById(R.id.cache_list_card_title);
      mSubtitleView = (TextView) v.findViewById(R.id.cache_list_card_part);
      mTimeOpenedView = (TextView) v.findViewById(
          R.id.cache_list_card_time_opened);
      mProgressView = (ProgressBar) v.findViewById(
          R.id.cache_list_card_progress);
      mNavigateButton = (Button) v.findViewById(
          R.id.cache_list_card_button_toc);
      mMoreButton = (Button) v.findViewById(R.id.cache_list_card_button_more);
      setupButtons();

      mId = id;
      mCoverImageUri = coverImageUri;
      mCallback = callback;
    }

    @Override
    public void onClick(View v) {
      mCallback.onItem(mId);
    }

    public void hasImage(final Context context) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        hasImageM(context);
      } else {
        mNavigateButton.setTextColor(
            context.getResources()
                   .getColor(android.R.color.primary_text_dark));
        mMoreButton.setTextColor(
            context.getResources()
                   .getColor(android.R.color.primary_text_dark));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
          hasImageICS(context);
        } else {
          hasImageBelowICS(context);
        }
      }
      mImageView.setVisibility(View.VISIBLE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void hasImageM(final Context context) {
      mTitleView.setTextAppearance(
          android.R.style.TextAppearance_Material_Large_Inverse);
      mSubtitleView.setTextAppearance(
          android.R.style.TextAppearance_Material_Subhead);
      mSubtitleView.setTextColor(
          context.getResources()
                 .getColor(android.R.color.primary_text_dark,
                           context.getTheme()));
      mTimeOpenedView.setTextAppearance(
          android.R.style.TextAppearance_Material_Small_Inverse);
      mNavigateButton.setTextColor(
          context.getResources()
                 .getColor(android.R.color.primary_text_dark,
                           context.getTheme()));
      mMoreButton.setTextColor(
          context.getResources()
                 .getColor(android.R.color.primary_text_dark,
                           context.getTheme()));
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void hasImageICS(final Context context) {
      mTitleView.setTextAppearance(
          context,
          android.R.style.TextAppearance_DeviceDefault_Large_Inverse);
      mSubtitleView.setTextAppearance(
          context,
          android.R.style.TextAppearance_DeviceDefault_Medium_Inverse);
      mTimeOpenedView.setTextAppearance(
          context,
          android.R.style.TextAppearance_DeviceDefault_Small_Inverse);
      mTimeOpenedView.setTextColor(
          context.getResources()
                 .getColor(android.R.color.secondary_text_dark));
    }

    private void hasImageBelowICS(final Context context) {
      mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
      mTitleView.setTextColor(
          context.getResources()
                 .getColor(android.R.color.primary_text_dark));
      mSubtitleView.setTextColor(
          context.getResources()
                 .getColor(android.R.color.secondary_text_dark));
      mTimeOpenedView.setTextColor(
          context.getResources()
                 .getColor(android.R.color.secondary_text_dark));
    }

    public void hasNoImage(final Context context) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        hasNoImageM(context);
      } else {
        mCardView.setBackgroundColor(
            context.getResources()
                   .getColor(R.color.cardview_light_background));
        mNavigateButton.setTextColor(
            context.getResources()
                   .getColor(R.color.accent));
        mMoreButton.setTextColor(
            context.getResources()
                   .getColor(android.R.color.primary_text_light));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
          hasNoImageICS(context);
        } else {
          hasNoImageBelowICS(context);
        }
      }

      mImageView.setVisibility(View.GONE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void hasNoImageM(final Context context) {
      mCardView.setBackgroundColor(
          context.getResources()
                 .getColor(R.color.cardview_light_background,
                           context.getTheme()));
      mTitleView.setTextAppearance(
          android.R.style.TextAppearance_Material_Display1);
      mTitleView.setTextColor(
          context.getResources()
                 .getColor(android.R.color.primary_text_light,
                           context.getTheme()));
      mSubtitleView.setTextColor(
          context.getResources()
                 .getColor(android.R.color.secondary_text_light,
                           context.getTheme()));
      mTimeOpenedView.setTextAppearance(
          android.R.style.TextAppearance_Material_Small);
      mNavigateButton.setTextColor(
          context.getResources()
                 .getColor(R.color.accent, context.getTheme()));
      mMoreButton.setTextColor(
          context.getResources()
                 .getColor(android.R.color.primary_text_light,
                           context.getTheme()));
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void hasNoImageICS(final Context context) {
      mTitleView.setTextAppearance(
          context,
          android.R.style.TextAppearance_DeviceDefault_Large);
      mSubtitleView.setTextAppearance(
          context,
          android.R.style.TextAppearance_DeviceDefault_Medium);
      mTimeOpenedView.setTextAppearance(
          context,
          android.R.style.TextAppearance_DeviceDefault_Small);
      mTimeOpenedView.setTextColor(
          context.getResources()
                 .getColor(android.R.color.secondary_text_light));
    }

    private void hasNoImageBelowICS(final Context context) {
      mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
      mTitleView.setTextColor(
          context.getResources()
                 .getColor(android.R.color.primary_text_light));
      mSubtitleView.setTextColor(
          context.getResources()
                 .getColor(android.R.color.secondary_text_light));
      mTimeOpenedView.setTextColor(
          context.getResources()
                 .getColor(android.R.color.secondary_text_light));
    }

    private void setupButtons() {
      mNavigateButton.setOnClickListener(v -> mCallback.onNavigateButton(mId));

      mMoreButton.setOnClickListener(v -> mCallback.onMoreButton(
          mImageView, mProgressView, mTitleView.getText().toString(), mId,
          mCoverImageUri));
    }

    public interface ItemClickCallback {
      void onItem(long id);

      void onNavigateButton(long id);

      void onMoreButton(ImageView imageView, ProgressBar progressBar,
                        String title, long id, String coverImageUri);
    }
  }
}
