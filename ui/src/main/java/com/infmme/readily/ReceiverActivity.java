package com.infmme.readily;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import com.infmme.readily.R;
import com.infmme.readily.util.BaseActivity;

public class ReceiverActivity extends BaseActivity implements /*FlurryAdListener,*/ ReaderFragment.ReaderListener {

	private static final String READER_FRAGMENT_TAG = "ReaSq!d99erFra{{1239gm..1ent1923";

	public static void startReceiverActivity(Context context, Integer intentType, String intentPath){
		Intent intent = new Intent(context, ReceiverActivity.class);

		Bundle bundle = new Bundle();
		bundle.putInt(Constants.EXTRA_TYPE, intentType);
		bundle.putString(Constants.EXTRA_PATH, intentPath);
		intent.putExtras(bundle);

		context.startActivity(intent);
	}

	public static void startReceiverActivity(Context context, Bundle args){
		Intent intent = new Intent(context, ReceiverActivity.class);
		intent.putExtras(args);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_receiver);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		Bundle bundle = bundleReceivedData();
		startReaderFragment(bundle);
	}

	private Bundle bundleReceivedData(){
		return getIntent().getExtras();
	}

	private void startReaderFragment(Bundle bundle){
		FragmentManager fragmentManager = getFragmentManager();
		ReaderFragment readerFragment = (ReaderFragment) fragmentManager.findFragmentByTag(READER_FRAGMENT_TAG);
		if (readerFragment == null){
			readerFragment = new ReaderFragment();
			if (bundle != null){
				readerFragment.setArguments(bundle);
				fragmentManager.beginTransaction().
						add(R.id.fragment_container, readerFragment, READER_FRAGMENT_TAG).
						addToBackStack(null).
						commit();
			}
		}
	}

	@Override
	public void stop(){
		finish();
	}
}
