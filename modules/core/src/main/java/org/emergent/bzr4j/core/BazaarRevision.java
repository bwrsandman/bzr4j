/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * <p>
 * A <u>very</u> simple implementation of a Bazaar revision
 * <p>
 * See 'bzr help revisionspec' for details
 * </p>
 *
 * @author Guillermo Gonzalez
 *
 */
public final class BazaarRevision {

  private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");

  private final Prefix prefix;

  private final String value;

  private BazaarRevision(String prefix, String value) {
    this(Prefix.fromString(prefix), value);
  }

  private BazaarRevision(Prefix prefix, String value) {
    this.prefix = prefix;
    this.value = value != null ? value.trim() : value;
  }

  @Override
  public String toString() {
    return (prefix != null) ? prefix.toString().concat(value) : "";
  }

  /**
   * <p>
   * At this time, this method don't validate the input
   * </p>
   *
   * @param prefixKind
   * @param value
   * @return a revision given the parameters
   * @throws BazaarException
   */
  public static BazaarRevision getRevision(String prefixKind, String value) {
    if (Prefix.isValid(prefixKind)) {
      return new BazaarRevision(prefixKind, value);
    }
    throw new IllegalArgumentException("Invalid revision format");
  }

  public static BazaarRevision getRevision(Prefix prefix, String value) {
    return new BazaarRevision(prefix, value);
  }

  /**
   * Convenience method for dates using java.util.Date
   *
   * @param date
   * @return
   */
  public static BazaarRevision getRevision(Date date) {
    return new BazaarRevision(Prefix.DATE, format(date));
  }

  protected synchronized static String format(Date date) {
    return dateFormat.format(date);
  }

  /**
   * Default method using "revno:" prefix
   *
   * @param revision
   * @return
   * @throws BazaarException
   */
  public static BazaarRevision getRevision(int revision) {
    return new BazaarRevision(Prefix.REVNO, String.valueOf(revision));
  }

  public static BazaarRevision getRevision(String revision) {
    int sepIdx = revision.indexOf(":");
    if (sepIdx < 0) {
      return new BazaarRevision(Prefix.REVNO, String.valueOf(Integer.parseInt(revision)));
    } else {
      String prefix = revision.substring(0, sepIdx);
      String suffix = revision.substring(sepIdx + 1, revision.length());
      return new BazaarRevision(prefix, suffix);
    }
  }

  public final Prefix getPrefix() {
    return prefix;
  }

  public final String getValue() {
    return value;
  }

  @Override
  public int hashCode() {
    final int PRIME = 31;
    int result = 1;
    result = PRIME * result + ((prefix == null) ? 0 : prefix.hashCode());
    result = PRIME * result + ((value == null) ? 0 : value.hashCode());
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
    final BazaarRevision other = (BazaarRevision)obj;
    if (prefix == null) {
      if (other.prefix != null)
        return false;
    } else if (!prefix.equals(other.prefix))
      return false;
    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
      return false;
    return true;
  }

  public int compareTo(BazaarRevision other) {
    if (value == null && other.getValue() == null) {
      return 0;
    } else if (value == null) {
      return -1;
    } else if (other.getValue() == null) {
      return 1;
    }
    Integer otherToken = null;
    Integer thisToken = null;
    int result = 0;
    StringTokenizer thisTokens = new StringTokenizer(value, ".");
    StringTokenizer otherTokens = new StringTokenizer(other.getValue(), ".");
    while (result == 0) {
      try {
        thisToken = Integer.valueOf(thisTokens.nextToken());
      }
      catch (NoSuchElementException ex) {
        result = 1;
      }
      try {
        otherToken = Integer.valueOf(otherTokens.nextToken());
      }
      catch (NoSuchElementException ex) {
        result = -1;
      }
      if (result == 0)
        result = thisToken.compareTo(otherToken);
    }
    return result;
  }

  public enum Prefix {
    // protected Pattern revidPattern =
    // Pattern.compile("[a-zA-Z]{4}@[a-zA-Z]{4}-[0-9]{9}");
    REVNO("revno:"),
    REVID("revid:"),
    LAST("last:"),
    BEFORE("before:"),
    TAG("tag:"),
    DATE("date:"),
    ANCESTOR("ancestor:"),
    BRANCH("branch:"),
    NONE(""),;

    private final String value;

    private Prefix(final String value) {
      this.value = value;
    }

    protected static boolean isValid(String value) {
      return fromString(value) != null;
    }

    public static Prefix fromString(String prefixString) {
      for (Prefix prefix : values()) {
        if (prefix.value.equals(prefixString)) {
          return prefix;
        }
      }
      return null;
    }

    @Override
    public String toString() {
      return value;
    }
  }
}
