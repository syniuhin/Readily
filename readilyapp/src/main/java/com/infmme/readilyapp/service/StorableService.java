package com.infmme.readilyapp.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import com.infmme.readilyapp.readable.interfaces.Storable;
import com.infmme.readilyapp.util.Constants;

/**
 * Created with love, by infm dated on 6/18/16.
 * <p>
 * Stores serialized Storable to a database.
 */
public class StorableService extends IntentService {
  private static final String ACTION_INSERT_OR_UPDATE =
      StorableService.class.getPackage().getName() + ".insertOrUpdate";

  public StorableService() {
    super(StorableService.class.getName());
  }

  public static void startStoring(final Context context,
                                  final Storable storable) {
    Intent intent = new Intent(context, StorableService.class);
    intent.setAction(ACTION_INSERT_OR_UPDATE);
    intent.putExtra(Constants.EXTRA_STORABLE, storable);
    context.startService(intent);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    if (intent != null) {
      final String action = intent.getAction();
      if (ACTION_INSERT_OR_UPDATE.equals(action)) {
        final Storable storable = (Storable) intent.getSerializableExtra(
            Constants.EXTRA_STORABLE);
        store(storable);
      }
    }
  }

  private void store(final Storable storable) {
    storable.setContext(this);
    storable.beforeStoringToDb();
    storable.storeToDb();
  }
}
