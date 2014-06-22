package com.infm.readit;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import com.infm.readit.utils.FileUtils;
import com.infm.readit.utils.Utils;

public class ReceiverActivity extends Activity {

    public static final String LOGTAG = "ReceiverActivity";

    /**
     * Starts receiver activity
     *
     * @param utils = all needed is put in Utils abstract class
     */
    public static void startReceiverActivity(Context context, Utils utils) {
        utils.process();
        if (utils.isProcessFailed())
            return;

        Intent intent = new Intent(context, ReceiverActivity.class);

        String text = utils.getSb().toString();
        Pair<Integer, String> existingData = utils.getExistingData();
        int type = utils.getType();

        if (type != FileUtils.TYPE_EPUB)
            intent.setType("text/plain");
        else
            intent.setType("text/html");

        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra("source_type", type);
        if (existingData != null) {
            intent.putExtra("position", existingData.first);
            intent.putExtra("path", existingData.second);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_receiver);
        startReaderFragment();
    }

    @Override
    public void onPause() {
        Log.d(LOGTAG, "onPause() called");
        super.onPause();
    }

    /**
     * probably should be moved to ReaderFragment. When db section is implemented, lol
     */
    @Override
    public void onStop() {
        Log.d(LOGTAG, "onStop() called");
        super.onStop();
    }

    /**
     * it seems crappy, really
     *
     * @return Bundle instance, which will be passed to ReaderFragment as bundle of args
     */
    private Bundle bundleReceivedData() {
        Intent intent = getIntent();
        Integer type = intent.getIntExtra("source_type", -1);
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        String path = intent.getStringExtra("path");
        Integer pos = intent.getIntExtra("position", 0);

        Bundle bundle = new Bundle();
        bundle.putInt("source_type", type);
        bundle.putString("text", text);
        bundle.putString("path", path);
        bundle.putInt("position", pos);
        return bundle;
    }

    private void startReaderFragment() {
        Fragment readerFragment = new ReaderFragment();
        readerFragment.setArguments(bundleReceivedData());
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, readerFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
