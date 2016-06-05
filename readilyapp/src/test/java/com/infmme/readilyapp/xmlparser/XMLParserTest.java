package com.infmme.readilyapp.xmlparser;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

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

  @Before
  public void setUp() throws Exception {

  }

  @Test
  public void testSmallXmlEventFlow() throws Exception {
    XMLParser parser = new XMLParser();
    parser.setInput(new ByteArrayInputStream(SMALL_XML.getBytes()));

    final List<XMLEventType> expectedEvents = new ArrayList<>();
    expectedEvents.add(XMLEventType.DOCUMENT_START);
    expectedEvents.add(XMLEventType.TAG_START);
    expectedEvents.add(XMLEventType.TAG_COMMENT);
    expectedEvents.add(XMLEventType.TAG_START);
    expectedEvents.add(XMLEventType.CONTENT);
    expectedEvents.add(XMLEventType.TAG_CLOSE);
    expectedEvents.add(XMLEventType.TAG_START);
    expectedEvents.add(XMLEventType.CONTENT);
    expectedEvents.add(XMLEventType.TAG_CLOSE);
    expectedEvents.add(XMLEventType.TAG_START);
    expectedEvents.add(XMLEventType.CONTENT);
    expectedEvents.add(XMLEventType.TAG_CLOSE);
    expectedEvents.add(XMLEventType.TAG_START);
    expectedEvents.add(XMLEventType.CONTENT);
    expectedEvents.add(XMLEventType.TAG_CLOSE);
    expectedEvents.add(XMLEventType.TAG_CLOSE);
    expectedEvents.add(XMLEventType.DOCUMENT_CLOSE);

    XMLEvent event = parser.next();
    XMLEventType eventType = event.getType();

    for (XMLEventType expected: expectedEvents) {
      assertEquals(expected, eventType);
      event = parser.next();
      eventType = event.getType();
    }
  }
}