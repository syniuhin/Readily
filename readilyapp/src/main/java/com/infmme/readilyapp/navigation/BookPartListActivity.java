package com.infmme.readilyapp.navigation;

import android.app.Activity;
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
import com.infmme.readilyapp.BaseActivity;
import com.infmme.readilyapp.R;
import com.infmme.readilyapp.readable.interfaces.Storable;
import com.infmme.readilyapp.readable.interfaces.Structured;
import com.infmme.readilyapp.readable.structure.AbstractTocReference;
import com.infmme.readilyapp.readable.structure.epub.EpubStorable;
import com.infmme.readilyapp.readable.type.ReadableType;
import com.infmme.readilyapp.util.Constants;
import com.infmme.readilyapp.view.FabOnScrollBehavior;
import com.infmme.readilyapp.view.adapter.BookNavigationAdapter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

import static com.infmme.readilyapp.R.id.fab;
import static com.infmme.readilyapp.R.id.toolbar;

public class BookPartListActivity extends BaseActivity implements
    BookNavigationAdapter.OnItemClickListener,
    OnChooseListener {

  private static final int DETAIL_ACTIVITY = 1232;

  /**
   * Whether or not the activity is in two-pane mode, i.e. running on a tablet
   * device.
   */
  private boolean mTwoPane;

  /**
   * Supported storable types implements both of interfaces, therefore instances
   * below will point to the same object.
   */
  private Storable mStorable;
  private Structured mStructured;

  private List<? extends AbstractTocReference> mTocReferenceList;

  private Toolbar mToolbar;
  private FloatingActionButton mFab;
  private RecyclerView mRecyclerView;

  private OnFabClickListener mCallback = null;

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
    loadStorable(bundle);
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
          mCallback.onClick();
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

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case DETAIL_ACTIVITY: {
        if (resultCode == Activity.RESULT_OK) {
          chooseItem(data.getStringExtra(Constants.EXTRA_ITEM_ID),
                     data.getIntExtra(Constants.EXTRA_POSITION, 0));
        }
      }
      break;
    }
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

  private void loadStorable(Bundle args) {
    final String path = args.getString(Constants.EXTRA_PATH);
    final ReadableType type = ReadableType.valueOf(
        args.getString(Constants.EXTRA_TYPE));
    Observable<List<? extends AbstractTocReference>> o = Observable.create(
        new Observable.OnSubscribe<List<? extends AbstractTocReference>>() {
          @Override
          public void call(
              Subscriber<? super List<? extends AbstractTocReference>>
                  subscriber) {
            switch (type) {
              case EPUB: {
                EpubStorable epubStorable = new EpubStorable(
                    BookPartListActivity.this);
                epubStorable.setPath(path);
                epubStorable.process();
                mStorable = epubStorable;
                mStructured = epubStorable;
                subscriber.onNext(epubStorable.getTableOfContents());
              }
              break;
              case FB2: {

              }
              break;
            }
          }
        });
    o.subscribeOn(Schedulers.newThread())
     .observeOn(AndroidSchedulers.mainThread())
     .subscribe(
         new Action1<List<? extends AbstractTocReference>>() {
           @Override
           public void call(
               List<? extends AbstractTocReference> abstractTocReferences) {
             mTocReferenceList = abstractTocReferences;
             setupViews();
           }
         });
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
      // Context context = v.getContext();
      Intent intent = new Intent(
          BookPartListActivity.this, BookPartDetailActivity.class);
      intent.putExtra(Constants.EXTRA_TOC_REFERENCE, tocReference);
      intent.putExtra(Constants.EXTRA_TWO_PANE, mTwoPane);
      startActivityForResult(intent, DETAIL_ACTIVITY);
    }
  }

  @Override
  public void chooseItem(String itemId, int textPosition) {
    mStructured.setCurrentId(itemId);
    mStructured.setCurrentPosition(textPosition);
    final Activity activity = this;
    new Thread(new Runnable() {
      @Override
      public void run() {
        mStorable.storeToDb();
        activity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            finish();
          }
        });
      }
    }).start();
  }
}
