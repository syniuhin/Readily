package com.infmme.readily;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import com.infmme.readily.database.LastReadContentProvider;
import com.infmme.readily.util.CachedFilesAdapter;

public class FileListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private CachedFilesAdapter adapter;

	private TextView tvEmpty;
	private ListView listView;

	public FileListFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_file_list, container, false);
		findViews(view);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		initViews(getActivity());
		getLoaderManager().initLoader(0, null, this);
	}

	private void findViews(View v){
		listView = (ListView) v.findViewById(R.id.fileListView);
		tvEmpty = (TextView) v.findViewById(R.id.text_view_empty);
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
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
			}
		});

		listView.setEmptyView(tvEmpty);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args){
		return new CursorLoader(getActivity(), LastReadContentProvider.CONTENT_URI,
								null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data){
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader loader){
		adapter.swapCursor(null);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
	}
}
