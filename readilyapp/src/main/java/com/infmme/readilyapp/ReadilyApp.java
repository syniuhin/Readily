package com.infmme.readilyapp;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created with love, by infm dated on 6/21/16.
 */

public class ReadilyApp extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    Fabric.with(this, new Crashlytics());
    JodaTimeAndroid.init(this);
  }
}
