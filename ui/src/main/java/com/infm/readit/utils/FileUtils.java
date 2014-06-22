package com.infm.readit.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

import cmc.readit.rsvp_reader.ui.R;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

/**
 * Created by infm on 5/24/14. Enjoy ;)
 */
public class FileUtils extends Utils {

    public static final String LOGTAG = "FileUtils";
    private Context context;
    private Uri uri;
    private String path;

    public FileUtils(Context context, Uri uri) {
        super();
        this.context = context;
        this.uri = uri;
    }

    public FileUtils(Context context, String path) {
        super();
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
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
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
        try {
            if (path == null) {
                if (uri != null) path = getPath(context, uri);
                else {
                    Log.d(LOGTAG, "path is null && uri is null");
                    return;
                }
            }
            existingData =
                    com.infm.readit.readable.Readable.getRowData(context.getContentResolver().query(LastReadContentProvider.CONTENT_URI,
                            null, null, null, null), path);
            if (existingData != null)
                position = existingData.first;
            String extension = MimeTypeMap.getFileExtensionFromUrl(path);
            if ("txt".equals(extension)) {
                FileReader fileReader = new FileReader(path);
                BufferedReader br = new BufferedReader(fileReader);
                String sCurrentLine;
                while ((sCurrentLine = br.readLine()) != null)
                    sb.append(sCurrentLine).append('\n');
                br.close();
                type = TYPE_TXT;
            }
            if ("epub".equals(extension)) {
                Book book = (new EpubReader()).readEpub(new FileInputStream(path));
                for (Resource res : book.getContents())
                    sb.append(new String(res.getData()));
                type = TYPE_EPUB;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (processFailed = type == -1)
            Toast.makeText(context, context.getResources().getString(R.string.wrong_ext), Toast.LENGTH_SHORT).show();
    }
}
