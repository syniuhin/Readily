package com.infmme.readilyapp.navigation;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import com.daimajia.androidanimations.library.BuildConfig;
import com.infmme.readilyapp.R;
import com.infmme.readilyapp.readable.Readable;
import com.infmme.readilyapp.readable.interfaces.Reading;
import com.infmme.readilyapp.readable.structure.AbstractTocReference;
import com.infmme.readilyapp.readable.structure.fb2.FB2Part;
import com.infmme.readilyapp.reader.TextParser;
import com.infmme.readilyapp.settings.SettingsBundle;
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
    OnFabClickListener {

  private AbstractTocReference mItemReference;
  private String mFilePath = null;

  private TextView mTitleTextView;
  private TextView mBodyTextView;

  private boolean mTwoPane = false;

  private OnChooseListener mCallback;

  private TextParser mTextParser;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public BookPartDetailFragment() {
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      if (context instanceof BookPartDetailActivity) {
        mCallback = (BookPartDetailActivity) context;
      } else {
        mCallback = (BookPartListActivity) context;
      }
    } catch (ClassCastException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle bundle = getArguments();
    if (bundle != null) {
      if (bundle.containsKey(Constants.EXTRA_TOC_REFERENCE)) {
        mItemReference = (AbstractTocReference) bundle.getSerializable(
            Constants.EXTRA_TOC_REFERENCE);

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout)
            activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
          appBarLayout.setTitle(mItemReference.getTitle());
        }
      }
      if (bundle.containsKey(Constants.EXTRA_PATH)) {
        mFilePath = bundle.getString(Constants.EXTRA_PATH);
      }
      mTwoPane = bundle.getBoolean(Constants.EXTRA_TWO_PANE, false);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    final View rootView = inflater.inflate(R.layout.bookpart_detail, container,
                                           false);
    mTitleTextView = (TextView) rootView.findViewById(
        R.id.bookpart_detail_title);
    if (mTwoPane) {
      mTitleTextView.setVisibility(View.VISIBLE);
    } else {
      mTitleTextView.setVisibility(View.GONE);
    }
    mBodyTextView = (TextView) rootView.findViewById(R.id.bookpart_detail_body);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      mBodyTextView.setCustomSelectionActionModeCallback(new StartFromHereCallback());
    }

    if (mItemReference != null) {
      Observable<ProcessedItem> o = Observable.create(
          new Observable.OnSubscribe<ProcessedItem>() {
            @Override
            public void call(Subscriber<? super ProcessedItem> subscriber) {
              try {
                // Loads and caches preview
                Reading r = new Readable();
                if (mFilePath != null && mItemReference instanceof FB2Part) {
                  ((FB2Part) mItemReference).setPath(mFilePath);
                }
                r.setText(mItemReference.getPreview());
                mTextParser = TextParser.newInstance(r, new SettingsBundle(
                    PreferenceManager.getDefaultSharedPreferences(
                        getActivity())).getDelayCoefficients());
                mTextParser.process();
                subscriber.onNext(
                    new ProcessedItem(mItemReference.getTitle(), r.getText()));
                subscriber.onCompleted();
              } catch (IOException e) {
                subscriber.onError(e);
              }
            }
          });
      o.subscribeOn(Schedulers.newThread())
       .observeOn(AndroidSchedulers.mainThread())
       .subscribe(new Action1<ProcessedItem>() {
         @Override
         public void call(ProcessedItem item) {
           if (mTwoPane) {
             mTitleTextView.setText(item.title);
           }
           mBodyTextView.setText(item.text);
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
  public void onClick() {
    int selectionStart;
    if (mBodyTextView.hasSelection()) {
      selectionStart = mBodyTextView.getSelectionStart();
    } else {
      selectionStart = 0;
    }
    startFromPosition(selectionStart);
  }

  private void startFromPosition(int selectionStart) {
    String text = mBodyTextView.getText().toString()
                               .substring(0, selectionStart + 1);
    int spacesCount = text.length() - text.replaceAll(" ", "").length();
    if (BuildConfig.DEBUG) {
      Log.d(this.getClass().getName(),
            String.format("Res id: %s, word position: %d",
                          mItemReference.getId(), spacesCount));
    }
    mCallback.chooseItem(mItemReference.getId(), spacesCount);
  }

  private class ProcessedItem {
    String title;
    String text;

    public ProcessedItem(String title, String text) {
      this.title = title;
      this.text = text;
    }
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private class StartFromHereCallback implements ActionMode.Callback {
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
      MenuInflater inflater = mode.getMenuInflater();
      inflater.inflate(R.menu.bookpart_detail_text, menu);
      menu.removeItem(android.R.id.selectAll);
      return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
      return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
      switch (item.getItemId()) {
        case R.id.bookpart_detail_start_from_here:
          int selectionStart = mBodyTextView.getSelectionStart();
          startFromPosition(selectionStart);
          return true;
      }
      return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
    }
  }
}
