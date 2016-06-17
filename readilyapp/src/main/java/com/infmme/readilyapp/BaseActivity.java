package com.infmme.readilyapp;

import android.support.v7.app.AppCompatActivity;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * created on 7/15/14 by infm. Enjoy ;)
 */
public abstract class BaseActivity extends AppCompatActivity {
  private CompositeSubscription mCompositeSubscription;

  abstract protected void findViews();
  // TODO: Add it
  // abstract protected void setupViews();

  protected void addSubscription(Subscription s) {
    if (mCompositeSubscription == null) {
      mCompositeSubscription = new CompositeSubscription();
    }
    mCompositeSubscription.add(s);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mCompositeSubscription != null &&
        mCompositeSubscription.hasSubscriptions()) {
      mCompositeSubscription.unsubscribe();;
    }
  }
}
