package org.emergent.bzr4j.core;

import static org.emergent.bzr4j.core.utils.BzrCoreUtil.unixFilePath;

import java.io.File;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

/**
 * @author Guillermo Gonzalez
 *
 */
public class BazaarStatus implements IBazaarStatus {

  protected final EnumSet<BazaarStatusType> statuses = EnumSet.noneOf(BazaarStatusType.class);

  protected final File branchRoot;

  protected final File file;

  protected File m_previousFile;

  public BazaarStatus(File path, File branchRoot) {
    this.branchRoot = branchRoot;
    this.file = path;
  }

  public BazaarStatus(final List<BazaarStatusType> statuses, File path, File branchRoot) {
    this(path, branchRoot);
    this.statuses.addAll(statuses);
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

  public BazaarStatusKind getNewKind() {
    return null;
  }

  public BazaarStatusKind getOldKind() {
    return null;
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
    return getPreviousFile() == null ? null : getPreviousFile().getPath();
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

  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(getShortStatus()).append(" ");
    sb.append(getPath()).append(" ");
    return sb.toString();
  }

}
