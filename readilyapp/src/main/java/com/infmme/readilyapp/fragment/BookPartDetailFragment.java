package com.infmme.readilyapp.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.infmme.readilyapp.BookPartDetailActivity;
import com.infmme.readilyapp.BookPartListActivity;
import com.infmme.readilyapp.R;
import com.infmme.readilyapp.readable.storable.AbstractTocReference;
import com.infmme.readilyapp.util.Constants;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.io.IOException;

/**
 * A fragment representing a single BookPart detail screen.
 * This fragment is either contained in a {@link BookPartListActivity}
 * in two-pane mode (on tablets) or a {@link BookPartDetailActivity}
 * on handsets.
 */
public class BookPartDetailFragment extends Fragment implements
    BookPartDetailActivity.BookDetailOnFabClicked {

  private AbstractTocReference mItemReference;

  private TextView mTextView;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public BookPartDetailFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getArguments().containsKey(Constants.EXTRA_TOC_REFERENCE)) {
      mItemReference = (AbstractTocReference) getArguments().getSerializable(
          Constants.EXTRA_TOC_REFERENCE);

      Activity activity = this.getActivity();
      CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout)
          activity.findViewById(R.id.toolbar_layout);
      if (appBarLayout != null) {
        appBarLayout.setTitle(mItemReference.getTitle());
      }
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    final View rootView = inflater.inflate(R.layout.bookpart_detail, container,
                                           false);
    mTextView = ((TextView) rootView.findViewById(R.id.bookpart_detail));
    if (mItemReference != null) {
      Observable<String> o = Observable.create(
          new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
              try {
                subscriber.onNext(mItemReference.getPreview());
                subscriber.onCompleted();
              } catch (IOException e) {
                subscriber.onError(e);
              }
            }
          });
      o.subscribeOn(Schedulers.newThread())
       .observeOn(AndroidSchedulers.mainThread())
       .subscribe(new Action1<String>() {
         @Override
         public void call(String s) {
           mTextView.setText(s);
         }
       }, new Action1<Throwable>() {
         @Override
         public void call(Throwable throwable) {
           throwable.printStackTrace();
           Snackbar.make(rootView, "Error occurred", Snackbar.LENGTH_SHORT)
                   .show();
         }
       });
    }

    return rootView;
  }

  @Override
  public void onFabClicked() {
    int selectionStart;
    if (mTextView.hasSelection()) {
      selectionStart = mTextView.getSelectionStart();
    } else {
      selectionStart = 0;
    }
    Log.d(this.getClass().getName(),
          String.format("Res id: %s, selection start: %d",
                        mItemReference.getId(), selectionStart));
  }
}
