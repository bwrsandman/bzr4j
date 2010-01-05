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
package org.emergent.bzr4j.intellij.provider.update;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.VcsDirtyScopeManager;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.intellij.openapi.vcs.update.FileGroup;
import com.intellij.openapi.vcs.update.UpdatedFiles;
import com.intellij.openapi.vfs.VirtualFile;
import org.emergent.bzr4j.intellij.BzrFile;
import org.emergent.bzr4j.intellij.BzrVcs;
import org.emergent.bzr4j.intellij.BzrVcsMessages;
import org.emergent.bzr4j.intellij.command.BzrMergeCommand;
import org.emergent.bzr4j.intellij.command.BzrStatusCommand;
import org.emergent.bzr4j.intellij.data.BzrChange;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

final class BzrHeadMerger {

  private final Project project;
  private final BzrMergeCommand hgMergeCommand;

  public BzrHeadMerger(Project project, @NotNull BzrMergeCommand hgMergeCommand) {
    this.project = project;
    this.hgMergeCommand = hgMergeCommand;
  }

  public void merge(VirtualFile repo, UpdatedFiles updatedFiles,
      ProgressIndicator indicator, VcsRevisionNumber revisionNumber) throws VcsException {
    Object[] params = new Object[] { };
    indicator.setText2(BzrVcsMessages.message("bzr4intellij.progress.merging", params));

    hgMergeCommand.execute();

    VcsDirtyScopeManager vcsDirtyScopeManager = VcsDirtyScopeManager.getInstance(project);
    vcsDirtyScopeManager.dirDirtyRecursively(repo);
    repo.refresh(false, true);

    BzrStatusCommand hgStatusCommand = new BzrStatusCommand(project);
    hgStatusCommand.setIncludeIgnored(false);
    hgStatusCommand.setIncludeUnknown(false);
    Set<BzrChange> changes = hgStatusCommand.execute(repo);
    if (changes.isEmpty()) {
      return;
    }

    for (BzrChange change : changes) {
      BzrFile afterFile = change.afterFile();
      BzrFile beforeFile = change.beforeFile();
      String fileGroupId = null;
      String filePath = null;
      if (afterFile != null && beforeFile != null) {
        fileGroupId = FileGroup.MODIFIED_ID;
        filePath = afterFile.getFile().getAbsolutePath();
      } else if (beforeFile != null) {
        fileGroupId = FileGroup.LOCALLY_REMOVED_ID;
        filePath = beforeFile.getFile().getAbsolutePath();
      } else if (afterFile != null) {
        fileGroupId = FileGroup.LOCALLY_ADDED_ID;
        filePath = afterFile.getFile().getAbsolutePath();
      }
      if (fileGroupId != null && filePath != null) {
        updatedFiles.getGroupById(fileGroupId).add(filePath, BzrVcs.VCS_NAME, revisionNumber);
      }
    }
  }

}
