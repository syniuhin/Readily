package com.infm.readit.readable;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.infm.readit.R;
import com.infm.readit.database.LastReadContentProvider;

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
 * Created by infm on 6/13/14. Enjoy ;)
 */
public class FileReadable extends Readable {

    private String extension;

    public static String uriToStringPath(Context context, Uri uri) throws URISyntaxException{
        if ("content".equalsIgnoreCase(uri.getScheme())){
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()){
                    String toReturn = cursor.getString(column_index);
                    cursor.close();
                    return toReturn;
                } else cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())){
            String path = uri.getPath();
            Log.d(LOGTAG, "path: " + path);
            return path;
        }
        return null;
    }

    public String getExtension(){
        return extension;
    }

    @Override
    public String getLink(){
        return null;
    }

    @Override
    public void setLink(String link){

    }

    @Override
    public ChunkData getChunkData(){
        return null;
    }

    @Override
    public void setChunkData(ChunkData data){

    }

    public void process(Context context){
        Log.d(LOGTAG, "process() is called");
        try {
            path = FileReadable.uriToStringPath(context, Uri.parse(path));
            if (path == null){
                Log.d(LOGTAG, "path is null");
                return;
            }

            extension = MimeTypeMap.getFileExtensionFromUrl(path);
            if ("txt".equals(extension)){
                FileReader fileReader = new FileReader(path);
                BufferedReader br = new BufferedReader(fileReader);
                String sCurrentLine;
                while ((sCurrentLine = br.readLine()) != null)
                    text.append(sCurrentLine).append('\n');
                br.close();
                type = TYPE_TXT;
                textType = "text/plain";
                Log.d(LOGTAG, "type: txt");
            }
            if ("epub".equals(extension)){
                Book book = (new EpubReader()).readEpub(new FileInputStream(path));
                for (Resource res : book.getContents())
                    text.append(new String(res.getData()));
                type = TYPE_EPUB;
                textType = "text/html";
                Log.d(LOGTAG, "type: epub");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (processFailed = type == -1)
            Toast.makeText(context, R.string.wrong_ext, Toast.LENGTH_SHORT).show();
        else {
            rowData = Readable.getRowData(context.getContentResolver().query(LastReadContentProvider.CONTENT_URI,
                    null, null, null, null), path); //looks weird, actually. upd: it will be in separate thread, so ok.
            if (rowData != null)
                position = rowData.getPosition();
        }
    }
}
