package com.infmme.readilyapp.xmlparser;

import android.support.annotation.NonNull;
import android.util.Pair;

import java.util.HashMap;

/**
 * created on 8/26/14 by infm. Enjoy ;)
 */
public class XMLEvent {
  private Pair<Long, Long> domain;
  private XMLEventType type;
  private XMLEventType closeType;

  private String content;
  private StringBuilder contentBuilder;

  private String contentType;

  private StringBuilder tagContents;
  private String tagName;
  private HashMap<String, String> tagAttributes;
  private boolean insideQuotes;

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

  public HashMap<String, String> getTagAttributes() {
    return tagAttributes;
  }

  public void clarifyTagType(XMLEventType tagType) {
    if (type == XMLEventType.TAG) {
      type = tagType;
      generateCloseType();
    }
  }

  public void appendContent(char c) {
    if (contentBuilder == null) {
      contentBuilder = new StringBuilder();
    }
    contentBuilder.append(c);
  }

  public void finishAppendingContent() {
    content = contentBuilder.toString();
    contentBuilder = new StringBuilder();
  }

  public void appendTagContents(char c) {
    // Still appending to tag name?
    if (!tagNameReady) {
      tagNameReady = Character.isWhitespace(c);
      if (tagNameReady) {
        // Finishing appending to tag name.
        setTagName(tagContents.toString());
        tagContents = new StringBuilder();
      } else {
        if (tagContents == null) {
          tagContents = new StringBuilder();
        }
        tagContents.append(c);
      }
    } else {
      insideQuotes ^= c == '\"';
      // Checks if we've met a delimiter for attribute pieces.
      if (!insideQuotes && Character.isWhitespace(c) &&
          tagContents.length() > 0) {
        addTagAttribute(tagContents.toString());
        tagContents = new StringBuilder();
      } else {
        if (tagContents == null) {
          tagContents = new StringBuilder();
        }
        tagContents.append(c);
      }
    }
  }

  public void finishAppendingTagContents() {
    appendTagContents(' ');
  }

  private void setTagName(String tagName) {
    this.tagName = tagName;
  }

  private void addTagAttribute(String piece) {
    String[] kv = piece.split("=");
    if (kv.length == 2) {
      // Get rid of quote marks.
      String value;
      if (kv[1].contains("\"")) {
        value = kv[1].substring(1, kv[1].lastIndexOf("\""));
      } else {
        value = kv[1];
      }
      if (tagAttributes == null) {
        tagAttributes = new HashMap<>();
      }
      tagAttributes.put(kv[0], value);
    } else {
      throw new IllegalArgumentException(String.format(
          "Attribute piece %s is not actually a key-value having %d pieces",
          piece, kv.length));
    }
  }

  public void appendTagComment(char c) {
    if (tagContents == null) {
      tagContents = new StringBuilder();
    }
    tagContents.append(c);
  }

  public void finishAppendingTagComment() {
    content = tagContents.toString();
    tagContents = new StringBuilder();
  }

  public boolean enteringTag(final String tagName) {
    return getType() == XMLEventType.TAG_START &&
        getTagName() != null &&
        getTagName().equals(tagName);
  }

  public boolean exitingTag(final String tagName) {
    return getType() == XMLEventType.TAG_CLOSE &&
        getTagName() != null &&
        getTagName().equals(tagName);
  }

  public boolean isImageTag() {
    return getType() == XMLEventType.TAG_SINGLE &&
        getTagName() != null &&
        getTagName().equals(FB2Tags.IMAGE);
  }

  public boolean checkHref(@NonNull final String href) {
    if (tagAttributes != null && tagAttributes.containsKey("id") &&
        href.indexOf('#') == 0) {
      // Takes into account '#' in the beginning.
      return tagAttributes.get("id").equals(href.substring(1));
    }
    return false;
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
