/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.core.utils;

import org.emergent.bzr4j.core.BazaarRevision;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Properties;
import java.util.TreeMap;

/**
 * @author Guillermo Gonzalez
 * @author Patrick Woodworth
 */
public final class BzrCoreUtil {

  private static final char UNIX_SEPARATOR = '/';

  private static final char WINDOWS_SEPARATOR = '\\';

  public static boolean isWindows() {
    return WINDOWS_SEPARATOR == File.separatorChar;
  }

  /**
   * Get relative path
   *
   * @param root a root path
   * @param path a path to file (possibly deleted file)
   * @return a relative path
   * @throws IllegalArgumentException if path is not under root.
   */
  public static String relativePath(final File root, File path) {
    String rc = FileUtil.getRelativePath(root, path);
    if (rc == null) {
      throw new IllegalArgumentException("The file " + path + " cannot be made relative to " + root);
    }
    return rc.replace(File.separatorChar, '/');
  }

  public static File getBzrRoot(File file) {
    if (file == null)
      return null;
    file = file.getAbsoluteFile();
    while (file != null && !file.isDirectory()) {
      file = file.getParentFile();
    }
    while (file != null) {
      if ((new File(file, ".bzr/branch/last-revision")).isFile())
        return file;
      file = file.getParentFile();
    }
    return null;
  }

  public static File getRelativeTo(File base, File file) {
    String normalizedBase = unixFilePath(base);
    String normalizedFile = unixFilePath(file);
    String[] workDirElements = normalizedBase.split("" + UNIX_SEPARATOR);
    String[] fileElements = normalizedFile.split("" + UNIX_SEPARATOR);
    File relativeFile = file;
    int elementIdx = 0;
    while (elementIdx < fileElements.length) {
      if (elementIdx < workDirElements.length && workDirElements[elementIdx]
          .equals(fileElements[elementIdx])) {
        elementIdx++;
      } else {
        final StringBuilder sb = new StringBuilder();
        for (int i = elementIdx; i < fileElements.length; i++) {
          sb.append(fileElements[i]).append(File.separator);
        }
        relativeFile = new File(sb.toString());
        return relativeFile;
      }
    }
    return file;
  }

  public static String unixFilePath(File file) {
    String path = "";
    if (file != null) {
      path = file.getPath();
    }
    if (path == null || path.indexOf(WINDOWS_SEPARATOR) == -1) {
      return path;
    }
    return path.replace(WINDOWS_SEPARATOR, UNIX_SEPARATOR);
  }

  public static BazaarRevision parseRevisionNumber(String revisionNumberString) {
    int idx = revisionNumberString.indexOf(':');
    String prefix = (idx > 0) ? revisionNumberString.substring(0, idx + 1) : "revno:";
    String revision =
        (idx > 0) ? revisionNumberString.substring(idx + 1) : revisionNumberString;
    return BazaarRevision.getRevision(prefix, revision);
  }

  public static String normalizeSeparator(String repPath) {
    return repPath.replace('\\', '/');
  }

  public static String dumpSystemProperties(Properties props, String name) {
    TreeMap<Object, Object> sorted = new TreeMap<Object, Object>(props);
    StringBuffer buf = new StringBuffer();
    buf.append(IOUtil.EOL).append("---- ").append(name).append(" properties : begin ----").append(IOUtil.EOL);
    for (Object key : sorted.keySet()) {
      if ("line.separator".equals(key)) {
        Object val = sorted.get(key);
        if ("\r".equals(val)) {
          val = "\\r";
        } else if ("\n".equals(val)) {
          val = "\\n";
        } else if ("\r\n".equals(val)) {
          val = "\\r\\n";  // Windows
        }
        buf.append("line.separator=").append(val).append(IOUtil.EOL);
      } else {
        buf.append(key).append('=').append(sorted.get(key)).append(IOUtil.EOL);
      }
    }
    buf.append("---- ").append(name).append(" properties : begin ----").append(IOUtil.EOL);
    return buf.toString();
  }

  public static boolean isEqual(Object o1, Object o2) {
    if (o1 == o2) {
      return true;
    } else if (o1 == null) {
      return false;
    } else {
      return o1.equals(o2);
    }
  }

  public static void main(String[] args) {
    System.out.println(BzrCoreUtil.dumpSystemProperties(System.getProperties(), "system"));
  }
}
