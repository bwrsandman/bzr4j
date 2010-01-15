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
package org.emergent.bzr4j.core;

import org.emergent.bzr4j.core.utils.StringUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Patrick Woodworth
 */
public abstract class BzrAbstractHandler {

  private static final ConcurrentMap<String, Lock> sm_workDirLocks = new ConcurrentHashMap<String, Lock>();

  private final File m_dir;

  private final String m_cmd;

  private final ArrayList<String> m_args = new ArrayList<String>();

  private boolean m_stderrValidationEnabled = true;

  private boolean m_exitValueValidationEnabled = true;

  public BzrAbstractHandler(File dir, String cmd) {
    m_dir = dir;
    m_cmd = cmd;
  }

  public void addArguments(String... args) {
    addArguments(Arrays.asList(args));
  }

  public void addArguments(List<String> args) {
    m_args.addAll(args);
  }

  protected BzrHandlerResult exec(BzrHandlerResult result) throws BzrHandlerException {
    ArrayList<String> args = new ArrayList<String>(Arrays.asList(getBzrExecutablePath(),m_cmd,"--no-aliases"));
    args.addAll(m_args);
    Process process = null;
    acquireLock();
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(args);
      File workDir = null;
      if (m_dir != null && m_dir.isDirectory()) {
        workDir = m_dir;
        processBuilder = processBuilder.directory(workDir);
      }
      logExec(result,workDir,args);
      process = processBuilder.start();
      Thread stdOutThread = result.startRelay(process.getInputStream(), false);
      Thread stdErrThread = result.startRelay(process.getErrorStream(), true);
      try {
        OutputStream stdIn = process.getOutputStream();
        if (stdIn != null)
          stdIn.close();
      } catch (Exception ignored) {
      }
      result.setExitValue(process.waitFor());
      stdOutThread.join();
      stdErrThread.join();
      if ((isExitValueValidationEnabled() && result.getExitValue() != 0)
          || (isStderrValidationEnabled() && !result.isStdErrEmpty())) {
        throw new BzrHandlerException(
            BzrMessages.message("bzr4j.exec.validation.error",
                result.getExitValue(), result.getStdErrAsString(), StringUtil.toString(args, "\n", "", ""), workDir));
      }
    } catch (IOException e) {
      throw new BzrHandlerException(e);
    } catch (InterruptedException e) {
      throw new BzrHandlerException(e);
    } finally {
      releaseLock();
      if (process != null)
        try {
          process.destroy();
        } catch (Exception ignored) {
        }
    }
    return result;
  }

  public String getCmd() {
    return m_cmd;
  }

  public File getDir() {
    return m_dir;
  }

  public boolean isStderrValidationEnabled() {
    return m_stderrValidationEnabled;
  }

  public void setStderrValidationEnabled(boolean stderrValidationEnabled) {
    m_stderrValidationEnabled = stderrValidationEnabled;
  }

  public boolean isExitValueValidationEnabled() {
    return m_exitValueValidationEnabled;
  }

  public void setExitValueValidationEnabled(boolean exitValueValidationEnabled) {
    m_exitValueValidationEnabled = exitValueValidationEnabled;
  }

  protected abstract String getBzrExecutablePath();

  protected void acquireLock() {
    String path = getDir() == null ? "." : getDir().getAbsolutePath();
//    logDebug(String.format("Locking   \"%s\"", path));
    getWorkDirLock(path).lock();
  }

  protected void releaseLock() {
    String path = getDir() == null ? "." : getDir().getAbsolutePath();
//    logDebug(String.format("Unlocking \"%s\"", path));
    getWorkDirLock(path).unlock();
  }

  public static Lock getWorkDirLock(String path) {
//    String path = workDir != null ? workDir.getAbsolutePath() : ".";
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

  protected void logExec(BzrHandlerResult result, File workDir, ArrayList<String> args) {
    logDebug(String.format("(%s) : %s", String.valueOf(workDir), args.toString()));
  }

  protected abstract void logDebug(String msg);

  protected abstract void logInfo(String msg);
}
