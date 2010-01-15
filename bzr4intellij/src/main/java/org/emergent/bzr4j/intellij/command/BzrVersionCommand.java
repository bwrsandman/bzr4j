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

import org.emergent.bzr4j.core.BzrHandlerException;

import java.util.Arrays;
import java.util.List;

public class BzrVersionCommand extends BzrAbstractCommand {

  public BzrVersionCommand() {
    super(null);
  }

  public boolean isValid(final String executable) {
    try {
      BzrIntellijHandler shellCommand = new BzrIntellijHandler(null,"version") {
        @Override
        protected String getBzrExecutablePath() {
          return executable;
        }
      };
      shellCommand.setExitValueValidationEnabled(true);
      shellCommand.setStderrValidationEnabled(true);
      shellCommand.execij();
      return true;
    } catch (BzrHandlerException e) {
      LOG.error(e.getMessage(), e);
      return false;
    }
  }
}
