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
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.infm.readit.database.LastReadContentProvider;
import com.infm.readit.database.LastReadDBHelper;
import com.infm.readit.utils.ClipboardUtils;
import com.infm.readit.utils.FileUtils;

public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOGTAG = "MainActivity";

    private static final int FILE_SELECT_CODE = 0;
    private SimpleCursorAdapter adapter;

    private TextView tvTitle;
    private TextView tvInfo;
    private TextView tvEmpty;
    private Button btnContinue;
    private ListView listView;
    private RelativeLayout lastReadLayout;

    private String lastReadPath;
    private int lastReadPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLastReadingView();
        // disable it for now
        // Crashlytics.start(this);

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
    private void getFromClipboard() {
        ReceiverActivity.startReceiverActivity(this, new ClipboardUtils(this));
    }

    private void getFromFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        try {
            startActivityForResult(intent, FILE_SELECT_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, getResources().getString(R.string.file_manager_required),
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK)
                    ReceiverActivity.startReceiverActivity(this, new FileUtils(this, data.getData()));
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, LastReadContentProvider.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
/*
        data.moveToNext();
        initLastReadParams(data);
        updateView(data);
        data.moveToFirst();
*/
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        adapter.swapCursor(null);
    }

    /**
     * Is it ok to update it on onLoadFinished()?
     *
     * @param data: Cursor to db
     */
    private void initLastReadParams(Cursor data) {
        if (data != null && data.getCount() > 0) {
            lastReadPath = data.getString(2);
            lastReadPosition = data.getInt(5);
        }
    }

    private void initLastReadingView() {
        tvTitle = (TextView) findViewById(R.id.textView_last_reading_title);
        tvInfo = (TextView) findViewById(R.id.textView_last_reading_info);
        btnContinue = (Button) findViewById(R.id.button_continue_reading);

        listView = (ListView) findViewById(R.id.listView);
        tvEmpty = new TextView(this);

        lastReadLayout = (RelativeLayout) findViewById(R.id.last_reading);

        tvEmpty.setText(R.string.list_empty_view);
        listView.setEmptyView(tvEmpty);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOGTAG, "listView's onItemClick called()");
            }
        });

        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null,
                new String[]{LastReadDBHelper.KEY_HEADER, LastReadDBHelper.KEY_PATH},
                new int[]{android.R.id.text1, android.R.id.text2}, 0);
        listView.setAdapter(adapter);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //is it ok to pass context in such way?
                ReceiverActivity.startReceiverActivity(MainActivity.this, new FileUtils(MainActivity.this, lastReadPath));
                Intent intent = new Intent(MainActivity.this, ReceiverActivity.class);
                intent.putExtra("path", lastReadPath);
                intent.putExtra("position", lastReadPosition);
                /**
                 * Should do it with static Map in FileUtils class
                 */
                if ("txt".equals(MimeTypeMap.getFileExtensionFromUrl(lastReadPath)))
                    intent.setType("text/plain");
                if ("epub".equals(MimeTypeMap.getFileExtensionFromUrl(lastReadPath)))
                    intent.setType("text/html");

                startActivity(intent);
            }
        });
    }

    private void updateView(final Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            lastReadLayout.setVisibility(View.VISIBLE);
            tvTitle.setText(cursor.getString(1));
            tvInfo.setText(cursor.getInt(4) + getResources().getString(R.string.last_reading_percent) +
                    " " + cursor.getInt(3) + getResources().getString(R.string.last_reading_time));
        } else {
            lastReadLayout.setVisibility(View.INVISIBLE);
        }
    }
}
