package org.emergent.bzr4j.core.commandline.parser;

import org.emergent.bzr4j.core.BazaarItemInfo;
import org.emergent.bzr4j.core.BazaarItemKind;
import org.emergent.bzr4j.core.BazaarStatusType;
import org.emergent.bzr4j.core.IBazaarItemInfo;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

class XMLLsParser extends XmlAbstractParser {

  private final static String LIST = "list";

  private final static String KIND = "kind";

  private final static String PATH = "path";

  private final static String ID = "id";

  private final static String ITEM = "item";

  private final static String STATUS_KIND = "status_kind";

  public static List<IBazaarItemInfo> parse(String xml) {
    try {
      XMLInputFactory factory = XMLInputFactory.newInstance();
      XMLStreamReader parser = factory.createXMLStreamReader(new StringReader(xml));
      return new XMLLsParser().parse(parser);
    }
    catch (XMLStreamException e) {
      LOG.error(e.getMessage(), e);
    }
    return new ArrayList<IBazaarItemInfo>(0);
  }

  private List<IBazaarItemInfo> parse(XMLStreamReader parser) {
    this.parser = parser;

    List<IBazaarItemInfo> result = new ArrayList<IBazaarItemInfo>();

    try {
      int eventType = parser.getEventType();

      while (eventType != XMLStreamConstants.END_DOCUMENT) {

        if (eventType == XMLStreamConstants.START_ELEMENT && !parser.getLocalName().equals(LIST)
            && parser.getLocalName().equals(ITEM)) {
          IBazaarItemInfo item = parseItem();
          if (item != null) {
            result.add(item);
          }
        }

        eventType = parser.next();
      }
    }
    catch (XMLStreamException e) {
      LOG.error(e.getMessage(), e);
    }
    catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  private IBazaarItemInfo parseItem() throws XMLStreamException, IOException {

    BazaarItemKind kind = null;
    String id = null;
    String path = null;
    BazaarStatusType type = null;

    int eventType = parser.getEventType();
    do {
      switch (eventType) {
        case XMLStreamConstants.START_ELEMENT:
          if (KIND.equals(parser.getLocalName())) {
            kind = BazaarItemKind.fromString(parser.getElementText());
          }
          if (PATH.equals(parser.getLocalName())) {
            path = parser.getElementText();
          }
          if (ID.equals(parser.getLocalName())) {
            id = parser.getElementText();
          }
          if (STATUS_KIND.equals(parser.getLocalName())) {
            type = BazaarStatusType.fromString(parser.getElementText());
          }
          break;
      }
    }
    while (!(XMLStreamConstants.END_ELEMENT == (eventType = parser.next()) && ITEM
        .equals(parser.getLocalName())));

    return new BazaarItemInfo(kind, id, path, type);

  }

}
