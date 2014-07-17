package com.infm.readit.settings;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import android.widget.NumberPicker;
import com.infm.readit.Constants;
import com.infm.readit.R;
import com.infm.readit.ReceiverActivity;
import com.infm.readit.instructions.InstructionsActivity;
import com.infm.readit.readable.Readable;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment {

    private static final String LOGTAG = "SettingsFragment";

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
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference){
		String key = preference.getKey();
		if (!TextUtils.isEmpty(key)){
			if (key.equals(Constants.Preferences.WPM)){
				showSpeedPickerDialog(getActivity(), Constants.MIN_WPM, Constants.MAX_WPM);
				return true;
			}
			if (key.equals(Constants.Preferences.INSTRUCTIONS)){
                InstructionsActivity.start(getActivity());
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

	@Override
	public void onStop(){
		super.onStop();
		PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString(Constants.Preferences.WPM, WPM.toString()).apply();
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
		new AlertDialog.Builder(context).
				setTitle(R.string.preferences_set_wpm).
				setView(numberPicker).
				setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which){
								WPM = min + Constants.WPM_STEP_PREFERENCES * (numberPicker.getValue() - min);
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
				).show();
	}
}
