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

import org.emergent.bzr4j.core.BazaarItemKind;
import org.emergent.bzr4j.core.testutil.QuickExec;
import org.emergent.bzr4j.core.xmloutput.XmlBazaarStatus;
import org.emergent.bzr4j.core.BazaarStatusType;
import org.emergent.bzr4j.core.IBazaarStatus;
import org.emergent.bzr4j.core.cli.BzrHandlerException;
import org.emergent.bzr4j.core.tests.ResourceTestHelper;
import org.emergent.bzr4j.core.utils.IOUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

  public void commit(String message) throws BzrHandlerException {
    getQexec().createCommand("commit").addArguments("-q", "-m", message).exectest();
    m_currentCommit.setMessage(message);
    m_currentCommit.finish();
    m_commitData.add(m_currentCommit);
    m_currentCommit = new CommitData();
    getQexec().popd();
  }
  public void commit(String message, boolean fail) throws BzrHandlerException {
    getQexec().createCommand("commit").addArguments("-m", message).exectest();
    m_currentCommit.setMessage(message);
    m_currentCommit.finish();
    m_commitData.add(m_currentCommit);
    m_currentCommit = new CommitData();
    getQexec().popd();
  }

  public void addNewDirectory(String fileName) throws BzrHandlerException, IOException {
    QuickExec qexec = getQexec();
    File newDir = new File(qexec.getCwd(), fileName);
    newDir.mkdirs();
    qexec.createCommand("add").addArguments(fileName).exectest();
    List<BazaarStatusType> newStats = Arrays.asList(BazaarStatusType.CREATED);
    m_currentCommit.getStatusList().add(
        new XmlBazaarStatus.Builder().setBranchRoot(qexec.getCwd()).setPath(new File(fileName))
            .setKind(BazaarItemKind.directory).setStatusTypes(newStats).createBazaarStatus());
  }

  public void addFileFromResource(String resName, String fileName) throws BzrHandlerException, IOException {
    QuickExec qexec = getQexec();
    ResourceTestHelper.copyResourceToFile(resName, new File(qexec.getCwd(), fileName));
    qexec.createCommand("add").addArguments(fileName).exectest();
    List<BazaarStatusType> newStats = Arrays.asList(BazaarStatusType.CREATED);
    m_currentCommit.getStatusList().add(
        new XmlBazaarStatus.Builder().setBranchRoot(qexec.getCwd()).setPath(new File(fileName)).setKind(BazaarItemKind.file)
            .setStatusTypes(newStats).createBazaarStatus());
  }

  public void addFileFromString(String content, String fileName) throws BzrHandlerException, IOException {
    QuickExec qexec = getQexec();
    ResourceTestHelper.copyStringToFile(content, new File(qexec.getCwd(), fileName));
    qexec.createCommand("add").addArguments(fileName).exectest();
    List<BazaarStatusType> newStats = Arrays.asList(BazaarStatusType.CREATED);
    m_currentCommit.getStatusList().add(
        new XmlBazaarStatus.Builder().setBranchRoot(qexec.getCwd()).setPath(new File(fileName)).setKind(BazaarItemKind.file)
            .setStatusTypes(newStats).createBazaarStatus());
  }

  public void editFileFromString(String content, String fileName) throws BzrHandlerException, IOException {
    QuickExec qexec = getQexec();
    ResourceTestHelper.copyStringToFile(content, new File(qexec.getCwd(), fileName));
    List<BazaarStatusType> newStats = Arrays.asList(BazaarStatusType.MODIFIED);
    m_currentCommit.getStatusList().add(
        new XmlBazaarStatus.Builder().setBranchRoot(qexec.getCwd()).setPath(new File(fileName)).setKind(BazaarItemKind.file)
            .setStatusTypes(newStats).createBazaarStatus());
  }

  public void merge(String srcbranch) throws BzrHandlerException {
    getQexec().createCommand("merge").addArguments("-q", "../" + srcbranch).exectest();
  }

  public ArrayList<CommitData> getCommitData() {
    return m_commitData;
  }

  public void remove(String fileName) throws BzrHandlerException, IOException {
    QuickExec qexec = getQexec();
    File theFile = new File(qexec.getCwd(), fileName);
    BazaarItemKind kind = theFile.isDirectory() ? BazaarItemKind.directory : BazaarItemKind.file;
    qexec.createCommand("rm").addArguments("-q", fileName).exectest();
    List<BazaarStatusType> newStats = Arrays.asList(BazaarStatusType.DELETED);
    m_currentCommit.getStatusList().add(
        new XmlBazaarStatus.Builder().setBranchRoot(qexec.getCwd()).setPath(new File(fileName)).setKind(kind)
            .setStatusTypes(newStats).createBazaarStatus());
  }

  public void move(String srcPath, String destPath) throws BzrHandlerException, IOException {
    QuickExec qexec = getQexec();
    File theFile = new File(qexec.getCwd(), srcPath);
    BazaarItemKind kind = theFile.isDirectory() ? BazaarItemKind.directory : BazaarItemKind.file;
    qexec.createCommand("mv").addArguments("-q", srcPath, destPath).exectest();
    List<BazaarStatusType> newStats = Arrays.asList(BazaarStatusType.RENAMED);
    IBazaarStatus newStatus =
        new XmlBazaarStatus.Builder().setBranchRoot(qexec.getCwd()).setPath(new File(destPath))
            .setOldPath(new File(srcPath)).setKind(kind).setOldKind(kind).setStatusTypes(newStats).createBazaarStatus();
    m_currentCommit.getStatusList().add(newStatus);
  }

  public void changeType(String path, boolean toDir) throws BzrHandlerException, IOException {
    QuickExec qexec = getQexec();
    File theFile = new File(qexec.getCwd(), path);
    IOUtil.deleteRecursively(theFile);
    List<BazaarStatusType> newStats = Arrays.asList(BazaarStatusType.KIND_CHANGED);
    BazaarItemKind kind;
    BazaarItemKind oldKind;
    if (toDir) {
      theFile.mkdirs();
      kind = BazaarItemKind.directory;
      oldKind = BazaarItemKind.file;
    } else {
      ResourceTestHelper.copyStringToFile("changed type", theFile);
      kind = BazaarItemKind.file;
      oldKind = BazaarItemKind.directory;
    }
    IBazaarStatus newStatus =
        new XmlBazaarStatus.Builder().setBranchRoot(qexec.getCwd()).setPath(new File(path)).setOldPath(null).setKind(kind)
            .setOldKind(oldKind).setStatusTypes(newStats).createBazaarStatus();
    m_currentCommit.getStatusList().add(newStatus);
  }

  public class CommitData {

    private String message;
    private List<IBazaarStatus> m_statusList = new ArrayList<IBazaarStatus>();

    public CommitData() {
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public void finish() {
    }

    private List<IBazaarStatus> getStatusList() {
      return m_statusList;
    }

    public Set<IBazaarStatus> getStatusSet() {
      return XmlBazaarStatus.orderAndCleanup(m_statusList);
    }
  }
}
