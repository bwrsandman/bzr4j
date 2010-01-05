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
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.StringUtils;
import org.emergent.bzr4j.commandline.parser.XMLStatusParser;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.IBazaarStatus;
import org.emergent.bzr4j.intellij.BzrFile;
import org.emergent.bzr4j.intellij.data.BzrChange;
import org.emergent.bzr4j.intellij.data.BzrFileStatusEnum;
import org.emergent.bzr4j.intellij.data.BzrParserUtil;

import java.io.File;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class BzrStatusCommand extends BzrAbstractCommand {

  private static final int ITEM_COUNT = 4;
  private static final String RENAME_ARROW = " => ";

  private boolean includeUnknown = true;
  private boolean includeIgnored = true;
  private static final String CONFLICTED_GARBAGE_TEXT = "Text conflict in ";

  public BzrStatusCommand(Project project) {
    super(project);
  }

  public void setIncludeUnknown(boolean includeUnknown) {
    this.includeUnknown = includeUnknown;
  }

  public void setIncludeIgnored(boolean includeIgnored) {
    this.includeIgnored = includeIgnored;
  }

  public Set<BzrChange> executeOld(VirtualFile repo) {
    if (repo == null) {
      return Collections.emptySet();
    }

    ShellCommandService service = ShellCommandService.getInstance(project);

    List<String> arguments = new LinkedList<String>();
    arguments.add("-S");
    ShellCommandResult result = service.execute(repo, "status", arguments);
    Set<BzrChange> changes = new HashSet<BzrChange>();
    for (String line : result.getOutputLines()) {
      if (StringUtils.isBlank(line) || line.length() < ITEM_COUNT) {
        LOG.warn("Unexpected line in status '" + line + '\'');
        continue;
      }
      EnumSet<BzrFileStatusEnum> statusSet = EnumSet.noneOf(BzrFileStatusEnum.class);
      for (int ii = 0; ii < 3; ii++) {
        BzrFileStatusEnum status = BzrFileStatusEnum.valueOf(line.charAt(ii));
        if (status == null)
          continue;
        statusSet.add(status);
      }

      String path = line.substring(4);
      if (statusSet.contains(BzrFileStatusEnum.CONFLICTED) && (path.startsWith(CONFLICTED_GARBAGE_TEXT))) {
        path = path.substring(CONFLICTED_GARBAGE_TEXT.length());
      }
      String origPath = null;
      int arrowIdx = path.indexOf(RENAME_ARROW);
      if (arrowIdx > 0) {
        origPath = path.substring(0, arrowIdx);
        path = path.substring(arrowIdx + RENAME_ARROW.length());
      }
      File ioFile = new File(repo.getPath(), path);
      BzrChange change = new BzrChange(new BzrFile(repo, ioFile), statusSet);
      if (origPath != null) {
        change.setBeforeFile(new BzrFile(repo, new File(repo.getPath(), origPath)));
      }
      changes.add(change);
    }
    return changes;
  }

  public Set<BzrChange> execute(VirtualFile repo) {
    if (repo == null) {
      return Collections.emptySet();
    }

    ShellCommandService service = ShellCommandService.getInstance(project);

    List<String> arguments = new LinkedList<String>();
    arguments.add(".");
    ShellCommandResult result = service.execute(repo, "xmlstatus", arguments);
    Set<BzrChange> changes = new HashSet<BzrChange>();
    try {
      XMLStatusParser parser = BzrParserUtil.parseXmlStatus(result);
      Set<IBazaarStatus> statii = parser.getStatusSet();
      for (IBazaarStatus bzrStatus : statii) {
        EnumSet<BzrFileStatusEnum> statusSet = EnumSet.noneOf(BzrFileStatusEnum.class);
        String shortStatus = bzrStatus.getShortStatus();
        for (char c : shortStatus.toCharArray()) {
          BzrFileStatusEnum status = BzrFileStatusEnum.valueOf(c);
          if (status == null)
            continue;
          statusSet.add(status);
        }
        File statusBranchRoot = bzrStatus.getBranchRoot();
        String newPath = bzrStatus.getPath();
        LOG.debug(String.format("newpath: \"%s\"", newPath));
        File ioFile = new File(statusBranchRoot, newPath);
        BzrChange change = new BzrChange(new BzrFile(repo, ioFile), statusSet);
        String oldPath = bzrStatus.getPreviousPath();
        if (!StringUtil.isEmpty(oldPath)) {
          LOG.debug(String.format("oldpath: \"%s\"", oldPath));
          change.setBeforeFile(new BzrFile(repo, new File(statusBranchRoot, oldPath)));
        }
        changes.add(change);
      }
    } catch (BazaarException e) {
      LOG.error(e);
      return Collections.emptySet();
    }
    return changes;
  }

}
