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

import org.emergent.bzr4j.core.BazaarChangeType;
import org.emergent.bzr4j.core.BazaarItemKind;
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
public class XmlStatusHandlerTest extends QuickExecTest {

  @Override
  public void setupWorkspaces(QuickTestBuilder qbuilder) throws Exception {
//    qbuilder.cloneBranch("lp:~sixseve/+junk/testdata","testdata");
    qbuilder.cloneBranch(TEST_BRANCH_URLBASE + ".branch3","testdata.branch3");
    qbuilder.cloneBranch(TEST_BRANCH_URLBASE + ".branch2","testdata.branch2");
    qbuilder.cloneBranch(TEST_BRANCH_URLBASE + ".branch1","testdata.branch1");
    qbuilder.merge("testdata.branch2");
  }

  @Test
  public void testFoo() throws Exception {
    QuickExec qexec = getQuickExec();
    qexec.popdAll();
    qexec.pushd("testdata.branch1");
    BzrTestExec handler = qexec.createCommand("xmlstatus");
//    handler.addArguments("-r0..1");
    final ArrayList<String> conflicts = new ArrayList<String>();
    XmlOutputHandler resultHandler = new XmlOutputHandler() {

      public void handleConflicts(String path, String type) {
        System.out.println("conflict: " + type + " " + path );
        conflicts.add(path);
      }
    };
    BzrAbstractResult result = handler.exectest(BzrXmlResult.createBzrXmlResult(resultHandler), true, true);
    assertEquals(conflicts.size(),3);
  }

  @Test
  public void testKindChanges() throws Exception {
    QuickExec qexec = getQuickExec();
    qexec.popdAll();
    qexec.pushd("testdata.branch3");
    BzrTestExec handler = qexec.createCommand("xmlstatus");
    handler.addArguments("-r1..2");
    final ArrayList<String[]> changes = new ArrayList<String[]>();
    changes.add(new String[] { "file", "foo", "directory" });
    changes.add(new String[] { "directory", "lorem.txt", "file" });
    XmlOutputHandler resultHandler = new XmlOutputHandler() {

      public void handleKindChanged(BazaarItemKind kind, String path, String oldKind) {
        for (Iterator<String[]> iter = changes.iterator(); iter.hasNext();) {
          String[] expected = iter.next();
          if (kind.equals(expected[0]) && path.equals(expected[1]) && oldKind.equals(expected[2])) {
            iter.remove();
            break;
          }
        }
      }
    };
    BzrAbstractResult result = handler.exectest(BzrXmlResult.createBzrXmlResult(resultHandler), true, true);
    assertEquals(changes.size(),0);
  }
}
