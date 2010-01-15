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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * @author Patrick Woodworth
 */
public class IOUtils {

  /**
   * The default buffer size to use.
   */
  private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

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
   * @throws IOException if an I/O error occurs
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
   * @throws IOException if an I/O error occurs
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
   * @throws IOException if an I/O error occurs
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
}
