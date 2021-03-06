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
package bazaar4idea.command;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.StringUtils;
import org.emergent.bzr4j.core.cli.BzrStandardResult;
import bazaar4idea.BzrFile;
import bazaar4idea.data.BzrResolveStatusEnum;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BzrResolveCommand {

  private static final int ITEM_COUNT = 3;

  private final Project project;

  public BzrResolveCommand(Project project) {
    this.project = project;
  }

  public Map<BzrFile, BzrResolveStatusEnum> list(VirtualFile repo) {
    if (repo == null) {
      return Collections.emptyMap();
    }

    BzrStandardResult result = ShellCommandService.getInstance(project)
        .execute2(repo, "resolve", Arrays.asList("--list"));

    Map<BzrFile, BzrResolveStatusEnum> resolveStatus = new HashMap<BzrFile, BzrResolveStatusEnum>();
    for (String line : result.getStdOutAsLines()) {
      if (StringUtils.isBlank(line) || line.length() < ITEM_COUNT) {
        continue;
      }
      BzrResolveStatusEnum status = BzrResolveStatusEnum.valueOf(line.charAt(0));
      if (status != null) {
        File ioFile = new File(repo.getPath(), line.substring(2));
        resolveStatus.put(new BzrFile(repo, ioFile), status);
      }
    }
    return resolveStatus;
  }

  public void resolve(VirtualFile repo, VirtualFile path) {
    ShellCommandService.getInstance(project).execute(repo, "resolve", Arrays.asList(path.getPath()));
  }

}
