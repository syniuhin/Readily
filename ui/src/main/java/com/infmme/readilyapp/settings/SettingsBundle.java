package com.infmme.readilyapp.settings;

import android.content.SharedPreferences;
import com.infmme.readilyapp.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by infm on 6/26/14. Enjoy ;)
 */
public class SettingsBundle {

	//preferences fields
	private Integer WPM;
	private Integer fontSize;
	private boolean punctuationSpeedDiffers;
	private List<Integer> delayCoefficients;
	private boolean showingContextEnabled;
	private boolean swipesEnabled;
	private boolean storingComplete;
	private Integer typeface;
	private boolean darkTheme;
	//preferences themselves
	private SharedPreferences sharedPreferences;

	public SettingsBundle(SharedPreferences sharedPreferences){
		this.sharedPreferences = sharedPreferences;
		assignFields();
	}

	public boolean isDarkTheme(){
		return darkTheme;
	}

	public void setDarkTheme(boolean darkTheme){
		this.darkTheme = darkTheme;
	}

	public Integer getFontSize(){
		return fontSize;
	}

	public void setFontSize(Integer fontSize){
		this.fontSize = fontSize;
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

	public void setDelayCoefficients(List<Integer> delayCoefficients){
		this.delayCoefficients = delayCoefficients;
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

	public void setPunctuationSpeedDiffers(boolean punctuationSpeedDiffers){
		this.punctuationSpeedDiffers = punctuationSpeedDiffers;
	}

	public boolean isShowingContextEnabled(){
		return showingContextEnabled;
	}

	public void setShowingContextEnabled(boolean showingContextEnabled){
		this.showingContextEnabled = showingContextEnabled;
	}

	public boolean isSwipesEnabled(){
		return swipesEnabled;
	}

	public void setSwipesEnabled(boolean swipesEnabled){
		this.swipesEnabled = swipesEnabled;
	}

	public Integer getTypeface(){
		return typeface;
	}

	public void setTypeface(Integer typeface){
		this.typeface = typeface;
	}

	public SharedPreferences getSharedPreferences(){
		return sharedPreferences;
	}

	public void setSharedPreferences(SharedPreferences sharedPreferences){
		this.sharedPreferences = sharedPreferences;
	}

	public void assignFields(){
		WPM = Integer.parseInt(sharedPreferences.getString(Constants.Preferences.WPM, Constants.DEFAULT_WPM));
		fontSize = Integer.parseInt(
				sharedPreferences.getString(Constants.Preferences.FONT_SIZE, Constants.DEFAULT_FONT_SIZE));
		typeface = Integer.parseInt(sharedPreferences.getString(Constants.Preferences.TYPEFACE, "0"));
		swipesEnabled = sharedPreferences.getBoolean(Constants.Preferences.SWIPE, false);
		showingContextEnabled = sharedPreferences.getBoolean(Constants.Preferences.SHOW_CONTEXT, true);
		punctuationSpeedDiffers = sharedPreferences.getBoolean(Constants.Preferences.PUNCTUATION_DIFFERS, true);
		delayCoefficients = buildDelayListCoefficients();
		storingComplete = sharedPreferences.getBoolean(Constants.Preferences.STORE_COMPLETE, false);
		darkTheme = sharedPreferences.getBoolean(Constants.Preferences.DARK_THEME, false);
	}

	public void updatePreferences(){
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString(Constants.Preferences.WPM, Integer.toString(WPM));
		editor.putString(Constants.Preferences.FONT_SIZE, Integer.toString(fontSize));
		editor.putString(Constants.Preferences.TYPEFACE, Integer.toString(typeface));
		editor.putBoolean(Constants.Preferences.SWIPE, swipesEnabled);
		editor.putBoolean(Constants.Preferences.SHOW_CONTEXT, showingContextEnabled);
		editor.putBoolean(Constants.Preferences.PUNCTUATION_DIFFERS, punctuationSpeedDiffers);
		editor.putBoolean(Constants.Preferences.STORE_COMPLETE, storingComplete);
		editor.putBoolean(Constants.Preferences.DARK_THEME, darkTheme);
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
			for (int i = 0; i < 6; ++i){
				delayCoeffs.add(Integer.parseInt(Constants.Preferences.STR_PUNCTUATION_DEFAULTS[i]));
			}
		} else {
			for (int i = 0; i < 6; ++i){
				delayCoeffs.add(Integer.parseInt(Constants.Preferences.STR_PUNCTUATION_DEFAULTS[0]));
			}
		}
		return delayCoeffs;
	}
}
