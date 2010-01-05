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
import com.intellij.openapi.vcs.update.UpdatedFiles;
import com.intellij.openapi.vfs.VirtualFile;
import org.emergent.bzr4j.intellij.BzrVcsMessages;
import org.emergent.bzr4j.intellij.command.BzrUpdateCommand;

class BzrRegularUpdater implements BzrUpdater {

  private final Project project;
  private final VirtualFile repository;

  public BzrRegularUpdater(Project project, VirtualFile repository) {
    this.project = project;
    this.repository = repository;
  }

  public void update(UpdatedFiles updatedFiles, ProgressIndicator indicator) throws VcsException {
    Object[] params2 = new Object[] { repository.getPath() };
    indicator.setText(
        BzrVcsMessages.message("bzr4intellij.progress.updating", params2)
    );

//        BzrShowConfigCommand configCommand = new BzrShowConfigCommand(project);
//        String defaultPath = configCommand.getDefaultPath(repository);
//
//        if (StringUtils.isBlank(defaultPath)) {
//            Object[] params = new Object[] { repository.getPath() };
//            VcsException e = new VcsException(
//                    BzrVcsMessages.message("bzr4intellij.warning.no-default-update-path", params)
//            );
//            e.setIsWarning(true);
//            throw e;
//        }

    pull(repository, indicator);

//        String currentBranch = new BzrTagBranchCommand(project, repository).getCurrentBranch();
//        if (StringUtils.isBlank(currentBranch)) {
//            Object[] params = new Object[] { };
//            throw new VcsException(
//                    BzrVcsMessages.message("bzr4intellij.update.error.currentBranch", params)
//            );
//        }

//        //count heads in repository
//        List<BzrRevisionNumber> heads = new BzrHeadsCommand(project, repository).execute(currentBranch);
//        Object[] params1 = new Object[] { };
//        indicator.setText2(BzrVcsMessages.message("bzr4intellij.progress.countingHeads", params1));
//        if (heads.size() < 2) {
//            return;
//        }

//        if (heads.size() > 2) {
//            Object[] params = new Object[] { heads.size() };
//            throw new VcsException(
//                    BzrVcsMessages.message("bzr4intellij.update.error.manyHeads", params)
//            );
//        }
//
//        new BzrHeadMerger(project, new BzrMergeCommand(project, repository))
//                .merge(repository, updatedFiles, indicator, heads.get(heads.size() - 1));
  }

  private void pull(VirtualFile repo, ProgressIndicator indicator) throws VcsException {
    Object[] params = new Object[] { };
    indicator.setText2(BzrVcsMessages.message("bzr4intellij.progress.pull.with.update", params));
//        BzrPullCommand command = new BzrPullCommand(project, repo);
    BzrUpdateCommand command = new BzrUpdateCommand(project, repo);
//        command.setSource(new BzrShowConfigCommand(project).getDefaultPath(repo));
//        command.setUpdate(true);
//        command.setRebase(false);
    command.execute();
  }

}
