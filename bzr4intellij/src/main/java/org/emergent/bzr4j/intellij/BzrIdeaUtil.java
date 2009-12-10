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

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.vfs.AbstractVcsVirtualFile;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcsUtil.VcsUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * @author Patrick Woodworth
 */
public class BzrIdeaUtil
{
    /**
     * Return a bzr root for the file path (the parent directory with ".bzr" subdirectory)
     *
     * @param filePath a file path
     * @return bzr root for the file
     * @throws IllegalArgumentException if the file is not under bzr
     * @throws com.intellij.openapi.vcs.VcsException             if the file is not under bzr
     */
    public static VirtualFile getBzrRoot(final FilePath filePath) throws VcsException
        {
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
    public static VirtualFile getBzrRootOrNull(final FilePath filePath) {
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
    public static VirtualFile getBzrRoot(@NotNull final VirtualFile file) throws VcsException {
      final VirtualFile root = bzrRootOrNull(file);
      if (root != null) {
        return root;
      }
      else {
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
        return getBzrRootOrNull( VcsUtil.getFilePath(file.getPath()));
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
    public static boolean isUnderBzr(final VirtualFile vFile) {
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
      return relativePath( VfsUtil.virtualToIoFile(root), path.getIOFile());
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

}
