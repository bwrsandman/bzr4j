/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.core;

import java.io.File;
import java.util.Collection;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface IBazaarStatus {

  public String getShortStatus();

  /**
   * @return a String representing the relative (to branch root) path to the
   *         file
   */
  public String getPath();

  /**
   * @return a String representing the relative (to branch root) previous path
   *         to the file
   */
  public String getPreviousPath();

  public BazaarItemKind getOldKind();

  public BazaarItemKind getNewKind();

  /**
   * @return a File which stands for the absolute path to the branch
   */
  public File getBranchRoot();

  /**
   * @return a File binded to this status (which is relative to branch root)
   */
  public File getFile();

  /**
   * @return the previous File binded to this status (which is relative to
   *         branch root)
   */
  public File getPreviousFile();

  /**
   * @return a String which contains the absolute path to this status file
   */
  public String getAbsolutePath();

  /**
   *
   * @param a
   *            {@link BazaarStatusType}
   * @return true if this status conatins the given {@link BazaarStatusType}
   */
  public boolean contains(BazaarStatusType kind);

  /**
   * Return a Collection with all the {@link BazaarStatusType} that belongs to
   * this status object
   *
   * @return Collection<? extends BazaarItemKind>
   */
  public Collection<? extends BazaarStatusType> getStatuses();

  public boolean equals(Object obj);

  public int hashCode();

}
