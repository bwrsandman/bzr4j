/*
 * Copyright (c) 2009-2010 Patrick Woodworth
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * @author Patrick Woodworth
 */
public class CmdCat {

  private String m_path;

  private String m_version;

  private File m_dir;

  public CmdCat(File dir, String version, String path) {
    m_dir = dir;
    m_version = version;
    m_path = path;
  }

  public byte[] exec(CmdContext context) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    BzrSinkResult sinkResult = new BzrSinkResult(baos, true);
    context.exec("cat", m_dir, sinkResult, "-r", m_version, m_path);
    return baos.toByteArray();
  }
}
