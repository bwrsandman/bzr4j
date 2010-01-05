// Copyright 2009 Victor Iacoban
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software distributed under
// the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific language governing permissions and
// limitations under the License.
package org.emergent.bzr4j.intellij.command;

import com.intellij.openapi.diagnostic.Logger;
import org.apache.commons.io.IOUtils;
import org.emergent.bzr4j.intellij.BzrVcsMessages;
import org.emergent.bzr4j.utils.StringUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;

public final class ShellCommand {

  private static final Logger LOG = Logger.getInstance(ShellCommand.class.getName());

  private static final int BUFFER_SIZE = 1024;

  private Charset m_charset;

  private boolean m_bad;

  private boolean m_stderrValidationEnabled = true;

  private boolean m_exitValueValidationEnabled = true;

  public ShellCommand() {
    this(Charset.defaultCharset());
  }

  public ShellCommand(Charset charset) {
    m_charset = charset;
  }

  public ShellCommandResult execute(File dir, List<String> commandLine) throws ShellCommandException {
    if (commandLine == null || commandLine.isEmpty()) {
      throw new IllegalArgumentException("commandLine is empty");
    }
    StringWriter out = new StringWriter();
    StringWriter err = new StringWriter();
    ShellCommandResult result = new ShellCommandResult(out, err);
    Process process = null;
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(commandLine);
      if (dir != null) {
        processBuilder = processBuilder.directory(dir);
      }
      process = processBuilder.start();
      Thread outReaderThread = startReader(
          new InputStreamReader(process.getInputStream(), m_charset), out
      );
      Thread errReaderThread = startReader(
          new InputStreamReader(process.getErrorStream()), err
      );
      int exitValue = process.waitFor();
      result.setExitValue(exitValue);
      outReaderThread.join();
      errReaderThread.join();
      if ((isExitValueValidationEnabled() && exitValue != 0) || (isStderrValidationEnabled() && !result.isStdErrEmpty())) {
        throw new ShellCommandException(BzrVcsMessages.message("bzr4intellij.exec.validation.error",
            exitValue, result.getRawStdErr(), StringUtil.toString(commandLine,"\n","",""), dir));
      }
      return result;
    } catch (IOException e) {
      throw new ShellCommandException(e);
    } catch (InterruptedException e) {
      throw new ShellCommandException(e);
    } finally {
      if (process != null)
        try {
          process.destroy();
        } catch (Exception ignored) {
        }
    }
  }

  private Thread startReader(final InputStreamReader in, final Writer writer) {
    Thread readingThread = new Thread(new Runnable() {
      public void run() {
        char[] buffer = new char[BUFFER_SIZE];
        int count;
        try {
          while ((count = in.read(buffer)) > 0) {
            writer.write(buffer, 0, count);
          }
          writer.flush();
        } catch (IOException e) {
          LOG.info(e.getMessage());
        } finally {
          IOUtils.closeQuietly(in);
        }
      }
    });
    readingThread.start();
    return readingThread;
  }

  public boolean isBad() {
    return m_bad;
  }

  public void setBad(boolean bad) {
    m_bad = bad;
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
}
