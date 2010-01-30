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
package org.emergent.bzr4j.intellij.command;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class BzrUpdateCommand {

  private final Project project;
  private final VirtualFile repo;

  private String branch;
  private String revision;
  private boolean clean;

  public BzrUpdateCommand(Project project, @NotNull VirtualFile repo) {
    this.project = project;
    this.repo = repo;
  }

  public void setBranch(String branch) {
    this.branch = branch;
  }

  public void setRevision(String revision) {
    this.revision = revision;
  }

  public void setClean(boolean clean) {
    this.clean = clean;
  }

  public ShellCommandResult execute() {
//        List<String> arguments = new LinkedList<String>();
//        if (clean) {
//            arguments.add("--clean");
//        }
//
//        if (StringUtils.isNotBlank(revision)) {
//            arguments.add("--rev");
//            arguments.add(revision);
//        } else if (StringUtils.isNotBlank(branch)) {
//            arguments.add(branch);
//        }

    BzrIntellijHandler shellCommand = ShellCommandService.getInstance(project).createCommand(repo, "update");
    shellCommand.setStderrValidationEnabled(false);
    // todo parse the status log found in stderr
    return ShellCommandService.getInstance(project).execute(shellCommand);
  }

}