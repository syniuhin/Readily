package com.infmme.readilyapp.xmlparser;

/**
 * Created with love, by infm dated on 6/5/16.
 */

public enum XMLEventType {
  DOCUMENT_START, DOCUMENT_CLOSE, TAG, TAG_START, TAG_CLOSE, TAG_SINGLE,
  TAG_COMMENT, EMPTINESS, CONTENT;
}
