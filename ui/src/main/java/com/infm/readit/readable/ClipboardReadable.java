package com.infm.readit.readable;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.widget.Toast;

import com.infm.readit.Constants;
import com.infm.readit.R;

/**
 * Created by infm on 6/12/14. Enjoy ;)
 */
public class ClipboardReadable extends Readable {

    public ClipboardReadable() {
        super();
        textType = "text/plain";
    }

    @Override
    public String getLink() {
        return null;
    }

    @Override
    public void setLink(String link) {

    }

    @Override
    public ChunkData getChunkData() {
        return null;
    }

    @Override
    public void setChunkData(ChunkData data) {

    }

    public void process(Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (processFailed = !clipboard.hasText()) {
            Toast.makeText(context, context.getResources().getString(R.string.clipboard_empty), Toast.LENGTH_SHORT).show();
        } else {
            text.append(clipboard.getText().toString());
            setType(TYPE_CLIPBOARD);
        }
    }
}
