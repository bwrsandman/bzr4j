/*
 * Copyright (c) 2009-2010 Patrick Woodworth
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

import org.emergent.bzr4j.core.BazaarChangeType;
import org.emergent.bzr4j.core.BazaarItemKind;
import org.xml.sax.Attributes;

/**
* @author Patrick Woodworth
*/
public class GenericChange {

  public final BazaarChangeType m_changeType;
  public final BazaarItemKind m_kind;
  public final String m_path;
  public final Attributes m_attributes;

  public GenericChange(String changeType, String kind, String path, Attributes attributes) {
    m_changeType = BazaarChangeType.valueOf(changeType);
    m_kind = BazaarItemKind.valueOf(kind);
    m_path = path;
    m_attributes = attributes;
  }

  public String getOldPath() {
    if (m_changeType == BazaarChangeType.renamed)
      return m_attributes.getValue("oldpath");
    return null;
  }

  public String getConflictType() {
    if (m_changeType == BazaarChangeType.conflicts)
      return m_attributes.getValue("type");
    return null;
  }

  public String getOldKind() {
    if (m_changeType == BazaarChangeType.kind_changed)
      return m_attributes.getValue("oldkind");
    return null;
  }

  @Override
  public String toString() {
    StringBuffer strbuf = new StringBuffer();
    strbuf.append("GenericChange:");
    strbuf.append( "\n  changeType: " + m_changeType);
    strbuf.append( "\n  kind: " + m_kind);
    strbuf.append( "\n  path: " + m_path);
    Attributes attributes = m_attributes;
    for (int ii = 0; ii < attributes.getLength(); ii++) {
      String attrName = attributes.getQName(ii);
      String attrVal = attributes.getValue(ii);
      strbuf.append( String.format("\n    attrib(%d): \"%s\" = \"%s\"", ii, attrName, attrVal ) );
    }
    return strbuf.toString();
  }  
}
