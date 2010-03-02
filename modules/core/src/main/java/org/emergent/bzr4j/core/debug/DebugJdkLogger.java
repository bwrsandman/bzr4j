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

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
* @author Patrick Woodworth
*/
final class DebugJdkLogger implements DebugLogger {

  private final Logger m_delegate;

  DebugJdkLogger(String name) {
    m_delegate = Logger.getLogger(name);
  }

  public void debug(Object o) {
    log(Level.FINE, o);
  }

  public void debug(String msg, Object... params) {
    log(Level.FINE, params);
  }

  public void debug(Throwable e, String msg) {
    log(Level.FINE, e, msg);
  }

  public void warn(Object o) {
    log(Level.WARNING, o);
  }

  public void warn(String msg, Object... params) {
    log(Level.WARNING, params);
  }

  public void warn(Throwable e, String msg) {
    log(Level.WARNING, e, msg);
  }

  public void error(Object o) {
    log(Level.SEVERE, o);
  }

  public void error(String msg, Object... params) {
    log(Level.SEVERE, params);
  }

  public void error(Throwable e, String msg) {
    log(Level.SEVERE, e, msg);
  }

  public void debugf(String msg, Object... params) {
    if (!m_delegate.isLoggable(Level.FINE))
      return;
    else {
      System.out.println(String.format(msg, params));
      if (true)
        return;
    }
    LogRecord lr = new DebugJdkLogRecord(Level.FINE, String.format(msg, params));
    lr.setLoggerName(m_delegate.getName());
    m_delegate.log(lr);
  }

  public void log(Level level, Object o) {
    if (!m_delegate.isLoggable(level))
      return;
    if (o instanceof Throwable)
      log(level, (Throwable)o, "Something was thrown!");
    else
      log(level, "{0}", o);
  }

  public void log(Level level, String msg, Object... params) {
    if (!m_delegate.isLoggable(level))
      return;
    LogRecord lr = new DebugJdkLogRecord(level, msg);
    lr.setParameters(params);
    lr.setLoggerName(m_delegate.getName());
    m_delegate.log(lr);
  }

  public void log(Level level, Throwable e, String msg) {
    if (!m_delegate.isLoggable(level))
      return;
    LogRecord lr = new DebugJdkLogRecord(level, msg);
    lr.setThrown(e);
    lr.setLoggerName(m_delegate.getName());
    m_delegate.log(lr);
  }

  public void logf(Level level, String msg, Object... params) {
    if (!m_delegate.isLoggable(level))
      return;
    LogRecord lr = new DebugJdkLogRecord(level, String.format(msg, params));
    lr.setLoggerName(m_delegate.getName());
    m_delegate.log(lr);
  }

  public boolean isDebug() {
    return m_delegate.isLoggable(Level.FINE);
  }

  public void addHandler(Handler handler) {
    m_delegate.addHandler(handler);
  }

  public void setLevel(Level newLevel) throws SecurityException {
    m_delegate.setLevel(newLevel);
  }

  public boolean isLoggable(Level level) {
    return m_delegate.isLoggable(level);
  }

  public void log(LogRecord lr) {
    m_delegate.log(lr);
  }

  public String getName() {
    return m_delegate.getName();
  }
}
