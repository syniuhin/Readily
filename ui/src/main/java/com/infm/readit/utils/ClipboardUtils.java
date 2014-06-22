package com.infm.readit.utils;

import android.content.Context;
import android.text.ClipboardManager;
import android.widget.Toast;

import com.infm.readit.R;

/**
 * Created by infm on 6/19/14. Enjoy ;)
 */
public class ClipboardUtils extends Utils {
    Context context;

    public ClipboardUtils(Context context) {
        super();
        this.context = context;
    }

    public void process() {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (processFailed = !clipboard.hasText()) {
            Toast.makeText(context, context.getResources().getString(R.string.clipboard_empty), Toast.LENGTH_SHORT).show();
        } else {
            sb.append(clipboard.getText().toString());
            setType(TYPE_CLIPBOARD);
        }
    }
}
