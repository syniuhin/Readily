package com.infmme.readily.settings;

import android.content.SharedPreferences;
import com.infmme.readily.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by infm on 6/26/14. Enjoy ;)
 */
public class SettingsBundle {

	//preferences fields
	private Integer WPM;
	private boolean punctuationSpeedDiffers;
	private List<Integer> delayCoefficients;
	private boolean showingContextEnabled;
	private boolean swipesEnabled;
	private boolean storingComplete;
	private Integer typeface;
	//preferences themselves
	private SharedPreferences sharedPreferences;

	public SettingsBundle(SharedPreferences sharedPreferences){
		this.sharedPreferences = sharedPreferences;
		assignFields();
	}

	public boolean isStoringComplete(){
		return storingComplete;
	}

	public void setStoringComplete(boolean storingComplete){
		this.storingComplete = storingComplete;
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
		swipesEnabled = sharedPreferences.getBoolean(Constants.Preferences.SWIPE, false);
		showingContextEnabled = sharedPreferences.getBoolean(Constants.Preferences.SHOW_CONTEXT, true);
		punctuationSpeedDiffers = sharedPreferences.getBoolean(Constants.Preferences.PUNCTUATION_DIFFERS, true);
		delayCoefficients = buildDelayListCoefficients();
		storingComplete = sharedPreferences.getBoolean(Constants.Preferences.STORE_COMPLETE, false);
	}

	public void updatePreferences(){
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString(Constants.Preferences.WPM, Integer.toString(WPM));
		editor.putString(Constants.Preferences.TYPEFACE, Integer.toString(typeface));
		editor.putBoolean(Constants.Preferences.SWIPE, swipesEnabled);
		editor.putBoolean(Constants.Preferences.SHOW_CONTEXT, showingContextEnabled);
		editor.putBoolean(Constants.Preferences.PUNCTUATION_DIFFERS, punctuationSpeedDiffers);
		editor.putBoolean(Constants.Preferences.STORE_COMPLETE, storingComplete);
		editor.apply(); //advised by IDE, lol
	}

	/**
	 * delayList: {default; coma/long word; end of sentence; '-' or ':' or ';'; beginning of a paragraph}
	 * default value is 10
	 * upd 07/03/14 : currently not optional
	 */
	private ArrayList<Integer> buildDelayListCoefficients(){
		ArrayList<Integer> delayCoeffs = new ArrayList<Integer>();
		if (punctuationSpeedDiffers){
			for (int i = 0; i < 5; ++i){
				delayCoeffs.add(Integer.parseInt(Constants.Preferences.STR_PUNCTUATION_DEFAULTS[i]));
			}
		} else {
			for (int i = 0; i < 5; ++i){
				delayCoeffs.add(Integer.parseInt(Constants.Preferences.STR_PUNCTUATION_DEFAULTS[0]));
			}
		}
		return delayCoeffs;
	}
}
