package com.infmme.readilyapp.xmlparser;

import android.util.Pair;

/**
 * created on 8/26/14 by infm. Enjoy ;)
 */
public class XMLEvent {
  private Pair<Long, Long> domain;
  private XMLEventType type;
  private XMLEventType closeType;

  private String content;
  private String contentType;
  private String tagName;

  private boolean tagNameReady;

  public XMLEvent(XMLEventType type) {
    content = "";
    tagName = "";
    domain = new Pair<>(-1L, -1L);
    this.type = type;
    generateCloseType();
  }

  public Pair<Long, Long> getDomain() {
    return domain;
  }

  public void setEndPosition(long endPosition) {
    domain = new Pair<>(domain.first, endPosition);
  }

  public void setStartPosition(long startPosition) {
    domain = new Pair<>(startPosition, domain.first);
  }

  public XMLEventType getType() {
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

  public void clarifyTagType(XMLEventType tagType) {
    if (type == XMLEventType.TAG) {
      type = tagType;
      generateCloseType();
    }
  }

  public void appendContent(char c) {
    content += c;
  }

  public void appendTagName(char c) {
    if (!tagNameReady)
      tagNameReady = Character.isWhitespace(c);
    if (!tagNameReady)
      tagName += c;
  }

  public void cutLastTagChar() {
    tagName = tagName.substring(0, tagName.length() - 1);
  }

  public boolean enteringSection() {
    return getType() == XMLEventType.TAG_START &&
        getTagName() != null &&
        getTagName().equals(FB2Tags.SECTION);
  }

  public boolean exitingSection() {
    return getType() == XMLEventType.TAG_CLOSE &&
        getTagName() != null &&
        getTagName().equals(FB2Tags.SECTION);
  }

  public boolean enteringTitle() {
    return getType() == XMLEventType.TAG_START &&
        getTagName() != null &&
        getTagName().equals(FB2Tags.TITLE);
  }

  public boolean exitingTitle() {
    return getType() == XMLEventType.TAG_CLOSE &&
        getTagName() != null &&
        getTagName().equals(FB2Tags.TITLE);
  }

  @Override
  public String toString() {
    return "domain: from " + domain.first + " to " + domain.second +
        "; type: " + type.toString() +
        "; close type: " + closeType.toString() +
        "; content: " + content +
        "; tag name: " + tagName;
  }

  private void generateCloseType() throws IllegalStateException {
    switch (type) {
      case DOCUMENT_START:
        closeType = XMLEventType.DOCUMENT_CLOSE;
        break;
      case TAG_START:
        closeType = XMLEventType.TAG_CLOSE;
        break;
      case CONTENT:
        closeType = XMLEventType.TAG;
        break;
      case EMPTINESS:
        closeType = XMLEventType.TAG_START;
        break;
      case TAG:
      case TAG_SINGLE:
      case TAG_COMMENT:
      case TAG_CLOSE:
      case DOCUMENT_CLOSE:
        closeType = type;
        break;
      default:
        throw new IllegalStateException("Illegal opening type");
    }
  }
}
