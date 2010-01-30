/*
 * Copyright (c) 2010 Emergent.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.emergent.bzr4j.core.xmloutput;

import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.utils.IOUtil;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Set;

/**
 * @author Patrick Woodworth
 */
class XmlVersionParser {

  static final Set<String> KNOWN_TEXT_ELEM_KEYS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
      "bazaar.version",
      "bazaar.bzrlib",
      "bazaar.configuration",
      "bazaar.log_file",
      "bazaar.copyright",
      "python.dll",
      "python.version",
      "python.standard_library"
  )));

  private final static String VERSION = "version";

  private LinkedList<String> m_elemNameDeque = new LinkedList<String>();

  public Properties parse(Reader xml) throws BazaarException {
    XMLStreamReader parser = null;
    try {
      Properties retval = new Properties();
      parser = XmlOutputUtil.getXMLInputFactory().createXMLStreamReader(xml);
      parser.nextTag();
      parser.require(XMLStreamConstants.START_ELEMENT, null, VERSION);
      while (parser.hasNext()) {
        int eventType = parser.next();
        switch (eventType) {
          case XMLStreamConstants.START_ELEMENT:
            String localName = parser.getLocalName();
            m_elemNameDeque.addLast(localName);
            String elemNameKey = calcKeyFromStack();
            if (!KNOWN_TEXT_ELEM_KEYS.contains(elemNameKey)) {
              break;
            }
            String elemText = parser.getElementText();
            retval.setProperty(elemNameKey, elemText);
            // intentional fall-through
          case XMLStreamConstants.END_ELEMENT:
            if (m_elemNameDeque.size() > 0) {
              String poppedName = m_elemNameDeque.removeLast();
            }
//            else
//              assert VERSION.equals(parser.getLocalName());
            break;
        }
      }
      return retval;
    } catch (XMLStreamException e) {
      throw new BazaarException(e);
    } finally {
      IOUtil.closeQuietly(parser);
    }
  }

  private String calcKeyFromStack() {
    StringBuilder retval = new StringBuilder();
    for (ListIterator<String> iter = m_elemNameDeque.listIterator(0); iter.hasNext();) {
      if (retval.length() > 0)
        retval.append('.');
      retval.append(iter.next());
    }
    return retval.toString();
  }
}
