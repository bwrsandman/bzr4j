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

package jetbrains.buildServer.buildTriggers.vcs.bazaar;

/**
 * @author Patrick Woodworth
 */
public class ChangeRev {

  private final String myId;

  public ChangeRev(final String fullVersion) {
    myId = fullVersion;
  }

  /**
   * Returns changeset revision id
   * @return changeset revision id
   */
  public String getId() {
    return myId;
  }

  /**
   * Returns full changeset version as reported by bzr log command: revnum:revid
   * @return full changeset version as reported by bzr log
   */
  public String getFullVersion() {
//    return myRevNumber + ":" + myId;
    return myId;
  }
}
