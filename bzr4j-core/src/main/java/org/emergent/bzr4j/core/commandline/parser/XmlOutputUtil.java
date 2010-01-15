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
package org.emergent.bzr4j.core.commandline.parser;

import org.emergent.bzr4j.core.BzrHandlerResult;
import org.emergent.bzr4j.core.IBazaarItemInfo;
import org.emergent.bzr4j.core.IPlugin;
import org.emergent.bzr4j.core.commandline.parser.CommandLineAnnotation;
import org.emergent.bzr4j.core.commandline.parser.XMLInfoParser;
import org.emergent.bzr4j.core.commandline.parser.XMLLogParser;
import org.emergent.bzr4j.core.commandline.parser.XMLStatusParser;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.IBazaarAnnotation;
import org.emergent.bzr4j.core.IBazaarInfo;
import org.emergent.bzr4j.core.IBazaarLogMessage;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Patrick Woodworth
 */
public class XmlOutputUtil {

  public static IBazaarAnnotation parseXmlAnnotate(BzrHandlerResult result) throws BazaarException {
    return CommandLineAnnotation.getAnnotationFromXml(result.getStdOutAsString());
  }

  public static IBazaarInfo parseXmlInfo(BzrHandlerResult result) throws BazaarException {
    return (new XMLInfoParser()).parse(result.getStdOutAsString());
  }

  public static List<IBazaarLogMessage> parseXmlLog(BzrHandlerResult result) throws BazaarException {
    return XMLLogParser.parse(result.getStdOutAsString());
  }

  public static List<IBazaarItemInfo> parseXmlLs(BzrHandlerResult result) throws BazaarException {
    return XMLLsParser.parse(result.getStdOutAsString());
  }

  public static Map<String,List<IBazaarLogMessage>> parseXmlMissing(BzrHandlerResult result) throws BazaarException {
    XMLMissingParser parser = new XMLMissingParser();
    return parser.parse(result.getStdOutAsString());
  }

  public static Set<IPlugin> parseXmlPlugin(BzrHandlerResult result) throws BazaarException {
    XMLPluginParser parser = new XMLPluginParser();
    return parser.parse(result.getStdOutAsString());
  }

  public static XmlStatusResult parseXmlStatus(BzrHandlerResult result) throws BazaarException {
    XMLStatusParser parser = new XMLStatusParser();
    parser.parse(result.getStdOutAsString());
    return parser;
  }
}
