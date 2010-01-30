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
package org.emergent.bzr4j.intellij;

import com.intellij.openapi.project.Project;
import org.emergent.bzr4j.intellij.command.ShellCommandService;
import org.emergent.bzr4j.intellij.ui.BzrSetExecutableDialog;

class BzrExecutableValidator {

  private final Project project;

  public BzrExecutableValidator(Project project) {
    this.project = project;
  }

  public boolean check(BzrGlobalSettings globalSettings) {
    if (ShellCommandService.isValid(globalSettings.getBzrExecutable())) {
      return true;
    }

    String previousHgPath = globalSettings.getBzrExecutable();
    boolean validHgExecutable;
    BzrSetExecutableDialog dialog;
    do {
      dialog = new BzrSetExecutableDialog(project);
      dialog.setBadHgPath(previousHgPath);
      dialog.show();
      validHgExecutable = dialog.isOK() && ShellCommandService.isValid(dialog.getNewHgPath());
      previousHgPath = dialog.getNewHgPath();
    } while (!validHgExecutable && dialog.isOK());

    if (validHgExecutable) {
      globalSettings.setBzrExecutable(dialog.getNewHgPath());
      return true;
    }

    return false;
  }

}
