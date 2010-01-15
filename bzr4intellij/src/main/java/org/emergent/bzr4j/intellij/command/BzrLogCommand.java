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
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.IBazaarLogMessage;
import org.emergent.bzr4j.core.commandline.parser.XmlOutputUtil;
import org.emergent.bzr4j.intellij.BzrFile;
import org.emergent.bzr4j.intellij.BzrFileRevision;
import org.emergent.bzr4j.intellij.BzrRevisionNumber;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class BzrLogCommand {

  private static final Logger LOG = Logger.getInstance(BzrLogCommand.class.getName());

  private static final String TEMPLATE =
      "{rev}|{node|short}|{date|isodate}|{author}|{branches}|{desc}\\n";

  private static final int REVISION_INDEX = 0;

  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");

  private final Project project;

  private boolean followCopies;

  public BzrLogCommand(Project project) {
    this.project = project;
  }

  public void setFollowCopies(boolean followCopies) {
    this.followCopies = followCopies;
  }

  public final List<BzrFileRevision> execute(BzrFile hgFile, int limit) {
    if (limit <= REVISION_INDEX || hgFile == null || hgFile.getRepo() == null) {
      return Collections.emptyList();
    }

    ShellCommandService hgCommandService = ShellCommandService.getInstance(project);

    List<String> arguments = new LinkedList<String>();
    arguments.add(hgFile.getRelativePath());
    ShellCommandResult result = hgCommandService.execute(hgFile.getRepo(), "xmllog", arguments);

    List<BzrFileRevision> revisions = new LinkedList<BzrFileRevision>();
    try {
      List<IBazaarLogMessage> protorevs = XmlOutputUtil.parseXmlLog(result);
      for (IBazaarLogMessage lm : protorevs) {
        BzrRevisionNumber vcsRevisionNumber = BzrRevisionNumber.getLocalInstance(lm.getRevision().getValue());
        Date revisionDate = lm.getDate();
        String author = lm.getCommiter();
        String branchName = lm.getBranchNick();
        String commitMessage = lm.getMessage();
        revisions.add(new BzrFileRevision(project, hgFile, vcsRevisionNumber,
            branchName, revisionDate, author, commitMessage));
      }
    } catch (BazaarException e) {
      e.printStackTrace();
    }
    return revisions;
  }

}
