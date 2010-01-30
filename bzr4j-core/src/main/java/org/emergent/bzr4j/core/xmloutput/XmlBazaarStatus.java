package org.emergent.bzr4j.core.xmloutput;

import org.emergent.bzr4j.core.BazaarItemKind;
import org.emergent.bzr4j.core.BazaarStatusType;
import org.emergent.bzr4j.core.IBazaarStatus;
import static org.emergent.bzr4j.core.utils.BzrCoreUtil.unixFilePath;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Guillermo Gonzalez
 *
 */
class XmlBazaarStatus implements IBazaarStatus {

  private final EnumSet<BazaarStatusType> statuses = EnumSet.noneOf(BazaarStatusType.class);

  private final File branchRoot;

  private final File file;

  private File m_previousFile;

  private BazaarItemKind newKind;

  private BazaarItemKind oldKind;

//  public XmlBazaarStatus(File branchRoot, File path, BazaarItemKind kind, BazaarStatusType... statusTypes) {
//    this(branchRoot, path, kind, Arrays.asList(statusTypes));
//  }

//  private XmlBazaarStatus(File branchRoot, File path, BazaarItemKind kind, List<BazaarStatusType> statusTypes) {
//    this(branchRoot, path, null, kind, null, statusTypes);
//  }

//  public XmlBazaarStatus(
//      File branchRoot,
//      File path,
//      File oldPath,
//      BazaarItemKind kind,
//      BazaarItemKind oldKind,
//      BazaarStatusType... statusTypes) {
//    this(branchRoot, path, oldPath, kind, oldKind, Arrays.asList(statusTypes));
//  }

  private XmlBazaarStatus(
      File branchRoot,
      File path,
      File oldPath,
      BazaarItemKind kind,
      BazaarItemKind oldKind,
      List<BazaarStatusType> statusTypes) {
    this.branchRoot = branchRoot;
    this.file = path;
    this.m_previousFile = oldPath;
    this.newKind = kind;
    this.oldKind = oldKind;
    this.statuses.addAll(statusTypes);
  }

  public boolean contains(BazaarStatusType kind) {
    return statuses.contains(kind);
  }

  public String getAbsolutePath() {
    return unixFilePath(new File(getBranchRoot(), getPath()));
  }

  public File getBranchRoot() {
    return branchRoot;
  }

  public File getFile() {
    return file;
  }

  public final BazaarItemKind getNewKind() {
    return newKind;
  }

  public final BazaarItemKind getOldKind() {
    return oldKind != null ? oldKind : getNewKind();
  }

  public String getPath() {
    if (file != null)
      return unixFilePath(file);
    else
      return "";
  }

  public File getPreviousFile() {
    return m_previousFile;
  }

  public String getPreviousPath() {
//    return getPreviousFile() == null ? null : getPreviousFile().getPath();
    if (m_previousFile != null)
      return unixFilePath(m_previousFile);
    else
      return "";
  }

  public String getShortStatus() {
    final StringBuilder versioned = new StringBuilder();
    final StringBuilder content = new StringBuilder();
    final StringBuilder execute = new StringBuilder();

    for (BazaarStatusType kind : statuses) {
      if (kind.getCategory() == BazaarStatusType.Category.VERSIONED) {
        versioned.append(kind.toChar());
      }
      if (kind.getCategory() == BazaarStatusType.Category.CONTENT) {
        content.append(kind.toChar());
      }
      if (kind.getCategory() == BazaarStatusType.Category.EXECUTABLE) {
        execute.append(kind.toChar());
      }
    }

    return versioned.append(content.toString()).append(execute.toString()).toString();
  }

  public Collection<? extends BazaarStatusType> getStatuses() {
    return statuses;
  }

  public void merge(IBazaarStatus status) {
    if (status.contains(BazaarStatusType.KIND_CHANGED)) {
      this.oldKind = status.getOldKind();
      this.newKind = status.getNewKind();
    }
    if (status.contains(BazaarStatusType.RENAMED)) {
      m_previousFile = status.getPreviousFile();
    }
    statuses.addAll(status.getStatuses());
  }

  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(getShortStatus()).append(" ");
    sb.append(getPath()).append(" ");
    if (!"".equals(getNewKind()))
      sb.append("newkind: ").append(getNewKind());
    if (!"".equals(getOldKind()))
      sb.append("oldkind: ").append(getOldKind());
    if (!"".equals(getPreviousPath()))
      sb.append("prevPath: ").append(getPreviousPath());
    return sb.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    } else if (obj instanceof IBazaarStatus) {
      // this comparison is done only by path (bazaar spit out duplicated
      // status for one file i.e: when a file was modified and has
      // conflicts)
      boolean equalPath = ((IBazaarStatus)obj).getPath().equals(this.getPath());
      return equalPath && statuses.containsAll(((IBazaarStatus)obj).getStatuses());
    } else {
      return super.equals(obj);
    }

  }

  @Override
  public int hashCode() {
    final int PRIME = 31;
    int result = super.hashCode();
    result = PRIME * result + ((newKind == null) ? 0 : newKind.hashCode());
    result = PRIME * result + ((oldKind == null) ? 0 : oldKind.hashCode());
    result = PRIME * result + ((m_previousFile == null) ? 0 : m_previousFile.hashCode());
    result = PRIME * result + ((file == null) ? 0 : file.hashCode());
    result = PRIME * result + ((branchRoot == null) ? 0 : branchRoot.hashCode());
    return result;
  }

  public static Set<IBazaarStatus> orderAndCleanup(List<IBazaarStatus> statuses) {
    final Map<String, List<IBazaarStatus>> map = new HashMap<String, List<IBazaarStatus>>();
    for (IBazaarStatus status : statuses) {
      List<IBazaarStatus> list = map.get(status.getPath());
      if (list == null) {
        list = new ArrayList<IBazaarStatus>();
        map.put(status.getPath(), list);
      }
      list.add(status);
    }
    return unifyStatuses(map);
  }

  private static Set<IBazaarStatus> unifyStatuses(Map<String, List<IBazaarStatus>> map) {
    final Set<String> keySet = map.keySet();
    final Set<IBazaarStatus> set = new LinkedHashSet<IBazaarStatus>(keySet.size());
    for (String key : keySet) {
      XmlBazaarStatus keeped = null;
      for (IBazaarStatus status : map.get(key)) {
        if (keeped == null) {
          keeped = (XmlBazaarStatus)status;
        } else {
          keeped.merge(status);
        }
      }
      set.add(keeped);
    }
    return set;
  }

  public static class Builder {

    private File m_branchRoot;
    private File m_path;
    private File m_oldPath;
    private BazaarItemKind m_kind;
    private BazaarItemKind m_oldKind;
    private List<BazaarStatusType> m_statusTypes;

    public Builder setBranchRoot(File branchRoot) {
      m_branchRoot = branchRoot;
      return this;
    }

    public Builder setPath(File path) {
      m_path = path;
      return this;
    }

    public Builder setOldPath(File oldPath) {
      m_oldPath = oldPath;
      return this;
    }

    public Builder setKind(BazaarItemKind kind) {
      m_kind = kind;
      return this;
    }

    public Builder setOldKind(BazaarItemKind oldKind) {
      m_oldKind = oldKind;
      return this;
    }

    public Builder addStatusTypes(BazaarStatusType... statusTypes) {
      if (m_statusTypes == null)
        m_statusTypes = new ArrayList<BazaarStatusType>();
      m_statusTypes.addAll(Arrays.asList(statusTypes));
      return this;
    }

    public Builder setStatusTypes(List<BazaarStatusType> statusTypes) {
      m_statusTypes = statusTypes;
      return this;
    }

    public IBazaarStatus createBazaarStatus() {
      return new XmlBazaarStatus(m_branchRoot, m_path, m_oldPath, m_kind, m_oldKind, m_statusTypes);
    }
  }
}
