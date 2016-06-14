package com.infmme.readilyapp.fragment;

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
import com.infmme.readilyapp.provider.cachedbook.CachedBookColumns;
import com.infmme.readilyapp.view.adapter.CachedBooksAdapter;

public class FileListFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

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
    initViews(getActivity());
    getLoaderManager().initLoader(0, null, this);
  }

  private void findViews(ViewGroup v) {
    mRecyclerView = (RecyclerView) v.findViewById(R.id.file_recycler_view);
    // mTextViewEmpty = (TextView) v.findViewById(R.id.text_view_empty);
  }

  private void initViews(final Context context) {
    mAdapter = new CachedBooksAdapter(null);
    mRecyclerView.setAdapter(mAdapter);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return new CursorLoader(getActivity(), CachedBookColumns.CONTENT_URI,
                            null, null, null, null);
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
}
