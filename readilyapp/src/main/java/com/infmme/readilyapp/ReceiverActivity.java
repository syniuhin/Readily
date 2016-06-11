package com.infmme.readilyapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.WindowManager;
import com.infmme.readilyapp.reader.ReaderFragment;
import com.infmme.readilyapp.readable.type.ReadableType;
import com.infmme.readilyapp.util.Constants;
import com.infmme.readilyapp.view.OnSwipeTouchListener;

public class ReceiverActivity extends BaseActivity
    implements ReaderFragment.ReaderFragmentCallback {

  private static final String READER_FRAGMENT_TAG =
      "ReaSq!d99erFra{{1239gm..1ent1923";
  private View contentView;

  public static void startReceiverActivity(Context context,
                                           ReadableType intentType,
                                           String intentPath) {
    Intent intent = new Intent(context, ReceiverActivity.class);

    Bundle bundle = new Bundle();
    bundle.putString(Constants.EXTRA_TYPE, intentType.name());
    bundle.putString(Constants.EXTRA_PATH, intentPath);
    intent.putExtras(bundle);

    context.startActivity(intent);
  }

  public static void startReceiverActivity(Context context, Bundle args) {
    Intent intent = new Intent(context, ReceiverActivity.class);
    intent.putExtras(args);
    context.startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_receiver);
    if (getSupportActionBar() != null)
      getSupportActionBar().hide();

    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    startReaderFragment(bundleReceivedData());
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
  }

  private void setOnSwipeListener(final ReaderFragment readerFragment) {
    if (contentView == null)
      contentView = findViewById(android.R.id.content);

    contentView.setOnTouchListener(new OnSwipeTouchListener(this) {
      @Override
      public void onSwipeTop() {
        readerFragment.onSwipeTop();
      }

      @Override
      public void onSwipeBottom() {
        readerFragment.onSwipeBottom();
      }
    });
  }

  private Bundle bundleReceivedData() {
    return getIntent().getExtras();
  }

  private void startReaderFragment(Bundle bundle) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    ReaderFragment readerFragment = (ReaderFragment) fragmentManager
        .findFragmentByTag(READER_FRAGMENT_TAG);
    if (readerFragment == null) {
      readerFragment = new ReaderFragment();
      if (bundle != null) {
        readerFragment.setArguments(bundle);
        fragmentManager.beginTransaction().
            add(R.id.fragment_container, readerFragment, READER_FRAGMENT_TAG).
                           addToBackStack(null).
                           commit();
        setOnSwipeListener(readerFragment);
      }
    }
  }

  @Override
  public void stop() {
    finish();
  }
}
