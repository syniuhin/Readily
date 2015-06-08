package com.infmme.readilyapp.readable;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import com.infmme.readilyapp.Constants;
import com.ipaulpro.afilechooser.utils.FileUtils;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by infm on 6/13/14. Enjoy ;)
 */
abstract public class FileStorable extends Storable {

  public static final HashMap<String, Integer> extensionsMap = new HashMap<String, Integer>();
  public static final int BUFFER_SIZE = 4096;
  public static final int LAST_WORD_PREFIX_SIZE = 10;

  static {
    extensionsMap.put(Constants.EXTENSION_TXT, Readable.TYPE_TXT);
    extensionsMap.put(Constants.EXTENSION_EPUB, Readable.TYPE_EPUB);
    extensionsMap.put(Constants.EXTENSION_FB2, Readable.TYPE_FB2);
  }

  protected FileInputStream fileInputStream;
  protected String lastWord = "";
  protected long inputDataLength;
  protected long fileSize;
  protected String encoding = "";

  public FileStorable() {}

  public FileStorable(FileStorable that) {
    super(that);
    fileInputStream = that.getFileInputStream();
    fileSize = that.getFileSize();
    encoding = that.getEncoding();
  }

  public static FileStorable createFileStorable(String intentPath) {
    FileStorable fileStorable;
    switch (getIntentType(intentPath)) {
      case Readable.TYPE_TXT:
        fileStorable = new TxtFileStorable(intentPath);
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

  public static String takePath(Context context, String s) {
    String candidate = FileUtils.getPath(context, Uri.parse(s));
    if (TextUtils.isEmpty(candidate)) { return s; }
    return candidate;
  }

  public static int getIntentType(String intentPath) {
    String extension = FileUtils.getExtension(intentPath);
    if (isExtensionValid(extension)) { return extensionsMap.get(extension); }
    return - 1;
  }

  public static boolean isExtensionValid(String extension) {
    return extensionsMap.containsKey(extension);
  }

  public static String guessCharset(InputStream is) throws IOException {
    UniversalDetector detector = new UniversalDetector(null);
    byte[] buf = new byte[Constants.ENCODING_HELPER_BUFFER_SIZE];
    int nread;
    while ((nread = is.read(buf)) > 0 && ! detector.isDone()) {
      detector.handleData(buf, 0, nread);
    }
    detector.dataEnd();
    String encoding = detector.getDetectedCharset();
    detector.reset();
    if (encoding != null)
      return encoding;
    return Constants.DEFAULT_ENCODING;
  }

  public FileInputStream getFileInputStream() {
    return fileInputStream;
  }

  public long getFileSize() {
    return fileSize;
  }

  public String getEncoding() {
    return encoding;
  }

  /**
   * must be called before TextParser.process();
   */
  public void cutLastWord() {
    String textString = text.toString();
    int index = textString.lastIndexOf(' ') + 1;
    lastWord = textString.substring(index);
    text = new StringBuilder(textString.substring(0, index));
  }

  public void copyListPrefix(Readable next) {
    List<String> nextWordList = next.getWordList();
    wordList.addAll(new ArrayList<String>(nextWordList.subList(0,
                                                               Math.min(LAST_WORD_PREFIX_SIZE,
                                                                        nextWordList.size()))));

    List<Integer> nextEmphasisList = next.getEmphasisList();
    emphasisList.addAll(new ArrayList<Integer>(nextEmphasisList.subList(0,
                                                                        Math.min(
                                                                            LAST_WORD_PREFIX_SIZE,
                                                                            nextEmphasisList.size
                                                                                ()))));

    List<Integer> nextDelayList = next.getDelayList();
    delayList.addAll(new ArrayList<Integer>(nextDelayList.subList(0,
                                                                  Math.min(LAST_WORD_PREFIX_SIZE,
                                                                           nextDelayList.size()))));
  }

  protected void createRowData(Context context) {
    rowData = takeRowData(context);
    if (rowData != null) {
      position = rowData.getPosition();
      bytePosition = rowData.getBytePosition();
    }
  }

  public FileStorable prepareNext(FileStorable result) {
    result.readData();
    if (TextUtils.isEmpty(result.getText())) {
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
    result.setBytePosition(bytePosition + inputDataLength);
    return result;
  }

  @Override
  public int calcProgress(int pos, long approxCharCount) {
    return Math.min((int) (100f * (bytePosition + approxCharCount) / fileSize + .5f), 99);
  }

  @Override
  public Intent putInsertionDataInIntent(Intent intent) {
    return super.putInsertionDataInIntent(intent).
        putExtra(Constants.EXTRA_POSITION, position).
                    putExtra(Constants.EXTRA_BYTE_POSITION, bytePosition);
  }

  protected boolean doesHaveLetters(StringBuilder text) {
    for (Character ch : text.toString().toCharArray())
      if (Character.isLetter(ch)) return true;
    return false;
  }
}
