package com.infmme.readilyapp;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import com.infmme.readilyapp.util.Constants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class StorableDetailActivity extends BaseActivity {

  private Toolbar mToolbar;
  private FloatingActionButton mFab;
  private ImageView mImageView;
  private TextView mAuthorTextView;
  private TextView mGenreTextView;
  private TextView mLanguageTextView;
  private TextView mCurrentPartTextView;
  private TextView mFileTextView;

  private String mCoverImageUri;
  private String mTitle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_storable_detail);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      postponeEnterTransition();
    }

    findViews();

    Intent i = getIntent();
    mCoverImageUri = i.getStringExtra(Constants.EXTRA_COVER_IMAGE_URI);
    mTitle = i.getStringExtra(Constants.EXTRA_TITLE);
    setupViews();
  }

  @Override
  protected void findViews() {
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    mFab = (FloatingActionButton) findViewById(R.id.fab);
    mImageView = (ImageView) findViewById(R.id.storable_detail_image_view);

    mAuthorTextView = (TextView) findViewById(R.id.storable_detail_author);
    mGenreTextView = (TextView) findViewById(R.id.storable_detail_genre);
    mLanguageTextView = (TextView) findViewById(R.id.storable_detail_language);
    mCurrentPartTextView = (TextView) findViewById(R.id.storable_detail_current_part);
    mFileTextView = (TextView) findViewById(R.id.storable_detail_file);
  }

  protected void setupViews() {
    mToolbar.setTitle(mTitle);
    setSupportActionBar(mToolbar);
    mFab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action",
                      Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
      }
    });
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
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.storable_detail, menu);
    return super.onCreateOptionsMenu(menu);
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
