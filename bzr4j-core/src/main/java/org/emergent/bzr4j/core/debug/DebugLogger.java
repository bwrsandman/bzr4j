package org.emergent.bzr4j.core.debug;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author Patrick Woodworth
 */
public interface DebugLogger {

  String getName();

  public void debug(Object o);

  public void debug(Throwable e, String msg);

  public boolean isDebug();

  void error(Object o);

  void error(Throwable e, String msg);

  interface DebugImplFactory {
    public DebugLogger getDebugLogger(String name);
  }
}
