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

import com.intellij.openapi.util.text.LineTokenizer;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

public final class ShellCommandResult {

  static final ShellCommandResult EMPTY = new ShellCommandResult(new StringWriter(), new StringWriter());

  private final StringWriter out;
  private final StringWriter err;

  private List<String> outLines;
  private List<String> errLines;
  private int m_exitValue;

  public ShellCommandResult(StringWriter out, StringWriter err) {
    this.out = out;
    this.err = err;
  }

  public List<String> getOutputLines() {
    if (outLines == null) {
      outLines = Arrays.asList(LineTokenizer.tokenize(out.getBuffer(), false));
    }
    return outLines;
  }

  public List<String> getErrorLines() {
    if (errLines == null) {
      errLines = Arrays.asList(LineTokenizer.tokenize(err.getBuffer(), false));
    }
    return errLines;
  }

  public String getRawStdOut() {
    return out.toString();
  }

  public String getRawStdErr() {
    return err.toString();
  }

  public boolean isStdOutEmpty() {
    return out.getBuffer().length() == 0;
  }

  public boolean isStdErrEmpty() {
    return err.getBuffer().length() == 0;
  }

  public int getExitValue() {
    return m_exitValue;
  }

  public void setExitValue(int exitValue) {
    m_exitValue = exitValue;
  }
}
