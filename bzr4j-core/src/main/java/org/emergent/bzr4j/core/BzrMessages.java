/*
 * Copyright 2010 Tripwire, Inc. All Rights Reserved.
 */
package org.emergent.bzr4j.core;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * @author Patrick Woodworth
 */
public class BzrMessages {

  private static Reference<ResourceBundle> ourBundle;

  private static final String BUNDLE = "org.emergent.bzr4j.core.BzrMessages";

  private BzrMessages() {
  }

  public static String message(String key, Object... params) {
    return MessageFormat.format(getBundle().getString(key), params);
  }

  private static ResourceBundle getBundle() {
    ResourceBundle bundle = null;
    if (ourBundle != null) bundle = ourBundle.get();
    if (bundle == null) {
      bundle = ResourceBundle.getBundle(BUNDLE);
      ourBundle = new SoftReference<ResourceBundle>(bundle);
    }
    return bundle;
  }
}
