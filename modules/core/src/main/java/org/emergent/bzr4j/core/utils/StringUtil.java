// Copyright (c) 2004-2006 by Leif Frenzel - see http://leiffrenzel.de
package org.emergent.bzr4j.core.utils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * <p>
 * helper with common string operations.
 * </p>
 *
 * @author Leif Frenzel
 * @author Isaac Devine
 * @author Guillermo Gonzalez <guillo.gonzo AT gmail DOT com>
 * @author Patrick Woodworth
 */
public class StringUtil {

  public static final String[] EMPTY_STRING_ARRAY = new String[0];

  private static Pattern badchars = Pattern.compile("[^a-zA-Z0-9.,:/\\\\_~-]");

  public static URI getAbsoluteURI(final String text) {
    // Use a File Object to convert the path into a opaque URI see the URI
    // javadoc. The use of the File object adds the current path to the string uri,
    // moving this to URI.create
    // fix Bug #294061. we need to scpe all spaces to make it java.net.URI "friendly"
    String escapedText = text.trim().replace(" ", "%20");
    return getAbsoluteURI(URI.create(escapedText));
  }

  public static URI getAbsoluteURI(final File file) {
    URI proposed = file.toURI();
    return getAbsoluteURI(proposed);
  }

  public static URI getAbsoluteURI(final URI uri) {
    URI proposed = uri;
    String schemeSpecificPart = makeDriveLetterLowerCase(proposed.getRawSchemeSpecificPart());
    if (proposed.getScheme() == null) {
      // the URI don't have shceme (possibly a raw path to the local file system)
      return proposed;
    } else {
      StringBuilder sb = new StringBuilder(":");
      for (int i = 2; i >= 0; i--) {
        if (i == 2 && !"file".equals(proposed.getScheme()))
          continue;
        if (schemeSpecificPart.charAt(i) != '/')
          sb.append("/");
      }
      proposed = URI.create(
          proposed.getScheme() + sb.toString() + schemeSpecificPart); //$NON-NLS-1$
      return proposed;
    }
  }

  private static String makeDriveLetterLowerCase(final String str) {
    String result = str;
    // the strange thing is that uris generated with File objects sometimes
    // have uppercase and sometimes lower case drive letters on windows
    // (which doesn't exactly help in comparisons with equals() ...)
    int colonIndex = str.indexOf(':');
    if (colonIndex > 0) {
      final StringBuilder sb = new StringBuilder(str.substring(0, colonIndex - 1));
      sb.append(Character.toLowerCase(str.charAt(colonIndex - 1)));
      sb.append(str.substring(colonIndex));
      result = sb.toString();
    }
    return result;
  }

  public static String[] addToArray(String[] array, String s) {
    String[] newArray = new String[array.length + 1];
    System.arraycopy(array, 0, newArray, 0, array.length);
    newArray[newArray.length - 1] = s;
    return newArray;
  }

  public static String shellQuote(String part) {
    if (part != null && badchars.matcher(part).find()) {
      return "\"" + part + "\"";
    } else {
      return part;
    }
  }

  public static String maybeQuote(String path) {
    if ((path != null)
        && (path.indexOf(' ') > -1)
        && !(path.startsWith("\"") && path.endsWith("\""))
        ) {
      return "\"" + path + "\"";
    }
    return path;
  }

  /**
   * FIXME: The formatter should parse: Sun 2007-06-24 19:36:42 -0300, this is a
   * workaround to the problem (see method parseLogDate)
   */
  private static final SimpleDateFormat DATE_FORMAT =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

  public synchronized static Date parseDate(String date) throws ParseException {
    return DATE_FORMAT.parse(date);
  }

  public synchronized static Date parseLogDate(String date) throws ParseException {
    return DATE_FORMAT.parse(date.substring(4).trim());
  }

  public static String nullSafeTrim(String aString) {
    if (aString == null) {
      return null;
    }
    return aString.trim();
  }

  public static boolean isEmpty(String str) {
    if (str == null)
      return true;
    else if (str.trim().length() < 1)
      return true;
    return false;
  }

  public static String concat(Object[] a, String joiner) {
    if (a == null)
      return "null";
    if (a.length == 0)
      return "";

    StringBuilder buf = new StringBuilder();

    for (int i = 0; i < a.length; i++) {
      if (i != 0)
        buf.append(joiner);

      buf.append(String.valueOf(a[i]));
    }
    return buf.toString();
  }

  public static String throwableStackToString(Throwable e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    pw.flush();
    pw.close();
    String retval = sw.getBuffer().toString();
    return retval;
  }

  public static <E> String toString(Collection<E> col) {
    return toString(col, ", ", "[", "]");
  }

  public static <E> String toString(Collection<E> col, String sep, String prefix, String suffix) {
    Iterator<E> i = col.iterator();
    if (!i.hasNext())
      return prefix + suffix;

    StringBuilder sb = new StringBuilder();
    sb.append(prefix);
    for (; ;) {
      E e = i.next();
      sb.append(e == col ? "(this Collection)" : e);
      if (!i.hasNext())
        return sb.append(suffix).toString();
      sb.append(sep);
    }
  }

  public static boolean endsWithChar(CharSequence s, char suffix) {
    return s != null && s.length() != 0 && s.charAt(s.length() - 1) == suffix;
  }
}
