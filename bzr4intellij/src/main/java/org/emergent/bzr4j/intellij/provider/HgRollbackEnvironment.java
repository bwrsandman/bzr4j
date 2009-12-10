// Copyright 2009 Victor Iacoban
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software distributed under
// the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific language governing permissions and
// limitations under the License.
package org.emergent.bzr4j.intellij.provider;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.VcsDirtyScopeManager;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.rollback.RollbackEnvironment;
import com.intellij.openapi.vcs.rollback.RollbackProgressListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcsUtil.VcsUtil;
import org.emergent.bzr4j.intellij.HgFile;
import org.emergent.bzr4j.intellij.HgVcsMessages;
import org.emergent.bzr4j.intellij.command.HgRevertCommand;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class HgRollbackEnvironment implements RollbackEnvironment {

  private final Project project;

  public HgRollbackEnvironment(Project project) {
    this.project = project;
  }

  public String getRollbackOperationName() {
    return HgVcsMessages.message("hg4idea.revert");
  }

  public void rollbackChanges(List<Change> changes, List<VcsException> vcsExceptions,
    @NotNull RollbackProgressListener listener) {
    if (changes == null || changes.isEmpty()) {
      return;
    }
    List<FilePath> filePaths = new LinkedList<FilePath>();
    for (Change change : changes) {
      ContentRevision contentRevision;
      if (Change.Type.DELETED == change.getType()) {
        contentRevision = change.getBeforeRevision();
      } else {
        contentRevision = change.getAfterRevision();
      }
      if (contentRevision != null) {
        filePaths.add(contentRevision.getFile());
      }
    }
    revert(filePaths);
  }

  public void rollbackMissingFileDeletion(List<FilePath> files,
    List<VcsException> exceptions, RollbackProgressListener listener) {
    revert(files);
  }

  public void rollbackModifiedWithoutCheckout(List<VirtualFile> files,
    List<VcsException> exceptions, RollbackProgressListener listener) {
  }

  public List<VcsException> rollbackMissingFileDeletion(List<FilePath> files) {
    if (files == null || files.isEmpty()) {
      return null;
    }
    revert(files);
    return null;
  }

  public void rollbackIfUnchanged(VirtualFile file) {
  }

  private void revert(List<FilePath> filePaths) {
    VcsDirtyScopeManager dirtyScopeManager = VcsDirtyScopeManager.getInstance(project);
    HgRevertCommand command = new HgRevertCommand(project);
    for (FilePath filePath : filePaths) {
      VirtualFile vcsRoot = VcsUtil.getVcsRootFor(project, filePath);
      if (vcsRoot == null) {
        continue;
      }
      command.execute(new HgFile(vcsRoot, filePath));
      dirtyScopeManager.dirDirtyRecursively(filePath.getParentPath());
    }
  }

}
