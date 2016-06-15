package com.infmme.readilyapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.infmme.readilyapp.R;
import com.infmme.readilyapp.ReceiverActivity;
import com.infmme.readilyapp.navigation.BookPartListActivity;
import com.infmme.readilyapp.provider.cachedbook.CachedBookColumns;
import com.infmme.readilyapp.provider.cachedbook.CachedBookCursor;
import com.infmme.readilyapp.provider.cachedbook.CachedBookSelection;
import com.infmme.readilyapp.readable.type.ReadableType;
import com.infmme.readilyapp.readable.type.ReadingSource;
import com.infmme.readilyapp.util.Constants;
import com.infmme.readilyapp.view.adapter.CachedBooksAdapter;

public class FileListFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor>,
    CachedBooksAdapter.CachedBookHolder.ItemClickCallback {

  private CachedBooksAdapter mAdapter;

  // private TextView mTextViewEmpty;
  private RecyclerView mRecyclerView;

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

  private void findViews(ViewGroup v) {
    mRecyclerView = (RecyclerView) v.findViewById(R.id.file_recycler_view);
    // mTextViewEmpty = (TextView) v.findViewById(R.id.text_view_empty);
  }

  private void initViews() {
    mAdapter = new CachedBooksAdapter(getActivity(), null, this);
    mRecyclerView.setAdapter(mAdapter);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return new CursorLoader(getActivity(), CachedBookColumns.CONTENT_URI,
                            CachedBookColumns.ALL_COLUMNS, null, null, null);
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
  public void onItem(final long id) {
    final Activity activity = getActivity();
    if (activity != null) {
      new Thread(new Runnable() {
        @Override
        public void run() {
          CachedBookCursor bookCursor = findCachedBook(activity, id);
          if (bookCursor.moveToFirst()) {
            final String path = bookCursor.getPath();
            final ReadableType type = inferReadableType(bookCursor);
            activity.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                ReceiverActivity.startReceiverActivity(
                    activity, type, ReadingSource.CACHE, path);
              }
            });
          }

          bookCursor.close();
        }
      }).start();
    }
  }

  @Override
  public void onNavigateButton(final long id) {
    final Activity activity = getActivity();
    if (activity != null) {
      new Thread(new Runnable() {
        @Override
        public void run() {
          CachedBookCursor bookCursor = findCachedBook(activity, id);
          if (bookCursor.moveToFirst()) {
            final String path = bookCursor.getPath();
            final ReadableType type = inferReadableType(bookCursor);
            activity.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Intent i = new Intent(activity, BookPartListActivity.class);
                i.putExtra(Constants.EXTRA_PATH, path);
                i.putExtra(Constants.EXTRA_TYPE, type.name());
                startActivity(i);
              }
            });
          }

          bookCursor.close();
        }
      }).start();
    }
  }

  private CachedBookCursor findCachedBook(final Context context, long id) {
    CachedBookSelection where = new CachedBookSelection();
    where.id(id);
    Cursor c = context.getContentResolver().query(
        CachedBookColumns.CONTENT_URI, CachedBookColumns.ALL_COLUMNS,
        where.sel(), where.args(), null);
    return new CachedBookCursor(c);
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
}
