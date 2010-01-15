/*
 * Copyright (c) 2010 Emergent.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.emergent.bzr4j.intellij.command;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.StringUtils;
import org.emergent.bzr4j.core.BzrHandlerException;
import org.emergent.bzr4j.intellij.BzrRevisionNumber;
import org.emergent.bzr4j.intellij.BzrUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * @author Patrick Woodworth
 */
public class BzrMiscCommand {

  public static BzrRevisionNumber revno(Project project, @NotNull VirtualFile repo) {
    ShellCommandService commandService = ShellCommandService.getInstance(project);
    ShellCommandResult result = commandService.execute(repo, "revno");
    List<String> lines = result.getOutputLines();
    if (!lines.isEmpty()) {
      String[] parts = StringUtils.split(lines.get(0), ' ');
      return BzrRevisionNumber.getLocalInstance(parts[0]);
    }
    return null;
  }

  public static boolean isIgnored(Project project, VirtualFile file) {
//    VirtualFile repo = VcsUtil.getVcsRootFor(project, file);
    VirtualFile repo = BzrUtil.bzrRootOrNull(file);
    if (repo == null)
      return false;
    BzrIntellijHandler handler = new BzrIntellijHandler(project,repo,"is-ignored");
    handler.setExitValueValidationEnabled(false);
    handler.addRelativePaths(repo,file);
    try {
      ShellCommandResult result = handler.execij();
      if (result.getExitValue() == 1)
        return true;
    } catch (BzrHandlerException ignored) {
    }
    return false;
  }

  public static boolean isVersioned(Project project, VirtualFile file) {
//    VirtualFile repo = VcsUtil.getVcsRootFor(project, file);
    VirtualFile repo = BzrUtil.bzrRootOrNull(file);
    if (repo == null)
      return false;
    BzrIntellijHandler handler = new BzrIntellijHandler(project,repo,"is-versioned");
    handler.setExitValueValidationEnabled(false);
    handler.addRelativePaths(repo,file);
    try {
      ShellCommandResult result = handler.execij();
      if (result.getExitValue() == 1)
        return true;
    } catch (BzrHandlerException ignored) {
    }
    return false;
  }
}
