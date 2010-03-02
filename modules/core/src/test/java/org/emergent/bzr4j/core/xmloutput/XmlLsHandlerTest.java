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

import org.emergent.bzr4j.core.cli.BzrAbstractResult;
import org.emergent.bzr4j.core.cli.BzrXmlResult;
import org.emergent.bzr4j.core.testutil.BzrTestExec;
import org.emergent.bzr4j.core.testutil.QuickExec;
import org.emergent.bzr4j.core.testutil.QuickExecTest;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Patrick Woodworth
 */
public class XmlLsHandlerTest extends QuickExecTest {

  @Override
  public void setupWorkspaces(QuickTestBuilder qbuilder) throws Exception {
    qbuilder.cloneBranch(TEST_BRANCH_URLBASE,"testdata.ignored");
    qbuilder.addFileFromString("I should be ignored", "foo.tmp", false);
    qbuilder.addNewDirectory("bar.tmp", false);
  }

  @Test
  public void testIgnored() throws Exception {
    QuickExec qexec = getQuickExec();
    qexec.popdAll();
    qexec.pushd("testdata.ignored");
    BzrTestExec handler = qexec.createCommand("xmlls");
    handler.addArguments("--ignored");
    final ArrayList<String[]> conflicts = new ArrayList<String[]>();
    conflicts.add(new String[] { "file", "foo.tmp", "ignored" });
    conflicts.add(new String[] { "directory", "bar.tmp", "ignored" });
    XmlOutputHandler resultHandler = new XmlOutputHandler() {
      @Override
      public void handleItem(String id, String kind, String path, String statusKind) {
        for (Iterator<String[]> iter = conflicts.iterator(); iter.hasNext();) {
          String[] expected = iter.next();
          if (kind.equals(expected[0]) && path.equals(expected[1]) && statusKind.equals(expected[2])) {
            iter.remove();
            break;
          }
        }
      }
    };
    BzrAbstractResult result = handler.exectest(BzrXmlResult.createBzrXmlResult(resultHandler), true, true);
    assertEquals(conflicts.size(),0);
  }
}
