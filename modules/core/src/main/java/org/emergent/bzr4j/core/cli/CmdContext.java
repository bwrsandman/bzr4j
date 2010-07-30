/*
 * Copyright (c) 2009-2010 Patrick Woodworth
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

import org.emergent.bzr4j.core.debug.DebugLogger;
import org.emergent.bzr4j.core.debug.DebugManager;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Patrick Woodworth
 */
public class CmdContext {

  private static final DebugLogger LOG = DebugManager.getLogger(CmdContext.class);

  private static final int WORK_DIR_LOG_FIELD_SIZE = 40;

  private boolean m_stderrValidationEnabled = true;

  private boolean m_exitValueValidationEnabled = true;

  public String getBzrExecutablePath() {
    return "bzr";
  }

  protected ProcessBuilder createProcessBuilder(List<String> args) {
    return new ProcessBuilder(args);
  }

  protected void logExec(BzrAbstractResult result, File workDir, ArrayList<String> args) {
    String workPath = String.valueOf(workDir);
    if (workPath.length() > WORK_DIR_LOG_FIELD_SIZE)
      workPath = workPath.substring(workPath.length() - WORK_DIR_LOG_FIELD_SIZE);
    LOG.debug(String.format("(%" + WORK_DIR_LOG_FIELD_SIZE + "s) : %s", workPath, args.toString()));
  }


  BzrAbstractResult exec(String m_cmd, File m_workingDir, BzrAbstractResult result, String... m_args) throws IOException {
    ArrayList<String> args = new ArrayList<String>(Arrays.asList(getBzrExecutablePath(),m_cmd,"--no-aliases"));
    args.addAll(Arrays.asList(m_args));
    Process process = null;
    try {
      ProcessBuilder processBuilder = createProcessBuilder(args);
      processBuilder = processBuilder.directory(m_workingDir);
      logExec(result, m_workingDir, args);
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
//      result.validate(m_workingDir, args, m_exitValueValidationEnabled, m_stderrValidationEnabled); // todo enable
    } catch (InterruptedException e) {
      throw new BazaarIOException(e);
    } finally {
      if (process != null)
        try {
          process.destroy();
        } catch (Exception ignored) {
        }
    }
    return result;
  }
}
