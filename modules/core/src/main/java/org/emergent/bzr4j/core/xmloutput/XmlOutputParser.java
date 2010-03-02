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
package org.emergent.bzr4j.core.xmloutput;

import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.IBazaarAnnotation;
import org.emergent.bzr4j.core.IBazaarInfo;
import org.emergent.bzr4j.core.IBazaarItemInfo;
import org.emergent.bzr4j.core.IBazaarLogMessage;
import org.emergent.bzr4j.core.IBazaarPlugin;
import org.emergent.bzr4j.core.cli.BzrHandlerResult;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Patrick Woodworth
 */
public class XmlOutputParser {

  private XmlOutputParser() {
  }

  public static Properties parseXmlVersion(Reader xml) throws BazaarException {
    XmlVersionParser parser = new XmlVersionParser();
    return parser.parse(xml);
  }

  public static IBazaarAnnotation parseXmlAnnotate(String stdOut) throws BazaarException {
    return XmlBazaarAnnotation.getAnnotationFromXml(stdOut);
  }

  public static IBazaarInfo parseXmlInfo(String stdOut) throws BazaarException {
    return (new XmlInfoParser()).parse(stdOut);
  }

  public static List<IBazaarLogMessage> parseXmlLog(String stdOut) throws BazaarException {
    return parseXmlLog(stdOut,true);
  }

  public static List<IBazaarLogMessage> parseXmlLog(String stdOut, boolean merges) throws BazaarException {
    List<IBazaarLogMessage> retval = XmlLogParser.parse(stdOut);
    if (!merges) {
      ListIterator<IBazaarLogMessage> iter = retval.listIterator();
      for (; iter.hasNext(); ) {
        IBazaarLogMessage logMsg = iter.next();
        String revno = logMsg.getRevision().getValue();
        if (revno.indexOf('.') > -1) {
          iter.remove();
        }
      }
    }
    return retval;
  }

  public static List<IBazaarItemInfo> parseXmlLs(String stdOut) throws BazaarException {
//    System.out.println("stdOut (parseXmlLs):\n" + stdOut);
    return XmlLsParser.parse(stdOut);
  }

  public static Map<String,List<IBazaarLogMessage>> parseXmlMissing(String stdOut) throws BazaarException {
    XmlMissingParser parser = new XmlMissingParser();
    return parser.parse(stdOut);
  }

  public static Set<IBazaarPlugin> parseXmlPlugin(String stdOut) throws BazaarException {
    XmlPluginParser parser = new XmlPluginParser();
    return parser.parse(stdOut);
  }

  public static XmlStatusResult parseXmlStatus(String stdOut) throws BazaarException {
    XmlStatusParser parser = new XmlStatusParser();
    parser.parse(stdOut);
    return parser;
  }

  public static Properties parseXmlVersion(String stdOut) throws BazaarException {
//    System.out.println("stdOut (parseXmlVersion):\n" + stdOut);
    return parseXmlVersion(new StringReader(stdOut));
  }

  public static IBazaarAnnotation parseXmlAnnotate(BzrHandlerResult result) throws BazaarException {
    return parseXmlAnnotate(result.getStdOutAsString());
  }

  public static IBazaarInfo parseXmlInfo(BzrHandlerResult result) throws BazaarException {
    return parseXmlInfo(result.getStdOutAsString());
  }

  public static List<IBazaarLogMessage> parseXmlLog(BzrHandlerResult result) throws BazaarException {
    return parseXmlLog(result,true);
  }

  public static List<IBazaarLogMessage> parseXmlLog(BzrHandlerResult result, boolean merges) throws BazaarException {
    return parseXmlLog(result.getStdOutAsString(),merges);
  }

  public static List<IBazaarItemInfo> parseXmlLs(BzrHandlerResult result) throws BazaarException {
    return parseXmlLs(result.getStdOutAsString());
  }

  public static Map<String,List<IBazaarLogMessage>> parseXmlMissing(BzrHandlerResult result) throws BazaarException {
    return parseXmlMissing(result.getStdOutAsString());
  }

  public static Set<IBazaarPlugin> parseXmlPlugin(BzrHandlerResult result) throws BazaarException {
    return parseXmlPlugin(result.getStdOutAsString());
  }

  public static XmlStatusResult parseXmlStatus(BzrHandlerResult result) throws BazaarException {
    return parseXmlStatus(result.getStdOutAsString());
  }

  public static Properties parseXmlVersion(BzrHandlerResult result) throws BazaarException {
    return parseXmlVersion(result.getStdOutAsString());
  }
}
