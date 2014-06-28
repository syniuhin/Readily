package com.infm.readit;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;

import com.infm.readit.readable.Readable;
import com.infm.readit.service.TextParserService;

public class ReceiverActivity extends Activity {

    public static final String LOGTAG = "ReceiverActivity";

    public static void startReceiverActivity(Context context, Integer intentType, String intentPath){
        Intent intent = new Intent(context, ReceiverActivity.class);

        intent.setType(((intentType == Readable.TYPE_EPUB ||
                (intentType == Readable.TYPE_FILE &&
                        MimeTypeMap.getFileExtensionFromUrl(intentPath).equals("epub")))
                ? "text/html"
                : "text/plain"));

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.EXTRA_TYPE, intentType);
        bundle.putString(Constants.EXTRA_PATH, intentPath);
        intent.putExtras(bundle);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        startReaderFragment();
        startService(createTextParserServiceIntent());
    }

    @Override
    public void onPause(){
        Log.d(LOGTAG, "onPause() called");
        super.onPause();
    }

    /**
     * probably should be moved to ReaderFragment. When db section is implemented, lol
     */
    @Override
    public void onStop(){
        Log.d(LOGTAG, "onStop() called");
        super.onStop();
    }

    /**
     * it seems crappy, really
     *
     * @return Bundle instance, which will be passed to ReaderFragment as bundle of args
     */
    private Bundle bundleReceivedData(){
        Bundle bundle = getIntent().getExtras();
        Log.d(LOGTAG, "bundle: " + ((bundle == null) ? "null" : bundle.toString()));
        return bundle;
    }

    private void startReaderFragment(){
        Fragment readerFragment = new ReaderFragment();
        readerFragment.setArguments(bundleReceivedData());
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, readerFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private Intent createTextParserServiceIntent(){
        Intent intent = new Intent(this, TextParserService.class);
        intent.putExtra(Constants.EXTRA_PATH, getIntent().getStringExtra(Constants.EXTRA_PATH));
        intent.putExtra(Intent.EXTRA_TEXT, getIntent().getStringExtra(Intent.EXTRA_TEXT));
        intent.putExtra(Constants.EXTRA_TYPE, getIntent().getIntExtra(Constants.EXTRA_TYPE, Readable.TYPE_TEST));
        return intent;
    }
}
