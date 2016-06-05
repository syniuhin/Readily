package com.infmme.readilyapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
public class BookPartDetailFragment extends Fragment {
  /**
   * The fragment argument representing the item ID that this fragment
   * represents.
   */
  public static final String ARG_ITEM_ID = "item_id";

  /**
   * The dummy content this fragment is presenting.
   */
  private TOCReference mItemReference;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public BookPartDetailFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getArguments().containsKey("TocReference")) {
      // Load the dummy content specified by the fragment
      // arguments. In a real-world scenario, use a Loader
      // to load content from a content provider.
      mItemReference = (TOCReference) getArguments().getSerializable(
          "TocReference");

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

    // Show the dummy content as text in a TextView.
    if (mItemReference != null) {
      Resource resource = mItemReference.getResource();
      try {
        Document doc = Jsoup.parse(new String(resource.getData()));
        String parsed = doc.select("p").text();
        ((TextView) rootView.findViewById(R.id.bookpart_detail)).setText(
            parsed);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return rootView;
  }
}
