package com.infmme.readilyapp.xmlparser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Stack;

import static com.infmme.readilyapp.xmlparser.XMLEventType.TAG_SINGLE;
import static com.infmme.readilyapp.xmlparser.XMLEventType.TAG_START;

/**
 * created on 8/26/14 by infm. Enjoy ;)
 */
public class XMLParser {
  private InputStreamReader isr;
  private XMLEvent currentEvent;
  private Stack<XMLEvent> eventStack = new Stack<XMLEvent>();

  private int currentInt = -1;
  private int nextInt = -1;

  private long position = 0;

  public void setInput(InputStream is, String encoding) {
    try {
      isr = new InputStreamReader(is, encoding);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  public void setInput(InputStream is) {
    setInput(is, "UTF-8");
  }

  public long getPosition() {
    return position;
  }

  public XMLEvent next() throws IOException {
    currentEvent = null;
    processEvent();
    return currentEvent;
  }

  private void readNext() throws IOException {
    currentInt = nextInt;
    nextInt = isr.read();
    position++;
  }

  private void processEvent() throws IOException {
    XMLEventType type;
    if (currentInt == -1) {
      currentInt = isr.read();
      nextInt = isr.read();
      position += 2;
    }
    if (Character.isWhitespace(nextInt))
      readNext();
    while (Character.isWhitespace(currentInt))
      readNext();
    if (currentInt == '<') {
      if (nextInt == '/') {
        currentEvent = new XMLEvent(type = XMLEventType.TAG_CLOSE);
        readNext();
      } else if (nextInt == '?') {
        currentEvent = new XMLEvent(type = XMLEventType.DOCUMENT_START);
        readNext();
      } else if (nextInt == '!') {
        currentEvent = new XMLEvent(type = XMLEventType.TAG_COMMENT);
      } else {
        currentEvent = new XMLEvent(type = XMLEventType.TAG);
      }
    } else if (currentInt != -1) { //consider random position as pointing to
      // content of text;
      currentEvent = new XMLEvent(type = XMLEventType.CONTENT);
    } else {
      currentEvent = new XMLEvent(type = XMLEventType.DOCUMENT_CLOSE);
      currentEvent.setStartPosition(position);
      currentEvent.setEndPosition(position);
      return;
    }
    currentEvent.setStartPosition(position);
    do {
      readNext();
      updateType(type, currentInt, nextInt);
      type = currentEvent.getType();
      switch (type) {
        case DOCUMENT_START:
        case TAG:
        case TAG_START:
        case TAG_CLOSE:
        case TAG_SINGLE:
        case TAG_COMMENT:
          currentEvent.appendTagName((char) currentInt);
          break;
        case CONTENT:
          currentEvent.appendContent((char) currentInt);
          break;
        case EMPTINESS:
          break;
        default:
          throw new IllegalArgumentException(
              "Illegal type in iterative block.");
      }
    } while (!breakClause(type, currentInt, nextInt));

    switch (type) {
      case CONTENT:
        if (currentEvent.getContent().length() > 1) {
          readNext();
          if (!eventStack.isEmpty())
            currentEvent.setContentType(eventStack.lastElement().getTagName());
        } else {
          currentEvent = null;
          processEvent();
        }
        break;
      case TAG:
        currentEvent.clarifyTagType(TAG_START);
        eventStack.push(currentEvent);
        readNext();
        break;
      case TAG_CLOSE:
        if (!eventStack.empty() && eventStack.lastElement()
                                             .getTagName()
                                             .equals(currentEvent.getTagName()))
          eventStack.pop();
        readNext();
        readNext();
        break;
      case TAG_START:
        eventStack.push(currentEvent);
        readNext();
        readNext();
        break;
      case DOCUMENT_START:
        currentEvent.cutLastTagChar();
        eventStack.push(currentEvent);
        readNext();
        readNext();
        break;
    }

    currentEvent.setEndPosition(position);
  }

  private boolean breakClause(XMLEventType type, int currentInt, int nextInt) {
    switch (type) {
      case CONTENT:
      case EMPTINESS:
        return currentInt == '<' || nextInt == '<';
      case TAG:
      case TAG_COMMENT:
      case TAG_START:
      case TAG_CLOSE:
        return nextInt == '>';
      case TAG_SINGLE:
        return currentInt == '/' && nextInt == '>';
      case DOCUMENT_START:
        return currentInt == '?' && nextInt == '>';
      case DOCUMENT_CLOSE:
        return nextInt == '>';
      default:
        throw new IllegalArgumentException("Type doesn't exist");
    }
  }

  private void updateType(XMLEventType type, int currentInt, int nextInt) {
    switch (type) {
      case TAG:
        if (currentInt == '/' && nextInt == '>')
          currentEvent.clarifyTagType(TAG_SINGLE);
        break;
    }
  }
}
