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
import org.emergent.bzr4j.core.BzrHandlerResult;
import org.emergent.bzr4j.intellij.BzrGlobalSettings;
import org.emergent.bzr4j.intellij.BzrUtil;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Patrick Woodworth
 */
public class BzrIntellijHandler extends BzrAbstractHandler {

  private static final Logger LOG = Logger.getInstance(BzrIntellijHandler.class.getName());

  private static final ConcurrentMap<String, Lock> sm_workDirLocks = new ConcurrentHashMap<String, Lock>();

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

  @Override
  protected BzrHandlerResult exec(BzrHandlerResult result) throws BzrHandlerException {
    final Lock lock = getWorkDirLock(getDir());
    lock.lock();
    try {
      return super.exec(result);
    } finally {
      lock.unlock();
    }
  }

  public ShellCommandResult execij() throws BzrHandlerException {
    return (ShellCommandResult)exec(new ShellCommandResult(m_charset));
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
  protected ProcessBuilder createProcessBuilder(List<String> args) {
    ProcessBuilder retval = super.createProcessBuilder(args);
    Map<String, String> envVars = retval.environment();
    for (Map.Entry<String,String> override : BzrGlobalSettings.getInstance().getEnvironmentVariables().entrySet()) {
      String key = override.getKey();
      String value = override.getValue();
      if (value != null && value.trim().length() > 0) {
        envVars.put(key, value);
      } else {
        envVars.remove(key);
      }
    }
    return retval;
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

  public static Lock getWorkDirLock(File workDir) {
    boolean fineGrain = BzrGlobalSettings.getInstance().isGranularExecLockingEnabled();
    String path = (fineGrain && (workDir != null)) ? workDir.getAbsolutePath() : ".";
    Lock lock = sm_workDirLocks.get(path);
    if (lock == null) {
      lock = new ReentrantLock();
      Lock curLock = sm_workDirLocks.putIfAbsent(path, lock);
      if (curLock != null) {
        lock = curLock;
      }
    }
    return lock;
  }
}
