package com.infm.readit;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.infm.readit.database.LastReadContentProvider;
import com.infm.readit.database.LastReadDBHelper;
import com.infm.readit.readable.Readable;

public class FileListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

	private static final String LOGTAG = "FileListFragment";

	private SimpleCursorAdapter adapter;

	private TextView tvEmpty;
	private ListView listView;

	public FileListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_file_list, container, false);
		findViews(layout);
		return layout;
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
		tvEmpty.setVisibility(View.VISIBLE);
	}

	private void initViews(final Context context){
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				ReceiverActivity.startReceiverActivity(context,
						Readable.TYPE_FILE,
						((TextView) view.findViewById(R.id.text_view_path)).getText().toString());
				Log.d(LOGTAG, "listView's onItemClick called()");
			}
		});

		adapter = new SimpleCursorAdapter(context, R.layout.list_element_main, null,
				new String[]{LastReadDBHelper.KEY_HEADER, LastReadDBHelper.KEY_PATH, LastReadDBHelper.KEY_PERCENT},
				new int[]{R.id.text_view_title, R.id.text_view_path, R.id.text_view_percent}, 0);
		listView.setAdapter(adapter);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args){
		return new CursorLoader(getActivity(), LastReadContentProvider.CONTENT_URI, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data){
		adapter.swapCursor(data);
		if (data.getCount() > 0)
			tvEmpty.setVisibility(View.GONE);
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
