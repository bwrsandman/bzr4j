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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author Patrick Woodworth
 */
public class BzrHandlerResult {

  private static final String OK_STDERR_MSG = "No handlers could be found for logger \"bzr\"";

  private final ByteArrayOutputStream m_out = new ByteArrayOutputStream();

  private final ByteArrayOutputStream m_err = new ByteArrayOutputStream();

  private final Charset m_charset;

  private int m_exitValue = -1;

  public BzrHandlerResult() {
    this(null);
  }

  public BzrHandlerResult(Charset charset) {
    m_charset = charset != null ? charset : Charset.defaultCharset();
  }

  public int getExitValue() {
    return m_exitValue;
  }

  public void setExitValue(int exitValue) {
    m_exitValue = exitValue;
  }

  public byte[] getByteOut() {
    return m_out.toByteArray();
  }

  public String getStdOutAsString() {
    try {
      return new String(m_out.toByteArray(), m_charset.name());
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException(e); // should never happen
    }
  }

  public String getStdErrAsString() {
    return m_err.toString();
  }

  public boolean isStdOutEmpty() {
    return m_out.size() == 0;
  }

  public boolean isStdErrEmpty() {
    return m_err.size() == 0 || OK_STDERR_MSG.equals(getStdErrAsString().trim());
  }

  public String getStdout() {
    return getStdOutAsString();
  }

  protected void logRelayException(IOException e) {
//    logInfo(e.getMessage());
  }

  Thread startRelay(final InputStream is, final boolean err) {
    Thread readingThread = new Thread(new Runnable() {
      public void run() {
        OutputStream os = err ? m_err : m_out;
        byte[] buffer = new byte[1024];
        int count;
        try {
          while ((count = is.read(buffer)) > 0) {
            os.write(buffer, 0, count);
          }
          os.flush();
        } catch (IOException e) {
          logRelayException(e);
        } finally {
          if (is != null) try { is.close(); } catch (Exception ignored) { }
        }
      }
    });
    readingThread.start();
    return readingThread;
  }
}
