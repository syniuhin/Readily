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
		startTextParserService(bundle);
	}

	private Bundle bundleReceivedData(){
		Bundle bundle = getIntent().getExtras();
		Log.d(LOGTAG, "bundle: " + ((bundle == null) ? "null" : bundle.toString()));
		return bundle;
	}

	private void startReaderFragment(Bundle bundle){
		if (bundle != null){
			Fragment readerFragment = new ReaderFragment();
			readerFragment.setArguments(bundle);
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.fragment_container, readerFragment);
			transaction.addToBackStack(null);
			transaction.commit();
		} else {
			Log.d(LOGTAG, "startReaderFragment(): bundle is null");
		}
	}

	private void startTextParserService(Bundle bundle){
		Intent serviceIntent = createParserServiceIntent(bundle);
		if (serviceIntent != null)
			startService(serviceIntent);
	}

	private Intent createParserServiceIntent(Bundle bundle){
		if (bundle != null){
			Intent intent = new Intent(this, TextParserService.class);
			intent.putExtras(bundle);
			return intent;
		} else {
			Log.d(LOGTAG, "createParserServiceIntent(): bundle is null");
			return null;
		}
	}
}
