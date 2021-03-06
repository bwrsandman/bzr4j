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
package bazaar4idea.action;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcsUtil.VcsUtil;
import bazaar4idea.BzrVcs;
import bazaar4idea.command.BzrResolveCommand;

import java.util.List;

public class BzrMarkResolved extends BzrAbstractFilesAction {

  protected boolean isEnabled(Project project, BzrVcs vcs, VirtualFile file) {
    final FileStatus fileStatus = FileStatusManager.getInstance(project).getStatus(file);
    return fileStatus != null && FileStatus.MERGED_WITH_CONFLICTS.equals(fileStatus);
  }

  protected void batchPerform(Project project, BzrVcs activeVcs, List<VirtualFile> files, DataContext context)
      throws VcsException {
    BzrResolveCommand resolveCommand = new BzrResolveCommand(project);
    for (VirtualFile file : files) {
      VirtualFile root = VcsUtil.getVcsRootFor(project, file);
      if (root == null) {
        return;
      }
      resolveCommand.resolve(root, file);
    }
  }

  @Override
  protected void performRefresh(Project project, final VirtualFile[] files, final List<VirtualFile> enabledFiles)
      throws VcsException {
//        super.performRefresh(project, files, enabledFiles);
    ApplicationManager.getApplication().runWriteAction(new Runnable() {
      public void run() {
        for (VirtualFile file : files) {
          file.refresh(false, true);
        }
        for (VirtualFile file : enabledFiles) {
          file.getParent().refresh(false, false);
        }
      }
    });

    for (VirtualFile file : enabledFiles) {
      doVcsRefresh(project, file);
    }
  }
}
