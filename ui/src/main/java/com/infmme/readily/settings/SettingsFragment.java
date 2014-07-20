package com.infmme.readily.settings;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.NumberPicker;
import com.infmme.readily.Constants;
import com.infmme.readily.R;
import com.infmme.readily.ReceiverActivity;
import com.infmme.readily.readable.Readable;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment {

	private static final String LOGTAG = "SettingsFragment";
	private static final int DIALOG_PICKER_WIDTH = 250;
	private static final int DIALOG_PICKER_HEIGHT = 300;

	private Integer WPM;

	public SettingsFragment(){}

	@Override
	public void onCreate(Bundle savedInstanceState){
		Log.d(LOGTAG, "onCreate() called");
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	public void onStart(){
		Log.d(LOGTAG, "onStart() called");
		super.onStart();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		WPM = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getActivity()).
				getString(Constants.Preferences.WPM, Constants.DEFAULT_WPM));
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, @NonNull Preference preference){
		String key = preference.getKey();
		if (!TextUtils.isEmpty(key)){
			if (key.equals(Constants.Preferences.WPM)){
				showSpeedPickerDialog(getActivity(), Constants.MIN_WPM, Constants.MAX_WPM);
				return true;
			}
			if (key.equals(Constants.Preferences.TEST)){
				ReceiverActivity.startReceiverActivity(getActivity(), Readable.TYPE_RAW,
													   getResources().getString(R.string.sample_text));
				return true;
			}
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	private int pxFromDp(int dp){
		return (int) (dp * getActivity().getResources().getDisplayMetrics().density + 0.5f);
	}

	private void showSpeedPickerDialog(Context context, final int min, final int max){
		final NumberPicker numberPicker = new NumberPicker(context);
		numberPicker.setMinValue(min);
		numberPicker.setMaxValue(min + (max - min) / Constants.WPM_STEP_PREFERENCES);

		List<String> values = new ArrayList<String>();
		for (Integer i = min; i <= max; i += Constants.WPM_STEP_PREFERENCES){
			values.add(i.toString());
		}
		numberPicker.setDisplayedValues(values.toArray(new String[values.size()]));
		numberPicker.setValue(min + (WPM - min) / Constants.WPM_STEP_PREFERENCES);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.preferences_set_wpm).
				setView(numberPicker).
				setPositiveButton(android.R.string.ok,
								  new DialogInterface.OnClickListener() {
									  @Override
									  public void onClick(DialogInterface dialog, int which){
										  WPM = min + Constants.WPM_STEP_PREFERENCES * (numberPicker.getValue() - min);
										  PreferenceManager.getDefaultSharedPreferences(getActivity())
												  .edit()
												  .putString(Constants.Preferences.WPM, WPM.toString())
												  .commit();
									  }
								  }
								 ).
				setNegativeButton(android.R.string.cancel,
								  new DialogInterface.OnClickListener() {
									  @Override
									  public void onClick(DialogInterface dialog, int which){
										  dialog.cancel();
									  }
								  }
								 );
		AlertDialog dialog = builder.create();
		dialog.show();
		dialog.getWindow().setLayout(pxFromDp(DIALOG_PICKER_WIDTH), pxFromDp(DIALOG_PICKER_HEIGHT));
	}
}
