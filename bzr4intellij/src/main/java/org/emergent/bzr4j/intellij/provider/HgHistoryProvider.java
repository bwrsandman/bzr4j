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

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.history.FileHistoryPanel;
import com.intellij.openapi.vcs.history.HistoryAsTreeProvider;
import com.intellij.openapi.vcs.history.VcsDependentHistoryComponents;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.openapi.vcs.history.VcsHistoryProvider;
import com.intellij.openapi.vcs.history.VcsHistorySession;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcsUtil.VcsUtil;
import com.intellij.util.ui.ColumnInfo;
import org.emergent.bzr4j.intellij.HgFile;
import org.emergent.bzr4j.intellij.HgFileRevision;
import org.emergent.bzr4j.intellij.command.HgLogCommand;
import org.emergent.bzr4j.intellij.command.HgWorkingCopyRevisionsCommand;

import javax.swing.JComponent;
import java.util.List;
import java.util.LinkedList;

public class HgHistoryProvider implements VcsHistoryProvider {

  private static final int DEFAULT_LIMIT = 500;

  private final Project project;

  public HgHistoryProvider(Project project) {
    this.project = project;
  }

  public VcsDependentHistoryComponents getUICustomization(VcsHistorySession session,
    JComponent forShortcutRegistration) {
    return VcsDependentHistoryComponents.createOnlyColumns(new ColumnInfo[0]);
  }

  public AnAction[] getAdditionalActions(FileHistoryPanel panel) {
    return new AnAction[0];
  }

  public boolean isDateOmittable() {
    return false;
  }

  public String getHelpId() {
    return null;
  }

  public VcsHistorySession createSessionFor(FilePath filePath) throws VcsException {
    final VirtualFile vcsRoot = VcsUtil.getVcsRootFor(project, filePath);
    if (vcsRoot == null) {
      return null;
    }
    HgFile hgFile = new HgFile(vcsRoot, filePath);
    HgLogCommand logCommand = new HgLogCommand(project);
    logCommand.setFollowCopies(true);
    List<HgFileRevision> revisions = logCommand.execute(hgFile, DEFAULT_LIMIT);
    List<VcsFileRevision> result = new LinkedList<VcsFileRevision>();
    result.addAll(revisions);
    return new VcsHistorySession(result) {
      @Override
      protected VcsRevisionNumber calcCurrentRevisionNumber() {
        return new HgWorkingCopyRevisionsCommand(project).identify(vcsRoot);
      }
    };
  }

  public HistoryAsTreeProvider getTreeHistoryProvider() {
    return null;
  }

  public boolean supportsHistoryForDirectories() {
    return false;
  }
}
