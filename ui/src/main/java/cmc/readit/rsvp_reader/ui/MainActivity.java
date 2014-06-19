package cmc.readit.rsvp_reader.ui;

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import cmc.readit.rsvp_reader.ui.utils.ClipboardUtils;
import cmc.readit.rsvp_reader.ui.utils.FileUtils;
import cmc.readit.rsvp_reader.ui.utils.LastReadContentProvider;
import cmc.readit.rsvp_reader.ui.utils.LastReadDBHelper;

public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOGTAG = "MainActivity";

    private static final int FILE_SELECT_CODE = 0;
    SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // disable it for now
        // Crashlytics.start(this);

        LoaderManager loaderManager = getLoaderManager();
        if (null != loaderManager)
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

    private void initLastReadingView(final Cursor cursor) {
        TextView tvTitle = (TextView) findViewById(R.id.textView_last_reading_title);
        TextView tvPercent = (TextView) findViewById(R.id.textView_last_reading_percent);
        TextView tvTimeModified = (TextView) findViewById(R.id.textView_last_reading_time_modified);
        Button btnContinue = (Button) findViewById(R.id.button_continue_reading);

        ListView listView = (ListView) findViewById(R.id.listView);
        TextView tvEmpty = new TextView(this);
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

        if (cursor.getCount() > 0) {
            tvTitle.setVisibility(View.VISIBLE);
            tvPercent.setVisibility(View.VISIBLE);
            tvTimeModified.setVisibility(View.VISIBLE);
            btnContinue.setVisibility(View.VISIBLE);
            tvTitle.setText(cursor.getString(1));
            tvPercent.setText(cursor.getInt(4) + "%");
            tvTimeModified.setText(cursor.getInt(3) + " sec");
            btnContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ReceiverActivity.class);
                    String path = cursor.getString(2);
                    intent.putExtra("path", path);
                    intent.putExtra("position", cursor.getInt(5));
                    if ("txt".equals(path.substring(path.lastIndexOf(".") + 1)))
                        intent.setType("text/plain");
                    if ("epub".equals(path.substring(path.lastIndexOf(".") + 1)))
                        intent.setType("text/html");

                    startActivity(intent);
                }
            });
        } else {
            tvTitle.setVisibility(View.INVISIBLE);
            tvPercent.setVisibility(View.INVISIBLE);
            tvTimeModified.setVisibility(View.INVISIBLE);
            btnContinue.setVisibility(View.INVISIBLE);
        }
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
        data.moveToNext();
        initLastReadingView(data);
        data.moveToFirst();
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        adapter.swapCursor(null);
    }
}
