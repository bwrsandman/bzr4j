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

import static org.emergent.bzr4j.core.xmloutput.XmlAbstractHandler.ParseMode.PLUGINS_MODE;
import static org.emergent.bzr4j.core.xmloutput.XmlAbstractHandler.ParseMode.PLUGIN_CHILD_MODE;
import static org.emergent.bzr4j.core.xmloutput.XmlAbstractHandler.ParseMode.PLUGIN_MODE;
import static org.emergent.bzr4j.core.xmloutput.XmlAbstractHandler.ParseMode.UNKNOWN_MODE;
import static org.emergent.bzr4j.core.xmloutput.XmlAbstractHandler.ParseMode.UNSTARTED_MODE;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Properties;

public abstract class XmlPluginHandler extends XmlAbstractHandler {

  public void handlePlugin(String name, String version, String path, String doc) {

  }

  private void handlePlugin(Properties props) {
    handlePlugin(
        props.getProperty("name"),
        props.getProperty("version"),
        props.getProperty("path"),
        props.getProperty("doc")
        );
  }

  private void handlePlugin(SimpleElement simpleEl) {
    Properties props = new Properties();
    for (SimpleElement childEl : simpleEl.m_children) {
      props.setProperty(childEl.m_name, childEl.m_text.toString());
    }
    handlePlugin(props);
  }

  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    StackEntry lastEntry = peekStack();
    ParseMode lastMode = lastEntry == null ? UNSTARTED_MODE : lastEntry.m_mode;
    SimpleElement thisElement = new SimpleElement(qName, attributes);
    ParseMode nextMode = UNKNOWN_MODE;
    switch (lastMode) {
      case UNSTARTED_MODE:
        if ("plugins".equals(qName)) {
          nextMode = PLUGINS_MODE;
        }
        break;
      case PLUGINS_MODE:
        if (qName.equals("plugin")) {
          nextMode = PLUGIN_MODE;
        }
        break;
      case PLUGIN_MODE:
        lastEntry.m_element.addChild(thisElement);
        nextMode = PLUGIN_CHILD_MODE;
        break;
    }
    pushStack(nextMode, thisElement);
  }

  public void endElement(String uri, String localName, String qName) throws SAXException {
    StackEntry lastEntry = popSimpleElement();
    SimpleElement simpleEl = lastEntry.m_element;
    ParseMode lastMode = lastEntry.m_mode;
    assertThat(simpleEl.m_name.equals(qName));
    switch (lastMode) {
      case PLUGIN_MODE:
        handlePlugin(simpleEl);
        break;
    }
  }
}
