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
import org.apache.commons.lang.StringUtils;
import org.emergent.bzr4j.intellij.BzrRevisionNumber;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BzrWorkingCopyRevisionsCommand {

  private final Project project;

  public BzrWorkingCopyRevisionsCommand(Project project) {
    this.project = project;
  }

  public BzrRevisionNumber parent(@NotNull VirtualFile repo) {
    return getRevision(repo, "parents");
  }

  public BzrRevisionNumber tip(@NotNull VirtualFile repo) {
    return revno(repo);
  }

  public BzrRevisionNumber revno(@NotNull VirtualFile repo) {
    ShellCommandService commandService = ShellCommandService.getInstance(project);
    ShellCommandResult result = commandService.execute(
        repo, "revno", Collections.<String>emptyList()
    );
    List<String> lines = result.getOutputLines();
    if (!lines.isEmpty()) {
      String[] parts = StringUtils.split(lines.get(0), ' ');
      return BzrRevisionNumber.getLocalInstance(parts[0]);
    }
    return null;
  }

  public BzrRevisionNumber identify(@NotNull VirtualFile repo) {
    ShellCommandService commandService = ShellCommandService.getInstance(project);
    ShellCommandResult result = commandService.execute2(
        repo, "identify", Arrays.asList("--num", "--id")
    );
    List<String> lines = result.getOutputLines();
    if (!lines.isEmpty()) {
      String[] parts = StringUtils.split(lines.get(0), ' ');
      if (parts.length >= 2) {
        return BzrRevisionNumber.getInstance(parts[1], parts[0]);
      }
    }
    return null;
  }

  private BzrRevisionNumber getRevision(VirtualFile repo, String command) {
    ShellCommandService commandService = ShellCommandService.getInstance(project);
    ShellCommandResult result = commandService.execute2(
        repo, command, Arrays.asList("--template", "{rev}|{node|short}\\n")
    );
    List<String> lines = result.getOutputLines();
    if (!lines.isEmpty()) {
      String[] parts = StringUtils.split(lines.get(0), '|');
      return BzrRevisionNumber.getInstance(parts[0], parts[1]);
    }
    return null;
  }

}
