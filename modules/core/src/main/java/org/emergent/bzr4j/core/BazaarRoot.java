/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.core;

import org.emergent.bzr4j.core.debug.DebugLogger;
import org.emergent.bzr4j.core.debug.DebugManager;
import org.emergent.bzr4j.core.utils.BzrCoreUtil;
import org.emergent.bzr4j.core.utils.IOUtil;
import static org.emergent.bzr4j.core.utils.StringUtil.getAbsoluteURI;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Guillermo Gonzalez
 * @author Patrick Woodworth
 */
public class BazaarRoot implements Comparable<BazaarRoot>, Serializable {

  public static final BazaarRoot ROOTLESS = new BazaarRoot(null);

  public static final String LP_SCHEME_PREFIX = "lp:";

  public static final Set<String> REMOTE_SCHEMES = Collections.unmodifiableSet(new HashSet<String>(
    Arrays.asList(new String[] { "http", "sftp", "ftp", "rsync", "https", "bzr+ssh", "bzr+http", "bzr+https" })
  ));

  private static final DebugLogger LOG = DebugManager.getLogger(BazaarRoot.class);

  private static final long serialVersionUID = -881174278540933857L;

  private final String m_location;

  private final String m_originalFilePath;

  private final boolean m_local;

  private BazaarRoot(String location) {
    this(location, null);
  }

  private BazaarRoot(String location, File originalFile) {
    m_location = location;
    m_local = location != null && location.startsWith("file:");
    m_originalFilePath = originalFile == null ? null : originalFile.getAbsolutePath();
  }

  public static BazaarRoot createRootLocation(String location) {
    if (location == null)
      throw new NullPointerException("location was null");
    if (location.startsWith(LP_SCHEME_PREFIX)) {
      return new BazaarRoot(location);
    } else {
      return new BazaarRoot(getAbsoluteURI(location).toString());
    }
  }

  public static BazaarRoot createRootLocation(URI location) {
    if (location == null)
      throw new NullPointerException("location was null");
    return new BazaarRoot(getAbsoluteURI(location).toString());
  }

  public static BazaarRoot createRootLocation(File location) {
    if (location == null)
      throw new NullPointerException("location was null");
    return new BazaarRoot(getAbsoluteURI(location).toString(), location);
  }

  public static BazaarRoot findBranchLocation(File file) {
    File rootFile = BzrCoreUtil.getBzrRoot(file);
    if (rootFile == null)
        return null;
    return createRootLocation(rootFile);
  }

  public boolean isLocal() {
    return m_local;
  }

  public URI getURI() {
    if (m_location == null) {
      return null;
    }
    return getAbsoluteURI(this.m_location);
  }

  public File getFile() {
    if (!isLocal())
      return null;

    if (m_originalFilePath != null)
      return new File(m_originalFilePath);

    return new File(getURI());
  }

  public String getRevno() {
    if (!isLocal())
      return null;

    String retval = null;
    try {
      retval = String.valueOf(IOUtil.loadFileText(new File(getFile(), ".bzr/branch/last-revision")));
      int sepIdx = retval.indexOf(' ');
      if (sepIdx > 0) {
        retval = retval.substring(0, sepIdx);
      }
    } catch (IOException e) {
      LOG.debug(e);
    }
    return retval;
  }


  @Override
  public String toString() {
    return this.m_location;
  }

  public int compareTo(BazaarRoot other) {
    if (m_location == other.m_location) {
      return 0;
    } else if (m_location == null) {
      return -1;
    } else {
      return this.m_location.compareTo(other.m_location);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((m_location == null) ? 0 : m_location.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BazaarRoot other = (BazaarRoot)obj;
    return compareTo(other) == 0;
  }
}
