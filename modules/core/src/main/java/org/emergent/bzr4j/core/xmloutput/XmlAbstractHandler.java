/*
 * Copyright (c) 2010 Patrick Woodworth
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

import org.emergent.bzr4j.core.debug.DebugLogger;
import org.emergent.bzr4j.core.debug.DebugManager;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import static org.emergent.bzr4j.core.xmloutput.XmlAbstractHandler.ParseMode.*;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * @author Patrick Woodworth
 */
public class XmlAbstractHandler extends DefaultHandler {

  protected final static DebugLogger LOG = DebugManager.getLogger(XmlAbstractHandler.class.getName());

  protected final LinkedList<StackEntry> m_elStack = new LinkedList<StackEntry>();

  public XmlAbstractHandler() {
    super();
  }

//  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
//    pushSimpleElement(qName, attributes);
//  }

  public void characters(char[] ch, int start, int length) throws SAXException {
    SimpleElement simpleEl = m_elStack.getLast().m_element;
    simpleEl.m_text.append(ch, start, length);
  }

//  public void endElement(String uri, String localName, String qName) throws SAXException {
//    SimpleElement simpleEl = popSimpleElement();
//    assertThat(simpleEl.m_name.equals(qName));
//  }

  @Override
  public void warning(SAXParseException e) throws SAXException {
    LOG.debug(e, "sax warning");
  }

  @Override
  public void error(SAXParseException e) throws SAXException {
    LOG.warn(e, "sax error");
  }

  @Override
  public void fatalError(SAXParseException e) throws SAXException {
    LOG.error(e, "sax fatal");
  }

  protected SimpleElement pushSimpleElement(String name, Attributes attributes) {
    return pushStack(UNKNOWN_MODE, name, attributes).m_element;
  }

  protected StackEntry pushStack(ParseMode parseMode, String name, Attributes attributes) {
    return pushStack(parseMode, new SimpleElement(name, attributes));
  }

  protected StackEntry pushStack(ParseMode parseMode, SimpleElement simpleEl) {
    StackEntry stackEntry = new StackEntry(parseMode, simpleEl);
    m_elStack.addLast(stackEntry);
    return stackEntry;
  }

  protected SimpleElement peekSimpleElement() {
    return m_elStack.size() == 0 ? null : m_elStack.getLast().m_element;
  }

  protected StackEntry peekStack() {
    return m_elStack.size() == 0 ? null : m_elStack.getLast();
  }

  protected StackEntry popSimpleElement() {
    return m_elStack.removeLast();
  }

  protected int getStackSize() {
    return m_elStack.size();
  }

  protected String calcKeyFromStack() {
    StringBuilder retval = new StringBuilder();
    for (ListIterator<StackEntry> iter = m_elStack.listIterator(0); iter.hasNext();) {
      if (retval.length() > 0)
        retval.append('.');
      retval.append(iter.next().m_element.m_name);
    }
    return retval.toString();
  }

  protected static void assertThat(boolean bool) throws SAXException {
    assertThat(bool, "assert failed");
  }

  protected static void assertThat(boolean bool, String msg) throws SAXException {
    if (!bool)
      throw new SAXException(msg);
  }

  public static class StackEntry {
    public final SimpleElement m_element;

    public final ParseMode m_mode;

    public StackEntry(ParseMode parseMode, SimpleElement element) {
      m_element = element;
      m_mode = parseMode;
    }
  }

  enum ParseMode {
    UNKNOWN_MODE,
    UNSTARTED_MODE,
    PLUGINS_MODE,
    PLUGIN_MODE,
    PLUGIN_CHILD_MODE,
    STATUS_MODE,
    STATUS_GROUP_MODE,
    STATUS_GROUP_ENTRY_MODE,
    STATUS_PENDING_MERGES_MODE,
    LOGS_MODE,
    LOG_MODE,
    LOG_ENTRY_MODE,
    LIST_MODE,
    ITEM_MODE,
    ITEM_CHILD_MODE,
    ANNOTATION_MODE,
    ANNOTATION_ENTRY_MODE
  }
}
