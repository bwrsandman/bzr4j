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

import org.apache.log4j.Logger;

/**
* @author Patrick Woodworth
*/
final class DebugLog4jLogger implements DebugLogger {

  private final Logger m_delegate;

  DebugLog4jLogger(String name) {
    m_delegate = Logger.getLogger(name);
  }

  public String getName() {
    return m_delegate.getName();
  }

  public boolean isDebug() {
    return m_delegate.isDebugEnabled();
  }

  public void debug(Object message) {
    m_delegate.debug(message);
  }

  public void debug(Throwable e, String msg) {
    m_delegate.debug(msg,e);
  }

  public void warn(Object message) {
    m_delegate.warn(message);
  }

  public void warn(Throwable e, String msg) {
    m_delegate.warn(msg,e);
  }

  public void error(Object o) {
    m_delegate.error(o);
  }

  public void error(Throwable e, String msg) {
    m_delegate.error(msg, e);
  }
}
