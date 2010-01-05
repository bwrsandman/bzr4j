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

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcsUtil.VcsUtil;
import org.emergent.bzr4j.intellij.BzrGlobalSettings;
import org.emergent.bzr4j.intellij.BzrVcsMessages;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class ShellCommandService {

  static final Logger LOG = Logger.getInstance(ShellCommandService.class.getName());

  private final Project project;
  private final BzrGlobalSettings settings;

  public ShellCommandService(Project project, BzrGlobalSettings settings) {
    this.project = project;
    this.settings = settings;
  }

  public static ShellCommandService getInstance(Project project) {
    return project.getComponent(ShellCommandService.class);
  }

  ShellCommandResult execute2(@NotNull VirtualFile repo, String operation, List<String> arguments) {
    ShellCommand shellCommand = new ShellCommand();
    shellCommand.setBad(true);
    String[] args = arguments == null ? new String[0] : arguments.toArray(new String[arguments.size()]);
    return execute(repo, shellCommand, operation, args);
  }

  ShellCommandResult execute(@NotNull VirtualFile repo, String operation, List<String> arguments) {
    return execute(repo, Charset.defaultCharset(), operation, arguments);
  }

  ShellCommandResult execute(@NotNull VirtualFile repo, Charset charset, String operation, List<String> arguments) {
    ShellCommand shellCommand = new ShellCommand(charset);
    return execute(repo, shellCommand, operation, arguments.toArray(new String[arguments.size()]));
  }

  ShellCommandResult execute(@NotNull VirtualFile repo, String operation, String... arguments) {
    ShellCommand shellCommand = new ShellCommand();
    return execute(repo, shellCommand, operation, arguments);
  }

  ShellCommandResult execute(@NotNull VirtualFile repo, ShellCommand shellCommand, String operation,
      String... arguments) {
    String repoPath = repo.getPath();
    File repoFile = repoPath != null ? new File(repoPath) : null;
    return execute(repoFile, shellCommand, operation, arguments);
  }

  @SuppressWarnings({ "ThrowableInstanceNeverThrown" })
  ShellCommandResult execute(File dir, ShellCommand shellCmd, String operation, String... arguments) {
    List<String> cmdLine = new LinkedList<String>();
    cmdLine.add(settings.getBzrExecutable());
    cmdLine.add("--no-aliases");
    cmdLine.add(operation);
    if (arguments != null && arguments.length != 0) {
      cmdLine.addAll(Arrays.asList(arguments));
    }
    if (shellCmd.isBad()) {
      Exception e = new Exception("badcmd: " + cmdLine.toString());
      LOG.debug(e);
    } else {
      try {
        LOG.debug(String.format("(%s) : %s", String.valueOf(dir), cmdLine.toString()));
        return shellCmd.execute(dir, cmdLine);
      } catch (ShellCommandException e) {
        showError(e);
        LOG.error("ShellCommandException", e);
      }
    }
    return ShellCommandResult.EMPTY;
  }

  private void showError(Exception e) {
    StringBuilder message = new StringBuilder();
    Object[] params1 = new Object[] { settings.getBzrExecutable() };
    message.append(BzrVcsMessages.message("bzr4intellij.command.executable.error", params1))
        .append("\n")
        .append("Original Error:\n")
        .append(e.getMessage());

    Object[] params = new Object[] { };
    VcsUtil.showErrorMessage(
        project,
        message.toString(),
        BzrVcsMessages.message("bzr4intellij.error", params)
    );
  }

}
