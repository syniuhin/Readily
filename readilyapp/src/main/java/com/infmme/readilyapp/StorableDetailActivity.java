package com.infmme.readilyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import com.infmme.readilyapp.util.Constants;
import com.squareup.picasso.Picasso;

public class StorableDetailActivity extends BaseActivity {

  private Toolbar mToolbar;
  private FloatingActionButton mFab;
  private ImageView mImageView;

  private String mCoverImageUri;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_storable_detail);
    findViews();

    Intent i = getIntent();
    mCoverImageUri = i.getStringExtra(Constants.EXTRA_COVER_IMAGE_URI);
    setupViews();
  }

  @Override
  protected void findViews() {
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    mFab = (FloatingActionButton) findViewById(R.id.fab);
    mImageView = (ImageView) findViewById(R.id.storable_detail_image_view);
  }

  protected void setupViews() {
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
           .into(mImageView);
  }
}
