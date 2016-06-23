package com.infmme.readilyapp.readable;

import android.content.ClipData;
import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.ClipboardManager;
import com.infmme.readilyapp.readable.interfaces.Unprocessed;

/**
 * Created with love, by infm dated on 6/8/16.
 */

public class ClipboardReadable extends Readable implements Unprocessed {
  // TODO: Handle link from the clipboard.
  // private String mLink;
  private boolean mProcessed = false;

  @SuppressWarnings("deprecation")
  private ClipboardManager clipboardOld;
  private android.content.ClipboardManager clipboardNew;

  // I don't like this, have to think of leaks.
  private final transient Context mContext;

  public ClipboardReadable(final Context context) {
    mContext = context;
  }

  @Override
  public boolean isProcessed() {
    return mProcessed;
  }

  @Override
  public void setProcessed(boolean processed) {
    mProcessed = processed;
  }

  @Override
  public void process() {
    Looper.prepare(); //TODO: review it CAREFULLY
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      clipboardNew = (android.content.ClipboardManager) mContext
          .getSystemService(
              Context.CLIPBOARD_SERVICE);
    } else {
      //noinspection deprecation
      clipboardOld = (ClipboardManager) mContext.getSystemService(
          Context.CLIPBOARD_SERVICE);
    }
    setText(paste());
    mProcessed = true;
  }

  @Nullable
  private String paste() {
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
    return null; // TODO: Figure out why not empty string.
  }
}
