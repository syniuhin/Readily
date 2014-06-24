package com.infm.readit;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.WindowManager;

import com.infm.readit.database.LastReadDBHelper;
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
        Pair<Integer, Integer> existingData = utils.getExistingData();
        int type = utils.getType();

        if (type != FileUtils.TYPE_EPUB)
            intent.setType("text/plain");
        else
            intent.setType("text/html");

        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra("source_type", type);
        intent.putExtra(LastReadDBHelper.KEY_PATH, utils.getPath());
        if (existingData != null) {
            intent.putExtra(LastReadDBHelper.KEY_ROWID, existingData.first);
            intent.putExtra(LastReadDBHelper.KEY_POSITION, existingData.second);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        String path = intent.getStringExtra(LastReadDBHelper.KEY_PATH);
        Integer pos = intent.getIntExtra(LastReadDBHelper.KEY_POSITION, -1);

        Bundle bundle = new Bundle();
        bundle.putInt("source_type", type);
        bundle.putString("text", text);
        bundle.putString(LastReadDBHelper.KEY_PATH, path);
        bundle.putInt(LastReadDBHelper.KEY_POSITION, pos);

        Log.d(LOGTAG, "bundle: " + bundle.toString());
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
