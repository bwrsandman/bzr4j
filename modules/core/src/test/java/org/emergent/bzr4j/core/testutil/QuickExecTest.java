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

import org.emergent.bzr4j.core.testutil.QuickExec;
import org.emergent.bzr4j.core.xmloutput.QuickTestBuilder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.IOException;

/**
 * @author Patrick Woodworth
 */
public abstract class QuickExecTest {

  protected static final String TEST_BRANCH_URLBASE =
      System.getProperty("bzr4j.test.urlbase","lp:~sixseve/+junk/testdata");

  private static QuickTestBuilder sm_quickBuilder;

  @BeforeClass(alwaysRun = true)
  public void setupClass() throws Exception {
    sm_quickBuilder = new QuickTestBuilder();
    setupWorkspaces(getQuickBuilder());
  }

  @AfterClass(alwaysRun = true)
  public void teardownClass() throws IOException {
    QuickExec.cleanupAll();
    sm_quickBuilder = null;
  }

  public static QuickExec getQuickExec() {
    return getQuickBuilder().getQexec();
  }

  public static QuickTestBuilder getQuickBuilder() {
    return sm_quickBuilder;
  }

  public abstract void setupWorkspaces(QuickTestBuilder qbuilder) throws Exception;
}
