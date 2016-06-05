package com.infmme.readilyapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.infmme.readilyapp.util.Constants;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * A fragment representing a single BookPart detail screen.
 * This fragment is either contained in a {@link BookPartListActivity}
 * in two-pane mode (on tablets) or a {@link BookPartDetailActivity}
 * on handsets.
 */
public class BookPartDetailFragment extends Fragment implements
    BookPartDetailActivity.BookDetailOnFabClicked {

  private TOCReference mItemReference;

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
      mItemReference = (TOCReference) getArguments().getSerializable(
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
    View rootView = inflater.inflate(R.layout.bookpart_detail, container,
                                     false);
    mTextView = ((TextView) rootView.findViewById(R.id.bookpart_detail));
    if (mItemReference != null) {
      Resource resource = mItemReference.getResource();
      try {
        // Blocking. TODO: make it asynchronous.
        Document doc = Jsoup.parse(new String(resource.getData()));
        String parsed = doc.select("p").text();
        mTextView.setText(parsed);
      } catch (IOException e) {
        e.printStackTrace();
      }
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
                        mItemReference.getResourceId(), selectionStart));
  }
}
