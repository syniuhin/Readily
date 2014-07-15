package com.infm.readit.settings;

import android.content.SharedPreferences;

import com.infm.readit.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by infm on 6/26/14. Enjoy ;)
 */
public class SettingsBundle {

	//preferences fields
	private boolean cachingEnabled;
	private Integer WPM;
	private boolean punctuationSpeedDiffers;
	private List<Integer> delayCoefficients;
	private boolean showingContextEnabled;
	private boolean swipesEnabled;
	private Integer typeface;
	//preferences themselves
	private SharedPreferences sharedPreferences;

	public SettingsBundle(SharedPreferences sharedPreferences){
		this.sharedPreferences = sharedPreferences;
		assignFields();
	}

	public List<Integer> getDelayCoefficients(){
		return delayCoefficients;
	}

	public Integer getWPM(){
		return WPM;
	}

	public void setWPM(Integer WPM){
		this.WPM = WPM;
	}

	public boolean isCachingEnabled(){
		return cachingEnabled;
	}

	public boolean isPunctuationSpeedDiffers(){
		return punctuationSpeedDiffers;
	}

	public boolean isShowingContextEnabled(){
		return showingContextEnabled;
	}

	public boolean isSwipesEnabled(){
		return swipesEnabled;
	}

	public Integer getTypeface(){
		return typeface;
	}

	public SharedPreferences getSharedPreferences(){
		return sharedPreferences;
	}

	public void assignFields(){
		WPM = Integer.parseInt(sharedPreferences.getString(Constants.Preferences.WPM, Constants.DEFAULT_WPM));
		typeface = Integer.parseInt(sharedPreferences.getString(Constants.Preferences.TYPEFACE, "0"));
		swipesEnabled = sharedPreferences.getBoolean(Constants.Preferences.SWIPE, true);
		showingContextEnabled = sharedPreferences.getBoolean(Constants.Preferences.SHOW_CONTEXT, true);
		punctuationSpeedDiffers = sharedPreferences.getBoolean(Constants.Preferences.PUNCTUATION_DIFFERS, true);
		cachingEnabled = sharedPreferences.getBoolean(Constants.Preferences.STORAGE, true);
		delayCoefficients = buildDelayListCoefficients();
	}

	public void updatePreferences(){
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString(Constants.Preferences.WPM, Integer.toString(WPM));
		editor.putString(Constants.Preferences.TYPEFACE, Integer.toString(typeface));
		editor.putBoolean(Constants.Preferences.SWIPE, swipesEnabled);
		editor.putBoolean(Constants.Preferences.SHOW_CONTEXT, showingContextEnabled);
		editor.putBoolean(Constants.Preferences.PUNCTUATION_DIFFERS, punctuationSpeedDiffers);
		editor.putBoolean(Constants.Preferences.STORAGE, cachingEnabled);
		for (int i = 0; i < delayCoefficients.size(); ++i)
			editor.putString(Constants.Preferences.STR_PUNCTUATION_PREFS[i], delayCoefficients.get(i).toString());
		editor.apply(); //advised by IDE, lol
	}
	
	/**
	 * delayList: {default; coma/long word; end of sentence; '-' or ':' or ';'; beginning of a paragraph}
	 * default value is 10
	 * upd 07/03/14 : currently not optional
	 */
	private ArrayList<Integer> buildDelayListCoefficients(){
		ArrayList<Integer> delayCoeffs = new ArrayList<Integer>();
		for (int i = 0; i < 5; ++i)
			delayCoeffs.add(
					Integer.parseInt(sharedPreferences.getString(Constants.Preferences.STR_PUNCTUATION_PREFS[i],
							Constants.Preferences.STR_PUNCTUATION_DEFAULTS[i]))
			); //might be tricky, look at Constants class
		return delayCoeffs;
	}
}
