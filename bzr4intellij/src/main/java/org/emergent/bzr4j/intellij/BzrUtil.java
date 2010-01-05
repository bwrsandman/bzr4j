/*
 * Copyright (c) 2009 Emergent.org
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
package org.emergent.bzr4j.intellij;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.VcsDirtyScopeManager;
import com.intellij.openapi.vcs.vfs.AbstractVcsVirtualFile;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.RefreshQueue;
import com.intellij.openapi.vfs.newvfs.RefreshSession;
import com.intellij.vcsUtil.VcsUtil;
import org.emergent.bzr4j.commandline.CommandLineClient;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.BazaarStatusKind;
import org.emergent.bzr4j.core.BazaarTreeStatus;
import org.emergent.bzr4j.core.BranchLocation;
import org.emergent.bzr4j.core.IBazaarClient;
import org.emergent.bzr4j.core.IBazaarStatus;
import org.emergent.bzr4j.debug.LogUtil;
import org.emergent.bzr4j.utils.BzrCoreUtil;
import org.emergent.bzr4j.utils.IOUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Patrick Woodworth
 */
public class BzrUtil {

  private static final Logger LOG = Logger.getInstance(BzrUtil.class.getName());
  private static final LogUtil sm_logger = LogUtil.getLogger(BzrUtil.class);
  private static final String EOL = System.getProperty("line.separator");

  /**
   * Return a bzr root for the file path (the parent directory with ".bzr" subdirectory)
   *
   * @param filePath a file path
   * @return bzr root for the file
   * @throws IllegalArgumentException if the file is not under bzr
   * @throws com.intellij.openapi.vcs.VcsException             if the file is not under bzr
   */
  private static VirtualFile getBzrRoot(final FilePath filePath) throws VcsException {
    VirtualFile root = getBzrRootOrNull(filePath);
    if (root != null) {
      return root;
    }
    throw new VcsException("The file " + filePath + " is not under bzr.");
  }

  /**
   * Return a bzr root for the file path (the parent directory with ".bzr" subdirectory)
   *
   * @param filePath a file path
   * @return bzr root for the file or null if the file is not under bzr
   */
  @Nullable
  private static VirtualFile getBzrRootOrNull(final FilePath filePath) {
    File file = filePath.getIOFile();
    while (file != null && (!file.exists() || !file.isDirectory() || !new File(file, ".bzr").exists())) {
      file = file.getParentFile();
    }
    if (file == null) {
      return null;
    }
    return LocalFileSystem.getInstance().findFileByIoFile(file);
  }

  /**
   * Return a bzr root for the file (the parent directory with ".bzr" subdirectory)
   *
   * @param file the file to check
   * @return bzr root for the file
   * @throws VcsException if the file is not under bzr
   */
  private static VirtualFile getBzrRoot(@NotNull final VirtualFile file) throws VcsException {
    final VirtualFile root = bzrRootOrNull(file);
    if (root != null) {
      return root;
    } else {
      throw new VcsException("The file " + file.getPath() + " is not under bzr.");
    }
  }

  /**
   * Return a bzr root for the file (the parent directory with ".bzr" subdirectory)
   *
   * @param file the file to check
   * @return bzr root for the file or null if the file is not not under Bzr
   */
  @Nullable
  public static VirtualFile bzrRootOrNull(final VirtualFile file) {
    if (file instanceof AbstractVcsVirtualFile) {
      return getBzrRootOrNull(VcsUtil.getFilePath(file.getPath()));
    }
    VirtualFile root = file;
    while (root != null) {
      if (root.findFileByRelativePath(".bzr") != null) {
        return root;
      }
      root = root.getParent();
    }
    return root;
  }

  /**
   * Check if the virtual file under bzr
   *
   * @param vFile a virtual file
   * @return true if the file is under bzr
   */
  private static boolean isUnderBzr(final VirtualFile vFile) {
    return bzrRootOrNull(vFile) != null;
  }

  /**
   * Get relative path
   *
   * @param root a root path
   * @param path a path to file (possibly deleted file)
   * @return a relative path
   * @throws IllegalArgumentException if path is not under root.
   */
  public static String relativePath(final VirtualFile root, FilePath path) {
    return relativePath(VfsUtil.virtualToIoFile(root), path.getIOFile());
  }

  /**
   * Get relative path
   *
   * @param root a root path
   * @param path a path to file (possibly deleted file)
   * @return a relative path
   * @throws IllegalArgumentException if path is not under root.
   */
  public static String relativePath(final File root, FilePath path) {
    return relativePath(root, path.getIOFile());
  }

  /**
   * Get relative path
   *
   * @param root a root path
   * @param file a virtual file
   * @return a relative path
   * @throws IllegalArgumentException if path is not under root.
   */
  public static String relativePath(final File root, VirtualFile file) {
    return relativePath(root, VfsUtil.virtualToIoFile(file));
  }

  /**
   * Get relative path
   *
   * @param root a root file
   * @param file a virtual file
   * @return a relative path
   * @throws IllegalArgumentException if path is not under root.
   */
  public static String relativePath(final VirtualFile root, VirtualFile file) {
    return relativePath(VfsUtil.virtualToIoFile(root), VfsUtil.virtualToIoFile(file));
  }

  /**
   * Get relative path
   *
   * @param root a root path
   * @param path a path to file (possibly deleted file)
   * @return a relative path
   * @throws IllegalArgumentException if path is not under root.
   */
  public static String relativePath(final File root, File path) {
    String rc = FileUtil.getRelativePath(root, path);
    if (rc == null) {
      throw new IllegalArgumentException("The file " + path + " cannot be made relative to " + root);
    }
    return rc.replace(File.separatorChar, '/');
  }

  public static FilePath toFilePath(VirtualFile vf) {
    return VcsUtil.getFilePath(vf.getPath());
  }

  public static FilePath toFilePath(File vf) {
    return VcsUtil.getFilePath(vf);
  }

  public static BranchLocation toBranchLocation(File file) {
    return new BranchLocation(file);
  }

  public static BranchLocation toBranchLocation(VirtualFile file) {
    return new BranchLocation(VfsUtil.virtualToIoFile(file));
  }

  public static BranchLocation toBranchLocation(FilePath filePath) {
    return new BranchLocation(filePath.getIOFile());
  }

  private static AssertionError notYetImplemented() {
    return new AssertionError();
  }

  private static AssertionError notYetHandled(Throwable t) {
    return new AssertionError(t);
  }

  @Nullable
  private static BzrVcs getBzrVcs(VirtualFile file) {
    for (Project project : ProjectManager.getInstance().getOpenProjects()) {
      AbstractVcs vcs = ProjectLevelVcsManager.getInstance(project).getVcsFor(file);
      if (vcs instanceof BzrVcs) {
        LOG.debug(String.format("Found BzrVcs for file %s: %s", file, vcs));
        return (BzrVcs)vcs;
      }
    }
    return null;
  }

  private static IBazaarClient createBzrClient() {
    return new CommandLineClient();
  }

  private static File root(FilePath file) {
    File f = file.getIOFile();
    return BzrCoreUtil.getRootBranch(f != null ? f : new File(file.getPath()));
  }

  private static File root(File file) {
    return BzrCoreUtil.getRootBranch(file);
  }

  private static boolean isUnknown(BzrVcs bzr, File file) throws BazaarException {
    BazaarTreeStatus tstat = createBzrClient().status(new File[] { file });
    IBazaarStatus[] stats = tstat.getStatusAsArray();
    sm_logger.debug("status array len: " + stats.length);
    File root = BzrCoreUtil.getRootBranch(file);
    File relPath = BzrCoreUtil.getRelativeTo(root, file);
    String nixRel = BzrCoreUtil.unixFilePath(relPath);
    for (IBazaarStatus status : stats) {
      if (nixRel.equals(status.getPath()) && status.contains(BazaarStatusKind.UNKNOWN))
        return true;
    }
    return false;
  }

  private static void refreshFiles(List<VirtualFile> myFilesToRefresh, final Project project,
      boolean async) {
    final List<VirtualFile> toRefreshFiles = new ArrayList<VirtualFile>();
    final List<VirtualFile> toRefreshDirs = new ArrayList<VirtualFile>();
    for (VirtualFile file : myFilesToRefresh) {
      if (file.isDirectory()) {
        sm_logger.debug("Gonna refresh: " + file.getName());
        toRefreshDirs.add(file);
      } else {
        sm_logger.debug("Gonna refresh: " + file.getName());
        toRefreshFiles.add(file);
      }
    }
    // if refresh asynchronously, local changes would also be notified that they are dirty asynchronously,
    // and commit could be executed while not all changes are visible
    final RefreshSession session =
        RefreshQueue.getInstance().createSession(async, true, new Runnable() {
          public void run() {
            if (project.isDisposed()) return;
            filterOutInvalid(toRefreshFiles);
            filterOutInvalid(toRefreshDirs);

            final VcsDirtyScopeManager vcsDirtyScopeManager =
                VcsDirtyScopeManager.getInstance(project);
            vcsDirtyScopeManager.filesDirty(toRefreshFiles, toRefreshDirs);
          }
        });
    session.addAllFiles(myFilesToRefresh);
    session.launch();
  }

  private static void filterOutInvalid(final Collection<VirtualFile> files) {
    for (Iterator<VirtualFile> iterator = files.iterator(); iterator.hasNext();) {
      final VirtualFile file = iterator.next();
      if (!file.isValid()) {
        sm_logger.info("Refresh root is not valid: " + file.getPath());
        iterator.remove();
      }
    }
  }

  private static boolean isVersioned(BzrVcs bzr, VirtualFile file) throws BazaarException {
    FileStatusManager statMgr = FileStatusManager.getInstance(bzr.getProject());
    final FileStatus fileStatus = statMgr.getStatus(file);
    return fileStatus != FileStatus.UNKNOWN;
  }

  private static boolean isIgnored(BzrVcs bzr, VirtualFile file) throws BazaarException {
    FileStatusManager statMgr = FileStatusManager.getInstance(bzr.getProject());
    final FileStatus fileStatus = statMgr.getStatus(file);
    return fileStatus == FileStatus.UNKNOWN;
  }

  private static String getCommonParent(Collection<FilePath> col) {
    String retval = null;
    for (FilePath path : col) {
      String next = path.getPath();
      if (retval != null) {
        IOUtil.getCommonParent(retval, next);
      } else {
        retval = next;
      }
    }
    return retval;
  }

  /**
   * Find longest common prefix of two strings.
   */
  private static String longestCommonPrefix(String s, String t) {
    return s.substring(0, longestCommonPrefixLength(s, t));
  }

  private static int longestCommonPrefixLength(String s, String t) {
    int m = Math.min(s.length(), t.length());
    for (int k = 0; k < m; ++k)
      if (s.charAt(k) != t.charAt(k))
        return k;
    return m;
  }
}
