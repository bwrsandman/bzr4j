/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.core.commandline.parser;

import org.emergent.bzr4j.core.BazaarStatus;
import org.emergent.bzr4j.core.BazaarStatusKind;
import org.emergent.bzr4j.core.BazaarStatusType;
import org.emergent.bzr4j.core.IBazaarStatus;
import static org.emergent.bzr4j.core.utils.BzrCoreUtil.unixFilePath;

import java.io.File;

/**
 * @author Guillermo Gonzalez
 *
 */
class CommandLineStatus extends BazaarStatus {

  private File previousFile;

  private BazaarStatusKind oldKind, newKind;

  public CommandLineStatus(BazaarStatusType statusKind, File path, File previousPath,
      BazaarStatusKind newKind, BazaarStatusKind oldKind, File branchRoot) {
    super(path, branchRoot);
    this.previousFile = previousPath;
    this.newKind = newKind;
    this.oldKind = oldKind;
    this.statuses.add(statusKind);
  }

  public final BazaarStatusKind getNewKind() {
    return newKind;
  }

  public final BazaarStatusKind getOldKind() {
    return oldKind != null ? oldKind : getNewKind();
  }

  public final String getPreviousPath() {
    if (previousFile != null)
      return unixFilePath(previousFile);
    else
      return "";
  }

  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(super.toString());
    if (!"".equals(getNewKind()))
      sb.append("newkind: ").append(getNewKind());
    if (!"".equals(getOldKind()))
      sb.append("oldkind: ").append(getOldKind());
    if (!"".equals(getPreviousPath()))
      sb.append("prevPath: ").append(getPreviousPath());
    return sb.toString();
  }

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
    result = PRIME * result + ((previousFile == null) ? 0 : previousFile.hashCode());
    result = PRIME * result + ((file == null) ? 0 : file.hashCode());
    result = PRIME * result + ((branchRoot == null) ? 0 : branchRoot.hashCode());
    return result;
  }

  public File getPreviousFile() {
    return previousFile;
  }

  public final void merge(IBazaarStatus status) {
    if (status.contains(BazaarStatusType.KIND_CHANGED)) {
      this.oldKind = status.getOldKind();
      this.newKind = status.getNewKind();
    }
    if (status.contains(BazaarStatusType.RENAMED)) {
      previousFile = status.getPreviousFile();
    }
    statuses.addAll(status.getStatuses());
  }

}
