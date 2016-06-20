package com.infmme.readilyapp.settings;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import com.infmme.readilyapp.R;

/**
 * Created with love, by infm dated on 6/20/16.
 */

public class SettingsFragment extends PreferenceFragmentCompat {
  @Override
  public void onCreatePreferences(Bundle bundle, String s) {
    addPreferencesFromResource(R.xml.preferences);
  }
}
