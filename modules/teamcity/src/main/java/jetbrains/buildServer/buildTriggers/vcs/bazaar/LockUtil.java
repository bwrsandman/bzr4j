/*
 * Copyright (c) 2009-2010 Patrick Woodworth
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

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Patrick Woodworth
 */
public class LockUtil {

  static final ConcurrentMap<String, Lock> sm_dirLocks = new ConcurrentHashMap<String, Lock>();

  static final ConcurrentMap<String, Lock> sm_urlLocks = new ConcurrentHashMap<String, Lock>();

  public static void lockWorkDir(@NotNull File workDir) {
    getDirLock(workDir).lock();
  }

  public static void unlockWorkDir(@NotNull File workDir) {
    getDirLock(workDir).unlock();
  }

  public static void lockWorkUrl(@NotNull String workUrl) {
    getUrlLock(workUrl).lock();
  }

  public static void unlockWorkUrl(@NotNull String workUrl) {
    getUrlLock(workUrl).unlock();
  }

  public static Lock getDirLock(File dir) {
    String path = dir != null ? dir.getAbsolutePath() : ".";
    Lock lock = sm_dirLocks.get(path);
    if (lock == null) {
      lock = new ReentrantLock();
      Lock curLock = sm_dirLocks.putIfAbsent(path, lock);
      if (curLock != null) {
        lock = curLock;
      }
    }
    return lock;
  }

  public static Lock getUrlLock(String url) {
    String path = url != null ? url : ".";
    Lock lock = sm_dirLocks.get(path);
    if (lock == null) {
      lock = new ReentrantLock();
      Lock curLock = sm_dirLocks.putIfAbsent(path, lock);
      if (curLock != null) {
        lock = curLock;
      }
    }
    return lock;
  }
}
