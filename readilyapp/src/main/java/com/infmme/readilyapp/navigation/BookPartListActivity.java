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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import com.daimajia.androidanimations.library.BuildConfig;
import com.infmme.readilyapp.BaseActivity;
import com.infmme.readilyapp.R;
import com.infmme.readilyapp.readable.epub.EpubStorable;
import com.infmme.readilyapp.readable.fb2.FB2Storable;
import com.infmme.readilyapp.readable.interfaces.AbstractTocReference;
import com.infmme.readilyapp.readable.interfaces.Storable;
import com.infmme.readilyapp.readable.interfaces.Structured;
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

  private String mFilePath = null;

  private List<? extends AbstractTocReference> mTocReferenceList;

  private Toolbar mToolbar;
  private FloatingActionButton mFab;
  private RecyclerView mRecyclerView;
  private ProgressBar mProgressBar;

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

    mProgressBar.setVisibility(View.VISIBLE);

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

  @Override
  protected void findViews() {
    mToolbar = (Toolbar) findViewById(toolbar);
    mFab = (FloatingActionButton) findViewById(fab);
    mRecyclerView = (RecyclerView) findViewById(R.id.bookpart_list);
    mProgressBar = (ProgressBar) findViewById(R.id.bookpart_list_progress_bar);
  }

  protected void setupViews() {
    mProgressBar.setVisibility(View.GONE);
    mToolbar.setTitle(mStorable.getTitle());

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
          chooseItem(
              (AbstractTocReference) data.getSerializableExtra(
                  Constants.EXTRA_TOC_REFERENCE),
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
    Observable<List<? extends AbstractTocReference>> tocObservable =
        Observable.create(
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
                    FB2Storable fb2Storable = new FB2Storable(
                        BookPartListActivity.this);
                    fb2Storable.setPath(path);
                    mFilePath = path;
                    fb2Storable.process();
                    if (!fb2Storable.isFullyProcessed()) {
                      try {
                        waitForFullProcessing();
                      } catch (InterruptedException e) {
                        e.printStackTrace();
                      }
                    }
                    mStorable = fb2Storable;
                    mStructured = fb2Storable;
                    subscriber.onNext(fb2Storable.getTableOfContents());
                  }
                  break;
                }
              }

              private void waitForFullProcessing() throws InterruptedException {
                boolean processed = FB2Storable.isFullyProcessed(
                    BookPartListActivity.this, path);
                while (!processed) {
                  Thread.sleep(1000);
                  processed = FB2Storable.isFullyProcessed(
                      BookPartListActivity.this, path);
                  if (BuildConfig.DEBUG) {
                    Log.d(BookPartListActivity.class.getName(),
                          String.format("%s fully processed: %s", path,
                                        String.valueOf(processed)));
                  }
                }
              }
            });
    addSubscription(
        tocObservable.subscribeOn(Schedulers.newThread())
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribe(
                         new Action1<List<? extends AbstractTocReference>>() {
                           @Override
                           public void call(
                               List<? extends AbstractTocReference>
                                   abstractTocReferences) {
                             mTocReferenceList = abstractTocReferences;
                             setupViews();
                           }
                         }));
  }

  @Override
  public void onBookPartClicked(View v, AbstractTocReference tocReference) {
    if (mTwoPane) {
      Bundle arguments = new Bundle();
      arguments.putSerializable(Constants.EXTRA_TOC_REFERENCE, tocReference);
      arguments.putBoolean(Constants.EXTRA_TWO_PANE, mTwoPane);
      if (mFilePath != null) {
        arguments.putString(Constants.EXTRA_PATH, mFilePath);
      }
      BookPartDetailFragment fragment = new BookPartDetailFragment();
      fragment.setArguments(arguments);
      getSupportFragmentManager()
          .beginTransaction()
          .replace(R.id.bookpart_detail_container, fragment)
          .commit();
      mCallback = fragment;
    } else {
      Intent intent = new Intent(
          BookPartListActivity.this, BookPartDetailActivity.class);
      intent.putExtra(Constants.EXTRA_TOC_REFERENCE, tocReference);
      intent.putExtra(Constants.EXTRA_TWO_PANE, mTwoPane);
      if (mFilePath != null) {
        intent.putExtra(Constants.EXTRA_PATH, mFilePath);
      }
      startActivityForResult(intent, DETAIL_ACTIVITY);
    }
  }

  @Override
  public void chooseItem(AbstractTocReference tocReference, int textPosition) {
    mStructured.setCurrentTocReference(tocReference);
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
