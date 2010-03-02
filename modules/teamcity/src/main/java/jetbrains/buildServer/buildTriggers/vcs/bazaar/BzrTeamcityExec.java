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

package jetbrains.buildServer.buildTriggers.vcs.bazaar;

import com.intellij.openapi.diagnostic.Logger;
import org.emergent.bzr4j.core.BazaarRoot;
import org.emergent.bzr4j.core.cli.BzrAbstractExec;
import org.emergent.bzr4j.core.cli.BzrAbstractResult;
import org.emergent.bzr4j.core.cli.BzrExecException;
import org.emergent.bzr4j.core.cli.BzrStandardResult;
import org.emergent.bzr4j.core.cli.BzrXmlResult;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Patrick Woodworth
 */
public class BzrTeamcityExec extends BzrAbstractExec {

  private static final Logger LOG = Logger.getInstance(BzrTeamcityExec.class.getName());

  private static final ConcurrentMap<String, Lock> sm_workDirLocks = new ConcurrentHashMap<String, Lock>();

  private final Settings m_settings;

  public BzrTeamcityExec(Settings settings, String cmd, String... args) {
    super(BazaarRoot.createRootLocation(getBzrRoot(settings)), cmd);
    m_settings = settings;
    addArguments(args);
  }

  @Override
  protected BzrAbstractResult exec(BzrAbstractResult result) throws BzrExecException {
    final Lock lock = getWorkDirLock(getWorkingDir());
    lock.lock();
    try {
      return super.exec(result);
    } finally {
      lock.unlock();
    }
  }

  public BzrAbstractResult exectc(BzrAbstractResult result) throws BzrExecException {
    return exec(result);
  }

  public BzrAbstractResult exectc(DefaultHandler handler) throws BzrExecException {
    return exec(BzrXmlResult.createBzrXmlResult(handler));
  }

  public BzrStandardResult exectc(boolean validate) throws BzrExecException {
    return (BzrStandardResult)exectc(new BzrStandardResult());
  }

  public BzrStandardResult exectc() throws BzrExecException {
    return exectc(true);
  }

  @Override
  protected String getBzrExecutablePath() {
    return m_settings.getBzrCommandPath();
  }

  private static File getBzrRoot(Settings settings) {
    return settings.getLocalRepositoryDir();
  }

  public static Lock getWorkDirLock(File workDir) {
    String path = workDir != null ? workDir.getAbsolutePath() : ".";
    Lock lock = sm_workDirLocks.get(path);
    if (lock == null) {
      lock = new ReentrantLock();
      Lock curLock = sm_workDirLocks.putIfAbsent(path, lock);
      if (curLock != null) {
        lock = curLock;
      }
    }
    return lock;
  }
}
