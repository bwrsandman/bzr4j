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

package org.emergent.bzr4j.core.testutil;

import org.emergent.bzr4j.core.cli.BzrAbstractHandler;
import org.emergent.bzr4j.core.cli.BzrHandlerException;
import org.emergent.bzr4j.core.cli.BzrHandlerResult;

import java.io.File;
import java.util.List;

/**
 * @author Patrick Woodworth
 */
public class BzrTestHandler extends BzrAbstractHandler {

  public BzrTestHandler(File dir, String cmd) {
    super(dir, cmd);
  }

  public BzrHandlerResult exectest() throws BzrHandlerException {
    return exectest(true);
  }

  public BzrHandlerResult exectest(boolean stdErrValidation) throws BzrHandlerException {
    return exectest(new BzrHandlerResult(), stdErrValidation);
  }

  public BzrHandlerResult exectest(BzrHandlerResult result, boolean stdErrValidation) throws BzrHandlerException {
    if (!stdErrValidation)
      setStderrValidationEnabled(false);
    return exec(result);
  }

  @Override
  public BzrTestHandler addArguments(String... args) {
    return (BzrTestHandler)super.addArguments(args);
  }

  @Override
  public BzrTestHandler addArguments(List<String> args) {
    return (BzrTestHandler)super.addArguments(args);
  }

  @Override
  protected String getBzrExecutablePath() {
    return "bzr";
  }

  @Override
  protected void logDebug(String msg) {
    System.err.println("debug: " + msg);
  }

  @Override
  protected void logInfo(String msg) {
    System.err.println(" info: " + msg);
  }
}
