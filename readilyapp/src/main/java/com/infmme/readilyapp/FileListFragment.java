package com.infmme.readilyapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import com.infmme.readilyapp.database.LastReadContentProvider;
import com.infmme.readilyapp.util.CachedFilesAdapter;

public class FileListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private CachedFilesAdapter adapter;

	private TextView textViewEmpty;
	private ListView listView;

	public FileListFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_file_list, container, false);
		findViews((ViewGroup) view);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		initViews(getActivity());
		getLoaderManager().initLoader(0, null, this);
	}

	private void findViews(ViewGroup v){
		listView = (ListView) v.findViewById(R.id.fileListView);
		textViewEmpty = (TextView) v.findViewById(R.id.text_view_empty);
	}

	private void initViews(final Context context){
		adapter = new CachedFilesAdapter(context);
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState){
				adapter.hideActionView();
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){}
		});

		listView.setEmptyView(textViewEmpty);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args){
		return new CursorLoader(getActivity(), LastReadContentProvider.CONTENT_URI,
								null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data){
		adapter.changeCursor(data);
	}

	@Override
	public void onLoaderReset(Loader loader){
		adapter.changeCursor(null);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
	}
}
