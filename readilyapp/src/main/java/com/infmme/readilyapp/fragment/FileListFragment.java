package com.infmme.readilyapp.fragment;

import android.app.Activity;
import android.content.Context;
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
import com.infmme.readilyapp.R;
import com.infmme.readilyapp.ReceiverActivity;
import com.infmme.readilyapp.StorableDetailActivity;
import com.infmme.readilyapp.provider.cachedbook.CachedBookColumns;
import com.infmme.readilyapp.provider.cachedbook.CachedBookCursor;
import com.infmme.readilyapp.provider.cachedbook.CachedBookSelection;
import com.infmme.readilyapp.readable.type.ReadableType;
import com.infmme.readilyapp.readable.type.ReadingSource;
import com.infmme.readilyapp.util.Constants;
import com.infmme.readilyapp.view.adapter.CachedBooksAdapter;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class FileListFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor>,
    CachedBooksAdapter.CachedBookHolder.ItemClickCallback {

  private CachedBooksAdapter mAdapter;

  // private TextView mTextViewEmpty;
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
    // mTextViewEmpty = (TextView) v.findViewById(R.id.text_view_empty);
  }

  private void initViews() {
    mAdapter = new CachedBooksAdapter(getActivity(), null, this);
    mRecyclerView.setAdapter(mAdapter);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return new CursorLoader(getActivity(), CachedBookColumns.CONTENT_URI,
                            CachedBookColumns.ALL_COLUMNS_FULL_JOIN, null, null,
                            null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    mAdapter.swapCursor(data);
  }

  @Override
  public void onLoaderReset(Loader loader) {
    mAdapter.swapCursor(null);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onItem(CachedBooksAdapter.CachedBookHolder holder) {
    startDetailActivity(holder);
  }

  @Override
  public void onReadButton(final long id) {
    final Activity activity = getActivity();
    if (activity != null) {
/*
      addSubscription(
          findCachedBook(activity, id)
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(bookCursor -> {
                final String path = bookCursor.getPath();
                final ReadableType type = inferReadableType(bookCursor);
                BookPartListActivity.startBookPartListActivity(
                    activity, type, path);
                bookCursor.close();
              }));
*/
      addSubscription(
          findCachedBook(activity, id)
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(bookCursor -> {
                final String path = bookCursor.getPath();
                final ReadableType type = inferReadableType(bookCursor);
                ReceiverActivity.startReceiverActivity(
                    activity, type, ReadingSource.CACHE, path);
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
    intent.putExtra(Constants.EXTRA_TITLE,
                    holder.getTitleView().getText().toString());
    intent.putExtra(Constants.EXTRA_ID, holder.getId());
    intent.putExtra(Constants.EXTRA_COVER_IMAGE_URI, holder.getCoverImageUri());
    Pair<View, String> p1 = Pair.create(
        holder.getImageView(), a.getResources().getString(
            R.string.storable_detail_transition_image_view));
    ActivityOptionsCompat options =
        ActivityOptionsCompat.makeSceneTransitionAnimation(a, p1);
    startActivity(intent, options.toBundle());
  }

  private Observable<CachedBookCursor> findCachedBook(
      final Context context, long id) {
    Observable<CachedBookCursor> res = Observable.create(subscriber -> {
      CachedBookSelection where = new CachedBookSelection();
      where.id(id);
      Cursor c = context.getContentResolver().query(
          CachedBookColumns.CONTENT_URI, CachedBookColumns.ALL_COLUMNS,
          where.sel(), where.args(), null);
      CachedBookCursor bookCursor = new CachedBookCursor(c);
      if (bookCursor.moveToFirst()) {
        subscriber.onNext(bookCursor);
        subscriber.onCompleted();
      } else {
        subscriber.onError(new IllegalArgumentException(
            String.format("CachedBook with a given id: %d not found.", id)));
      }
    });
    res.subscribeOn(Schedulers.io());
    return res;
  }

  private ReadableType inferReadableType(final CachedBookCursor bookCursor) {
    if (bookCursor.getEpubBookId() != null) {
      return ReadableType.EPUB;
    } else if (bookCursor.getFb2BookId() != null) {
      return ReadableType.FB2;
    } else if (bookCursor.getTxtBookId() != null) {
      return ReadableType.TXT;
    }
    throw new IllegalStateException(
        "Integrity violation: can't infer ReadableType.");
  }

  private void addSubscription(Subscription s) {
    if (mCompositeSubscription == null) {
      mCompositeSubscription = new CompositeSubscription();
    }
    mCompositeSubscription.add(s);
  }
}
