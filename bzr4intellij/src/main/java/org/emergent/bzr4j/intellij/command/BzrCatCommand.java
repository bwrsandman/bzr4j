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
import org.emergent.bzr4j.intellij.BzrFile;
import org.emergent.bzr4j.intellij.BzrRevisionNumber;

import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

public class BzrCatCommand extends BzrAbstractCommand {

  public BzrCatCommand(Project project) {
    super(project);
  }

  public String execute(BzrFile hgFile, BzrRevisionNumber vcsRevisionNumber, Charset charset) {
    List<String> arguments = new LinkedList<String>();
    if (vcsRevisionNumber != null && StringUtils.isNotBlank(vcsRevisionNumber.asString())) {
      arguments.add("-r");
      arguments.add(vcsRevisionNumber.asString());
    }
    arguments.add(hgFile.getRelativePath());

    ShellCommandService service = ShellCommandService.getInstance(project);
    ShellCommandResult result = service.execute(hgFile.getRepo(), charset, "cat", arguments);
    return result.getStdOutAsString();
  }

}
