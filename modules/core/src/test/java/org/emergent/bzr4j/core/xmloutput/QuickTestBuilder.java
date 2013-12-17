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

import org.apache.commons.io.FileUtils;
import org.emergent.bzr4j.core.BazaarItemKind;
import org.emergent.bzr4j.core.IBazaarStatus;
import org.emergent.bzr4j.core.cli.BzrExecException;
import org.emergent.bzr4j.core.tests.ResourceTestHelper;
import org.emergent.bzr4j.core.testutil.QuickExec;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

/**
 * @author Patrick Woodworth
 */
public class QuickTestBuilder {

  private final QuickExec m_quickExec;

  private final ArrayList<CommitData> m_commitData = new ArrayList<CommitData>();

  private CommitData m_currentCommit = new CommitData();

  public QuickTestBuilder() throws IOException {
    m_quickExec = new QuickExec();
  }

  public QuickTestBuilder(File cwd) {
    m_quickExec = new QuickExec(cwd);
  }

  public QuickExec getQexec() {
    return m_quickExec;
  }

  public void initBranch(String branchname) throws Exception {
    getQexec().popdAll();
    getQexec().createCommand("init").addArguments(branchname).exectest();
    getQexec().pushd(branchname);
  }

  public void cloneBranch(String srcname, String branchname) throws Exception {
    getQexec().popdAll();
    getQexec().createCommand("clone").addArguments("-q", srcname, branchname).exectest();
    getQexec().pushd(branchname);
  }

  public void commit(String message) throws BzrExecException {
    getQexec().createCommand("commit").addArguments("-q", "-m", message).exectest();
    m_currentCommit.setMessage(message);
    m_currentCommit.finish();
    m_commitData.add(m_currentCommit);
    m_currentCommit = new CommitData();
    getQexec().popd();
  }
  public void commit(String message, boolean fail) throws BzrExecException {
    getQexec().createCommand("commit").addArguments("-m", message).exectest();
    m_currentCommit.setMessage(message);
    m_currentCommit.finish();
    m_commitData.add(m_currentCommit);
    m_currentCommit = new CommitData();
    getQexec().popd();
  }

  public void addNewDirectory(String fileName) throws BzrExecException, IOException {
    addNewDirectory(fileName, true);
  }

  public void addNewDirectory(String fileName, boolean add) throws BzrExecException, IOException {
    QuickExec qexec = getQexec();
    File newDir = new File(qexec.getCwd(), fileName);
    newDir.mkdirs();
    if (!add)
      return;
    qexec.createCommand("add").addArguments(fileName).exectest();
  }

  public void addFileFromResource(String resName, String fileName) throws BzrExecException, IOException {
    QuickExec qexec = getQexec();
    ResourceTestHelper.copyResourceToFile(resName, new File(qexec.getCwd(), fileName));
    qexec.createCommand("add").addArguments(fileName).exectest();
  }

  public void addFileFromString(String content, String fileName) throws BzrExecException, IOException {
    addFileFromString(content, fileName, true);
  }

  public void addFileFromString(String content, String fileName, boolean add) throws BzrExecException, IOException {
    QuickExec qexec = getQexec();
    ResourceTestHelper.copyStringToFile(content, new File(qexec.getCwd(), fileName));
    if (!add)
      return;
    qexec.createCommand("add").addArguments(fileName).exectest();
  }

  public void editFileFromString(String content, String fileName) throws BzrExecException, IOException {
    QuickExec qexec = getQexec();
    ResourceTestHelper.copyStringToFile(content, new File(qexec.getCwd(), fileName));
  }

  public void merge(String srcbranch) throws BzrExecException {
    getQexec().createCommand("merge").addArguments("-q", "../" + srcbranch).exectest(false, false);
  }

  public ArrayList<CommitData> getCommitData() {
    return m_commitData;
  }

  public void remove(String fileName) throws BzrExecException, IOException {
    QuickExec qexec = getQexec();
    File theFile = new File(qexec.getCwd(), fileName);
    BazaarItemKind kind = theFile.isDirectory() ? BazaarItemKind.directory : BazaarItemKind.file;
    qexec.createCommand("rm").addArguments("-q", fileName).exectest();
  }

  public void move(String srcPath, String destPath) throws BzrExecException, IOException {
    QuickExec qexec = getQexec();
    qexec.createCommand("mv").addArguments("-q", srcPath, destPath).exectest();
  }

  public void changeType(String path, boolean toDir) throws BzrExecException, IOException {
    QuickExec qexec = getQexec();
    File theFile = new File(qexec.getCwd(), path);
    FileUtils.deleteDirectory(theFile);
    if (toDir) {
      theFile.mkdirs();
    } else {
      ResourceTestHelper.copyStringToFile("changed type", theFile);
    }
  }

  public class CommitData {

    private String message;
    Set<IBazaarStatus> statusSet;

    public CommitData() {
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public Set<IBazaarStatus> getStatusSet() {
      return statusSet;
    }

    public void finish() {
    }
  }
}
