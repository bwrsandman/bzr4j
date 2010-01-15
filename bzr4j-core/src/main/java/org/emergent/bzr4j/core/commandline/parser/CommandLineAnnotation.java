/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.core.commandline.parser;

import org.emergent.bzr4j.core.commandline.parser.XMLAnnotateParser;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.IBazaarAnnotation;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

/**
 * <p>
 * Represent a annotation, with the limitations of the command line output
 * </p>
 *
 * @author Guillermo Gonzalez
 *
 * TODO: review
 */
class CommandLineAnnotation implements IBazaarAnnotation {

  private final String[] revisionByLine;

  private final String[] authorByLine;

  private final String[] dateByLine;

  private final String[] lines;

  private final String[] filesIds;

  private final File file;

  private final File branchRoot;

  private final int numberOflines;

  protected CommandLineAnnotation(final String[] revsionByLine, final String[] authorByLine,
      final String[] dateByLine, final String[] lines, final String[] filesIds,
      final File file,
      final File branchRoot) {
    this.revisionByLine = revsionByLine;
    this.authorByLine = authorByLine;
    this.dateByLine = dateByLine;
    this.lines = lines;
    this.filesIds = filesIds;
    this.file = file;
    this.branchRoot = branchRoot;
    numberOflines = lines.length;
  }

  public static IBazaarAnnotation getAnnotationFromXml(String annoatexmlOutput)
      throws BazaarException {
    final XMLAnnotateParser parser = new XMLAnnotateParser();
    final IBazaarAnnotation ann;
    try {
      parser.parse(annoatexmlOutput);
      ann = new CommandLineAnnotation(parser.getRevisions(), parser.getAuthors(),
          parser.getDates(), parser.getLines(), parser.getFileIds(), parser
              .getFile(), parser.getBranchRoot());
    }
    catch (XMLStreamException e) {
      throw new BazaarException(e);
    }
    catch (IOException e) {
      throw new BazaarException(e);
    }
    return ann;
  }

  /*
  * (non-Javadoc)
  *
  * @see org.emergent.bzr4j.core.IBazaarAnnotation#getAuthor(int)
  */

  public String getAuthor(int lineNumber) {
    return authorByLine[lineNumber];
  }

  /*
  * (non-Javadoc)
  *
  * @see org.emergent.bzr4j.core.IBazaarAnnotation#getDate(int)
  */

  public String getDate(int lineNumber) {
    return dateByLine[lineNumber];
  }

  /*
  * (non-Javadoc)
  *
  * @see org.emergent.bzr4j.core.IBazaarAnnotation#getNumberOfLines()
  */

  public int getNumberOfLines() { // never would be -1 constructor will never allow it
    return numberOflines;
  }

  /*
  * (non-Javadoc)
  *
  * @see org.emergent.bzr4j.core.IBazaarAnnotation#getRevision(int)
  */

  public String getRevision(int lineNumber) {
    return revisionByLine[lineNumber];
  }

  /*
  * (non-Javadoc)
  *
  * @see org.emergent.bzr4j.core.IBazaarAnnotation#getline(int)
  */

  public String getline(int lineNumber) {
    return lines[lineNumber];
  }

  /*
  * (non-Javadoc)
  *
  * @see org.emergent.bzr4j.core.IBazaarAnnotation#getFileId(int)
  */

  public String getFileId(int lineNumber) {
    return filesIds[lineNumber];
  }

  /*
  * (non-Javadoc)
  *
  * @see org.emergent.bzr4j.core.IBazaarAnnotation#getBranchRoot()
  */

  public File getBranchRoot() {
    return branchRoot;
  }

  /*
  * (non-Javadoc)
  *
  * @see org.emergent.bzr4j.core.IBazaarAnnotation#getFile()
  */

  public File getFile() {
    return file;
  }

}
