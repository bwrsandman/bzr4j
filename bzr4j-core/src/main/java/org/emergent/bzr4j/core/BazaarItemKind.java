package org.emergent.bzr4j.core;

public enum BazaarItemKind {

  FILE("file"),
  DIRECTORY("directory"),
  SYMLINK("symlink");

  private String bzrKind;

  private BazaarItemKind(String bzrKind) {
    this.bzrKind = bzrKind;
  }

  @Override
  public String toString() {
    return bzrKind;
  }

  public static BazaarItemKind fromString(String name) {
    for (BazaarItemKind item : BazaarItemKind.values()) {
      if (item.toString().equals(name))
        return item;
    }
    throw new EnumConstantNotPresentException(BazaarItemKind.class, name);
  }
}
