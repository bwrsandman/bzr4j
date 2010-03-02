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
import org.emergent.bzr4j.intellij.BzrFile;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class BzrAddCommand {

  private final Project project;

  public BzrAddCommand(Project project) {
    this.project = project;
  }

  public void execute(@NotNull BzrFile bzrFile) {
    ShellCommandService.getInstance(project)
        .execute(bzrFile.getRepo(), "add", Arrays.asList(bzrFile.getRelativePath()));
  }

}