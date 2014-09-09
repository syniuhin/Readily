package com.infmme.readily.readable;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import com.infmme.readily.Constants;
import com.ipaulpro.afilechooser.utils.FileUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by infm on 6/13/14. Enjoy ;)
 */
abstract public class FileStorable extends Storable {

	public static final HashMap<String, Integer> extensionsMap = new HashMap<String, Integer>();
	public static final int BUFFER_SIZE = 4096;
	public static final int LAST_WORD_SUFFIX_SIZE = 10;
	public static final int LAST_WORD_PREFIX_SIZE = 10;

	protected FileInputStream fileInputStream;
	protected String lastWord = "";
	protected long inputDataLength;
	protected long fileSize;

	static{
		extensionsMap.put(Constants.EXTENSION_TXT, Readable.TYPE_TXT);
		extensionsMap.put(Constants.EXTENSION_EPUB, Readable.TYPE_EPUB);
		extensionsMap.put(Constants.EXTENSION_FB2, Readable.TYPE_FB2);
	}

	public FileStorable(){}

	public FileStorable(FileStorable that){
		super(that);
		fileInputStream = that.getFileInputStream();
	}

	public static FileStorable createFileStorable(String intentPath){
		FileStorable fileStorable;
		switch (getIntentType(intentPath)){
			case Readable.TYPE_TXT:
				fileStorable = new TxtFileStorable(intentPath, "UTF-8"); //TODO: make an option of it
				break;
			case Readable.TYPE_EPUB:
				fileStorable = new EpubFileStorable(intentPath);
				break;
			case Readable.TYPE_FB2:
				fileStorable = new FB2FileStorable(intentPath);
				break;
			default:
				fileStorable = null;
				break;
		}
		return fileStorable;
	}

	public static String takePath(Context context, String s){
		String candidate = FileUtils.getPath(context, Uri.parse(s));
		if (TextUtils.isEmpty(candidate)){ return s; }
		return candidate;
	}

	public static int getIntentType(String intentPath){
		String extension = FileUtils.getExtension(intentPath);
		if (isExtensionValid(extension)){ return extensionsMap.get(extension); }
		return -1;
	}

	public static boolean isExtensionValid(String extension){
		return extensionsMap.containsKey(extension);
	}

	public FileInputStream getFileInputStream(){
		return fileInputStream;
	}

	public long getFileSize(){
		return fileSize;
	}

	/**
	 * must be called before TextParser.process();
	 */
	public void cutLastWord(){
		String textString = text.toString();
		int index = textString.lastIndexOf(' ') + 1;
		lastWord = textString.substring(index);
		text = new StringBuilder(textString.substring(0, index));
	}

	public void copyListSuffix(Readable previous){
		wordList.clear();
		List<String> previousWordList = previous.getWordList();
		ArrayList<String> temp =
				new ArrayList<String>(
						previousWordList.subList(Math.max(0, previousWordList.size() - LAST_WORD_SUFFIX_SIZE),
																				previousWordList.size()));
		temp.addAll(wordList);
		wordList = temp;
	}

	public void copyListPrefix(Readable next){
		List<String> nextWordList = next.getWordList();
		wordList.addAll(new ArrayList<String>(nextWordList.subList(0,
				Math.min(LAST_WORD_PREFIX_SIZE, nextWordList.size()))));
	}

	public void clearWordList(){
		wordList.clear();
	}

	protected void createRowData(Context context){
		rowData = takeRowData(context);
		if (rowData != null){
			position = rowData.getPosition();
			bytePosition = rowData.getBytePosition();
		}
	}

	public FileStorable prepareNext(FileStorable result){
		result.readData();
		if (TextUtils.isEmpty(result.getText())){
			try {
				FileInputStream fis = result.getFileInputStream();
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		result.cutLastWord();
		result.insertLastWord(lastWord);
		result.clearWordList();
		result.setBytePosition(bytePosition + inputDataLength);
		return result;
	}

	protected boolean doesHaveLetters(StringBuilder text){
		for (Character ch : text.toString().toCharArray())
			if (Character.isLetter(ch)) return true;
		return false;
	}
}
