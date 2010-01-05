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
package org.emergent.bzr4j.intellij.data;

import org.emergent.bzr4j.commandline.CommandLineAnnotation;
import org.emergent.bzr4j.commandline.parser.XMLInfoParser;
import org.emergent.bzr4j.commandline.parser.XMLLogParser;
import org.emergent.bzr4j.commandline.parser.XMLStatusParser;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.IBazaarAnnotation;
import org.emergent.bzr4j.core.IBazaarInfo;
import org.emergent.bzr4j.core.IBazaarLogMessage;
import org.emergent.bzr4j.intellij.command.ShellCommandResult;

import java.util.List;

/**
 * @author Patrick Woodworth
 */
public class BzrParserUtil {

  public static IBazaarAnnotation parseXmlAnnotate(ShellCommandResult result) throws BazaarException {
    return CommandLineAnnotation.getAnnotationFromXml(result.getRawStdOut());
  }

  public static IBazaarInfo parseXmlInfo(ShellCommandResult result) throws BazaarException {
    return (new XMLInfoParser()).parse(result.getRawStdOut());
  }

  public static List<IBazaarLogMessage> parseXmlLog(ShellCommandResult result) throws BazaarException {
    return XMLLogParser.parse(result.getRawStdOut());
  }

  public static XMLStatusParser parseXmlStatus(ShellCommandResult result) throws BazaarException {
    XMLStatusParser retval = new XMLStatusParser();
    retval.parse(result.getRawStdOut());
    return retval;
  }
}
