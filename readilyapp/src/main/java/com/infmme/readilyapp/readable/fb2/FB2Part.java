package com.infmme.readilyapp.readable.fb2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with love, by infm dated on 6/5/16.
 */

public class FB2Part implements Serializable {
  private String title = "";
  private StringBuilder text = new StringBuilder();
  private double percentile;

  private List<FB2Part> children = new ArrayList<>();

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public StringBuilder getText() {
    return text;
  }

  public void setText(StringBuilder text) {
    this.text = text;
  }

  public void appendText(String part) {
    this.text.append(" ").append(part);
  }

  public double getPercentile() {
    return percentile;
  }

  public void setPercentile(double percentile) {
    this.percentile = percentile;
  }

  public List<FB2Part> getChildren() {
    return children;
  }

  public void addChild(FB2Part child) {
    children.add(child);
  }
}
