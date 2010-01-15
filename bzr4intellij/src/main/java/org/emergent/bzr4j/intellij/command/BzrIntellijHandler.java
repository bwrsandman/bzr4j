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

package org.emergent.bzr4j.intellij.command;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.emergent.bzr4j.core.BzrAbstractHandler;
import org.emergent.bzr4j.core.BzrHandlerException;
import org.emergent.bzr4j.intellij.BzrGlobalSettings;
import org.emergent.bzr4j.intellij.BzrUtil;

import java.io.File;
import java.nio.charset.Charset;

/**
 * @author Patrick Woodworth
 */
public class BzrIntellijHandler extends BzrAbstractHandler {

  private static final Logger LOG = Logger.getInstance(BzrIntellijHandler.class.getName());

  private final Project m_project;

  private final Charset m_charset;

  private final VirtualFile m_repo;

  private boolean m_bad;

  public BzrIntellijHandler(VirtualFile repo, String cmd) {
    this(null, repo, null, cmd);
  }

  public BzrIntellijHandler(VirtualFile repo, Charset charset, String cmd) {
    this(null, repo, charset, cmd);
  }  

  public BzrIntellijHandler(Project project, VirtualFile repo, String cmd) {
    this(project, repo, null, cmd);
  }

  public BzrIntellijHandler(Project project, VirtualFile repo, Charset charset, String cmd) {
    super(getBzrRoot(repo), cmd);
    m_repo = repo;
    m_project = project;
    m_charset = charset;
  }

  public ShellCommandResult execij() throws BzrHandlerException {
    return (ShellCommandResult)super.exec(new ShellCommandResult(m_charset));
  }

  public void addRelativePaths(VirtualFile... files) {
    for (VirtualFile file : files) {
      addArguments(BzrUtil.relativePath(m_repo,file));
    }
  }

  public boolean isBad() {
    return m_bad;
  }

  public void setBad(boolean bad) {
    m_bad = bad;
  }

  @Override
  protected String getBzrExecutablePath() {
    return BzrGlobalSettings.getInstance().getBzrExecutable();
  }

  @Override
  protected void logDebug(String msg) {
    LOG.debug(msg);
  }

  @Override
  protected void logInfo(String msg) {
    LOG.info(msg);
  }

  private static File getBzrRoot(VirtualFile repo) {
    if (repo == null)
      return null;
    String repoPath = repo.getPath();
    File repoFile = repoPath != null ? new File(repoPath) : null;
    return BzrUtil.getBzrRootOrNull(repoFile);
  }
}
