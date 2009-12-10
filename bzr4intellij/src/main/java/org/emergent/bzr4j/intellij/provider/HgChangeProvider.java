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
import com.intellij.openapi.vcs.changes.VcsDirtyScope;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcsUtil.VcsUtil;
import org.emergent.bzr4j.intellij.HgContentRevision;
import org.emergent.bzr4j.intellij.HgFile;
import org.emergent.bzr4j.intellij.HgRevisionNumber;
import org.emergent.bzr4j.intellij.command.HgChange;
import org.emergent.bzr4j.intellij.command.HgFileStatusEnum;
import org.emergent.bzr4j.intellij.command.HgResolveCommand;
import org.emergent.bzr4j.intellij.command.HgResolveStatusEnum;
import org.emergent.bzr4j.intellij.command.HgStatusCommand;
import org.emergent.bzr4j.intellij.command.HgWorkingCopyRevisionsCommand;

import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HgChangeProvider implements ChangeProvider {

  private final Project project;
  private final VcsKey vcsKey;

  private static final EnumMap<HgFileStatusEnum, HgChangeProcessor> PROCESSORS =
    new EnumMap<HgFileStatusEnum, HgChangeProcessor>(HgFileStatusEnum.class);

  static {
    PROCESSORS.put(HgFileStatusEnum.ADDED, HgChangeProcessor.ADDED);
    PROCESSORS.put(HgFileStatusEnum.DELETED, HgChangeProcessor.DELETED);
    PROCESSORS.put(HgFileStatusEnum.IGNORED, HgChangeProcessor.IGNORED);
    PROCESSORS.put(HgFileStatusEnum.MISSING, HgChangeProcessor.MISSING);
    PROCESSORS.put(HgFileStatusEnum.COPY, HgChangeProcessor.COPIED);
    PROCESSORS.put(HgFileStatusEnum.MODIFIED, HgChangeProcessor.MODIFIED);
    PROCESSORS.put(HgFileStatusEnum.UNMODIFIED, HgChangeProcessor.UNMODIFIED);
    PROCESSORS.put(HgFileStatusEnum.UNVERSIONED, HgChangeProcessor.UNVERSIONED);
  }

  public HgChangeProvider(Project project, VcsKey vcsKey) {
    this.project = project;
    this.vcsKey = vcsKey;
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

  private void process(ChangelistBuilder builder,
    FilePath filePath, Set<VirtualFile> processedRoots) {
    VirtualFile repo = VcsUtil.getVcsRootFor(project, filePath);
    if (repo == null || processedRoots.contains(repo)) {
      return;
    }
    Set<HgChange> hgChanges = new HgStatusCommand(project).execute(repo);
    if (hgChanges == null || hgChanges.isEmpty()) {
      return;
    }
    sendChanges(builder, hgChanges,
      new HgResolveCommand(project).list(repo),
      new HgWorkingCopyRevisionsCommand(project).identify(repo),
      new HgWorkingCopyRevisionsCommand(project).parent(repo)
    );
    processedRoots.add(repo);
  }

  private void sendChanges(ChangelistBuilder builder, Set<HgChange> changes,
    Map<HgFile, HgResolveStatusEnum> resolveStatus, HgRevisionNumber workingRevision,
    HgRevisionNumber parentRevision) {
    for (HgChange change : changes) {
      HgFile afterFile = change.afterFile();
      HgFile beforeFile = change.beforeFile();
      HgFileStatusEnum status = change.getStatus();

      if (resolveStatus.containsKey(afterFile)
        && resolveStatus.get(afterFile) == HgResolveStatusEnum.UNRESOLVED) {
        builder.processChange(
          new Change(
            new HgContentRevision(project, beforeFile, parentRevision),
            HgCurrentContentRevisionFactory.build(afterFile, workingRevision),
            FileStatus.MERGED_WITH_CONFLICTS
          ), vcsKey);
        continue;
      }

      HgChangeProcessor processor = PROCESSORS.get(status);
      if (processor != null) {
        processor.process(project, vcsKey, builder,
          workingRevision, parentRevision, beforeFile, afterFile);
      }
    }
  }

}


enum HgChangeProcessor {
  ADDED() {
    @Override
    void process(Project project, VcsKey vcsKey, ChangelistBuilder builder,
      HgRevisionNumber currentNumber, HgRevisionNumber parentRevision,
      HgFile beforeFile, HgFile afterFile) {
      processChange(
        null,
        HgCurrentContentRevisionFactory.build(afterFile, currentNumber),
        FileStatus.ADDED,
        builder,
        vcsKey
      );
    }
  },

  DELETED() {
    @Override
    void process(Project project, VcsKey vcsKey, ChangelistBuilder builder,
      HgRevisionNumber currentNumber, HgRevisionNumber parentRevision,
      HgFile beforeFile, HgFile afterFile) {
      processChange(
        new HgContentRevision(project, beforeFile, parentRevision),
        null,
        FileStatus.DELETED,
        builder,
        vcsKey
      );
    }
  },

  IGNORED() {
    @Override
    void process(Project project, VcsKey vcsKey, ChangelistBuilder builder,
      HgRevisionNumber currentNumber, HgRevisionNumber parentRevision,
      HgFile beforeFile, HgFile afterFile) {
      builder.processIgnoredFile(VcsUtil.getVirtualFile(afterFile.getFile()));
    }
  },

  MISSING() {
    @Override
    void process(Project project, VcsKey vcsKey, ChangelistBuilder builder,
      HgRevisionNumber currentNumber, HgRevisionNumber parentRevision,
      HgFile beforeFile, HgFile afterFile) {
      processChange(
        new HgContentRevision(project, beforeFile, parentRevision),
        null,
        FileStatus.DELETED_FROM_FS,
        builder,
        vcsKey
      );
    }
  },

  COPIED() {
    @Override
    void process(Project project, VcsKey vcsKey, ChangelistBuilder builder,
      HgRevisionNumber currentNumber, HgRevisionNumber parentRevision,
      HgFile beforeFile, HgFile afterFile) {
      processChange(
        new HgContentRevision(project, beforeFile, parentRevision),
        HgCurrentContentRevisionFactory.build(afterFile, currentNumber),
        FileStatus.MODIFIED,
        builder,
        vcsKey
      );
    }
  },

  MODIFIED() {
    @Override
    void process(Project project, VcsKey vcsKey, ChangelistBuilder builder,
      HgRevisionNumber currentNumber, HgRevisionNumber parentRevision,
      HgFile beforeFile, HgFile afterFile) {
      processChange(
        new HgContentRevision(project, beforeFile, parentRevision),
        HgCurrentContentRevisionFactory.build(afterFile, currentNumber),
        FileStatus.MODIFIED,
        builder,
        vcsKey
      );
    }
  },

  UNMODIFIED() {
    @Override
    void process(Project project, VcsKey vcsKey, ChangelistBuilder builder,
      HgRevisionNumber currentNumber, HgRevisionNumber parentRevision,
      HgFile beforeFile, HgFile afterFile) {
      //DO NOTHING
    }
  },

  UNVERSIONED() {
    @Override
    void process(Project project, VcsKey vcsKey, ChangelistBuilder builder,
      HgRevisionNumber currentNumber, HgRevisionNumber parentRevision,
      HgFile beforeFile, HgFile afterFile) {
      builder.processUnversionedFile(VcsUtil.getVirtualFile(afterFile.getFile()));
    }
  };

  abstract void process(
    Project project,
    VcsKey vcsKey,
    ChangelistBuilder builder,
    HgRevisionNumber currentNumber,
    HgRevisionNumber parentRevision,
    HgFile beforeFile,
    HgFile afterFile
  );

  final void processChange(ContentRevision contentRevisionBefore,
    ContentRevision contentRevisionAfter, FileStatus fileStatus,
    ChangelistBuilder builder, VcsKey vcsKey) {
    if (contentRevisionBefore == null && contentRevisionAfter == null) {
      return;
    }
    builder.processChange(
      new Change(contentRevisionBefore, contentRevisionAfter, fileStatus),
      vcsKey
    );
  }
}
