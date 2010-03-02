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

import org.apache.commons.io.LineIterator;
import org.emergent.bzr4j.core.BzrMessages;
import org.emergent.bzr4j.core.debug.DebugLogger;
import org.emergent.bzr4j.core.debug.DebugManager;
import org.emergent.bzr4j.core.utils.StringUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick Woodworth
 */
public abstract class BzrAbstractResult {

  private static final DebugLogger LOG = DebugManager.getLogger(BzrStandardResult.class.getName());
  private static final String OK_STDERR_MSG = "No handlers could be found for logger \"bzr\"";

  private final ByteArrayOutputStream m_err = new ByteArrayOutputStream();

  private boolean m_stderrValidationEnabled = true;

  private boolean m_exitValueValidationEnabled = true;

  private int m_exitValue = -1;

  public int getExitValue() {
    return m_exitValue;
  }

  public String getStdErrAsString() {
    return m_err.toString();
  }

  public List<String> getStdErrAsLines() {
    return tokenize(getStdErrAsString());
  }

  public boolean isStdErrEmpty() {
    return m_err.size() == 0 || OK_STDERR_MSG.equals(getStdErrAsString().trim());
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

  void setExitValue(int exitValue) {
    m_exitValue = exitValue;
  }

  Thread startErrRelay(final InputStream is) {
    return startRelay(new BufferedInputStream(is), m_err, false);
  }

  abstract Thread startOutRelay(InputStream is);

  protected static List<String> tokenize(String in) {
    ArrayList<String> retval = new ArrayList<String>();
    LineIterator iter = new LineIterator(new StringReader(in));
    while (iter.hasNext()) {
      retval.add(iter.nextLine());
    }
    return retval;
  }


  protected static void logRelayException(Exception e) {
    LOG.debug(e.getMessage());
  }

  protected static Thread startRelay(final InputStream is, final OutputStream os, final boolean closeOut) {
    Thread readingThread = new Thread(new Runnable() {
      public void run() {
        try {
          byte[] buffer = new byte[1024];
          int count;
          while ((count = is.read(buffer)) > 0) {
            os.write(buffer, 0, count);
          }
          os.flush();
        } catch (IOException e) {
          logRelayException(e);
        } finally {
          if (is != null) try { is.close(); } catch (Exception ignored) { }
          if (closeOut && os != null) try { os.close(); } catch (Exception ignored) { }
        }
      }
    });
    readingThread.start();
    return readingThread;
  }

  public void validate(File workDir, List<String> args) throws BzrExecException {
    validate(workDir, args, isExitValueValidationEnabled(), isStderrValidationEnabled());
  }

  public void validate(File workDir, List<String> args, boolean exitCheck, boolean errCheck) throws BzrExecException {
    if ((exitCheck && getExitValue() != 0) || (errCheck && !isStdErrEmpty())) {
      throw new BzrExecException(
          BzrMessages.message("bzr4j.exec.validation.error",
              getExitValue(), getStdErrAsString(), StringUtil.toString(args, "\n", "", ""), workDir));
    }
  }
}
