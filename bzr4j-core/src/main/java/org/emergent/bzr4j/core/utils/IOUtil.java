/*
 * Copyright (c) 2010 Emergent.org
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

import javax.xml.stream.XMLStreamReader;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * @author Patrick Woodworth
 */
public class IOUtil {

  /**
   * The default buffer size to use.
   */
  private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
  public static final String EOL = System.getProperty("line.separator");

  public static void closeQuietly(InputStream closeable) {
    if (closeable != null) try { closeable.close(); } catch (Exception ignored) { }
  }

  public static void closeQuietly(OutputStream closeable) {
    if (closeable != null) try { closeable.close(); } catch (Exception ignored) { }
  }

  public static void closeQuietly(Reader closeable) {
    if (closeable != null) try { closeable.close(); } catch (Exception ignored) { }
  }

  public static void closeQuietly(Writer closeable) {
    if (closeable != null) try { closeable.close(); } catch (Exception ignored) { }
  }

  public static void closeQuietly(XMLStreamReader closeable) {
    if (closeable != null) try { closeable.close(); } catch (Exception ignored) { }
  }

  /**
   * Get the contents of a <code>Reader</code> as a <code>byte[]</code>
   * using the default character encoding of the platform.
   * <p>
   * This method buffers the input internally, so there is no need to use a
   * <code>BufferedReader</code>.
   *
   * @param input  the <code>Reader</code> to read from
   * @return the requested byte array
   * @throws NullPointerException if the input is null
   * @throws java.io.IOException if an I/O error occurs
   */
  public static byte[] toByteArray(Reader input) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    copy(input, output);
    return output.toByteArray();
  }

  /**
   * Copy chars from a <code>Reader</code> to bytes on an
   * <code>OutputStream</code> using the default character encoding of the
   * platform, and calling flush.
   * <p>
   * This method buffers the input internally, so there is no need to use a
   * <code>BufferedReader</code>.
   * <p>
   * Due to the implementation of OutputStreamWriter, this method performs a
   * flush.
   * <p>
   * This method uses {@link java.io.OutputStreamWriter}.
   *
   * @param input  the <code>Reader</code> to read from
   * @param output  the <code>OutputStream</code> to write to
   * @throws NullPointerException if the input or output is null
   * @throws java.io.IOException if an I/O error occurs
   * @since Commons IO 1.1
   */
  public static void copy(Reader input, OutputStream output)
      throws IOException {
    OutputStreamWriter out = new OutputStreamWriter(output);
    copy(input, out);
    // XXX Unless anyone is planning on rewriting OutputStreamWriter, we
    // have to flush here.
    out.flush();
  }

  /**
   * Copy chars from a <code>Reader</code> to a <code>Writer</code>.
   * <p>
   * This method buffers the input internally, so there is no need to use a
   * <code>BufferedReader</code>.
   * <p>
   * Large streams (over 2GB) will return a chars copied value of
   * <code>-1</code> after the copy has completed since the correct
   * number of chars cannot be returned as an int. For large streams
   * use the <code>copyLarge(Reader, Writer)</code> method.
   *
   * @param input  the <code>Reader</code> to read from
   * @param output  the <code>Writer</code> to write to
   * @return the number of characters copied
   * @throws NullPointerException if the input or output is null
   * @throws java.io.IOException if an I/O error occurs
   * @throws ArithmeticException if the character count is too large
   * @since Commons IO 1.1
   */
  public static int copy(Reader input, Writer output) throws IOException {
    long count = copyLarge(input, output);
    if (count > Integer.MAX_VALUE) {
      return -1;
    }
    return (int)count;
  }

  /**
   * Copy chars from a large (over 2GB) <code>Reader</code> to a <code>Writer</code>.
   * <p>
   * This method buffers the input internally, so there is no need to use a
   * <code>BufferedReader</code>.
   *
   * @param input  the <code>Reader</code> to read from
   * @param output  the <code>Writer</code> to write to
   * @return the number of characters copied
   * @throws NullPointerException if the input or output is null
   * @throws java.io.IOException if an I/O error occurs
   * @since Commons IO 1.3
   */
  public static long copyLarge(Reader input, Writer output) throws IOException {
    char[] buffer = new char[DEFAULT_BUFFER_SIZE];
    long count = 0;
    int n = 0;
    while (-1 != (n = input.read(buffer))) {
      output.write(buffer, 0, n);
      count += n;
    }
    return count;
  }

  /**
   * Recursively deletes the given file or directory.
   *
   * @param file          the file or directory to delete
   * @throws java.io.IOException  if an I/O error occurs
   */
  public static void deleteRecursively(File file) throws IOException {
    deleteRecursively(file, true);
  }

  /**
   * Recursively deletes the given file or directory.
   *
   * @param file          the file or directory to delete
   * @param throwOnError  indicates whether or not to throw an exception if a file
   *                      is encountered that cannot be deleted
   * @return true if all files were deleted successfully, or false
   *                      if there was an error and 'throwOnError' is false
   * @throws java.io.IOException  if an I/O error occurs
   */
  public static boolean deleteRecursively(File file, final boolean throwOnError) throws IOException {
    return deleteRecursively(file, true, throwOnError);
  }

  /**
   * Recursively deletes the given file or directory.
   *
   * @param file          the file or directory to delete
   * @param deleteObject  deletes the given object if true
   * @param filter        files must pass this filter in order to be deleted
   * @param throwOnError  indicates whether or not to throw an exception if a file
   *                      is encountered that cannot be deleted
   * @return true if all files were deleted successfully, or false
   *                      if there was an error and 'throwOnError' is false
   * @throws java.io.IOException  if an I/O error occurs and throwOnError is true
   */
  private static boolean deleteRecursively(File file, boolean deleteObject, boolean throwOnError) throws IOException {
    boolean success = true;
    if (file.isDirectory()) {
      final File[] children = file.listFiles();
      final int count = children == null ? 0 : children.length;
      for (int i = 0; i < count; ++i) {
        final File child = children[i];
        if (child.isDirectory()) {
          if (!deleteRecursively(child, true, throwOnError)) {
            success = false;
          }
        } else {
          if (!delete(child, throwOnError)) {
            success = false;
          }
        }
      }
    }

    if (deleteObject) {
      if (!delete(file, throwOnError)) {
        success = false;
      }
    }

    return success;
  }

  /**
   * Deletes the specified file. If throwOnError is true, and the file exists
   * but cannot be delete, an IOException is thrown
   *
   * @param file             The file to delete
   * @param throwOnError     Indicates whether or not an Exception should be thrown
   *                         if there is an error deleting the file
   * @return true if all files were deleted successfully, or false
   *                         if there was an error and 'throwOnError' is false
   * @throws java.io.IOException     If the file exists, throwOnError is true, and the file
   *                         cannot be deleted
   */
  public static boolean delete(final File file, boolean throwOnError) throws IOException {
    if (!file.exists()) {
      return true;
    }
    if (!file.delete()) {
      String message = "Could not delete: " + file.getAbsolutePath();
      if (throwOnError) {
        throw new IOException(message);
      }
      System.out.println(message);
      return false;
    }
    return true;
  }

  /**
   * Copy a file from source to destination
   *
   * @param source A pathname for the source file
   * @param destination A pathname for the destination file
   * @throws java.io.IOException if an I/O error occurs
   */
  public static void copyFile( String source, String destination ) throws IOException
  {
      File fin = new File( source );
      File fout = new File( destination );
      copyFile( fin, fout );
  }

  /**
   * Copy a file from source to destination.
   *
   * @param source A File object for the source
   * @param destination A File object for the destination
   * @throws java.io.IOException if an I/O error occurs
   */
  public static void copyFile( File source, File destination ) throws IOException
  {
      FileInputStream in = null;
      FileOutputStream out = null;
      try
      {
          in = new FileInputStream( source );
          out = new FileOutputStream( destination );

          copy( in, out );
      }
      finally
      {
          closeQuietly( in );
          closeQuietly( out );
      }
  }

  public static int copy(final InputStream input, final OutputStream output) throws IOException {
    return copy(input, output, new byte[DEFAULT_BUFFER_SIZE]);
  }

  public static int copy(final InputStream input, final OutputStream output, final byte[] buffer) throws IOException {
    final int size = buffer.length;
    int read;
    int written = 0;
    while ((read = input.read(buffer, 0, size)) != -1) {
      output.write(buffer, 0, read);
      written += read;
    }
    return written;
  }

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

  public static void writeToFile(File file, byte[] bytes) throws IOException {
    OutputStream os = null;
    try {
      os = new BufferedOutputStream(new FileOutputStream(file));
      os.write(bytes);
      os.flush();
    }
    finally {
      closeQuietly(os);
    }
  }

  public static File toCanonical(File file) {
    try {
      return file.getCanonicalFile();
    }
    catch (IOException e) {
      return file.getAbsoluteFile();
    }
  }
}
