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
package bazaar4idea;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcsUtil.VcsUtil;
import bazaar4idea.ui.BzrCurrentBranchStatus;

class BzrCurrentBranchStatusUpdater implements BzrUpdater {

  private final BzrCurrentBranchStatus hgCurrentBranchStatus;

  public BzrCurrentBranchStatusUpdater(BzrCurrentBranchStatus hgCurrentBranchStatus) {
    this.hgCurrentBranchStatus = hgCurrentBranchStatus;
  }

  public void update(Project project) {
    hgCurrentBranchStatus.setCurrentBranch(null);
    Editor textEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
    if (textEditor == null) {
      return;
    }
    Document document = textEditor.getDocument();
    VirtualFile file = FileDocumentManager.getInstance().getFile(document);

    VirtualFile repo = VcsUtil.getVcsRootFor(project, file);
    String branch = null;
    if (repo != null) {
// todo uncomment
//            BzrTagBranchCommand hgTagBranchCommand = new BzrTagBranchCommand(project, repo);
//            branch = hgTagBranchCommand.getCurrentBranch();
    }
    hgCurrentBranchStatus.setCurrentBranch(branch);
  }

}
