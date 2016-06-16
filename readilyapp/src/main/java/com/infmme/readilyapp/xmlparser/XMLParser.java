package com.infmme.readilyapp.xmlparser;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Stack;

import static com.infmme.readilyapp.xmlparser.XMLEventType.*;

/**
 * created on 8/26/14 by infm. Enjoy ;)
 */
public class XMLParser {
  private PositionInputStreamReader isr;
  private XMLEvent currentEvent;
  private Stack<XMLEvent> eventStack = new Stack<XMLEvent>();

  private int currentInt = -1;
  private int nextInt = -1;

  public void setInput(InputStream is, String encoding) {
    try {
      isr = new PositionInputStreamReader(is, encoding);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  public void setInput(InputStream is) {
    setInput(is, "UTF-8");
  }

  public void close() throws IOException {
    if (isr != null) {
      isr.close();
    }
  }
  /**
   * Returns the position of InputStreamReader needed to speedup next reads
   * of file.
   */
  public long getPosition() {
    return isr.getPosition();
  }

  public XMLEvent next() throws IOException {
    currentEvent = null;
    processEvent();
    return currentEvent;
  }

  /**
   * Skips to a specified position in InputStreamReader.
   *
   * @param bytes Position to skip to.
   * @return Result of InputStreamReader skip.
   */
  public long skip(long bytes) throws IOException {
    return isr.skip(bytes);
  }

  private void readNext() throws IOException {
    currentInt = nextInt;
    nextInt = isr.read();
  }

  private void processEvent() throws IOException {
    XMLEventType type;
    // Checks if currentInt is not initialized.
    if (currentInt == -1) {
      currentInt = isr.read();
      nextInt = isr.read();
    }
    // ???
    if (Character.isWhitespace(nextInt))
      readNext();
    while (Character.isWhitespace(currentInt))
      readNext();

    final long startPosition = isr.getPosition();
    // Checks if we've entered a tag.
    if (currentInt == '<') {
      if (nextInt == '/') {
        currentEvent = new XMLEvent(type = TAG_CLOSE);
      } else if (nextInt == '?') {
        currentEvent = new XMLEvent(type = DOCUMENT_START);
      } else if (nextInt == '!') {
        currentEvent = new XMLEvent(type = XMLEventType.TAG_COMMENT);
      } else {
        currentEvent = new XMLEvent(type = XMLEventType.TAG);
      }
    } else if (currentInt != -1) {
      // Consider random position as pointing to content of text.
      currentEvent = new XMLEvent(type = XMLEventType.CONTENT);
    } else {
      currentEvent = new XMLEvent(type = XMLEventType.DOCUMENT_CLOSE);
      currentEvent.setStartPosition(startPosition);
      currentEvent.setEndPosition(isr.getPosition());
      return;
    }
    currentEvent.setStartPosition(startPosition);

    // Reads one character to start from tag contents.
    switch (type) {
      case DOCUMENT_START:
      case TAG_CLOSE:
        readNext();
      case TAG:
      case TAG_START:
      case TAG_SINGLE:
      case TAG_COMMENT:
        readNext();
        updateType(type, currentInt, nextInt);
        type = currentEvent.getType();
        break;
    }

    do {
      switch (type) {
        case DOCUMENT_START:
        case TAG:
        case TAG_START:
        case TAG_CLOSE:
        case TAG_SINGLE:
        case TAG_COMMENT:
          currentEvent.appendTagContents((char) currentInt);
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
      readNext();
      updateType(type, currentInt, nextInt);
      type = currentEvent.getType();
    } while (!breakClause(type, currentInt, nextInt));

    switch (type) {
      case CONTENT:
        // Checks if we met content which we should take into account.
        if (currentEvent.getContent().length() > 1) {
          if (!eventStack.isEmpty() &&
              (currentEvent.getContentType() == null ||
                  TextUtils.isEmpty(currentEvent.getContentType()))) {
            currentEvent.setContentType(eventStack.lastElement().getTagName());
          }
        } else {
          currentEvent = null;
          processEvent();
        }
        break;
      case TAG:
        // Since we didn't meet other requirements
        currentEvent.clarifyTagType(TAG_START);
        currentEvent.finishAppendingTagContents();
        eventStack.push(currentEvent);
        readNext();
        break;
      case TAG_CLOSE:
        currentEvent.finishAppendingTagContents();
        if (!eventStack.empty() && eventStack.lastElement()
                                             .getTagName()
                                             .equals(currentEvent.getTagName()))
          eventStack.pop();
        readNext();
        break;
      case TAG_START:
        currentEvent.finishAppendingTagContents();
        eventStack.push(currentEvent);
        readNext();
        break;
      case DOCUMENT_START:
      case TAG_SINGLE:
        currentEvent.finishAppendingTagContents();
        eventStack.push(currentEvent);
        readNext();
        readNext();
        break;
    }

    currentEvent.setEndPosition(isr.getPosition());
  }

  private boolean breakClause(XMLEventType type, int currentInt, int nextInt) {
    switch (type) {
      case CONTENT:
      case EMPTINESS:
        return currentInt == '<';
      case TAG:
      case TAG_COMMENT:
      case TAG_START:
      case TAG_CLOSE:
        return currentInt == '>';
      case TAG_SINGLE:
        return currentInt == '/' && nextInt == '>';
      case DOCUMENT_START:
        return currentInt == '?' && nextInt == '>';
      // ???
      case DOCUMENT_CLOSE:
        return nextInt == '>';
      default:
        throw new IllegalArgumentException("Type doesn't exist");
    }
  }

  /**
   * Updates type which couldn't be clarified earlier.
   *
   * @param type       Current type (general one) to clarify.
   * @param currentInt Current char.
   * @param nextInt    Next char.
   */
  private void updateType(XMLEventType type, int currentInt, int nextInt) {
    switch (type) {
      case TAG:
        if (currentInt == '/' && nextInt == '>')
          currentEvent.clarifyTagType(TAG_SINGLE);
        break;
    }
  }
}
