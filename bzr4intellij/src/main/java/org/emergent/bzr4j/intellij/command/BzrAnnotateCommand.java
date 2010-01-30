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
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.IBazaarAnnotation;
import org.emergent.bzr4j.core.xmloutput.XmlOutputParser;
import org.emergent.bzr4j.intellij.BzrFile;
import org.emergent.bzr4j.intellij.BzrGlobalSettings;
import org.emergent.bzr4j.intellij.BzrRevisionNumber;
import org.emergent.bzr4j.intellij.provider.annotate.BzrAnnotationLine;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BzrAnnotateCommand extends BzrAbstractCommand {

  public BzrAnnotateCommand(Project project) {
    super(project);
  }

  public List<BzrAnnotationLine> execute(@NotNull BzrFile hgFile) {
    ShellCommandService service = ShellCommandService.getInstance(project);
    ShellCommandResult result = service.execute(
        hgFile.getRepo(), "xmlannotate", Arrays.asList(hgFile.getRelativePath())
    );

    List<BzrAnnotationLine> annotations = new ArrayList<BzrAnnotationLine>();
    try {
      IBazaarAnnotation lm = XmlOutputParser.parseXmlAnnotate(result);
      int lineCount = lm.getNumberOfLines();
      for (int ii = 0; ii < lineCount; ii++) {
        BzrRevisionNumber revision = BzrRevisionNumber.getLocalInstance(lm.getRevision(ii));
        String date = lm.getDate(ii);
        String user = lm.getAuthor(ii);
        int atIdx = user.indexOf('@');
        if (atIdx > 0 && BzrGlobalSettings.getInstance().isAnnotationTrimmingEnabled()) {
          user = user.substring(0, atIdx);
        }
        String content = lm.getline(ii);
        BzrAnnotationLine annotationLine = new BzrAnnotationLine(
            user, revision, date, ii, content
        );
        annotations.add(annotationLine);
      }
    } catch (BazaarException e) {
      e.printStackTrace();
    }

//        for (String line : result.getOutputLines()) {
//            Matcher matcher = LINE_PATTERN.matcher(line);
//            if (matcher.matches()) {
//                String user = matcher.group(USER_GROUP);
//                BzrRevisionNumber revision = BzrRevisionNumber.getInstance(
//                        matcher.group(REVISION_GROUP),
//                        matcher.group(CHANGESET_GROUP)
//                );
//                String date = matcher.group(DATE_GROUP);
//                Integer lineNumber = Integer.valueOf(matcher.group(LINE_NUMBER_GROUP));
//                String content = matcher.group(CONTENT_GROUP);
//                BzrAnnotationLine annotationLine = new BzrAnnotationLine(
//                        user, revision, date, lineNumber, content
//                );
//                annotations.add(annotationLine);
//            }
//        }
    return annotations;
  }

}
