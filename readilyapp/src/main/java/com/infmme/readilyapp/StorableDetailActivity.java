package com.infmme.readilyapp;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.*;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import com.infmme.readilyapp.navigation.BookPartListActivity;
import com.infmme.readilyapp.provider.cachedbook.CachedBookCursor;
import com.infmme.readilyapp.readable.type.ReadableType;
import com.infmme.readilyapp.util.Constants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import rx.android.schedulers.AndroidSchedulers;

import static com.infmme.readilyapp.provider.cachedbook.CachedBookCursor.*;

public class StorableDetailActivity extends BaseActivity {

  private CoordinatorLayout mRootView;
  private CollapsingToolbarLayout mCollapsingToolbarLayout;
  private AppBarLayout mAppBarLayout;
  private Toolbar mToolbar;
  private FloatingActionButton mFab;
  private ImageView mImageView;
  private TextView mTitleTextView;
  private TextView mDescriptionTextView;
  private TextView mAuthorTextView;
  private TextView mGenreTextView;
  private TextView mLanguageTextView;
  private TextView mCurrentPartTextView;
  private TextView mFileTextView;

  private String mCoverImageUri;
  private String mTitle;

  private String mAppBarTitle;

  private long mId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_storable_detail);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      postponeEnterTransition();
    }

    findViews();

    Intent i = getIntent();
    mId = i.getLongExtra(Constants.EXTRA_ID, -1);
    mCoverImageUri = i.getStringExtra(Constants.EXTRA_COVER_IMAGE_URI);
    mTitle = i.getStringExtra(Constants.EXTRA_TITLE);
    setupViews();
  }

  @Override
  protected void findViews() {
    mRootView = (CoordinatorLayout) findViewById(R.id.storable_detail_root);
    mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
    mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(
        R.id.toolbar_layout);
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    mFab = (FloatingActionButton) findViewById(R.id.fab);
    mImageView = (ImageView) findViewById(R.id.storable_detail_image_view);

    mTitleTextView = (TextView) findViewById(R.id.storable_detail_title);
    mAuthorTextView = (TextView) findViewById(R.id.storable_detail_author);
    mDescriptionTextView = (TextView) findViewById(
        R.id.storable_detail_description);
    mGenreTextView = (TextView) findViewById(R.id.storable_detail_genre);
    mLanguageTextView = (TextView) findViewById(R.id.storable_detail_language);
    mCurrentPartTextView = (TextView) findViewById(
        R.id.storable_detail_current_part);
    mFileTextView = (TextView) findViewById(R.id.storable_detail_file);
  }

  @Override
  protected void setupViews() {
    mToolbar.setTitle("");
    setSupportActionBar(mToolbar);

    mTitleTextView.setText(mTitle);

    mAppBarTitle = getResources().getString(R.string.storable_detail_title);
    mAppBarLayout.addOnOffsetChangedListener(
        new AppBarLayout.OnOffsetChangedListener() {
          boolean isShow = false;
          int scrollRange = -1;

          @Override
          public void onOffsetChanged(AppBarLayout appBarLayout,
                                      int verticalOffset) {
            if (scrollRange == -1) {
              scrollRange = appBarLayout.getTotalScrollRange();
            }
            if (scrollRange + verticalOffset == 0) {
              mCollapsingToolbarLayout.setTitle(mAppBarTitle);
              isShow = true;
            } else if (isShow) {
              mCollapsingToolbarLayout.setTitle("");
              isShow = false;
            }
          }
        });
    mFab.setOnClickListener(view -> addSubscription(
        findCachedBook(this, mId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(bookCursor -> {
              final String path = bookCursor.getPath();
              final ReadableType type = inferReadableType(bookCursor);
              ReceiverActivity.startReceiverActivityCached(this, type, path);
              bookCursor.close();
            })));
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    Picasso.with(this)
           .load("file:" + mCoverImageUri)
           .centerInside()
           .fit()
           .into(mImageView, new Callback() {
             @Override
             public void onSuccess() {
               scheduleStartPostponedTransition(mImageView);
             }

             @Override
             public void onError() {
               scheduleStartPostponedTransition(mImageView);
             }
           });
    addSubscription(
        findCachedBook(this, mId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::setupViews, throwable -> {
              throwable.printStackTrace();
              Snackbar.make(mRootView, "Error occurred", Snackbar.LENGTH_SHORT)
                      .show();
            }));
  }

  private void setupViews(CachedBookCursor infoCursor) {
    String author = infoCursor.getCachedBookInfoAuthor();
    if (author != null && !TextUtils.isEmpty(author)) {
      mAuthorTextView.setText(author);
    } else {
      findViewById(R.id.storable_detail_author_prefix).setVisibility(View.GONE);
      mAuthorTextView.setVisibility(View.GONE);
    }

    String description = infoCursor.getCachedBookInfoDescription();
    if (description != null && !TextUtils.isEmpty(description)) {
      mDescriptionTextView.setText(description);
    } else {
      findViewById(R.id.storable_detail_description_prefix).setVisibility(
          View.GONE);
      mDescriptionTextView.setVisibility(View.GONE);
    }

    String genre = infoCursor.getCachedBookInfoGenre();
    if (genre != null && !TextUtils.isEmpty(genre)) {
      mGenreTextView.setText(genre);
    } else {
      findViewById(R.id.storable_detail_genre_prefix).setVisibility(View.GONE);
      mGenreTextView.setVisibility(View.GONE);
    }

    String language = infoCursor.getCachedBookInfoLanguage();
    if (language != null && !TextUtils.isEmpty(language)) {
      mLanguageTextView.setText(language);
    } else {
      findViewById(R.id.storable_detail_language_prefix).setVisibility(
          View.GONE);
      mLanguageTextView.setVisibility(View.GONE);
    }

    String currentPart = infoCursor.getCachedBookInfoCurrentPartTitle();
    if (currentPart != null && !TextUtils.isEmpty(currentPart)) {
      mCurrentPartTextView.setText(currentPart);
    } else {
      findViewById(R.id.storable_detail_current_part_prefix).setVisibility(
          View.GONE);
      mCurrentPartTextView.setVisibility(View.GONE);
    }

    String path = infoCursor.getPath();
    mFileTextView.setText(path);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.storable_detail, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_navigate:
        addSubscription(
            findCachedBook(this, mId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bookCursor -> {
                  final String path = bookCursor.getPath();
                  final ReadableType type = inferReadableType(bookCursor);
                  BookPartListActivity.startBookPartListActivity(
                      this, type, path);
                  bookCursor.close();
                }));
        return true;
      case R.id.action_edit:
        Snackbar.make(mRootView, "To be implemented", Snackbar.LENGTH_SHORT)
                .show();
        break;
      case R.id.action_delete:
        addSubscription(
            removeCachedBook(this, mId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isDeleted -> {
                  if (isDeleted) {
                    finish();
                  } else {
                    Snackbar.make(mRootView, "Error deleting this reading",
                                  Snackbar.LENGTH_LONG).show();
                  }
                }, throwable -> {
                  throwable.printStackTrace();
                  Snackbar.make(mRootView, "Error deleting this reading",
                                Snackbar.LENGTH_LONG).show();
                }));
        break;
      case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /**
   * Schedules the shared element transition to be started immediately
   * after the shared element has been measured and laid out within the
   * activity's view hierarchy. Some common places where it might make
   * sense to call this method are:
   * <p>
   * (1) Inside a Fragment's onCreateView() method (if the shared element
   * lives inside a Fragment hosted by the called Activity).
   * <p>
   * (2) Inside a Picasso Callback object (if you need to wait for Picasso to
   * asynchronously load/scale a bitmap before the transition can begin).
   * <p>
   * (3) Inside a LoaderCallback's onLoadFinished() method (if the shared
   * element depends on data queried by a Loader).
   */
  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private void scheduleStartPostponedTransition(final View sharedElement) {
    sharedElement.getViewTreeObserver().addOnPreDrawListener(
        new ViewTreeObserver.OnPreDrawListener() {
          @Override
          public boolean onPreDraw() {
            sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
            startPostponedEnterTransition();
            return true;
          }
        });
  }
}
