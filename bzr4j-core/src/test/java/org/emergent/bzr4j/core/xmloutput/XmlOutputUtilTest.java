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

import org.emergent.bzr4j.core.IBazaarLogMessage;
import org.emergent.bzr4j.core.IBazaarStatus;
import org.emergent.bzr4j.core.cli.BzrHandlerResult;
import org.emergent.bzr4j.core.testutil.BzrTestHandler;
import org.emergent.bzr4j.core.testutil.QuickExec;
import org.emergent.bzr4j.core.testutil.QuickExecTest;
import org.emergent.bzr4j.core.xmloutput.QuickTestBuilder;
import org.emergent.bzr4j.core.xmloutput.XmlStatusResult;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Patrick Woodworth
 */
public class XmlOutputUtilTest extends QuickExecTest {

  public void setupWorkspaces(QuickTestBuilder qbuilder) throws Exception {
    QuickExec qexec = qbuilder.getQexec();
    qbuilder.initBranch("test1");
    List<IBazaarLogMessage> logMsgs = XmlOutputParser.parseXmlLog(qexec.createCommand("xmllog").exectest());
    assertEquals(logMsgs.size(), 0);

    qbuilder.addFileFromResource("empty.txt",".bzrignore");
    qbuilder.addNewDirectory("foodir");
    qbuilder.addFileFromString("foo","foodir/foo.txt");
    qbuilder.commit("Adding .bzrignore file");
    qbuilder.cloneBranch("test1","test2");
    qbuilder.addNewDirectory("bardir");
    qbuilder.addFileFromString("bar","bardir/bar.txt");
    qbuilder.addFileFromResource("lorem.txt","lorem.txt");
    qbuilder.remove(".bzrignore");
//    qbuilder.editFileFromString("zoo","foodir/foo.txt");
    qbuilder.move("foodir/foo.txt","foodir/zoo.txt");
    qbuilder.editFileFromString("zoo","foodir/zoo.txt");
    qbuilder.commit("Adding lorem.txt file");
    qbuilder.cloneBranch("test1","test3");
    qbuilder.merge("test2");
    qbuilder.commit("Merging in test2 branch");
    qbuilder.cloneBranch("test3","test4");
    qbuilder.changeType("bardir/bar.txt",true);
    qbuilder.commit("Changing type");
//    qexec.popd();
    qexec.pushd("test4");
  }

  @Test
  public void testXmlLog() throws Exception {
    QuickExec qexec = getQuickExec();
    BzrTestHandler handler = qexec.createCommand("xmllog");
    BzrHandlerResult result = handler.exectest();
    List<IBazaarLogMessage> logMsgs = XmlOutputParser.parseXmlLog(result);
    assertEquals(logMsgs.size(), 4);
    String lastRev = "0";
    QuickTestBuilder qbuilder = getQuickBuilder();
    ArrayList<QuickTestBuilder.CommitData> commitData = qbuilder.getCommitData();
    for (IBazaarLogMessage lm : logMsgs) {
      String revno = lm.getRevision().getValue();
      System.err.println("rev: " + revno);
      Date revisionDate = lm.getDate();
      assertTrue(revisionDate.before(new Date()), "Revision date is after current date");
      String author = lm.getCommiter();
      assertNotNull(author);
      String branchName = lm.getBranchNick();
      assertNotNull(branchName);
      String commitMessage = lm.getMessage();
      if ("1".equals(revno)) {
        assertEquals(commitMessage,commitData.get(0).getMessage());
      }
      lastRev = revno;
    }
  }

  @Test(groups = "normal")
  public void testXmlLogWithoutMerges() throws Exception {
    QuickExec qexec = getQuickExec();
    BzrTestHandler handler = qexec.createCommand("xmllog");
    handler.addArguments("-v");
    BzrHandlerResult result = handler.exectest();
    List<IBazaarLogMessage> logMsgs = XmlOutputParser.parseXmlLog(result,false);
    assertEquals(logMsgs.size(), 3);
    String lastRev = "0";
    QuickTestBuilder qbuilder = getQuickBuilder();
    ArrayList<QuickTestBuilder.CommitData> commitData = qbuilder.getCommitData();
    for (IBazaarLogMessage lm : logMsgs) {
      String revno = lm.getRevision().getValue();
      System.err.println("rev: " + revno);
      {
        List<IBazaarStatus> affected = lm.getAffectedFiles(true);
        System.err.println("affected: " + affected.size());
      }
      Date revisionDate = lm.getDate();
      assertTrue(revisionDate.before(new Date()), "Revision date is after current date");
      String author = lm.getCommiter();
      assertNotNull(author);
      String branchName = lm.getBranchNick();
      assertNotNull(branchName);
      String commitMessage = lm.getMessage();
      QuickTestBuilder.CommitData commitDatum = null;
      if ("1".equals(revno)) {
        commitDatum = commitData.get(0);
        assertEquals(commitMessage,commitDatum.getMessage());
        List<IBazaarStatus> affectedSansMerges = lm.getAffectedFiles(false);
        List<IBazaarStatus> affectedWithMerges = lm.getAffectedFiles(true);
        assertEquals(affectedSansMerges.size(), affectedWithMerges.size());
        checkXmlStatus(commitDatum.getStatusSet(),affectedSansMerges);
      } else if ("2".equals(revno)) {
        commitDatum = commitData.get(1);
        List<IBazaarStatus> affectedSansMerges = lm.getAffectedFiles(false);
        List<IBazaarStatus> affectedWithMerges = lm.getAffectedFiles(true);
//        assertFalse(affectedSansMerges.size() == affectedWithMerges.size(), "affected sizes match: " + affectedSansMerges.size());
        checkXmlStatus(commitDatum.getStatusSet(),affectedSansMerges);
      } else {
        commitDatum = commitData.get(3);
        List<IBazaarStatus> affectedSansMerges = lm.getAffectedFiles(false);
        List<IBazaarStatus> affectedWithMerges = lm.getAffectedFiles(true);
//        assertFalse(affectedSansMerges.size() == affectedWithMerges.size(), "affected sizes match: " + affectedSansMerges.size());
        checkXmlStatus(commitDatum.getStatusSet(),affectedSansMerges);
      }
      lastRev = revno;
    }
  }

  @Test
  public void testXmlStatus() throws Exception {
    QuickTestBuilder qbuilder = getQuickBuilder();
    QuickExec qexec = qbuilder.getQexec();
    BzrTestHandler xmlStatusCmd = qexec.createCommand("xmlstatus");
    String lastRev = "0";
    String curRev = "1";
    xmlStatusCmd.addArguments("-r", lastRev + ".." + curRev);
    BzrHandlerResult result = xmlStatusCmd.exectest();
    XmlStatusResult parser = XmlOutputParser.parseXmlStatus(result);
    Set<IBazaarStatus> statii = parser.getStatusSet();
    assertEquals(statii.size(),3);
    ArrayList<QuickTestBuilder.CommitData> commitDataList = qbuilder.getCommitData();
    QuickTestBuilder.CommitData commitData = commitDataList.get(0);
    Set<IBazaarStatus> expectedSet = commitData.getStatusSet();
    checkXmlStatus(expectedSet,statii);
  }

  @Test
  public void testXmlStatusMergeRev() throws Exception {
    QuickTestBuilder qbuilder = getQuickBuilder();
    QuickExec qexec = qbuilder.getQexec();
    BzrTestHandler xmlStatusCmd = qexec.createCommand("xmlstatus");
    String lastRev = "1";
    String curRev = "2";
    xmlStatusCmd.addArguments("-r", lastRev + ".." + curRev);
    BzrHandlerResult result = xmlStatusCmd.exectest();
    XmlStatusResult parser = XmlOutputParser.parseXmlStatus(result);
    Set<IBazaarStatus> statii = parser.getStatusSet();
    assertEquals(statii.size(),5);
    ArrayList<QuickTestBuilder.CommitData> commitDataList = qbuilder.getCommitData();
    QuickTestBuilder.CommitData commitData = commitDataList.get(1);
    Set<IBazaarStatus> expectedSet = commitData.getStatusSet();
    checkXmlStatus(expectedSet,statii);
  }

  @Test
  public void testXmlStatusTypeChange() throws Exception {
    QuickTestBuilder qbuilder = getQuickBuilder();
    QuickExec qexec = qbuilder.getQexec();
    BzrTestHandler xmlStatusCmd = qexec.createCommand("xmlstatus");
    String lastRev = "2";
    String curRev = "3";
    xmlStatusCmd.addArguments("-r", lastRev + ".." + curRev);
    BzrHandlerResult result = xmlStatusCmd.exectest();
    XmlStatusResult parser = XmlOutputParser.parseXmlStatus(result);
    Set<IBazaarStatus> statii = parser.getStatusSet();
    assertEquals(statii.size(),1);
    ArrayList<QuickTestBuilder.CommitData> commitDataList = qbuilder.getCommitData();
    QuickTestBuilder.CommitData commitData = commitDataList.get(3);
    Set<IBazaarStatus> expectedSet = commitData.getStatusSet();
    checkXmlStatus(expectedSet,statii);
  }

  private static void checkXmlStatus(Set<IBazaarStatus> expectedSet, Collection<IBazaarStatus> actualSet) throws Exception {
    assertEquals(actualSet.size(),expectedSet.size());
    Iterator<IBazaarStatus> expecteds = expectedSet.iterator();
    for (IBazaarStatus bzrStatus : actualSet) {
      IBazaarStatus expected = expecteds.next();
      assertEquals(bzrStatus.getFile(), expected.getFile());
      assertEquals(bzrStatus.getStatuses(), expected.getStatuses());
    }
  }
}
