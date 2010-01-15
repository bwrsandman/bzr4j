package org.emergent.bzr4j.core;

public interface IBazaarItemInfo {

  BazaarItemKind getKind();

  String getId();

  String getPath();

  BazaarStatusType getStatusKind();

}
