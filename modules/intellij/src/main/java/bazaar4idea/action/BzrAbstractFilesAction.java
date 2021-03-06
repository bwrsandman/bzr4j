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

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.AbstractVcsHelper;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.TransactionRunnable;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.VcsDirtyScopeManager;
import com.intellij.openapi.vfs.VirtualFile;
import bazaar4idea.BzrVcs;

import java.util.LinkedList;
import java.util.List;

abstract class BzrAbstractFilesAction extends AnAction {

  protected abstract boolean isEnabled(Project project, BzrVcs vcs, VirtualFile file);

  protected abstract void batchPerform(Project project, final BzrVcs activeVcs,
      List<VirtualFile> files, DataContext context) throws VcsException;

  protected void performRefresh(Project project, final VirtualFile[] files, List<VirtualFile> enabledFiles)
      throws VcsException {
    ApplicationManager.getApplication().runWriteAction(new Runnable() {
      public void run() {
        for (VirtualFile file : files) {
          file.refresh(false, true);
        }
      }
    });

    for (VirtualFile file : enabledFiles) {
      doVcsRefresh(project, file);
    }
  }

  public final void actionPerformed(AnActionEvent event) {
    final DataContext dataContext = event.getDataContext();

    final Project project = PlatformDataKeys.PROJECT.getData(dataContext);
    final VirtualFile[] files = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);
    if (project == null || files == null || files.length == 0) {
      return;
    }

    project.save();

    final BzrVcs vcs = getBzrVcs(project);
    if (!ProjectLevelVcsManager.getInstance(project).checkAllFilesAreUnder(vcs, files)) {
      return;
    }

    final AbstractVcsHelper helper = AbstractVcsHelper.getInstance(project);
    List<VcsException> exceptions = helper.runTransactionRunnable(vcs, new TransactionRunnable() {
      public void run(List<VcsException> exceptions) {
        try {
          execute(project, vcs, files, dataContext);
        } catch (VcsException ex) {
          exceptions.add(ex);
        }
      }
    }, null);

    helper.showErrors(exceptions, vcs.getName());
  }

  public final void update(AnActionEvent e) {
    super.update(e);

    Presentation presentation = e.getPresentation();
    final DataContext dataContext = e.getDataContext();

    Project project = PlatformDataKeys.PROJECT.getData(dataContext);
    if (project == null) {
      presentation.setEnabled(false);
      return;
    }

    VirtualFile[] files = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);
    if (files == null || files.length == 0) {
      presentation.setEnabled(false);
      return;
    }

    BzrVcs vcs = getBzrVcs(project);
    if (!vcs.isStarted()) {
      presentation.setEnabled(false);
      return;
    }

    if (!ProjectLevelVcsManager.getInstance(project).checkAllFilesAreUnder(vcs, files)) {
      presentation.setEnabled(false);
      return;
    }

    boolean enabled = false;
    for (VirtualFile file : files) {
      boolean fileEnabled = isEnabled(project, vcs, file);
      if (fileEnabled) {
        enabled = true;
        break;
      }
    }

    presentation.setEnabled(enabled);
  }

  private void execute(Project project, final BzrVcs activeVcs, final VirtualFile[] files, DataContext context)
      throws VcsException {

    List<VirtualFile> enabledFiles = new LinkedList<VirtualFile>();
    for (VirtualFile file : files) {
      if (isEnabled(project, activeVcs, file)) {
        enabledFiles.add(file);
      }
    }

    batchPerform(project, activeVcs, enabledFiles, context);

    performRefresh(project, files, enabledFiles);
  }

  protected final void doVcsRefresh(final Project project, final VirtualFile file) {
    VcsDirtyScopeManager.getInstance(project).fileDirty(file);
  }

  private BzrVcs getBzrVcs(Project project) {
    return (BzrVcs)ProjectLevelVcsManager.getInstance(project).findVcsByName(BzrVcs.VCS_NAME);
  }

}
