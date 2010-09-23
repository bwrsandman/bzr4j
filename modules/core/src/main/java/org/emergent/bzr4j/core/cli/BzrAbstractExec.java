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
package org.emergent.bzr4j.core.cli;

import org.apache.commons.io.IOUtils;
import org.emergent.bzr4j.core.BazaarRoot;
import org.emergent.bzr4j.core.debug.DebugLogger;
import org.emergent.bzr4j.core.debug.DebugManager;
import org.emergent.bzr4j.core.utils.BzrCoreUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Patrick Woodworth
 */
public abstract class BzrAbstractExec {

  private static final DebugLogger LOG = DebugManager.getLogger(BzrAbstractExec.class);

  private static final int WORK_DIR_LOG_FIELD_SIZE = 25;

  private static final File DEFAULT_WORKDIR = (new File(System.getProperty("user.dir"))).getAbsoluteFile();

  private static final Set<String> ROOTLESS_WHITELIST = new HashSet<String>(
      Arrays.asList("clone", "help", "plugins", "revno", "version", "xmlplugins", "xmlversion")
  );

  private static final HashMap<String,Long> sm_timings = new HashMap<String, Long>();

  private static final boolean COLLECT_TIMINGS = Boolean.getBoolean("bzr4j.collect_statistics");

  private static final Timer sm_timer = new Timer(true);

  static {
    if (COLLECT_TIMINGS) {
      sm_timer.schedule(new TimerTask() {
        @Override
        public void run() {
          OutputStream os = null;
          try {
            Properties props = new Properties();
            synchronized (sm_timings) {
              for (Map.Entry<String,Long> entry : sm_timings.entrySet()) {
                props.setProperty(entry.getKey(), "" + entry.getValue());
              }
            }
            os = new FileOutputStream(new File(System.getProperty("user.home"), "bzr4j-timings.properties"));
            props.store(os, null);
            os.flush();
          } catch (Exception ignored) {
          } finally {
            IOUtils.closeQuietly(os);
          }
        }
      }, 5000, 5000);
    }
  }

  private final BazaarRoot m_bazaarRoot;

  private final File m_workingDir;

  private final String m_cmd;

  private final ArrayList<String> m_args = new ArrayList<String>();

  private boolean m_stderrValidationEnabled = true;

  private boolean m_exitValueValidationEnabled = true;

  public BzrAbstractExec(BazaarRoot root, String cmd) {
    if (root == null)
      throw new NullPointerException();
    if (cmd == null)
      throw new NullPointerException();

    if (BazaarRoot.ROOTLESS.equals(root) && !ROOTLESS_WHITELIST.contains(cmd))
      throw new IllegalArgumentException("Illegal to exec \"" + cmd + "\" rootless");

    m_bazaarRoot = root;
    m_cmd = cmd;
    File dir = root.getFile();
    m_workingDir = (dir != null && dir.exists()) ? dir : DEFAULT_WORKDIR;
  }

  public BzrAbstractExec addArguments(String... args) {
    return addArguments(Arrays.asList(args));
  }

  public BzrAbstractExec addArguments(List<String> args) {
    m_args.addAll(args);
    return this;
  }

  public void addRelativePaths(File... paths) {
    File workingDir = getWorkingDir();
    for (File path : paths) {
      addArguments(BzrCoreUtil.relativePath(workingDir,path));
    }
  }

  public String getCmd() {
    return m_cmd;
  }

  public BazaarRoot getBazaarRoot() {
    return m_bazaarRoot;
  }

  public File getWorkingDir() {
    return m_workingDir;
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

  private static AtomicLong execCount = new AtomicLong(0);

  protected BzrAbstractResult exec(BzrAbstractResult result) throws BzrExecException {
    long startTime = System.currentTimeMillis();
    long execId = execCount.incrementAndGet();
    ArrayList<String> args = new ArrayList<String>(Arrays.asList(getBzrExecutablePath(),m_cmd,"--no-aliases"));
    args.addAll(m_args);
    Process process = null;
    try {
      ProcessBuilder processBuilder = createProcessBuilder(args);
      processBuilder = processBuilder.directory(m_workingDir);
      logExec(result, execId, m_workingDir, args);
      process = processBuilder.start();
      Thread stdOutThread = result.startOutRelay(process.getInputStream());
      Thread stdErrThread = result.startErrRelay(process.getErrorStream());
      try {
        OutputStream stdIn = process.getOutputStream();
        if (stdIn != null)
          stdIn.close();
      } catch (Exception ignored) {
      }
      result.setExitValue(process.waitFor());
      stdOutThread.join();
      stdErrThread.join();
      result.validate(m_workingDir, args, isExitValueValidationEnabled(), isStderrValidationEnabled());
    } catch (IOException e) {
      throw new BzrExecException(e);
    } catch (InterruptedException e) {
      throw new BzrExecException(e);
    } finally {
      if (process != null)
        try {
          process.destroy();
        } catch (Exception ignored) {
        }
    }
    long deltaTime = System.currentTimeMillis() - startTime;
    logExec(result, execId, deltaTime, m_workingDir, args);
    synchronized (sm_timings) {
      Long aggregate = sm_timings.get(m_cmd);
      sm_timings.put(m_cmd, deltaTime + (aggregate == null ? 0 : aggregate.longValue()));
    }
    return result;
  }

  protected ProcessBuilder createProcessBuilder(List<String> args) {
    return new ProcessBuilder(args);
  }

  protected abstract String getBzrExecutablePath();

  protected void logExec(BzrAbstractResult result, long execId, File workDir, ArrayList<String> args) {
    logExec(result, execId, -1, m_workingDir, args);
  }

  protected void logExec(BzrAbstractResult result, long execId, long deltaTime, File workDir, ArrayList<String> args) {
    String workPath = String.valueOf(workDir);
    if (workPath.length() > WORK_DIR_LOG_FIELD_SIZE)
      workPath = workPath.substring(workPath.length() - WORK_DIR_LOG_FIELD_SIZE);
    if (deltaTime >= 0) {
        LOG.debug(String.format("(%" + WORK_DIR_LOG_FIELD_SIZE + "s) : %d : %s : %d", workPath, execId, args.toString(), deltaTime));
    } else {
        LOG.debug(String.format("(%" + WORK_DIR_LOG_FIELD_SIZE + "s) : %d : %s", workPath, execId, args.toString()));
    }
  }
}
