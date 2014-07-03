package com.infm.readit;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.infm.readit.readable.FileStorable;
import com.infm.readit.readable.Readable;
import com.infm.readit.service.StorageCheckerService;
import com.infm.readit.settings.SettingsFragment;
import com.newrelic.agent.android.NewRelic;

public class MainActivity extends Activity {

	public static final String LOGTAG = "MainActivity";

	private static final int FILE_SELECT_CODE = 0;
	private static final String SETTINGS_FRAGMENT_TAG = "FileListFragment0182";

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		isAnybodyOutThere(this);

		startService(createCheckerServiceIntent());

		Crashlytics.start(this);
		NewRelic.withApplicationToken(
				"AAb54a33233473ebe708b5daec8505d0928bd07238"
		).start(this.getApplication());

		startFileListFragment();
	}

	private void isAnybodyOutThere(Context context){
		if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.PREF_NEWCOMER, false)){
			Constants.showInstructionsDialog(context);
			PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(Constants.PREF_NEWCOMER, true).apply();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.action_settings:
				startSettingsFragment();
				break;
			case R.id.action_clipboard:
				getFromClipboard();
				break;
			case R.id.action_file:
				getFromFile();
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * This section is handled using abstract class Utils. Hope it's ok.
	 */
	private void getFromClipboard(){
		ReceiverActivity.startReceiverActivity(this, Readable.TYPE_CLIPBOARD, "");
	}

	private void getFromFile(){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("*/*");
		try {
			startActivityForResult(intent, FILE_SELECT_CODE);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, getResources().getString(R.string.file_manager_required),
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		switch (requestCode){
			case FILE_SELECT_CODE:
				if (resultCode == RESULT_OK){
					String relativePath = data.getData().toString();
					if (FileStorable.isExtensionValid(FileStorable.getExtension(relativePath)))
						ReceiverActivity.startReceiverActivity(this, Readable.TYPE_FILE, relativePath);
					else
						Toast.makeText(this, R.string.wrong_ext, Toast.LENGTH_SHORT).show();
				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private Intent createCheckerServiceIntent(){
		return new Intent(this, StorageCheckerService.class);
	}

	private void startSettingsFragment(){
		FragmentManager fragmentManager = getFragmentManager();
		Fragment existing = fragmentManager.findFragmentByTag(SETTINGS_FRAGMENT_TAG);
		if (existing == null){
			getFragmentManager().beginTransaction().
					replace(R.id.content_layout, new SettingsFragment(), SETTINGS_FRAGMENT_TAG).
					addToBackStack(null).
					setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).
					commit();
		} else {
			YoYo.with(Techniques.Tada).
					duration(700).
					playOn(findViewById(R.id.content_layout));
		}
	}

	private void startFileListFragment(){
		getFragmentManager().beginTransaction().
				replace(R.id.content_layout, new FileListFragment()).
				commit();
	}
}
