package org.emergent.bzr4j.core;

public enum BazaarItemKind {

  conflict,
  file,
  directory,
  symlink;

  public static BazaarItemKind fromString(String name) {
    return valueOf(name);
  }
}
