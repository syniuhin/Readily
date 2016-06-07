package com.infmme.readilyapp.readable.epub;

import android.support.annotation.NonNull;
import com.infmme.readilyapp.readable.storable.AbstractTocReference;
import nl.siegmann.epublib.domain.TOCReference;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with love, by infm dated on 6/6/16.
 */

/**
 * Adapts TOCReference from epublib to AbstractTocReference.
 */
public class EpubPart implements AbstractTocReference, Serializable {

  private TOCReference mAdaptee;
  private ArrayList<EpubPart> mChildren = null;

  public static ArrayList<EpubPart> adaptList(@NonNull List<TOCReference> list) {
    ArrayList<EpubPart> result = new ArrayList<>();
    for (TOCReference tocReference : list) {
      result.add(new EpubPart(tocReference));
    }
    return result;
  }

  public EpubPart(TOCReference adaptee) {
    mAdaptee = adaptee;
  }

  @Override
  public String getId() {
    return mAdaptee.getResourceId();
  }

  @Override
  public String getTitle() {
    return mAdaptee.getTitle();
  }

  @Override
  public String getPreview() throws IOException {
    Document doc = Jsoup.parse(new String(mAdaptee.getResource().getData()));
    return doc.select("p").text();
  }

  @Override
  public double getPercentile() {
    return 0;
  }

  @Override
  public ArrayList<EpubPart> getChildren() {
    if (mChildren == null && mAdaptee.getChildren() != null) {
      mChildren = adaptList(mAdaptee.getChildren());
    }
    return mChildren;
  }
}
