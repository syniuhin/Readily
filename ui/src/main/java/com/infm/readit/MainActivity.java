package com.infm.readit;

import android.annotation.TargetApi;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import com.infm.readit.util.BaseActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;

public class MainActivity extends BaseActivity {

	public static final String LOGTAG = "MainActivity";

	private static final int FILE_SELECT_CODE = 7331;
	private static final String SETTINGS_FRAGMENT_TAG = "SettingsFragment0182";
    private static final String FILE_LIST_FRAGMENT_TAG = "FileLis11101asdtFraagment123";

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		isAnybodyOutThere(this);

		startService(createCheckerServiceIntent());

		Crashlytics.start(this);

        changeActionBarIcon();
		startFileListFragment();
	}

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void changeActionBarIcon(){
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setIcon(R.drawable.logo_up);
    }

	private void isAnybodyOutThere(Context context){
		if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.Preferences.NEWCOMER, false)){
			Constants.showInstructionsDialog(context);
			PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(Constants.Preferences.NEWCOMER, true).apply();
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

	private void getFromClipboard(){
		ReceiverActivity.startReceiverActivity(this, Readable.TYPE_CLIPBOARD, "");
	}

	private void getFromFile(){
		Intent intent = Intent.createChooser(FileUtils.createGetContentIntent(), getResources().getString(R.string.choose_file));
		startActivityForResult(intent, FILE_SELECT_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		switch (requestCode){
			case FILE_SELECT_CODE:
				if (resultCode == RESULT_OK){
					if (data != null){
						String relativePath = FileUtils.getPath(this, data.getData());
						if (FileStorable.isExtensionValid(FileUtils.getExtension(relativePath)))
							ReceiverActivity.startReceiverActivity(this, Readable.TYPE_FILE, relativePath);
						else
							Toast.makeText(this, R.string.wrong_ext, Toast.LENGTH_SHORT).show();
					}
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
					addToBackStack(SETTINGS_FRAGMENT_TAG).
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
