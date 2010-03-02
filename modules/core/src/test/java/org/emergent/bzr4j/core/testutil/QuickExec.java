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

import org.apache.commons.io.FileUtils;
import org.emergent.bzr4j.core.BazaarRoot;
import org.emergent.bzr4j.core.cli.BzrExecFactory;
import org.emergent.bzr4j.core.utils.IOUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick Woodworth
 */
public class QuickExec implements BzrExecFactory<BzrTestExec> {

  private final File m_initialCwd;

  private List<File> m_dirStack = new ArrayList<File>();

  public QuickExec() throws IOException {
    this(createTempDir());
  }

  public QuickExec(File cwd) {
    if (cwd == null || cwd.getPath().equals("."))
      throw new IllegalArgumentException("must use a test directory!");
    m_initialCwd = IOUtil.toCanonical(cwd);
    if (!m_initialCwd.isDirectory() && !m_initialCwd.mkdirs()) {
      throw new IllegalStateException("Could not create \"" + m_initialCwd + "\"");
    }
    pushd(m_initialCwd);
    System.err.printf("Created QuickExec (%s)\n", m_initialCwd);
  }

  public void pushd(File dir) {
    m_dirStack.add(0,dir);
  }

  public void pushd(String dir) {
    m_dirStack.add(0,new File(getCwd(), dir));
  }

  public File popd() {
    return m_dirStack.remove(0);
  }

  public void popdAll() {
    while (m_dirStack.size() > 1)
      popd();
  }

  public File getCwd() {
    return m_dirStack.get(0);
  }

  public void cleanup() throws IOException {
    popdAll();
    File theDir = getCwd();
    System.err.printf("Deleting: \"%s\"\n", theDir);
    FileUtils.deleteDirectory(theDir);
  }

  public BzrTestExec createCommand(String operation) {
    return new BzrTestExec(BazaarRoot.createRootLocation(getCwd()), operation);
  }

  public static void cleanupAll() throws IOException {
    File theDir = getBaseWorkDir();
    System.err.printf("Deleting: \"%s\"\n", theDir);
    FileUtils.deleteDirectory(theDir);
  }

  private static File getBaseWorkDir() {
    return new File(System.getProperty("java.io.tmpdir"), "bzr4j-tests/workng");
  }

  private static File createTempDir() throws IOException {
    File baseDir = getBaseWorkDir();
    baseDir.mkdirs();
    File retval = File.createTempFile("testws", "", baseDir);
    retval.delete();
    return retval;
  }
}
