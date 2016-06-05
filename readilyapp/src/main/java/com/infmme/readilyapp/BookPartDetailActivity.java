package com.infmme.readilyapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import com.infmme.readilyapp.util.BaseActivity;
import com.infmme.readilyapp.util.Constants;

import static com.infmme.readilyapp.R.id.fab;

/**
 * An activity representing a single BookPart detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link BookPartListActivity}.
 */
public class BookPartDetailActivity extends BaseActivity {

  private Toolbar mToolbar;
  private FloatingActionButton mFab;
  private BookDetailOnFabClicked mCallback = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bookpart_detail);

    findViews();
    setupViews();
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    if (savedInstanceState == null) {
      // Create the detail fragment and add it to the activity
      // using a fragment transaction.
      Bundle arguments = new Bundle();
      arguments.putSerializable(Constants.EXTRA_TOC_REFERENCE,
                                getIntent().getSerializableExtra(
                                    Constants.EXTRA_TOC_REFERENCE));
      BookPartDetailFragment fragment = new BookPartDetailFragment();
      fragment.setArguments(arguments);
      getSupportFragmentManager().beginTransaction()
                                 .add(R.id.bookpart_detail_container, fragment)
                                 .commit();
      mCallback = fragment;
    }
  }

  protected void findViews() {
    mToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
    mFab = (FloatingActionButton) findViewById(fab);
  }

  protected void setupViews() {
    setSupportActionBar(mToolbar);

    mFab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (mCallback != null) {
          mCallback.onFabClicked();
        }
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      NavUtils.navigateUpFromSameTask(this);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public interface BookDetailOnFabClicked {
    void onFabClicked();
  }
}
