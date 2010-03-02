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

package org.emergent.bzr4j.core.debug;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.WeakHashMap;

/**
 * @author Patrick Woodworth
 */
public class DebugLog4jFactory implements DebugLogger.DebugImplFactory {

  private static final WeakHashMap<Logger, DebugLogger> sm_loggers = new WeakHashMap<Logger, DebugLogger>();

  private static final boolean sm_defaultConfig = Boolean.getBoolean("bzr4j.log4j.config.default");

  static {
    // mostly to make sure that if we can instantiate this class then we definitely have log4j as well.
    Level level = Level.DEBUG;
    assert level != null;
    if (sm_defaultConfig) {
      BasicConfigurator.configure();
    }
  }

  public DebugLogger getDebugLogger(String name) {
    Logger jdkLogger = Logger.getLogger(name);
    synchronized (sm_loggers) {
      DebugLogger retval = sm_loggers.get(jdkLogger);
      if (retval == null) {
        retval = new DebugLog4jLogger(name);
        sm_loggers.put(jdkLogger, retval);
      }
      return retval;
    }
  }
}
