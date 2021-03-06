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
package bazaar4idea.provider.commit;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.CommitSession;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.changes.VcsDirtyScopeManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcsUtil.VcsImplUtil;
import com.intellij.vcsUtil.VcsUtil;
import org.apache.commons.lang.StringUtils;
import bazaar4idea.BzrVcsMessages;
import bazaar4idea.command.BzrCommandException;
import bazaar4idea.command.BzrCommitCommand;

import javax.swing.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class BzrCommitSession implements CommitSession {

  private final Project project;

  public BzrCommitSession(Project project) {
    this.project = project;
  }

  @Deprecated
  public JComponent getAdditionalConfigurationUI() {
    return null;
  }

  public JComponent getAdditionalConfigurationUI(Collection<Change> changes, String commitMessage) {
    return null;
  }

  public boolean canExecute(Collection<Change> changes, String commitMessage) {
    return changes != null && !changes.isEmpty() && StringUtils.isNotBlank(commitMessage);
  }

  public void execute(Collection<Change> changes, String commitMessage) {
    for (VirtualFile root : extractRoots(changes)) {
      BzrCommitCommand command = new BzrCommitCommand(project, root, commitMessage);
      try {
        command.execute();
        Object[] params = new Object[] { root.getPath() };
        VcsUtil.showStatusMessage(
            project, BzrVcsMessages.message("bzr4intellij.commit.success", params)
        );
        VcsDirtyScopeManager vcsDirtyScopeManager = VcsDirtyScopeManager.getInstance(project);
        vcsDirtyScopeManager.dirDirtyRecursively(root);
        root.refresh(true, true);
      } catch (BzrCommandException e) {
        VcsImplUtil.showErrorMessage(project, e.getMessage(), "Error");
      }
    }
  }

  public void executionCanceled() {
  }

  private Set<VirtualFile> extractRoots(Collection<Change> changes) {
    Set<VirtualFile> result = new HashSet<VirtualFile>();
    for (Change change : changes) {
      ContentRevision afterRevision = change.getAfterRevision();
      ContentRevision beforeRevision = change.getBeforeRevision();

      FilePath filePath = null;
      if (afterRevision != null) {
        filePath = afterRevision.getFile();
      } else if (beforeRevision != null) {
        filePath = beforeRevision.getFile();
      }

      if (filePath == null || filePath.isDirectory()) {
        continue;
      }

      VirtualFile vcsRoot = VcsUtil.getVcsRootFor(project, filePath);
      if (vcsRoot != null) {
        result.add(vcsRoot);
      }
    }
    return result;
  }

  public String getHelpId() {
    return null;
  }
}
