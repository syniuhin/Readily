package com.infm.readit;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.infm.readit.database.LastReadContentProvider;
import com.infm.readit.database.LastReadDBHelper;
import com.infm.readit.readable.Readable;
import com.infm.readit.service.StorageCheckerService;
import com.newrelic.agent.android.NewRelic;

public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

	public static final String LOGTAG = "MainActivity";

	private static final int FILE_SELECT_CODE = 0;
	private SimpleCursorAdapter adapter;

	private TextView tvEmpty;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		startService(createCheckerServiceIntent());
		initLastReadingView();

		Crashlytics.start(this);
		NewRelic.withApplicationToken(
				"AAb54a33233473ebe708b5daec8505d0928bd07238"
		).start(this.getApplication());

		LoaderManager loaderManager = getLoaderManager();
		loaderManager.initLoader(0, null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.action_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				break;
			case R.id.action_clipboard:
				getFromClipboard();
				break;
			case R.id.action_file:
				getFromFile();
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * This section is handled using abstract class Utils. Hope it's ok.
	 */
	private void getFromClipboard(){
		ReceiverActivity.startReceiverActivity(this, Readable.TYPE_CLIPBOARD, "");
	}

	private void getFromFile(){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("file/*");
		try {
			startActivityForResult(intent, FILE_SELECT_CODE);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, getResources().getString(R.string.file_manager_required),
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		switch (requestCode){
			case FILE_SELECT_CODE:
				if (resultCode == RESULT_OK)
					ReceiverActivity.startReceiverActivity(this, Readable.TYPE_FILE, data.getData().toString());
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args){
		return new CursorLoader(this, LastReadContentProvider.CONTENT_URI, null, null, null, null);
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

	private void initLastReadingView(){
		listView = (ListView) findViewById(R.id.listView);
		tvEmpty = (TextView) findViewById(R.id.text_view_empty);
		tvEmpty.setVisibility(View.VISIBLE);

		listView.setEmptyView(tvEmpty);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				ReceiverActivity.startReceiverActivity(MainActivity.this,
						Readable.TYPE_FILE,
						((TextView) view.findViewById(R.id.text_view_path)).getText().toString());
				Log.d(LOGTAG, "listView's onItemClick called()");
			}
		});

		adapter = new SimpleCursorAdapter(this, R.layout.list_element_main, null,
				new String[]{LastReadDBHelper.KEY_HEADER, LastReadDBHelper.KEY_PATH, LastReadDBHelper.KEY_PERCENT},
				new int[]{R.id.text_view_title, R.id.text_view_path, R.id.text_view_percent}, 0);
		listView.setAdapter(adapter);
	}

	private Intent createCheckerServiceIntent(){
		return new Intent(this, StorageCheckerService.class);
	}
}
