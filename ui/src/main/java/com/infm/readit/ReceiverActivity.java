package com.infm.readit;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import com.infm.readit.util.BaseActivity;

public class ReceiverActivity extends BaseActivity implements /*FlurryAdListener,*/ ReaderFragment.ReaderListener{

	private static final String LOGTAG = "ReceiverActivity";
	private static final String READER_FRAGMENT_TAG = "ReaSq!d99erFra{{1239gm..1ent1923";

/*
    private ViewGroup adViewGroup;
    private String adSpace = "ReadItIntAd";
*/

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
		Bundle bundle = getIntent().getExtras();
		if (bundle != null){
            Log.i(LOGTAG, "bundle: " + bundle.toString());
        } else {
            Log.w(LOGTAG, "bundle: null");
        }
		return bundle;
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
            } else {
                Log.w(LOGTAG, "startReaderFragment(): bundle is null");
            }
        }
	}
/*

    @Override
    protected void onStart(){
        super.onStart();
        integrateAds();
    }

    @Override
    protected void onStop(){
        FlurryAds.removeAd(this, adSpace, adViewGroup);
        super.onStop();
    }

    private void integrateAds(){
        FlurryAds.setAdListener(this);
        adViewGroup = (ViewGroup) findViewById(R.id.fragment_container);
    }

    @Override
    public boolean shouldDisplayAd(String s, FlurryAdType flurryAdType){
        return true;
    }

    @Override
    public void onAdClosed(String s){
        finish();
    }

    @Override
    public void onApplicationExit(String s){
        finish();
    }

    @Override
    public void onRendered(String s){

    }

    @Override
    public void onRenderFailed(String s){
        finish();
    }

    @Override
    public void spaceDidReceiveAd(String s){
        FlurryAds.displayAd(this, s, adViewGroup);
    }

    @Override
    public void spaceDidFailToReceiveAd(String s){
        finish();
    }

    @Override
    public void onAdClicked(String s){

    }

    @Override
    public void onAdOpened(String s){

    }

    @Override
    public void onVideoCompleted(String s){
        finish();
    }

*/
    @Override
    public void stop(){
/*
        FlurryAds.fetchAd(this, adSpace, adViewGroup, FlurryAdSize.FULLSCREEN);
*/
        finish();
    }
}
