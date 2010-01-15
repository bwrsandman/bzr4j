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

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.VcsKey;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManagerGate;
import com.intellij.openapi.vcs.changes.ChangeProvider;
import com.intellij.openapi.vcs.changes.ChangelistBuilder;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.changes.CurrentContentRevision;
import com.intellij.openapi.vcs.changes.VcsDirtyScope;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcsUtil.VcsUtil;
import org.emergent.bzr4j.intellij.BzrContentRevision;
import org.emergent.bzr4j.intellij.BzrFile;
import org.emergent.bzr4j.intellij.BzrRevisionNumber;
import org.emergent.bzr4j.intellij.BzrRootConverter;
import org.emergent.bzr4j.intellij.BzrUtil;
import org.emergent.bzr4j.intellij.command.BzrLsCommand;
import org.emergent.bzr4j.intellij.command.BzrMiscCommand;
import org.emergent.bzr4j.intellij.command.BzrStatusCommand;
import org.emergent.bzr4j.intellij.command.BzrWorkingCopyRevisionsCommand;
import org.emergent.bzr4j.intellij.data.BzrChange;
import org.emergent.bzr4j.intellij.data.BzrFileStatusEnum;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BzrChangeProvider implements ChangeProvider {

  private static final Logger LOG = Logger.getInstance(BzrChangeProvider.class.getName());

  private final Project m_project;
  private final VcsKey m_vcsKey;

  public BzrChangeProvider(Project project, VcsKey vcsKey) {
    this.m_project = project;
    this.m_vcsKey = vcsKey;
  }

  public void getChanges(VcsDirtyScope dirtyScope, ChangelistBuilder builder,
      ProgressIndicator progress, ChangeListManagerGate addGate) throws VcsException {
    Set<VirtualFile> processedRoots = new LinkedHashSet<VirtualFile>();
    for (FilePath filePath : dirtyScope.getRecursivelyDirtyDirectories()) {
      process(builder, filePath, processedRoots);
    }
    for (FilePath filePath : dirtyScope.getDirtyFiles()) {
      process(builder, filePath, processedRoots);
    }
  }

  public boolean isModifiedDocumentTrackingRequired() {
    return true;
  }

  public void doCleanup(List<VirtualFile> files) {
  }

  private void process(ChangelistBuilder builder, FilePath filePath, Set<VirtualFile> processedRoots) {
    VirtualFile vcsRepo = VcsUtil.getVcsRootFor(m_project, filePath);
    List<VirtualFile> mappedRoots = BzrRootConverter.INSTANCE.convertRoots(Arrays.asList(vcsRepo));
    VirtualFile repo = mappedRoots.size() < 1 ? null : mappedRoots.get(0);
    if (repo == null || processedRoots.contains(repo)) {
      LOG.debug("no processing: " + String.valueOf(repo));
      return;
    }
    Set<BzrChange> hgChanges = new HashSet<BzrChange>();
    String relpath = BzrUtil.relativePath(repo,vcsRepo);
    LOG.debug("is processing: " + String.valueOf(repo) + " : " + relpath);
    hgChanges.addAll((new BzrStatusCommand(m_project)).execute(repo,vcsRepo));
    hgChanges.addAll((new BzrLsCommand(m_project)).execute(repo,vcsRepo));

    if (hgChanges == null || hgChanges.isEmpty()) {
      return;
    }
    sendChanges(builder, hgChanges, BzrMiscCommand.revno(m_project,repo));
    processedRoots.add(repo);
  }

  private void sendChanges(ChangelistBuilder builder, Set<BzrChange> changes, BzrRevisionNumber workingRevision) {
    for (BzrChange change : changes) {
      BzrFile afterFile = change.afterFile();
      BzrFile beforeFile = change.beforeFile();

      EnumSet<BzrFileStatusEnum> statusSet = change.getStatus();
      HgChangeProcessor processor = null;
      if (statusSet.contains(BzrFileStatusEnum.ADDED)) {
        processor = HgChangeProcessor.ADDED;
      } else if (statusSet.contains(BzrFileStatusEnum.CONFLICTED)) {
        processor = HgChangeProcessor.CONFLICTED;
      } else if (statusSet.contains(BzrFileStatusEnum.DELETED)) {
        processor = HgChangeProcessor.DELETED;
      } else if (statusSet.contains(BzrFileStatusEnum.IGNORED)) {
        processor = HgChangeProcessor.IGNORED;
      } else if (statusSet.contains(BzrFileStatusEnum.MODIFIED)
          || statusSet.contains(BzrFileStatusEnum.RENAMED)) {
        processor = HgChangeProcessor.MODIFIED;
      } else if (statusSet.contains(BzrFileStatusEnum.UNKNOWN)) {
        processor = HgChangeProcessor.UNVERSIONED;
      }

      if (processor != null) {
        processor.process(m_project, m_vcsKey, builder, workingRevision, beforeFile, afterFile);
      }
    }
  }

  enum HgChangeProcessor {

    ADDED() {
      @Override
      void process(Project project, VcsKey vcsKey, ChangelistBuilder builder, BzrRevisionNumber revision,
          BzrFile beforeFile, BzrFile afterFile) {
        processChange(
            null,
            CurrentContentRevision.create(afterFile.toFilePath()),
            FileStatus.ADDED,
            builder,
            vcsKey
        );
      }
    },

    CONFLICTED() {
      @Override
      void process(Project project, VcsKey vcsKey, ChangelistBuilder builder, BzrRevisionNumber revision,
          BzrFile beforeFile, BzrFile afterFile) {
        processChange(
            new BzrContentRevision(project, beforeFile, revision),
            CurrentContentRevision.create(afterFile.toFilePath()),
            FileStatus.MERGED_WITH_CONFLICTS,
            builder,
            vcsKey
        );
      }
    },

    DELETED() {
      @Override
      void process(Project project, VcsKey vcsKey, ChangelistBuilder builder, BzrRevisionNumber revision,
          BzrFile beforeFile, BzrFile afterFile) {
        processChange(
            new BzrContentRevision(project, beforeFile, revision),
            null,
            FileStatus.DELETED,
            builder,
            vcsKey
        );
      }
    },

    IGNORED() {
      @Override
      void process(Project project, VcsKey vcsKey, ChangelistBuilder builder, BzrRevisionNumber revision,
          BzrFile beforeFile, BzrFile afterFile) {
        builder.processIgnoredFile(VcsUtil.getVirtualFile(afterFile.getFile()));
      }
    },

    MODIFIED() {
      @Override
      void process(Project project, VcsKey vcsKey, ChangelistBuilder builder, BzrRevisionNumber revision,
          BzrFile beforeFile, BzrFile afterFile) {
        processChange(
            new BzrContentRevision(project, beforeFile, revision),
            CurrentContentRevision.create(afterFile.toFilePath()),
            FileStatus.MODIFIED,
            builder,
            vcsKey
        );
      }
    },

    UNVERSIONED() {
      @Override
      void process(Project project, VcsKey vcsKey, ChangelistBuilder builder, BzrRevisionNumber revision,
          BzrFile beforeFile, BzrFile afterFile) {
        VirtualFile vFile = VcsUtil.getVirtualFile(afterFile.getFile());
        if (vFile == null)
          return;
        processRecursive(builder, vFile);
      }

      private void processRecursive(ChangelistBuilder builder, VirtualFile vFile) {
        LOG.debug("processUnversionedFile: " + String.valueOf(vFile));
        builder.processUnversionedFile(vFile);
        if (vFile.isDirectory()) {
          for (VirtualFile child : vFile.getChildren()) {
            processRecursive(builder, child);
          }
        }
      }
    };

    abstract void process(
        Project project,
        VcsKey vcsKey,
        ChangelistBuilder builder,
        BzrRevisionNumber revision,
        BzrFile beforeFile,
        BzrFile afterFile
    );

    final void processChange(ContentRevision contentRevisionBefore,
        ContentRevision contentRevisionAfter, FileStatus fileStatus,
        ChangelistBuilder builder, VcsKey vcsKey) {
      if (contentRevisionBefore == null && contentRevisionAfter == null) {
        return;
      }
      builder.processChange(new Change(contentRevisionBefore, contentRevisionAfter, fileStatus), vcsKey);
    }
  }
}
