/*
 * Copyright 2000-2007 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.buildServer.buildTriggers.vcs.bazaar;

import jetbrains.buildServer.vcs.VcsChangeInfo;
import org.emergent.bzr4j.core.BazaarItemKind;

/**
 * Represents repository modified file
 */
public class ModifiedFile {

  private VcsChangeInfo.Type myStatus;

  private String myPath;

  public ModifiedFile(final VcsChangeInfo.Type status, final String path) {
    myStatus = status;
    myPath = path;
  }

  /**
   * Returns file path
   * @return file path
   */
  public String getPath() {
    return myPath;
  }

  public VcsChangeInfo.Type getChangeType() {
    return myStatus;
  }
}
