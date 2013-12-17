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

import javax.xml.stream.XMLInputFactory;
import java.io.File;

/**
 * @author Patrick Woodworth
 */
class XmlOutputUtil {

  static File getAsFile(final String relativePathTofile) {
    if (relativePathTofile != null && !"".equals(relativePathTofile)) {
      return new File(relativePathTofile);
    }
    return null;
  }

  static XMLInputFactory getXMLInputFactory() {
    return _xmlInputFactory;
  }


  private static XMLInputFactory _createXMLInputFactory() {
    XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

    xmlInputFactory.setProperty(
            XMLInputFactory.IS_COALESCING, Boolean.TRUE);

    return xmlInputFactory;
  }

  private static XMLInputFactory _xmlInputFactory = _createXMLInputFactory();

}
