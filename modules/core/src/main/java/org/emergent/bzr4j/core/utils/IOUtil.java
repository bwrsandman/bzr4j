/*
 * Copyright (c) 2010 Patrick Woodworth
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.emergent.bzr4j.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * @author Patrick Woodworth
 */
public class IOUtil {

  /**
   * The default buffer size to use.
   */
  public static final String EOL = System.getProperty("line.separator");

  /**
   * Replace all separators like / and \ to File.separatorChar
   *
   * @param filePath path to ve normilized
   * @return path with replaced separators
   */
  public static String deNormalizeSeparator(String filePath) {
    final StringBuilder result = new StringBuilder(filePath.length());
    for (int i = 0; i < filePath.length(); i++) {
      final char c = filePath.charAt(i);
      result.append(c == '/' || c == '\\' ? File.separatorChar : c);
    }
    return result.toString();
  }

  public static String getCommonParent(final String firstUrl, final String secondUrl) {
    final StringBuffer result = new StringBuffer();

    StringBuffer nextPathElem = new StringBuffer();

    boolean addSeparator = false;

    int i = 0;
    for (; i < firstUrl.length() && i < secondUrl.length(); i++) {
      final char c1 = firstUrl.charAt(i);
      final char c2 = secondUrl.charAt(i);

      if (c1 != c2) {
        break;
      } else if (isFileSeparator(c1) || isFileSeparator(c2)) {
        if (addSeparator) {
          result.append("/");
        }

        if (nextPathElem.length() > 0) {
          result.append(nextPathElem.toString());
          nextPathElem = new StringBuffer();
          addSeparator = true;
        } else {
          if (addSeparator) {
            result.append("/");
            addSeparator = false;
          } else {
            addSeparator = true;
          }
        }

      } else {
        nextPathElem.append(c1);
      }
    }

    if (i == firstUrl.length() || i == secondUrl.length()) {
      if (nextPathElem.length() > 0) {
        if (result.length() > 0) {
          result.append("/");
        }
        result.append(nextPathElem.toString());
      }
    }

    return result.toString();
  }

  public static boolean isFileSeparator(final char c1) {
    return c1 == '\\' || c1 == '/';
  }

  public static File createTempDirectory(String prefix, String suffix) throws IOException {
    return createTempDirectory(prefix, suffix, new File(System.getProperty("java.io.tmpdir")));
  }

  public static File createTempDirectory(final String prefix, final String suffix, final File in)
      throws IOException {
    final File tempFile = File.createTempFile(prefix, suffix, in);
    if (!tempFile.delete())
      throw new IOException(String.format("Could not delete file '%s'", tempFile));
    if (!tempFile.mkdir())
      throw new IOException(String.format("Could not create directory '%s'", tempFile));
    return tempFile;
  }

  public static File toCanonical(File file) {
    if (file == null)
      return null;
    try {
      return file.getCanonicalFile();
    } catch (IOException e) {
      return file.getAbsoluteFile();
    }
  }

  public static char[] loadFileText(File file) throws IOException {
    return loadFileText(file, null);
  }

  public static char[] loadFileText(File file, String encoding) throws IOException{
    InputStream stream = new FileInputStream(file);
    Reader reader = encoding == null ? new InputStreamReader(stream) : new InputStreamReader(stream, encoding);
    try{
      return loadText(reader, (int)file.length());
    }
    finally{
      reader.close();
    }
  }

  public static char[] loadText(Reader reader, int length) throws IOException {
    char[] chars = new char[length];
    int count = 0;
    while (count < chars.length) {
      int n = reader.read(chars, count, chars.length - count);
      if (n <= 0) break;
      count += n;
    }
    if (count == chars.length){
      return chars;
    }
    else{
      char[] newChars = new char[count];
      System.arraycopy(chars, 0, newChars, 0, count);
      return newChars;
    }
  }

  public static void close(InputStream closeable) {
    if (closeable != null)
      try {
        closeable.close();
      } catch (IOException ignored) {
      }
  }

  public static void close(OutputStream closeable) {
    if (closeable != null)
      try {
        closeable.close();
      } catch (IOException ignored) {
      }
  }

  public static void close(Reader closeable) {
    if (closeable != null)
      try {
        closeable.close();
      } catch (IOException ignored) {
      }
  }

  public static void close(Writer closeable) {
    if (closeable != null)
      try {
        closeable.close();
      } catch (IOException ignored) {
      }
  }
}
