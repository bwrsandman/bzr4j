/*
 * Copyright 2010 Tripwire, Inc. All Rights Reserved.
 */
package org.emergent.bzr4j.core.tests;

import org.emergent.bzr4j.core.BzrAbstractHandler;

import java.io.File;

/**
 * @author Patrick Woodworth
 */
public class BzrTestHandler extends BzrAbstractHandler {

  public BzrTestHandler(File dir, String cmd) {
    super(dir, cmd);
  }

  @Override
  protected String getBzrExecutablePath() {
    return "bzr";
  }

  @Override
  protected void logDebug(String msg) {
    System.err.println("debug: " + msg);
  }

  @Override
  protected void logInfo(String msg) {
    System.err.println(" info: " + msg);
  }
}
