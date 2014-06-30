package com.infm.readit.readable;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.infm.readit.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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

    public static String takePath(Context context, Uri uri) throws URISyntaxException{
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
        return "";
    }

    //implement it properly
    public static String takePath(Context context, String s) throws URISyntaxException{
        if ("content".equals(s.substring(0, 7))){
            return FileReadable.takePath(context, Uri.parse(s));
        } else
            return s;
    }

    public String getExtension(){ return extension; }

    public void process(Context context){
        Log.d(LOGTAG, "process() is called");
        try {
            path = FileReadable.takePath(context, path);
            if (path == null){
                Log.d(LOGTAG, "path is null");
                return;
            }

            extension = MimeTypeMap.getFileExtensionFromUrl(path);
            StringBuilder text = new StringBuilder();
            type = -1;

            if ("txt".equals(extension)){ //TODO: read all plain text files, not only txt ones
                FileReader fileReader = new FileReader(path);
                BufferedReader br = new BufferedReader(fileReader);
                String sCurrentLine;
                while ((sCurrentLine = br.readLine()) != null)
                    text.append(sCurrentLine).append('\n');
                br.close();
                type = TYPE_TXT;
                Log.d(LOGTAG, "type: txt");
            }

            if ("epub".equals(extension)){
                Book book = (new EpubReader()).readEpub(new FileInputStream(path));
                for (Resource res : book.getContents())
                    text.append(new String(res.getData()));
                text = new StringBuilder(parseEpub(text.toString())); //NullPointerEx can be thrown
                type = TYPE_EPUB;
                Log.d(LOGTAG, "type: epub");
            }

            this.text = text;
            textType = "text/plain";
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
            rowData = takeRowData(context);
            if (rowData != null)
                position = rowData.getPosition();
        }
    }

    private String parseEpub(String text){
        Document doc = null;
        try {
            doc = Jsoup.parse(text);
            return doc.title() + doc.select("p").text();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
