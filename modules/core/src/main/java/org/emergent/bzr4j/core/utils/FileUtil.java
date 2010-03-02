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

/**
 * @author Patrick Woodworth
 */
public class FileUtil {

  private static final String OS_NAME = System.getProperty("os.name").toLowerCase();

  private static final boolean CASE_SENSITIVE_FS = !OS_NAME.startsWith("windows") && !OS_NAME.startsWith("mac");

  private FileUtil() {
  }

  public static String getRelativePath(File base, File file) {
    if (base == null || file == null) return null;

    if (!base.isDirectory()) {
      base = base.getParentFile();
      if (base == null) return null;
    }

    if (base.equals(file)) return ".";

    final String filePath = file.getAbsolutePath();
    String basePath = base.getAbsolutePath();
    return getRelativePath(basePath, filePath, File.separatorChar);
  }

  public static String getRelativePath(String basePath, String filePath, final char separator) {
    return getRelativePath(basePath, filePath, separator, CASE_SENSITIVE_FS);
  }

  public static String getRelativePath(String basePath, String filePath, final char separator, final boolean caseSensitive) {
    if (!StringUtil.endsWithChar(basePath, separator)) basePath += separator;

    int len = 0;
    int lastSeparatorIndex = 0; // need this for cases like this: base="/temp/abcde/base" and file="/temp/ab"
    String basePathToCompare = caseSensitive ? basePath : basePath.toLowerCase();
    String filePathToCompare = caseSensitive ? filePath : filePath.toLowerCase();
    while (len < filePath.length() && len < basePath.length() && filePathToCompare.charAt(len) == basePathToCompare.charAt(len)) {
      if (basePath.charAt(len) == separator) {
        lastSeparatorIndex = len;
      }
      len++;
    }

    if (len == 0) return null;

    StringBuilder relativePath = new StringBuilder();
    for (int i=len; i < basePath.length(); i++) {
      if (basePath.charAt(i) == separator) {
        relativePath.append("..");
        relativePath.append(separator);
      }
    }
    relativePath.append(filePath.substring(lastSeparatorIndex + 1));

    return relativePath.toString();
  }
}
