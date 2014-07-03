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
		WPM = Integer.parseInt(sharedPreferences.getString(Constants.PREF_WPM, Constants.DEFAULT_WPM));
		typeface = Integer.parseInt(sharedPreferences.getString(Constants.PREF_TYPEFACE, "0"));
		swipesEnabled = sharedPreferences.getBoolean(Constants.PREF_SWIPE, false);
		showingContextEnabled = sharedPreferences.getBoolean(Constants.PREF_SHOW_CONTEXT, true);
		punctuationSpeedDiffers = sharedPreferences.getBoolean(Constants.PREF_PUNCTUATION_DIFFERS, true);
		cachingEnabled = sharedPreferences.getBoolean(Constants.PREF_STORAGE, true);
		delayCoefficients = buildDelayListCoefficients();
	}

	public void updatePreferences(){
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString(Constants.PREF_WPM, Integer.toString(WPM));
		editor.putString(Constants.PREF_TYPEFACE, Integer.toString(typeface));
		editor.putBoolean(Constants.PREF_SWIPE, swipesEnabled);
		editor.putBoolean(Constants.PREF_SHOW_CONTEXT, showingContextEnabled);
		editor.putBoolean(Constants.PREF_PUNCTUATION_DIFFERS, punctuationSpeedDiffers);
		editor.putBoolean(Constants.PREF_STORAGE, cachingEnabled);
		for (int i = 0; i < delayCoefficients.size(); ++i)
			editor.putString(Constants.STR_PUNCTUATION_PREFS[i], delayCoefficients.get(i).toString());
		editor.apply(); //advised by IDE, lol
	}

	/**
	 * delayList: {default; coma/long word; end of sentence; '-' or ':' or ';'; beginning of a paragraph}
	 * default value is 10
	 * upd 07/03/14 : currently not optional
	 */
	private ArrayList<Integer> buildDelayListCoefficients(){
		ArrayList<Integer> delayCoeffs = new ArrayList<Integer>();
/*
		if (!punctuationSpeedDiffers)
			for (int i = 0; i < 5; ++i)
				delayCoeffs.add(
						Integer.parseInt(sharedPreferences.getString(Constants.STR_PUNCTUATION_PREFS[0],
								Constants.STR_PUNCTUATION_DEFAULTS[0]))
				);
		else
*/
			for (int i = 0; i < 5; ++i)
				delayCoeffs.add(
						Integer.parseInt(sharedPreferences.getString(Constants.STR_PUNCTUATION_PREFS[i],
								Constants.STR_PUNCTUATION_DEFAULTS[i]))
				); //might be tricky, look at Constants class
		return delayCoeffs;
	}
}
