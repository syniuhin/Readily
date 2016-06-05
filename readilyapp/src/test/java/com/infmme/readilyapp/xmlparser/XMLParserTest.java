package com.infmme.readilyapp.xmlparser;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 * Created with love, by infm dated on 6/5/16.
 */

public class XMLParserTest {

  private static final String SMALL_XML =
      "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
          "<resources>" +
          "<!-- Basic -->" +
          "<string name=\"app_name\">Readily</string>" +
          "<string name=\"title_activity_main\">Readily</string>" +
          "<string name=\"title_activity_receiver\">Add to " +
          "Readily</string>" +
          "<string name=\"title_activity_settings\">Settings</string>" +
          "</resources>";

  private static final List<XMLEventType> SMALL_XML_EXPECTED_EVENTS;
  private static final List<String> SMALL_XML_EXPECTED_TAGNAMES;
  private static final List<String> SMALL_XML_EXPECTED_CONTENT;

  static {
    SMALL_XML_EXPECTED_EVENTS = new ArrayList<>();
    SMALL_XML_EXPECTED_EVENTS.add(XMLEventType.DOCUMENT_START);
    SMALL_XML_EXPECTED_EVENTS.add(XMLEventType.TAG_START);
    SMALL_XML_EXPECTED_EVENTS.add(XMLEventType.TAG_COMMENT);
    SMALL_XML_EXPECTED_EVENTS.add(XMLEventType.TAG_START);
    SMALL_XML_EXPECTED_EVENTS.add(XMLEventType.CONTENT);
    SMALL_XML_EXPECTED_EVENTS.add(XMLEventType.TAG_CLOSE);
    SMALL_XML_EXPECTED_EVENTS.add(XMLEventType.TAG_START);
    SMALL_XML_EXPECTED_EVENTS.add(XMLEventType.CONTENT);
    SMALL_XML_EXPECTED_EVENTS.add(XMLEventType.TAG_CLOSE);
    SMALL_XML_EXPECTED_EVENTS.add(XMLEventType.TAG_START);
    SMALL_XML_EXPECTED_EVENTS.add(XMLEventType.CONTENT);
    SMALL_XML_EXPECTED_EVENTS.add(XMLEventType.TAG_CLOSE);
    SMALL_XML_EXPECTED_EVENTS.add(XMLEventType.TAG_START);
    SMALL_XML_EXPECTED_EVENTS.add(XMLEventType.CONTENT);
    SMALL_XML_EXPECTED_EVENTS.add(XMLEventType.TAG_CLOSE);
    SMALL_XML_EXPECTED_EVENTS.add(XMLEventType.TAG_CLOSE);
    SMALL_XML_EXPECTED_EVENTS.add(XMLEventType.DOCUMENT_CLOSE);

    SMALL_XML_EXPECTED_TAGNAMES = new ArrayList<>();
    SMALL_XML_EXPECTED_TAGNAMES.add("resources");
    SMALL_XML_EXPECTED_TAGNAMES.add("string");
    SMALL_XML_EXPECTED_TAGNAMES.add("string");
    SMALL_XML_EXPECTED_TAGNAMES.add("string");
    SMALL_XML_EXPECTED_TAGNAMES.add("string");
    SMALL_XML_EXPECTED_TAGNAMES.add("string");
    SMALL_XML_EXPECTED_TAGNAMES.add("string");
    SMALL_XML_EXPECTED_TAGNAMES.add("string");
    SMALL_XML_EXPECTED_TAGNAMES.add("string");
    SMALL_XML_EXPECTED_TAGNAMES.add("resources");

    SMALL_XML_EXPECTED_CONTENT = new ArrayList<>();
    SMALL_XML_EXPECTED_CONTENT.add("Readily");
    SMALL_XML_EXPECTED_CONTENT.add("Readily");
    SMALL_XML_EXPECTED_CONTENT.add("Add to Readily");
    SMALL_XML_EXPECTED_CONTENT.add("Settings");
  }

  @Before
  public void setUp() throws Exception {

  }

  @Test
  public void testSmallXmlEventFlow() throws Exception {
    XMLParser parser = new XMLParser();
    parser.setInput(new ByteArrayInputStream(SMALL_XML.getBytes()));

    XMLEvent event = parser.next();
    XMLEventType eventType = event.getType();

    for (XMLEventType expected : SMALL_XML_EXPECTED_EVENTS) {
      assertEquals(expected, eventType);
      event = parser.next();
      eventType = event.getType();
    }
  }

  @Test
  public void testSmallXmlTagNames() throws Exception {
    XMLParser parser = new XMLParser();
    parser.setInput(new ByteArrayInputStream(SMALL_XML.getBytes()));

    XMLEvent event;
    XMLEventType eventType;
    do {
      event = parser.next();
      eventType = event.getType();
    } while (eventType != XMLEventType.TAG &&
        eventType != XMLEventType.TAG_START &&
        eventType != XMLEventType.TAG_CLOSE &&
        eventType != XMLEventType.TAG_SINGLE &&
        eventType != XMLEventType.DOCUMENT_CLOSE);

    assertFalse(XMLEventType.DOCUMENT_CLOSE == eventType);

    for (String expected : SMALL_XML_EXPECTED_TAGNAMES) {
      assertEquals(expected, event.getTagName());
      do {
        event = parser.next();
        eventType = event.getType();
      } while (eventType != XMLEventType.TAG &&
          eventType != XMLEventType.TAG_START &&
          eventType != XMLEventType.TAG_CLOSE &&
          eventType != XMLEventType.TAG_SINGLE &&
          eventType != XMLEventType.DOCUMENT_CLOSE);
    }
  }


  @Test
  public void testSmallXmlContent() throws Exception {
    XMLParser parser = new XMLParser();
    parser.setInput(new ByteArrayInputStream(SMALL_XML.getBytes()));

    XMLEvent event;
    XMLEventType eventType;
    do {
      event = parser.next();
      eventType = event.getType();
    } while (eventType != XMLEventType.CONTENT &&
        eventType != XMLEventType.DOCUMENT_CLOSE);

    assertFalse(XMLEventType.DOCUMENT_CLOSE == eventType);

    for (String expected : SMALL_XML_EXPECTED_CONTENT) {
      assertEquals(expected, event.getContent());
      do {
        event = parser.next();
        eventType = event.getType();
      } while (eventType != XMLEventType.CONTENT &&
          eventType != XMLEventType.DOCUMENT_CLOSE);
    }
  }
}