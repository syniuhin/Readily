package com.infmme.readily.readable;

import android.content.Context;
import com.infmme.readily.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by infm on 7/2/14. Enjoy ;)
 */
public class TxtFileStorable extends FileStorable {

	private byte[] inputData = new byte[BUFFER_SIZE];

	public TxtFileStorable(String path){
		type = TYPE_TXT;
		this.path = path;
	}

	public TxtFileStorable(TxtFileStorable that){
		super(that);
		type = TYPE_TXT;
	}

	public void process(Context context){
		try {
			path = FileStorable.takePath(context, path);
			if (path == null){
				return;
			}
			File file = new File(path);
			fileSize = file.length();
			FileInputStream encodingHelper = new FileInputStream(file);
			encoding = guessCharset(encodingHelper);
			encodingHelper.close();

			fileInputStream = new FileInputStream(file);
			createRowData(context);
			if (bytePosition > 0)
				fileInputStream.skip(bytePosition);
			processed = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void readData(){
		try {
			if ((inputDataLength = fileInputStream.read(inputData)) != -1){
				String toSet = new String(inputData, encoding);
				if (inputDataLength < toSet.length())
					toSet = toSet.substring(0, (int) inputDataLength);
				setText(new StringBuilder(toSet));
			} else {
				setText("");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setText(StringBuilder nextText){
		text = nextText;
	}

	@Override
	public Readable getNext(){
		return prepareNext(new TxtFileStorable(this));
	}
}
