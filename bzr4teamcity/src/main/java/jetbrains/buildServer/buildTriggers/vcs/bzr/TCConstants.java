/*
 * Copyright (c) 2009 Patrick Woodworth.
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
package jetbrains.buildServer.buildTriggers.vcs.bzr;

import jetbrains.buildServer.vcs.VcsRoot;

/**
 * @author Patrick Woodworth
 */
public class TCConstants {

  public static final String VCS_NAME = "bzr";

  public static final String REPOSITORY_PROP = "repositoryPath";

  public static final String BRANCH_NAME_PROP = "branchName";

  public static final String BZR_COMMAND_PATH_PROP = "bzrCommandPath";

  public static final String USERNAME = "username";

  public static final String PASSWORD = VcsRoot.SECURE_PROPERTY_PREFIX + "password";
}
