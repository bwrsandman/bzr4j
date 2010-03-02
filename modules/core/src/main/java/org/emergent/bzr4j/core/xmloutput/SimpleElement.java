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

import org.xml.sax.Attributes;

import java.util.ArrayList;

/**
* @author Patrick Woodworth
*/
class SimpleElement {
  public final String m_name;
  public final Attributes m_attributes;
  public final StringBuilder m_text = new StringBuilder();
  public final ArrayList<SimpleElement> m_children = new ArrayList<SimpleElement>();

  public SimpleElement(String name, Attributes attributes) {
    m_name = name;
    m_attributes = attributes;
  }

  public void addChild(SimpleElement child) {
    m_children.add(child);
  }
}
