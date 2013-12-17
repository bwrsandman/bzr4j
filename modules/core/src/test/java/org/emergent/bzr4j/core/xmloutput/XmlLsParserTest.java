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

import org.emergent.bzr4j.core.IBazaarItemInfo;
import org.emergent.bzr4j.core.cli.BzrStandardResult;
import org.emergent.bzr4j.core.testutil.BzrTestExec;
import org.emergent.bzr4j.core.testutil.QuickExecTest;
import org.emergent.bzr4j.core.testutil.QuickExec;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Patrick Woodworth
 */
public class XmlLsParserTest extends QuickExecTest {

  @Override
  public void setupWorkspaces(QuickTestBuilder qbuilder) throws Exception {
    qbuilder.initBranch("test1");
    qbuilder.addFileFromResource("empty.txt",".bzrignore");
    qbuilder.addNewDirectory("foodir");
    qbuilder.addFileFromString("foo","foodir/foo.txt");
    qbuilder.commit("Adding .bzrignore file");
    qbuilder.getQexec().pushd("test1");
  }

  @Test
  public void testXmlLs() throws Exception {
    QuickExec qexec = getQuickExec();
    BzrTestExec handler = qexec.createCommand("xmlls");
    BzrStandardResult result = (BzrStandardResult)handler.exectest();
    List<IBazaarItemInfo> itemInfos = XmlOutputParser.parseXmlLs(result);
    assertEquals(itemInfos.size(),3);
  }
}
