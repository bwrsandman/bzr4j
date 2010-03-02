/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.core;

import java.io.File;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface IBazaarAnnotation {

  public String getAuthor(int lineNumber);

  public String getDate(int lineNumber);

  public String getRevision(int lineNumber);

  public String getline(int lineNumber);

  public String getFileId(int lineNumber);

  public int getNumberOfLines();

  public File getFile();

  public File getBranchRoot();
}
