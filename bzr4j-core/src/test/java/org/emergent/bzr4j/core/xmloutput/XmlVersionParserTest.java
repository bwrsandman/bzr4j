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
import org.emergent.bzr4j.core.testutil.QuickExec;
import org.emergent.bzr4j.core.testutil.QuickExecTest;
import org.emergent.bzr4j.core.utils.PropertyExpander;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

import java.util.Properties;

/**
 * @author Patrick Woodworth
 */
public class XmlVersionParserTest extends QuickExecTest {

  @Override
  public void setupWorkspaces(QuickTestBuilder qbuilder) throws Exception {
  }

  @Test
  public void testXmlVersionParsingFromStdout() throws Exception {
    QuickExec qexec = getQuickExec();
    Properties versionProps = XmlOutputParser.parseXmlVersion(qexec.createCommand("xmlversion").exectest());
    PropertyExpander.dumpProps(versionProps);
    for (String key : XmlVersionParser.KNOWN_TEXT_ELEM_KEYS) {
      assertNotNull(versionProps.getProperty(key), String.format("version property \"%s\" was null",key));
    }
  }
}
