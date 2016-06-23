package com.infmme.readilyapp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.infmme.readilyapp.R;
import com.infmme.readilyapp.ReceiverActivity;
import com.infmme.readilyapp.StorableDetailActivity;
import com.infmme.readilyapp.provider.cachedbook.CachedBookColumns;
import com.infmme.readilyapp.readable.type.ReadableType;
import com.infmme.readilyapp.util.Constants;
import com.infmme.readilyapp.view.adapter.CachedBooksAdapter;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static com.infmme.readilyapp.provider.cachedbook.CachedBookCursor
    .findCachedBook;
import static com.infmme.readilyapp.provider.cachedbook.CachedBookCursor
    .inferReadableType;

public class FileListFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor>,
    CachedBooksAdapter.CachedBookHolder.ItemClickCallback {

  private CachedBooksAdapter mAdapter;

  private TextView mTextViewEmpty;
  private ProgressBar mProgressBar;
  private RecyclerView mRecyclerView;

  private CompositeSubscription mCompositeSubscription;

  public FileListFragment() {}

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_file_list, container, false);
    findViews((ViewGroup) view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    initViews();
    getLoaderManager().initLoader(0, null, this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mCompositeSubscription != null &&
        mCompositeSubscription.hasSubscriptions()) {
      mCompositeSubscription.unsubscribe();
    }
  }

  private void findViews(ViewGroup v) {
    mRecyclerView = (RecyclerView) v.findViewById(R.id.cache_list);
    mTextViewEmpty = (TextView) v.findViewById(R.id.text_view_empty);
    mProgressBar = (ProgressBar) v.findViewById(R.id.cache_list_progress_bar);
  }

  private void initViews() {
    mAdapter = new CachedBooksAdapter(getActivity(), null, this);
    mRecyclerView.setAdapter(mAdapter);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    mRecyclerView.setVisibility(View.GONE);
    mTextViewEmpty.setVisibility(View.GONE);
    mProgressBar.setVisibility(View.VISIBLE);
    return new CursorLoader(
        getActivity(), CachedBookColumns.CONTENT_URI,
        CachedBookColumns.ALL_COLUMNS_FULL_JOIN, null, null, null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    mProgressBar.setVisibility(View.GONE);
    if (data.getCount() > 0) {
      mRecyclerView.setVisibility(View.VISIBLE);
      mTextViewEmpty.setVisibility(View.GONE);
    } else {
      mRecyclerView.setVisibility(View.GONE);
      mTextViewEmpty.setVisibility(View.VISIBLE);
    }
    mAdapter.swapCursor(data);
  }

  @Override
  public void onLoaderReset(Loader loader) {
    mAdapter.swapCursor(null);
  }

  @Override
  public void onItem(CachedBooksAdapter.CachedBookHolder holder) {
    startDetailActivity(holder);
  }

  @Override
  public void onReadButton(final long id) {
    final Activity activity = getActivity();
    if (activity != null) {
      addSubscription(
          findCachedBook(activity, id)
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(bookCursor -> {
                final String path = bookCursor.getPath();
                final ReadableType type = inferReadableType(bookCursor);
                ReceiverActivity.startReceiverActivityCached(
                    activity, type, path);
                bookCursor.close();
              }));
    }
  }

  @Override
  public void onMoreButton(CachedBooksAdapter.CachedBookHolder holder) {
    startDetailActivity(holder);
  }

  private void startDetailActivity(
      final CachedBooksAdapter.CachedBookHolder holder) {
    final Activity a = getActivity();
    Intent intent = new Intent(a, StorableDetailActivity.class);
    intent.putExtra(Constants.EXTRA_TITLE, holder.getTitle());
    intent.putExtra(Constants.EXTRA_ID, holder.getId());
    intent.putExtra(Constants.EXTRA_COVER_IMAGE_URI, holder.getCoverImageUri());
    if (holder.isWithImage()) {
      Pair<View, String> p1 = Pair.create(
          holder.getImageView(), a.getResources().getString(
              R.string.storable_detail_transition_image_view));
      @SuppressWarnings("unchecked")
      ActivityOptionsCompat options =
          ActivityOptionsCompat.makeSceneTransitionAnimation(a, p1);
      startActivity(intent, options.toBundle());
    } else {
      startActivity(intent);
    }
  }

  private void addSubscription(Subscription s) {
    if (mCompositeSubscription == null) {
      mCompositeSubscription = new CompositeSubscription();
    }
    mCompositeSubscription.add(s);
  }
}
