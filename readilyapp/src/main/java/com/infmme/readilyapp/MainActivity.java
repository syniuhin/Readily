package com.infmme.readilyapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.infmme.readilyapp.instructions.InstructionsActivity;
import com.infmme.readilyapp.readable.FileStorable;
import com.infmme.readilyapp.readable.Readable;
import com.infmme.readilyapp.service.StorageCheckerService;
import com.infmme.readilyapp.settings.SettingsActivity;
import com.infmme.readilyapp.util.BaseActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;

public class MainActivity extends BaseActivity {

	private static final int FILE_SELECT_CODE = 7331;

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
		if (getSupportActionBar() != null){
			getSupportActionBar().setIcon(R.drawable.logo_up);
		}
	}

	/**
	 * Checks if user opens app first time
	 * @param context is used to get SharedPreferences
	 */
	private void isAnybodyOutThere(Context context){
		if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.Preferences.NEWCOMER, false)){
			InstructionsActivity.start(this);
			PreferenceManager.getDefaultSharedPreferences(context)
					.edit()
					.putBoolean(Constants.Preferences.NEWCOMER, true)
					.apply();
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
				startSettingsActivity();
				break;
			case R.id.action_clipboard:
				getFromClipboard();
				break;
			case R.id.action_file:
				getFromFile();
				break;
			case R.id.action_instructions:
				InstructionsActivity.start(this);
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void getFromClipboard(){
		ReceiverActivity.startReceiverActivity(this, Readable.TYPE_CLIPBOARD, "");
	}

	private void getFromFile(){
		startActivityForResult(Intent.createChooser(FileUtils.createGetContentIntent(),
													getResources().getString(R.string.choose_file)),
							   FILE_SELECT_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		switch (requestCode){
			case FILE_SELECT_CODE:
				if (resultCode == RESULT_OK){
					if (data != null){
						String relativePath = FileUtils.getPath(this, data.getData());
						if (FileStorable.isExtensionValid(FileUtils.getExtension(relativePath))){
							ReceiverActivity.startReceiverActivity(this, Readable.TYPE_FILE, relativePath);
						} else {
							Toast.makeText(this, R.string.wrong_ext, Toast.LENGTH_SHORT).show();
						}
					}
				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private Intent createCheckerServiceIntent(){
		return new Intent(this, StorageCheckerService.class);
	}

	private void startSettingsActivity(){
		startActivity(new Intent(this, SettingsActivity.class));
	}

	private void startFileListFragment(){
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		fragmentManager.beginTransaction().
				replace(R.id.content_layout, new FileListFragment()).
				commit();
	}
}
