package org.emergent.bzr4j.core.debug;

import java.util.WeakHashMap;
import java.util.logging.Logger;

/**
 * @author Patrick Woodworth
 */
public class DebugManager {

  public static final boolean DEBUG_ENABLED = true;

  private static final DebugLogger NULL_LOGGER_SINGLETON = new NullLoggerImpl();

  private static final DebugLogger.DebugImplFactory sm_implFactory;

  static {
    DebugLogger.DebugImplFactory implFactory = new DebugLogger.DebugImplFactory() {

      private final WeakHashMap<Logger, DebugLogger> sm_loggers = new WeakHashMap<Logger, DebugLogger>();

      public DebugLogger getDebugLogger(String name) {
        Logger jdkLogger = Logger.getLogger(name);
        synchronized (sm_loggers) {
          DebugLogger retval = sm_loggers.get(jdkLogger);
          if (retval == null) {
            retval = new DebugJdkLogger(name);
            sm_loggers.put(jdkLogger, retval);
          }
          return retval;
        }
      }
    };
    try {
      Class clazz = DebugManager.class.getClassLoader().loadClass("org.emergent.bzr4j.core.debug.DebugLog4jFactory");
      implFactory = (DebugLogger.DebugImplFactory)clazz.newInstance();
    } catch (Exception ignored) {
      ignored.printStackTrace();
    }
    sm_implFactory = implFactory;
  }

  @SuppressWarnings({ "PointlessBooleanExpression", "ConstantConditions" })
  public static DebugLogger getLogger(Class clazz) {
    return getLogger(clazz.getName());
  }

  @SuppressWarnings({ "PointlessBooleanExpression", "ConstantConditions" })
  public static DebugLogger getLogger(String name) {
    if (!DEBUG_ENABLED)
      return NULL_LOGGER_SINGLETON;

    return sm_implFactory.getDebugLogger(name);
  }

  /**
   * A logger that outputs nothing.
   */
  private final static class NullLoggerImpl implements DebugLogger {

    public String getName() {
      return null;
    }

    public boolean isDebug() {
      return false;
    }

    public void debug(Object o) {
    }

    public void debug(Throwable e, String msg) {
    }

    public void warn(Object o) {
    }

    public void warn(Throwable e, String msg) {
    }

    public void error(Object o) {
    }

    public void error(Throwable e, String msg) {
    }
  }
}
