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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.AbstractVcsHelper;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.merge.MergeData;
import com.intellij.openapi.vcs.merge.MergeProvider;
import com.intellij.openapi.vcs.update.FileGroup;
import com.intellij.openapi.vcs.update.UpdatedFiles;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcsUtil.VcsUtil;
import org.apache.commons.io.IOUtils;
import org.emergent.bzr4j.intellij.HgFile;
import org.emergent.bzr4j.intellij.HgRevisionNumber;
import org.emergent.bzr4j.intellij.HgVcs;
import org.jetbrains.annotations.NotNull;
import org.emergent.bzr4j.intellij.command.HgCatCommand;
import org.emergent.bzr4j.intellij.command.HgResolveCommand;
import org.emergent.bzr4j.intellij.command.HgResolveStatusEnum;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class HgConflictResolver {

  private final Project project;
  private final HgRevisionNumber incomingRevision;
  private final HgRevisionNumber localRevision;
  private final UpdatedFiles updatedFiles;

  public HgConflictResolver(Project project,
    HgRevisionNumber incomingRevision, HgRevisionNumber localRevision) {
    this(project, incomingRevision, localRevision, null);
  }

  public HgConflictResolver(Project project, HgRevisionNumber incomingRevision,
    HgRevisionNumber localRevision, UpdatedFiles updatedFiles) {
    this.project = project;
    this.incomingRevision = incomingRevision;
    this.localRevision = localRevision;
    this.updatedFiles = updatedFiles;
  }

  public void resolve(final VirtualFile repo) {
    Map<HgFile, HgResolveStatusEnum> resolves = new HgResolveCommand(project).list(repo);
    final List<VirtualFile> conflicts = new ArrayList<VirtualFile>();
    for (Map.Entry<HgFile, HgResolveStatusEnum> entry : resolves.entrySet()) {
      File file = entry.getKey().getFile();
      String fileGroupId = null;
      switch (entry.getValue()) {
        case UNRESOLVED:
          conflicts.add(VcsUtil.getVirtualFile(file));
          fileGroupId = FileGroup.MERGED_WITH_CONFLICT_ID;
          break;
        case RESOLVED:
          fileGroupId = FileGroup.MERGED_ID;
          break;
        default:
      }
      if (updatedFiles != null && fileGroupId != null) {
        updatedFiles.getGroupById(fileGroupId)
          .add(file.getPath(), HgVcs.VCS_NAME, incomingRevision);
      }
    }

    if (conflicts.isEmpty()) {
      return;
    }

    AbstractVcsHelper.getInstance(project).showMergeDialog(conflicts, buildMergeProvider(repo));
  }

  private MergeProvider buildMergeProvider(final VirtualFile repo) {
    return new MergeProvider() {
      @NotNull
      public MergeData loadRevisions(VirtualFile file) throws VcsException {
        try {
          MergeData mergeData = new MergeData();
          mergeData.ORIGINAL = IOUtils.toByteArray(new FileReader(file.getPath() + ".orig"));
          mergeData.LAST_REVISION_NUMBER = incomingRevision;

          HgFile hgFile = new HgFile(repo, VfsUtil.virtualToIoFile(file));

          HgCatCommand hgCatCommand = new HgCatCommand(project);
          String last = hgCatCommand.execute(
            hgFile,
            (HgRevisionNumber) mergeData.LAST_REVISION_NUMBER,
            file.getCharset()
          );
          if (last != null) {
            mergeData.LAST = last.getBytes(file.getCharset().name());
          } else {
            mergeData.LAST = new byte[0];
          }

          String current = hgCatCommand.execute(
            hgFile, localRevision, file.getCharset()
          );

          if (current != null) {
            mergeData.CURRENT = current.getBytes(file.getCharset().name());
          } else {
            mergeData.CURRENT = new byte[0];
          }

          return mergeData;
        } catch (IOException e) {
          throw new VcsException(e);
        }
      }

      public void conflictResolvedForFile(VirtualFile file) {
        new HgResolveCommand(project).resolve(repo, file);
      }

      public boolean isBinary(VirtualFile file) {
        return false;
      }
    };
  }


}
