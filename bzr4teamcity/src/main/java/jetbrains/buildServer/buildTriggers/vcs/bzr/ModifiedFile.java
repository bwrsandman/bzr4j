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
package jetbrains.buildServer.buildTriggers.vcs.bzr;

/**
 * Represents repository modified file
 */
public class ModifiedFile {

  /**
   * Type of modification
   */
  public static enum Status {

    ADDED("added"),
    MODIFIED("modified"),
    REMOVED("removed"),
    UNKNOWN("unknown");

    private String myName;

    Status(final String name) {
      myName = name;
    }

    public String getName() {
      return myName;
    }
  }

  private Status myStatus;

  private String myPath;

  public ModifiedFile(final Status status, final String path) {
    myStatus = status;
    myPath = path;
  }

  /**
   * Returns type of modification
   * @return type of modification
   */
  public Status getStatus() {
    return myStatus;
  }

  /**
   * Returns file path
   * @return file path
   */
  public String getPath() {
    return myPath;
  }
}
