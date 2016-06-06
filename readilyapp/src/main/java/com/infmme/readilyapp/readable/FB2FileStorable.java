package com.infmme.readilyapp.readable;

import android.content.Context;
import android.text.TextUtils;
import com.infmme.readilyapp.readable.fb2.FB2Part;
import com.infmme.readilyapp.xmlparser.FB2Tags;
import com.infmme.readilyapp.xmlparser.XMLEvent;
import com.infmme.readilyapp.xmlparser.XMLEventType;
import com.infmme.readilyapp.xmlparser.XMLParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * created on 7/20/14 by infm. Enjoy ;)
 */
public class FB2FileStorable extends FileStorable {

  private XMLParser parser;

  public FB2FileStorable(String path) {
    type = TYPE_FB2;
    this.path = path;
  }

  public FB2FileStorable(FB2FileStorable that) {
    super(that);
    type = TYPE_FB2;
    parser = that.getParser();
  }

  public XMLParser getParser() {
    return parser;
  }

  @Override
  public void process(Context context) {
    path = FileStorable.takePath(context, path);
    if (path == null) {
      return;
    }
    try {
      File file = new File(path);
      FileInputStream encodingHelper = new FileInputStream(file);
      encoding = guessCharset(encodingHelper);
      encodingHelper.close();

      fileInputStream = new FileInputStream(file);
      fileSize = file.length();
      createRowData(context);
      if (bytePosition > 0)
        fileInputStream.skip(bytePosition);

      parser = new XMLParser();
      parser.setInput(fileInputStream, encoding);
      processed = true;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void readData() {
    setText("");
    try {
      if (parser == null) { return; }
      XMLEvent event = parser.next();
      XMLEventType eventType = event.getType();
      boolean needTitle = TextUtils.isEmpty(title);

      while (eventType != XMLEventType.DOCUMENT_CLOSE && text.length() <
          BUFFER_SIZE) {
        if (eventType == XMLEventType.CONTENT) {
          String contentType = event.getContentType();
          if (!TextUtils.isEmpty(contentType)) {
            if (needTitle && contentType.equals("book-title"))
              title = event.getContent();
            if (contentType.equals("p"))
              text.append(event.getContent());
          } else { //TODO: handle this situation carefully
            text.append(event.getContent());
          }
          text.append(" ");
        }
        event = parser.next();
        eventType = event.getType();
      }
      inputDataLength = event.getDomain().second;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Readable getNext() {
    return prepareNext(new FB2FileStorable(this));
  }

  @Override
  protected void makeHeader() {
    if (TextUtils.isEmpty(title)) {
      super.makeHeader();
    } else {
      header = title;
    }
  }

  public List<FB2Part> getTableOfContents() throws IOException {
    if (!processed) {
      return null;
    }
    List<FB2Part> toc = new ArrayList<>();
    Stack<FB2Part> stack = new Stack<>();
    FB2Part currentPart = null;

    XMLEvent event = parser.next();
    XMLEventType eventType = event.getType();
    boolean insideTitle = false;


    while (eventType != XMLEventType.DOCUMENT_CLOSE) {
      if (event.enteringSection()) {
        if (currentPart == null) {
          currentPart = new FB2Part();
        } else {
          FB2Part childPart = new FB2Part();
          currentPart.addChild(childPart);
          stack.add(currentPart);
          currentPart = childPart;
        }
      }
      if (event.exitingSection()) {
        if (currentPart == null) {
          throw new IllegalStateException("Can't exit non-existing part");
        }
        currentPart.setId("section" + String.valueOf(parser.getPosition()));
        if (stack.isEmpty()) {
          toc.add(currentPart);
          currentPart = null;
        } else {
          currentPart = stack.pop();
        }
      }
      if (event.enteringTitle()) {
        insideTitle = true;
      }
      if (event.exitingTitle()) {
        insideTitle = false;
      }

      if (eventType == XMLEventType.CONTENT) {
        if (insideTitle && currentPart != null) {
          currentPart.setTitle(
              currentPart.getTitle() + " " + event.getContent());
        } else if (currentPart != null) {
          String contentType = event.getContentType();
          if (!TextUtils.isEmpty(contentType) && contentType.equals(
              FB2Tags.PLAIN_TEXT)) {
            currentPart.appendText(event.getContent());
            currentPart.appendText(" ");
          }
        }
      }
      event = parser.next();
      eventType = event.getType();
    }
    return toc;
  }
}
