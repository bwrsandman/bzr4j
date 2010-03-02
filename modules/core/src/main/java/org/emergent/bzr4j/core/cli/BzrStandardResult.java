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
package org.emergent.bzr4j.core.cli;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author Patrick Woodworth
 */
public class BzrStandardResult extends BzrAbstractResult {

  public static final BzrStandardResult EMPTY = new BzrStandardResult();

  private final ByteArrayOutputStream m_out = new ByteArrayOutputStream();

  private final Charset m_charset;

  public BzrStandardResult() {
    this(null);
  }

  public BzrStandardResult(Charset charset) {
    m_charset = charset != null ? charset : Charset.defaultCharset();
  }

  public byte[] getByteOut() {
    return m_out.toByteArray();
  }

  public String getStdOutAsString() {
    try {
      return new String(m_out.toByteArray(), m_charset.name());
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException(e); // should never happen
    }
  }

  public List<String> getStdOutAsLines() {
    return tokenize(getStdOutAsString());
  }

  @Override
  Thread startOutRelay(final InputStream is) {
    return startRelay(new BufferedInputStream(is), m_out, false);
  }
}
