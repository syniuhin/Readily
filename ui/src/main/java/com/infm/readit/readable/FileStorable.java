package com.infm.readit.readable;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
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
public class FileStorable extends Storable { //TODO: implement separate class for each extension

	private static final String LOGTAG = "FileStorable";

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
	        return FileStorable.takePath(context, Uri.parse(s));
        } else
            return s;
    }

    public String getExtension(){ return extension; }

    public void process(Context context){
        Log.d(LOGTAG, "process() is called");
        try {
	        path = FileStorable.takePath(context, path);
	        if (path == null){
		        Log.d(LOGTAG, "path is null");
                return;
            }

            makeExtension();
            StringBuilder text = new StringBuilder();
            type = -1;

            if ("txt".equals(extension))//TODO: read all plain text files, not only txt ones
                processTxt();

            if ("epub".equals(extension))
                processEpub();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (processFailed = type == -1){
            Toast.makeText(context, R.string.wrong_ext, Toast.LENGTH_SHORT).show();
        } else {
            rowData = takeRowData(context);
            if (rowData != null)
                position = rowData.getPosition();
        }
    }

    private void processTxt() throws IOException{
        Log.d(LOGTAG, "type: txt");
        FileReader fileReader = new FileReader(path);
        BufferedReader br = new BufferedReader(fileReader);
        String sCurrentLine;
        while ((sCurrentLine = br.readLine()) != null)
            text.append(sCurrentLine).append('\n');
        br.close();
        type = TYPE_TXT;
    }

    private void processEpub() throws IOException{
        Log.d(LOGTAG, "type: epub");
        Book book = (new EpubReader()).readEpub(new FileInputStream(path));
        for (Resource res : book.getContents())
            text.append(new String(res.getData()));
        text = new StringBuilder(parseEpub(text.toString())); //NullPointerEx can be thrown
        type = TYPE_EPUB;
    }

    private String parseEpub(String text){
        Document doc = null;
        try {
            doc = Jsoup.parse(text);
            title = doc.title();
            return title + " | " + doc.select("p").text();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void makeHeader(){
        if (TextUtils.isEmpty(title))
            super.makeHeader();
        else
            header = title;
    }
}
