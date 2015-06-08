package com.infmme.readilyapp.readable;

import android.content.ClipData;
import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.text.ClipboardManager;

/**
 * Created by infm on 6/12/14. Enjoy ;)
 */
public class ClipboardReadable extends Readable {

  private ClipboardManager clipboardOld;
  private android.content.ClipboardManager clipboardNew;
  private boolean oncePasted;

  public ClipboardReadable() {
    type = TYPE_CLIPBOARD;
  }

  public ClipboardReadable(ClipboardReadable that) {
    super(that);
    clipboardOld = that.getClipboardOld();
    clipboardNew = that.getClipboardNew();
    oncePasted = that.isOncePasted();
  }

  public boolean isOncePasted() {
    return oncePasted;
  }

  public ClipboardManager getClipboardOld() {
    return clipboardOld;
  }

  public android.content.ClipboardManager getClipboardNew() { return clipboardNew; }

  public void process(final Context context) {
    Looper.prepare(); //TODO: review it CAREFULLY
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      clipboardNew = (android.content.ClipboardManager) context.getSystemService(
          Context.CLIPBOARD_SERVICE);
      processFailed = ! clipboardNew.hasText();
      processed = true;
    } else {
      clipboardOld = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
      processFailed = ! clipboardOld.hasText();
      processed = true;
    }
  }

  @Override
  public void readData() {
    if (! oncePasted)
      text.append(paste());
    else
      setText("");
  }

  @Override
  public Readable getNext() {
    ClipboardReadable result = new ClipboardReadable(this);
    result.readData();
    return result;
  }

  private String paste() {
    oncePasted = true;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      ClipData clipData = clipboardNew.getPrimaryClip();
      if (clipData != null && clipData.getItemCount() > 0) {
        ClipData.Item item = clipboardNew.getPrimaryClip().getItemAt(0);
        CharSequence pasteData = item.getText();
        if (pasteData != null) {
          return pasteData.toString();
        }
      }
    } else {
      return clipboardOld.getText().toString();
    }
    return null;
  }
}
