package com.infmme.readilyapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.daimajia.androidanimations.library.BuildConfig;
import com.infmme.readilyapp.readable.fb2.FB2Storable;
import com.infmme.readilyapp.util.Constants;

import java.io.IOException;

/**
 * Created with love, by infm dated on 6/16/16.
 */

public class FB2ProcessingService extends IntentService {
  /**
   * Creates an IntentService.  Invoked by your subclass's constructor.
   *
   * @param name Used to name the worker thread, important only for debugging.
   */
  public FB2ProcessingService(String name) {
    super(name);
  }

  public FB2ProcessingService() {
    super(FB2ProcessingService.class.getName());
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    String path = intent.getStringExtra(Constants.EXTRA_PATH);
    FB2Storable fb2Storable = new FB2Storable(this);
    fb2Storable.setPath(path);
    fb2Storable.process();
    if (!fb2Storable.isFullyProcessed()) {
      if (BuildConfig.DEBUG) {
        Log.d(FB2ProcessingService.class.getName(),
              "Starts fully processing of %s" + fb2Storable.getPath());
      }
      try {
        fb2Storable.processFully();
        if (BuildConfig.DEBUG) {
          Log.d(FB2ProcessingService.class.getName(),
                "Finishes fully processing of %s" + fb2Storable.getPath());
        }
        fb2Storable.storeToDb();
        if (BuildConfig.DEBUG) {
          Log.d(FB2ProcessingService.class.getName(),
                "Finishes storing of %s" + fb2Storable.getPath());
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
