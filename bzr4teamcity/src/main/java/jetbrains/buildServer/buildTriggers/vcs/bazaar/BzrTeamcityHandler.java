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

package jetbrains.buildServer.buildTriggers.vcs.bazaar;

import com.intellij.openapi.diagnostic.Logger;
import org.emergent.bzr4j.core.BzrAbstractHandler;
import org.emergent.bzr4j.core.BzrHandlerException;
import org.emergent.bzr4j.core.BzrHandlerResult;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Patrick Woodworth
 */
public class BzrTeamcityHandler extends BzrAbstractHandler {

  private static final Logger LOG = Logger.getInstance(BzrTeamcityHandler.class.getName());

  private static final ConcurrentMap<String, Lock> sm_workDirLocks = new ConcurrentHashMap<String, Lock>();

  private final Settings m_settings;

  public BzrTeamcityHandler(Settings settings, String cmd, String... args) {
    super(getBzrRoot(settings), cmd);
    m_settings = settings;
    addArguments(args);
  }

  @Override
  protected BzrHandlerResult exec(BzrHandlerResult result) throws BzrHandlerException {
    final Lock lock = getWorkDirLock(getDir());
    lock.lock();
    try {
      return super.exec(result);
    } finally {
      lock.unlock();
    }
  }

  public BzrHandlerResult exectc(boolean validate) throws BzrHandlerException {
    return exec(new BzrHandlerResult());
  }

  public BzrHandlerResult exectc() throws BzrHandlerException {
    return exectc(true);
  }

  @Override
  protected String getBzrExecutablePath() {
    return m_settings.getBzrCommandPath();
  }

  @Override
  protected void logDebug(String msg) {
    LOG.debug(msg);
  }

  @Override
  protected void logInfo(String msg) {
    LOG.info(msg);
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
