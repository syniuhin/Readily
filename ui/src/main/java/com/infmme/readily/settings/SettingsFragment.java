package com.infmme.readily.settings;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.*;
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
	private static final int MIN_FONT_SIZE = 12;
	private static final int MAX_FONT_SIZE = 30;

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
			if (key.equals(Constants.Preferences.FEEDBACK)){
				try {
					Resources resources = getResources();
					startActivity(Intent.createChooser(new Intent(Intent.ACTION_SENDTO,
																  Uri.parse(
																		  resources.getString(R.string.mail_to_me)
																		   )), resources.getString(R.string.send_email)));
				} catch (ActivityNotFoundException e) {
					Toast.makeText(getActivity(), R.string.email_app_not_found, Toast.LENGTH_SHORT).show();
				}
			}
			if (key.equals(Constants.Preferences.FONT_SIZE)){
				showFontSizeDialog(getActivity());
			}
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	private int pxFromDp(int dp){
		return (int) (dp * getActivity().getResources().getDisplayMetrics().density + 0.5f);
	}

	private float spToPixels(Context context, float sp) {
		float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return sp * scaledDensity;
	}

	private void showFontSizeDialog(Context context){
		final NumberPicker numberPicker = new NumberPicker(context);
		numberPicker.setMinValue(MIN_FONT_SIZE);
		numberPicker.setMaxValue(MAX_FONT_SIZE);
		int currentFontSize = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).
				getString(Constants.Preferences.FONT_SIZE, "18"));
		numberPicker.setValue(currentFontSize);

		final LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
																  ViewGroup.LayoutParams.WRAP_CONTENT));
		final TextView sampleText = new TextView(context);
		sampleText.setText(R.string.just_sample);
		sampleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentFontSize);
		int textHeight = (int) (spToPixels(getActivity(), MAX_FONT_SIZE + 10) + .5f);
		sampleText.setHeight(textHeight);
		sampleText.setGravity(Gravity.CENTER_HORIZONTAL);

		linearLayout.addView(sampleText);
		linearLayout.addView(numberPicker);
		numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal){
				sampleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, newVal);
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.preferences_set_wpm).
				setView(linearLayout).
				setPositiveButton(android.R.string.ok,
								  new DialogInterface.OnClickListener() {
									  @Override
									  public void onClick(DialogInterface dialog, int which){
										  PreferenceManager.getDefaultSharedPreferences(getActivity())
												  .edit()
												  .putString(Constants.Preferences.FONT_SIZE,
															 String.valueOf(numberPicker.getValue()))
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
		dialog.getWindow().setLayout(pxFromDp(DIALOG_PICKER_WIDTH), pxFromDp(DIALOG_PICKER_HEIGHT) + textHeight);
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
