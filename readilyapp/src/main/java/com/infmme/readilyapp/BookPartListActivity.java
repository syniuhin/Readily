package com.infmme.readilyapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import com.infmme.readilyapp.fragment.BookPartDetailFragment;
import com.infmme.readilyapp.readable.storable.AbstractTocReference;
import com.infmme.readilyapp.util.Constants;
import com.infmme.readilyapp.view.FabOnScrollBehavior;
import com.infmme.readilyapp.view.adapter.BookNavigationAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.infmme.readilyapp.R.id.fab;
import static com.infmme.readilyapp.R.id.toolbar;

public class BookPartListActivity extends BaseActivity implements
    BookNavigationAdapter.OnItemClickListener {

  /**
   * Whether or not the activity is in two-pane mode, i.e. running on a tablet
   * device.
   */
  private boolean mTwoPane;

  private ArrayList<AbstractTocReference> mTocReferenceList;

  private Toolbar mToolbar;
  private FloatingActionButton mFab;
  private RecyclerView mRecyclerView;

  private BookPartDetailActivity.BookDetailOnFabClicked mCallback = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bookpart_list);

    mTwoPane = isTwoPane();

    findViews();
    setSupportActionBar(mToolbar);
    // Show the Up button in the action bar.
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    Bundle bundle = getIntent().getExtras();
    mTocReferenceList = (ArrayList<AbstractTocReference>) bundle
        .getSerializable(Constants.EXTRA_TOC_REFERENCE_LIST);

    setupViews();
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

  protected void findViews() {
    mToolbar = (Toolbar) findViewById(toolbar);
    mFab = (FloatingActionButton) findViewById(fab);
    mRecyclerView = (RecyclerView) findViewById(R.id.bookpart_list);
  }

  protected void setupViews() {
    mToolbar.setTitle(getTitle());

    mFab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (mCallback != null) {
          mCallback.onFabClicked();
        }
      }
    });
    if (mTwoPane) {
      CoordinatorLayout.LayoutParams p =
          (CoordinatorLayout.LayoutParams) mFab.getLayoutParams();
      p.setAnchorId(R.id.bookpart_frame_layout);
      p.setBehavior(new FabOnScrollBehavior(null, null));
      mFab.setLayoutParams(p);
      mFab.setVisibility(View.VISIBLE);
    } else {
      CoordinatorLayout.LayoutParams p =
          (CoordinatorLayout.LayoutParams) mFab.getLayoutParams();
      p.setAnchorId(View.NO_ID);
      p.setBehavior(null);
      mFab.setLayoutParams(p);
      mFab.setVisibility(View.GONE);
    }

    setupRecyclerView(mRecyclerView);
  }

  private boolean isTwoPane() {
    return getResources().getBoolean(R.bool.has_two_panes);
  }

  private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
    List<BookNavigationAdapter.ParentPart> parentParts = new ArrayList<>();
    for (int i = 0; i < mTocReferenceList.size(); i++) {
      parentParts.add(
          new BookNavigationAdapter.ParentPart(mTocReferenceList.get(i)));
    }
    recyclerView.setAdapter(new BookNavigationAdapter(this, this, parentParts));
  }

  @Override
  public void onBookPartClicked(View v, AbstractTocReference tocReference) {
    if (mTwoPane) {
      Bundle arguments = new Bundle();
      arguments.putSerializable(Constants.EXTRA_TOC_REFERENCE, tocReference);
      arguments.putBoolean(Constants.EXTRA_TWO_PANE, mTwoPane);
      BookPartDetailFragment fragment = new BookPartDetailFragment();
      fragment.setArguments(arguments);
      getSupportFragmentManager()
          .beginTransaction()
          .replace(R.id.bookpart_detail_container, fragment)
          .commit();
      mCallback = fragment;
    } else {
      Context context = v.getContext();
      Intent intent = new Intent(
          context, BookPartDetailActivity.class);
      intent.putExtra(Constants.EXTRA_TOC_REFERENCE, tocReference);
      intent.putExtra(Constants.EXTRA_TWO_PANE, mTwoPane);
      context.startActivity(intent);
    }
  }
}
