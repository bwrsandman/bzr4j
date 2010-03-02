package org.emergent.bzr4j.core.cli;

import org.emergent.bzr4j.core.debug.DebugLogger;
import org.emergent.bzr4j.core.debug.DebugManager;

/**
 * @author Patrick Woodworth
 */
public abstract class BzrExecService<E extends BzrAbstractExec> {

  private static final DebugLogger LOG = DebugManager.getLogger(BzrExecService.class);

  public <T extends BzrAbstractResult> T execute(E shellCmd, T result) {
    return execute(shellCmd, result, result);
  }

  public <T extends BzrAbstractResult> T execute(E shellCmd, T result, T defaultResult) {
    try {
      shellCmd.exec(result);
      return result;
    } catch (BzrExecException e) {
      showError(e);
//      LOG.error(e, "BzrExecException");
    }
    return defaultResult;
  }

  public <T extends BzrAbstractResult> T executeUnsafe(E shellCmd, T result) throws BzrExecException {
    return executeUnsafe(shellCmd, result, result);
  }

  public <T extends BzrAbstractResult> T executeUnsafe(E shellCmd, T result, T defaultResult) throws BzrExecException {
    shellCmd.exec(result);
    return result;
  }

  protected void showError(Throwable e) {
  }
}
