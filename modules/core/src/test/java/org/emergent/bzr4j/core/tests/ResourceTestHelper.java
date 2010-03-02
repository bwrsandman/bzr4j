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

package org.emergent.bzr4j.core.tests;

import org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Patrick Woodworth
 */
public class ResourceTestHelper {

  public static void copyResourceToFile(String res, File file) throws IOException {
    InputStream is = null;
    OutputStream os = null;
    try {
      is = ResourceTestHelper.class.getResourceAsStream(res);
      os = new BufferedOutputStream(new FileOutputStream(file));
      IOUtils.copy(is, os);
      os.flush();
    } finally {
      IOUtils.closeQuietly(is);
      IOUtils.closeQuietly(os);
    }
  }

  public static void copyStringToFile(String content, File file) throws IOException {
    InputStream is = null;
    OutputStream os = null;
    try {
      is = new ByteArrayInputStream(content.getBytes("UTF-8"));
      os = new BufferedOutputStream(new FileOutputStream(file));
      IOUtils.copy(is, os);
      os.flush();
    } finally {
      IOUtils.closeQuietly(is);
      IOUtils.closeQuietly(os);
    }
  }
}
