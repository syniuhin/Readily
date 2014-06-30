package com.infm.readit;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.infm.readit.readable.Readable;
import com.infm.readit.service.TextParserService;

public class ReceiverActivity extends Activity {

    public static final String LOGTAG = "ReceiverActivity";

    public static void startReceiverActivity(Context context, Integer intentType, String intentPath){
        Intent intent = new Intent(context, ReceiverActivity.class);

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

        Bundle bundle = bundleReceivedData();
        startReaderFragment(bundle);
        startService(createServiceIntent(bundle));
    }

    private Bundle bundleReceivedData(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && !bundle.containsKey(Intent.EXTRA_TEXT) && !bundle.containsKey(Constants.EXTRA_TYPE)) //obscure
            bundle.putInt(Constants.EXTRA_TYPE, Readable.TYPE_TEST);
        Log.d(LOGTAG, "bundle: " + ((bundle == null) ? "null" : bundle.toString()));
        return bundle;
    }

    private void startReaderFragment(Bundle bundle){
        Fragment readerFragment = new ReaderFragment();
        readerFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, readerFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private Intent createServiceIntent(Bundle bundle){
        Intent intent = new Intent(this, TextParserService.class);
        intent.putExtras(bundle);
        return intent;
    }
}
