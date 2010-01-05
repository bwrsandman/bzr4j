/*
 * Copyright (c) 2009 Emergent.org
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
import org.emergent.bzr4j.intellij.BzrFile;
import org.emergent.bzr4j.intellij.data.BzrChange;
import org.emergent.bzr4j.intellij.data.BzrFileStatusEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

public class BzrLsCommand extends BzrAbstractCommand {

  public BzrLsCommand(Project project) {
    super(project);
  }

  public List<BzrChange> execute(VirtualFile repo) {
    if (repo == null) {
      return Collections.emptyList();
    }

    ShellCommandService service = ShellCommandService.getInstance(project);

    List<String> arguments = new LinkedList<String>();
    arguments.add("--recursive");
    arguments.add("--ignored");

    ShellCommandResult result = service.execute(repo, "ls", arguments);
    List<BzrChange> changes = new ArrayList<BzrChange>();
    for (String line : result.getOutputLines()) {
      if (StringUtils.isBlank(line)) {
        LOG.warn("Unexpected line in status '" + line + '\'');
        continue;
      }
      File ioFile = new File(repo.getPath(), line);
      BzrFile change = new BzrFile(repo, ioFile);
      changes.add(new BzrChange(change, EnumSet.of(BzrFileStatusEnum.IGNORED)));
    }
    return changes;
  }

}
