package com.infm.readit.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.infm.readit.R;
import com.infm.readit.database.LastReadContentProvider;
import com.infm.readit.readable.Readable;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

/**
 * Created by infm on 5/24/14. Enjoy ;)
 */
public class FileUtils extends Utils {

    private static final String LOGTAG = "FileUtils";
    private Context context;
    private Uri uri;
    private String path;

    public FileUtils(Context context, Uri uri) {
        super();
        Log.d(LOGTAG, "constructor with uri: " + uri.toString() + " called");
        this.context = context;
        this.uri = uri;
        try {
            this.path = getPath(context, uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public FileUtils(Context context, String path) {
        super();
        Log.d(LOGTAG, "constructor with path: " + path + " called");
        this.context = context;
        this.path = path;
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    String toReturn = cursor.getString(column_index);
                    cursor.close();
                    return toReturn;
                } else cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            String path = uri.getPath();
            Log.d(LOGTAG, "path: " + path);
            return path;
        }

        return null;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void process() {
        Log.d(LOGTAG, "process() is called");
        try {
            if (path == null) {
                Log.d(LOGTAG, "path is null");
                return;
            }

            String extension = MimeTypeMap.getFileExtensionFromUrl(path);
            if ("txt".equals(extension)) {
                FileReader fileReader = new FileReader(path);
                BufferedReader br = new BufferedReader(fileReader);
                String sCurrentLine;
                while ((sCurrentLine = br.readLine()) != null)
                    sb.append(sCurrentLine).append('\n');
                br.close();
                type = TYPE_TXT;
                Log.d(LOGTAG, "type: txt");
            }
            if ("epub".equals(extension)) {
                Book book = (new EpubReader()).readEpub(new FileInputStream(path));
                for (Resource res : book.getContents())
                    sb.append(new String(res.getData()));
                type = TYPE_EPUB;
                Log.d(LOGTAG, "type: epub");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (processFailed = type == -1)
            Toast.makeText(context, context.getResources().getString(R.string.wrong_ext), Toast.LENGTH_SHORT).show();
        else
            existingData =
                    Readable.getRowData(context.getContentResolver().query(LastReadContentProvider.CONTENT_URI,
                            null, null, null, null), path); //looks weird, actually
    }
}
