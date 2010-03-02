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

import jetbrains.buildServer.util.Hash;
import jetbrains.buildServer.vcs.VcsRoot;
import org.emergent.bzr4j.core.utils.BzrCoreUtil;
import org.emergent.bzr4j.core.utils.IOUtil;
import org.emergent.bzr4j.core.utils.StringUtil;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents Bazaar repository settings
 */
public class Settings {

  private static final String DEFAULT_BRANCH_NAME = "default";

  private String myRepository;

  private String myBzrCommandPath;

  private File myWorkingDir;

  private File myWorkFolderParentDir;

  private String myUsername;

  private String myPassword;

  private String myBranchName;

  public Settings(File workFolderParentDir, VcsRoot vcsRoot) {
    myWorkFolderParentDir = workFolderParentDir;
    setRepository(vcsRoot.getProperty(TCConstants.REPOSITORY_PROP));
    setBzrCommandPath(vcsRoot.getProperty(TCConstants.BZR_COMMAND_PATH_PROP));
    setBranchName(vcsRoot.getProperty(TCConstants.BRANCH_NAME_PROP));
    setUsername(vcsRoot.getProperty(TCConstants.USERNAME));
    setPassword(vcsRoot.getProperty(TCConstants.PASSWORD));
  }

  public void setBranchName(String branchName) {
    myBranchName = branchName;
  }

  public void setUsername(String username) {
    myUsername = username;
  }

  public void setPassword(String password) {
    myPassword = password;
  }

  public void setRepository(final String repository) {
    myRepository = repository;
  }

  /**
   * Returns repository path
   * @return repository path
   */
  public String getRepositoryUrl() {
    String retval = myRepository;
    int fragIdx = retval.lastIndexOf('#');
    if (fragIdx > 0) {
      retval = retval.substring(0,fragIdx);
    }
    return retval;
  }

  /**
   * Returns name of the branch to use (returns 'default' if no branch specified)
   * @return see above
   */
  public String getBranchName() {
    return StringUtil.isEmpty(myBranchName) ? DEFAULT_BRANCH_NAME : myBranchName;
  }

  /**
   * Returns true if current branch is default branch
   * @return see above
   */
  public boolean isDefaultBranch() {
    return getBranchName().equals(DEFAULT_BRANCH_NAME);
  }

  /**
   * Returns path to bzr command
   * @return path to bzr command
   */
  public String getBzrCommandPath() {
    return myBzrCommandPath;
  }

  private final static Set<String> AUTH_PROTOS = new HashSet<String>();

  static {
    AUTH_PROTOS.add("http://");
    AUTH_PROTOS.add("https://");
    AUTH_PROTOS.add("ssh://");
  }

  /**
   * Returns URL to use for push command
   * @return URL to use for push command
   */
  public String getPushUrl() {
/*
        String cre = "";
        if ( !StringUtil.isEmpty( myUsername ) )
        {
            cre += myUsername;
            if ( !StringUtil.isEmpty( myPassword ) )
            {
                cre += ":" + myPassword;
            }
            cre += "@";
        }

        for ( String proto : AUTH_PROTOS )
        {
            if ( myRepository.startsWith( proto ) )
            {
                return proto + cre + myRepository.substring( proto.length() );
            }
        }
*/

    return getRepositoryUrl();
  }

  public void setBzrCommandPath(final String bzrCommandPath) {
    myBzrCommandPath = bzrCommandPath;
  }

  public void setWorkingDir(final File workingDir) {
    myWorkingDir = IOUtil.toCanonical(workingDir);
  }

  /**
   * Returns directory where repository is supposed to be cloned, i.e. working directory of cloned repository
   * @return repository working directory
   */
  public File getLocalRepositoryDir() {
    if (myWorkingDir != null) {
      return myWorkingDir;
    }

    return getDefaultWorkDir(myWorkFolderParentDir, myRepository);
  }

  /**
   * Returns true if current working directory contains copy of repository (contains .bzr directory)
   * @return see above
   */
  public boolean hasCopyOfRepository() {
    // need better way to check that repository copy is ok
    return getLocalRepositoryDir().isDirectory() && new File(getLocalRepositoryDir(), ".bzr").isDirectory();
  }

  public static String DEFAULT_WORK_DIR_PREFIX = "bzr_";

  private static File getDefaultWorkDir(File workFolderParentDir, String repPath) {
    String workingDirname = DEFAULT_WORK_DIR_PREFIX + String.valueOf(Hash.calc(normalize(repPath)));
    return IOUtil.toCanonical(new File(workFolderParentDir, workingDirname));
  }

  private static String normalize(final String path) {
    String normalized = BzrCoreUtil.normalizeSeparator(path);
    if (path.endsWith("/")) {
      return normalized.substring(0, normalized.length() - 1);
    }
    return normalized;
  }
}
