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

package org.emergent.bzr4j.core.testutil;

import org.emergent.bzr4j.core.BazaarRoot;
import org.emergent.bzr4j.core.cli.BzrAbstractExec;
import org.emergent.bzr4j.core.cli.BzrAbstractResult;
import org.emergent.bzr4j.core.cli.BzrExecException;
import org.emergent.bzr4j.core.cli.BzrStandardResult;
import org.emergent.bzr4j.core.cli.BzrXmlResult;

import java.io.File;
import java.util.List;

/**
 * @author Patrick Woodworth
 */
public class BzrTestExec extends BzrAbstractExec {

  public BzrTestExec(BazaarRoot root, String cmd) {
    super(root, cmd);
  }

  public BzrAbstractResult exectest() throws BzrExecException {
    return exectest(true);
  }

  public BzrAbstractResult exectest(boolean stdErrValidation) throws BzrExecException {
    return exectest(stdErrValidation, true);
  }

  public BzrAbstractResult exectest(boolean stdErrValidation, boolean exitValueValidation) throws BzrExecException {
    return exectest(new BzrStandardResult(), stdErrValidation, exitValueValidation);
  }

  public BzrAbstractResult exectest(BzrXmlResult result, boolean stdErrValidation, boolean exitValidation)
      throws BzrExecException {
    if (!stdErrValidation)
      setStderrValidationEnabled(false);
    if (!exitValidation)
      setExitValueValidationEnabled(false);
    return exec(result);
  }

  public BzrAbstractResult exectest(BzrStandardResult result, boolean stdErrValidation, boolean exitValidation)
      throws BzrExecException {
    if (!stdErrValidation)
      setStderrValidationEnabled(false);
    if (!exitValidation)
      setExitValueValidationEnabled(false);
    return exec(result);
  }

  @Override
  public BzrTestExec addArguments(String... args) {
    return (BzrTestExec)super.addArguments(args);
  }

  @Override
  public BzrTestExec addArguments(List<String> args) {
    return (BzrTestExec)super.addArguments(args);
  }

  @Override
  protected String getBzrExecutablePath() {
    return "bzr";
  }
}
