/**
 *
 */
package org.emergent.bzr4j.core.xmloutput;

import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.IBazaarLogMessage;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Guillermo Gonzalez <guillo.gonzo@gmail.com>
 *
 */
class XmlMissingParser extends XmlAbstractParser {

  private static final String OTHER = "OTHER";

  private static final String MINE = "MINE";

  private final static String MISSING = "missing";

  private final static String LAST_LOCATION = "last_location";

  private final static String MISSING_REVISIONS = "missing_revisions";

  private final static String EXTRA_REVISIONS = "extra_revisions";

  public Map<String, List<IBazaarLogMessage>> parse(String xml) throws BazaarException {
    final Map<String, List<IBazaarLogMessage>> missingOutput = new HashMap<String, List<IBazaarLogMessage>>(2);
    try {
      XMLInputFactory factory = XMLInputFactory.newInstance();
      parser = factory.createXMLStreamReader(new StringReader(xml));
      parser.nextTag();
      parser.require(XMLStreamConstants.START_ELEMENT, null, MISSING);
      parser.nextTag();
      parser.require(XMLStreamConstants.START_ELEMENT, null, LAST_LOCATION);
      parser.getElementText();// last location
      parser.nextTag();
      try {
        parser.require(XMLStreamConstants.START_ELEMENT, null, EXTRA_REVISIONS);
        missingOutput.put(MINE, parseLogs(EXTRA_REVISIONS));
      }
      catch (XMLStreamException e) {
        // do nothing, we don't have any new revisions
      }
      parser.nextTag(); // end_tag <logs>
      parser.nextTag();
      try {
        parser.require(XMLStreamConstants.START_ELEMENT, null, MISSING_REVISIONS);
        missingOutput.put(OTHER, parseLogs(MISSING_REVISIONS));
      }
      catch (XMLStreamException e) {
        // do nothing, the other branch don't have any new revisions
      }

    }
    catch (XMLStreamException e) {
      throw new BazaarException(e);
    }
    return missingOutput;
  }

  private List<IBazaarLogMessage> parseLogs(final String section) throws BazaarException {
    // more efficient to reference a stack variable(within a method) instead of a class variable. everytime you do an add etc.
    final List<IBazaarLogMessage> logs = new ArrayList<IBazaarLogMessage>();
    final XmlLogParser logParser = new XmlLogParser();
    logParser.parser = parser;
    try {
      int eventType = parser.nextTag();
      // iterate over all tags (actually only care about first level <log/> tags)
      while (eventType != XMLStreamConstants.END_ELEMENT && !section.equals(parser.getLocalName())) {
        if (eventType == XMLStreamConstants.START_ELEMENT && XmlLogParser.LOG.equals(parser.getLocalName())) {
          IBazaarLogMessage log = logParser.parseLog();
          if (log != null) {
            logs.add(log);
          }
        }
        eventType = parser.next();
      }
    }
    catch (XMLStreamException e) {
      throw new BazaarException(e);
    }
    catch (IOException e) {
      throw new BazaarException(e);
    }
    return logs;
  }
}
