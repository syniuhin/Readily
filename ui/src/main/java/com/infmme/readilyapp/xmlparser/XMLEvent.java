package com.infmme.readilyapp.xmlparser;

import android.util.Pair;

/**
 * created on 8/26/14 by infm. Enjoy ;)
 */
public class XMLEvent {
  private Pair<Long, Long> domain;
  private int type;
  private int closeType;

  private String content;
  private String contentType;
  private String tagName;

  private boolean tagNameReady;

  public XMLEvent(int type) {
    content = "";
    tagName = "";
    domain = new Pair<Long, Long>(- 1L, - 1L);
    this.type = type;
    generateCloseType();
  }

  public Pair<Long, Long> getDomain() {
    return domain;
  }

  public void setEndPosition(long endPosition) {
    domain = new Pair<Long, Long>(domain.first, endPosition);
  }

  public void setStartPosition(long startPosition) {
    domain = new Pair<Long, Long>(startPosition, domain.first);
  }

  public int getType() {
    return type;
  }

  public String getContent() {
    return content;
  }

  public String getTagName() {
    return tagName;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public void clarifyTagType(int tagType) {
    if (type == XMLParser.TAG) {
      type = tagType;
      generateCloseType();
    }
  }

  public void appendContent(char c) {
    content += c;
  }

  public void appendTagName(char c) {
    if (! tagNameReady)
      tagNameReady = Character.isWhitespace(c);
    if (! tagNameReady)
      tagName += c;
  }

  public void cutLastTagChar() {
    tagName = tagName.substring(0, tagName.length() - 1);
  }

  @Override
  public String toString() {
    return "domain: from " + domain.first + " to " + domain.second +
        "; type: " + XMLParser.getTypeName(type) +
        "; close type: " + XMLParser.getTypeName(closeType) +
        "; content: " + content +
        "; tag name: " + tagName;
  }

  private void generateCloseType() {
    switch (type) {
      case XMLParser.DOCUMENT_START:
        closeType = XMLParser.DOCUMENT_CLOSE;
        break;
      case XMLParser.TAG_START:
        closeType = XMLParser.TAG_CLOSE;
        break;
      case XMLParser.CONTENT:
        closeType = XMLParser.TAG;
        break;
      case XMLParser.EMPTINESS:
        closeType = XMLParser.TAG_START;
        break;
      case XMLParser.TAG_SINGLE:
        closeType = type;
        break;
      default:
        closeType = - 1;
    }
  }
}
