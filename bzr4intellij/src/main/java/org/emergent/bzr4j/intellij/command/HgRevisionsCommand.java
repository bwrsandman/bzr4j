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
import org.apache.commons.lang.StringUtils;
import org.emergent.bzr4j.intellij.HgFile;
import org.emergent.bzr4j.intellij.HgFileRevision;
import org.emergent.bzr4j.intellij.HgRevisionNumber;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

abstract class HgRevisionsCommand {

  private static final Logger LOG = Logger.getInstance(HgRevisionsCommand.class.getName());

  private static final String TEMPLATE =
    "{rev}|{node|short}|{date|isodate}|{author}|{branches}|{desc}\\n";

  private static final int REVISION_INDEX = 0;
  private static final int CHANGESET_INDEX = 1;
  private static final int DATE_INDEX = 2;
  private static final int AUTHOR_INDEX = 3;
  private static final int BRANCH_INDEX = 4;
  private static final int MESSAGE_INDEX = 5;
  private static final int ITEM_COUNT = 6;

  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");

  private final Project project;

  public HgRevisionsCommand(Project project) {
    this.project = project;
  }

  protected abstract HgCommandResult execute(
    HgCommandService service, VirtualFile repo, String template, int limit, HgFile hgFile
  );

  public final List<HgFileRevision> execute( HgFile hgFile, int limit) {
    if (limit <= REVISION_INDEX || hgFile == null || hgFile.getRepo() == null) {
      return Collections.emptyList();
    }

    HgCommandService hgCommandService = HgCommandService.getInstance(project);

    HgCommandResult result = execute(
      hgCommandService, hgFile.getRepo(), TEMPLATE, limit, hgFile
    );

    List<HgFileRevision> revisions = new LinkedList<HgFileRevision>();
    for (String line : result.getOutputLines()) {
      try {
        String[] attributes = StringUtils.splitPreserveAllTokens(line, '|');
        if (attributes.length != ITEM_COUNT) {
          LOG.warn("Wrong format. Skipping line " + line);
          continue;
        }
        HgRevisionNumber vcsRevisionNumber = HgRevisionNumber.getInstance(
          attributes[REVISION_INDEX],
          attributes[CHANGESET_INDEX]
        );
        Date revisionDate = DATE_FORMAT.parse(attributes[DATE_INDEX]);
        String author = attributes[AUTHOR_INDEX];
        String branchName = attributes[BRANCH_INDEX];
        String commitMessage = attributes[MESSAGE_INDEX];
        revisions.add(new HgFileRevision(project, hgFile, vcsRevisionNumber,
          branchName, revisionDate, author, commitMessage));
      } catch (NumberFormatException e) {
        LOG.warn("Error parsing rev in line " + line);
      } catch (ParseException e) {
        LOG.warn("Error parsing date in line " + line);
      }
    }
    return revisions;
  }


}
