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
import org.apache.commons.lang.StringUtils;
import org.emergent.bzr4j.intellij.HgFile;
import org.emergent.bzr4j.intellij.HgRevisionNumber;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class HgCatCommand {

  private final Project project;

  public HgCatCommand(Project project) {
    this.project = project;
  }

  public String execute( HgFile hgFile, HgRevisionNumber vcsRevisionNumber, Charset charset) {
    List<String> arguments = new LinkedList<String>();
    if (StringUtils.isNotBlank(vcsRevisionNumber.getChangeset())) {
      arguments.add("--rev");
      arguments.add(vcsRevisionNumber.getChangeset());
    }
    arguments.add(hgFile.getRelativePath());

    HgCommandService service = HgCommandService.getInstance(project);
    HgCommandResult result = service.execute(
      hgFile.getRepo(), Collections.<String>emptyList(), "cat", arguments, charset
    );
    return result.getRawOutput();
  }

}
