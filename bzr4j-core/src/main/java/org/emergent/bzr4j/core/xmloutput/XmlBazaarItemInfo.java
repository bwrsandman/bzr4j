package org.emergent.bzr4j.core.xmloutput;

import org.emergent.bzr4j.core.BazaarItemKind;
import org.emergent.bzr4j.core.BazaarStatusType;
import org.emergent.bzr4j.core.IBazaarItemInfo;

class XmlBazaarItemInfo implements IBazaarItemInfo {

  private String id;

  private String path;

  private BazaarItemKind kind;

  private BazaarStatusType statusKind;

  public XmlBazaarItemInfo(BazaarItemKind kind, String id, String path) {
    this.kind = kind;
    this.id = id;
    this.path = path;
  }

  public XmlBazaarItemInfo(BazaarItemKind kind, String id, String path, BazaarStatusType type) {
    this(kind, id, path);
    this.statusKind = type;
  }

  public String getId() {
    return id;
  }

  public BazaarItemKind getKind() {
    return kind;
  }

  public String getPath() {
    return path;
  }

  public BazaarStatusType getStatusKind() {
    return statusKind;
  }
}
