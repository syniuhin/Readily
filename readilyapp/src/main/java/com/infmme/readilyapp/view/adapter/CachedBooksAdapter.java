package com.infmme.readilyapp.view.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
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
import org.joda.time.LocalDate;
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

    holder.mTitle = bookCursor.getTitle();
    holder.mTitleView.setText(holder.mTitle);
    if (holder.mCoverImageUri != null) {
      bindWithImage(holder, bookCursor);
    } else {
      bindWithoutImage(holder);
    }

    if (supportsNavigation(bookCursor)) {
      String subtitle = bookCursor.getCachedBookInfoCurrentPartTitle();
      if (subtitle != null) {
        holder.mSubtitleView.setVisibility(View.VISIBLE);
        holder.mSubtitleView.setText(subtitle);
      } else {
        holder.mSubtitleView.setVisibility(View.GONE);
      }
    } else {
      holder.mSubtitleView.setVisibility(View.GONE);
    }

    LocalDateTime timeOpened = LocalDateTime.parse(bookCursor.getTimeOpened());
    String strTimeOpened;
    if (timeOpened.toLocalDate().equals(new LocalDate())) {
      strTimeOpened = mContext.getResources().getString(R.string.today) + " " +
          timeOpened.toString(DateTimeFormat.shortTime());
    } else if (timeOpened
        .toLocalDate().plusDays(1).equals(new LocalDate())) {
      strTimeOpened = mContext.getResources().getString(R.string.yesterday) +
          " " + timeOpened.toString(DateTimeFormat.shortTime());
    } else {
      strTimeOpened = timeOpened.toString(DateTimeFormat.shortDateTime());
    }
    holder.mTimeOpenedView.setText(strTimeOpened);
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
    boolean mWithImage;

    View mRootView;
    CardView mCardView;
    PercentRelativeLayout mActionView;

    ImageView mImageView;
    TextView mTitleView;
    TextView mSubtitleView;
    TextView mTimeOpenedView;
    TextView mTimeOpenedPrefixView;
    ProgressBar mProgressView;
    Button mReadButton;
    Button mMoreButton;

    long mId;
    String mTitle;
    String mCoverImageUri;
    ItemClickCallback mCallback;

    /**
     * Constructs an immutable copy to pass an instance as an argument.
     */
    public CachedBookHolder(CachedBookHolder that) {
      super(that.mRootView);
      this.mWithImage = that.isWithImage();
      this.mId = that.getId();
      this.mTitle = that.getTitle();
      this.mCoverImageUri = that.getCoverImageUri();

      // It's mutable, though.
      this.mImageView = that.getImageView();
    }

    public CachedBookHolder(View v, long id, String coverImageUri,
                            ItemClickCallback callback) {
      super(v);
      mRootView = v;
      mCardView = (CardView) v.findViewById(R.id.cache_list_card);
      mActionView = (PercentRelativeLayout) v.findViewById(
          R.id.cache_list_card_child);

      mImageView = (ImageView) v.findViewById(R.id.cache_list_card_image);
      mTitleView = (TextView) v.findViewById(R.id.cache_list_card_title);
      mSubtitleView = (TextView) v.findViewById(R.id.cache_list_card_part);
      mTimeOpenedView = (TextView) v.findViewById(
          R.id.cache_list_card_time_opened);
      mTimeOpenedPrefixView = (TextView) v.findViewById(
          R.id.cache_list_card_time_opened_prefix);
      mProgressView = (ProgressBar) v.findViewById(
          R.id.cache_list_card_progress);
      mReadButton = (Button) v.findViewById(
          R.id.cache_list_card_button_read);
      mMoreButton = (Button) v.findViewById(R.id.cache_list_card_button_more);
      setupButtons();

      mId = id;
      mCoverImageUri = coverImageUri;
      mCallback = callback;
    }

    @Override
    public void onClick(View v) {
      mCallback.onItem(new CachedBookHolder(this));
    }

    public void hasImage(final Context context) {
      mWithImage = true;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        hasImageM(context);
      } else {
        mProgressView.getProgressDrawable().setColorFilter(
            Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
        mReadButton.setTextColor(
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
      final Resources resources = context.getResources();
      final Resources.Theme theme = context.getTheme();

      mProgressView.setProgressTintList(ColorStateList.valueOf(Color.WHITE));
      mTitleView.setTextAppearance(
          android.R.style.TextAppearance_Material_Large_Inverse);
      mSubtitleView.setTextAppearance(
          android.R.style.TextAppearance_Material_Subhead);
      mSubtitleView.setTextColor(
          resources.getColor(android.R.color.primary_text_dark, theme));
      mTimeOpenedView.setTextAppearance(
          android.R.style.TextAppearance_Material_Small_Inverse);
      mTimeOpenedPrefixView.setTextAppearance(
          android.R.style.TextAppearance_Material_Small_Inverse);
      mReadButton.setTextColor(
          resources.getColor(android.R.color.primary_text_dark, theme));
      mMoreButton.setTextColor(
          resources.getColor(android.R.color.primary_text_dark, theme));
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
      mTimeOpenedPrefixView.setTextAppearance(
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
      mTimeOpenedPrefixView.setTextColor(
          context.getResources()
                 .getColor(android.R.color.secondary_text_dark));
    }

    public void hasNoImage(final Context context) {
      mWithImage = false;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        hasNoImageM(context);
      } else {
        final Resources resources = context.getResources();
        mCardView.setBackgroundColor(
            resources.getColor(R.color.cardview_light_background));
        mProgressView.getProgressDrawable().setColorFilter(
            resources.getColor(R.color.accent),
            android.graphics.PorterDuff.Mode.SRC_IN);
        mReadButton.setTextColor(resources.getColor(R.color.accent));
        mMoreButton.setTextColor(
            resources.getColor(android.R.color.primary_text_light));
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
      final Resources resources = context.getResources();
      final Resources.Theme theme = context.getTheme();

      mCardView.setBackgroundColor(
          resources.getColor(R.color.cardview_light_background, theme));
      mProgressView.setProgressTintList(ColorStateList.valueOf(
          resources.getColor(R.color.accent, theme)));
      mTitleView.setTextAppearance(
          android.R.style.TextAppearance_Material_Display1);
      mTitleView.setTextColor(
          resources.getColor(android.R.color.primary_text_light, theme));
      mSubtitleView.setTextColor(
          resources.getColor(android.R.color.secondary_text_light, theme));
      mTimeOpenedView.setTextAppearance(
          android.R.style.TextAppearance_Material_Small);
      mTimeOpenedPrefixView.setTextAppearance(
          android.R.style.TextAppearance_Material_Small);
      mReadButton.setTextColor(resources.getColor(R.color.accent, theme));
      mMoreButton.setTextColor(
          resources.getColor(android.R.color.primary_text_light, theme));
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
      mTimeOpenedPrefixView.setTextAppearance(
          context,
          android.R.style.TextAppearance_DeviceDefault_Small);
      mTimeOpenedPrefixView.setTextColor(
          context.getResources()
                 .getColor(android.R.color.secondary_text_light));
    }

    private void hasNoImageBelowICS(final Context context) {
      final Resources resources = context.getResources();

      mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
      mTitleView.setTextColor(
          resources.getColor(android.R.color.primary_text_light));
      mSubtitleView.setTextColor(
          resources.getColor(android.R.color.secondary_text_light));
      mTimeOpenedView.setTextColor(
          resources.getColor(android.R.color.secondary_text_light));
      mTimeOpenedPrefixView.setTextColor(
          resources.getColor(android.R.color.secondary_text_light));
    }

    private void setupButtons() {
      mReadButton.setOnClickListener(v -> mCallback.onReadButton(mId));

      mMoreButton.setOnClickListener(
          v -> mCallback.onMoreButton(new CachedBookHolder(this)));
    }

    public boolean isWithImage() {
      return mWithImage;
    }

    public long getId() {
      return mId;
    }

    public String getTitle() {
      return mTitle;
    }

    public String getCoverImageUri() {
      return mCoverImageUri;
    }

    public TextView getTitleView() {
      return mTitleView;
    }

    public ImageView getImageView() {
      return mImageView;
    }

    public interface ItemClickCallback {
      void onItem(CachedBookHolder holder);

      void onReadButton(long id);

      void onMoreButton(CachedBookHolder holder);
    }
  }
}
