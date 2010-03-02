package org.emergent.bzr4j.core.debug;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author Patrick Woodworth
 */
public interface DebugLogger {

  String getName();

  void debug(Object o);

  void debug(Throwable e, String msg);

  boolean isDebug();

  void warn(Object o);

  void warn(Throwable e, String msg);

  void error(Object o);

  void error(Throwable e, String msg);

  interface DebugImplFactory {
    public DebugLogger getDebugLogger(String name);
  }
}
