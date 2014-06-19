package cmc.readit.rsvp_reader.ui;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int TYPE_CLIPBOARD = 0;
    public static final int TYPE_TXT = 1;
    public static final int TYPE_EPUB = 2;
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
     * Gets text from a clipboard. Need to fix deprecated classes.
     */
    private void getFromClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboard.hasText()) {
            String text = clipboard.getText().toString();
            startReceiverActivity(text, null, TYPE_CLIPBOARD);
        } else {
            Toast.makeText(getApplicationContext(), "There's nothing in clipboard", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Gets a file to parse. Currently supports `.txt` & `.epub` files.
     */
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

    /**
     * Start receiver activity
     *
     * @param text : plain text. Needs
     * @param existingData : pair of position and path. Both cannot exists, default: existingData = (0, null)
     * @param type : type of source
     */
    private void startReceiverActivity(String text, Pair<Integer, String> existingData, int type) {
        Intent intent = new Intent(MainActivity.this, ReceiverActivity.class);

        // why should I use MIME types?
        if (type != TYPE_EPUB)
            intent.setType("text/plain");
        else
            intent.setType("text/html");

        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra("source_type", type);
        intent.putExtra("position", existingData.first);
        intent.putExtra("path", existingData.second);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String path = null;
                    StringBuilder sb = new StringBuilder();
                    try {
                        path = FileUtils.getPath(this, uri);
                        Pair<Integer, String> existingData =
                                Readable.getRowData(getContentResolver().query(LastReadContentProvider.CONTENT_URI,
                                        null, null, null, null), path);
                        int type = -1;
                        int position = 0;
                        if (existingData != null)
                            position = existingData.first;
                        if ("txt".equals(path.substring(path.lastIndexOf(".") + 1))) {
                            FileReader fileReader = new FileReader(path);
                            BufferedReader br = new BufferedReader(fileReader);
                            String sCurrentLine;
                            while ((sCurrentLine = br.readLine()) != null)
                                sb.append(sCurrentLine).append('\n');
                            br.close();
                            type = TYPE_TXT;
                        }

                        if ("epub".equals(path.substring(path.lastIndexOf(".") + 1))) {
                            Book book = (new EpubReader()).readEpub(new FileInputStream(path));
                            for (Resource res : book.getContents())
                                sb.append(new String(res.getData()));
                            type = TYPE_EPUB;

                        }
                        if (type == -1)
                            Toast.makeText(this, getResources().getString(R.string.wrong_ext), Toast.LENGTH_SHORT).show();
                        else
                            startReceiverActivity(sb.toString(), new Pair<Integer, String>(position, path), type);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
