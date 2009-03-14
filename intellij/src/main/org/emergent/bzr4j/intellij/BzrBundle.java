/*
 * Copyright (c) 2005 JetBrains s.r.o. All Rights Reserved.
 */
package org.emergent.bzr4j.intellij;

import com.intellij.CommonBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

public class BzrBundle
{
  private static Reference<ResourceBundle> ourBundle;

  @NonNls private static final String BUNDLE = "org.emergent.bzr4j.intellij.BzrBundle";

  private BzrBundle() {
  }

  public static String message(@PropertyKey(resourceBundle = BUNDLE)String key, Object... params) {
    return CommonBundle.message(getBundle(), key, params);
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
