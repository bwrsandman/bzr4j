/*
 * Copyright (c) 2010 Patrick Woodworth
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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import static org.emergent.bzr4j.core.xmloutput.XmlAbstractHandler.ParseMode.*;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

public abstract class XmlOutputHandler extends XmlAbstractHandler {

  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE yyyy-MM-dd HH:mm:ss Z", Locale.US);

  private final static String BRANCH_ROOT = "workingtree_root";

  private String m_currentChangeType = null;

  private File workDir;

  private String workPath;

  public void handleAnnotationEntry(String content, String revno, String author, String date) {
  }

  public static Date parseBzrTimeStamp(String timestamp) throws ParseException {
    return DATE_FORMAT.parse(timestamp);
  }

  public void handleLog(String revno, String committer, String branchNick, String timestamp, String message) {
    try {
      Date tstamp = parseBzrTimeStamp(timestamp);
      handleLog(revno, committer, branchNick, tstamp, message);
    } catch (ParseException e) {
      LOG.error(e, "failed to parse: " + timestamp);
    }
  }

  public void handleLog(String revno, String committer, String branchNick, Date timestamp, String message) {

  }

  private void handleChange(SimpleElement simpleEl) {
    Properties props = new Properties();
    for (SimpleElement childEl : simpleEl.m_children) {
      props.setProperty(childEl.m_name, childEl.m_text.toString());
    }
    LOG.debug("handling log: " + props.getProperty("revno"));
    handleLog(
        props.getProperty("revno"),
        props.getProperty("committer"),
        props.getProperty("branch-nick"),
        props.getProperty("timestamp"),
        props.getProperty("message")
        );
  }


  public void handleAdded(String kind, String path) {

  }

  public void handleModified(String kind, String path) {

  }

  public void handleRemoved(String kind, String path) {

  }

  public void handleRenamed(String kind, String path, String oldPath) {

  }

  public void handleUnknown(String kind, String path) {

  }

  public void handleConflicts(String path, String type) {

  }

  public void handleKindChanged(String kind, String path, String oldKind) {

  }

  public void handleGenericChange(String changeType, String kind, String path, Attributes attributes) {
    if ("added".equals(changeType)) {
      handleAdded(kind, path);
    } else if ("modified".equals(changeType)) {
      handleModified(kind, path);
    } else if ("removed".equals(changeType)) {
      handleRemoved(kind, path);
    } else if ("renamed".equals(changeType)) {
      handleRenamed(kind, path, attributes.getValue("oldpath"));
    } else if ("unknown".equals(changeType)) {
      handleUnknown(kind, path);
    } else if ("conflicts".equals(changeType)) {
      handleConflicts(path, attributes.getValue("type"));
    } else if ("kind_changed".equals(changeType)) {
      handleKindChanged(kind, path, attributes.getValue("oldkind"));
    }
  }

  public void handleStatusGroupEntry(String changeType, SimpleElement simpleEl) {
//    System.out.println("handleChange: " + changeType + " " + simpleEl.m_name + " " + simpleEl.m_text.toString() );
    handleGenericChange(changeType, simpleEl.m_name, simpleEl.m_text.toString(), simpleEl.m_attributes);
  }

  public void handleItem(String id, String kind, String path, String statusKind) {

  }

  private void handleItem(SimpleElement simpleEl) {
    Properties props = new Properties();
    for (SimpleElement childEl : simpleEl.m_children) {
      props.setProperty(childEl.m_name, childEl.m_text.toString());
    }
    handleItem(
        props.getProperty("id"),
        props.getProperty("kind"),
        props.getProperty("path"),
        props.getProperty("status_kind")
        );
  }


  public File getWorkDir() {
    return workDir;
  }

  public String getWorkPath() {
    return workPath;
  }

  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    StackEntry lastEntry = peekStack();
    ParseMode lastMode = lastEntry == null ? UNSTARTED_MODE : lastEntry.m_mode;
    SimpleElement thisElement = new SimpleElement(qName, attributes);
    ParseMode nextMode = UNKNOWN_MODE;
    switch (lastMode) {
      case UNSTARTED_MODE:
        if ("status".equals(qName)) {
          nextMode = STATUS_MODE;
          workDir = XmlOutputUtil.getAsFile(attributes.getValue(BRANCH_ROOT));
        } else if ("logs".equals(qName)) {
          nextMode = LOGS_MODE;
        } else if ("list".equals(qName)) {
          nextMode = LIST_MODE;
        } else if ("annotation".equals(qName)) {
          nextMode = ANNOTATION_MODE;
          workDir = XmlOutputUtil.getAsFile(attributes.getValue("workingtree-root"));
          workPath = attributes.getValue("file");
        }
        break;
      case STATUS_MODE:
        if (qName.equals("pending_merges")) {
          nextMode = STATUS_PENDING_MERGES_MODE;
        } else {
          nextMode = STATUS_GROUP_MODE;
          m_currentChangeType = qName;
        }
        break;
      case STATUS_GROUP_MODE:
        nextMode = STATUS_GROUP_ENTRY_MODE;
        break;
      case LOGS_MODE:
        if ("log".equals(qName)) {
          nextMode = LOG_MODE;
        }
        break;
      case LOG_MODE:
        if ("affected-files".equals(qName)) {
          nextMode = STATUS_MODE;
        } else if ("merge".equals(qName)) {
          nextMode = LOGS_MODE;
        } else {
          lastEntry.m_element.addChild(thisElement);
          nextMode = LOG_ENTRY_MODE;
        }
        break;
      case LIST_MODE:
        if ("item".equals(qName)) {
          nextMode = ITEM_MODE;
        }
        break;
      case ITEM_MODE:
        lastEntry.m_element.addChild(thisElement);
        nextMode = ITEM_CHILD_MODE;
        break;
      case ANNOTATION_MODE:
        Attributes attribs = lastEntry.m_element.m_attributes;
        handleAnnotationEntry(lastEntry.m_element.m_text.toString(),
            attribs.getValue("revno"),
            attribs.getValue("author"),
            attribs.getValue("date")
        );
        break;
    }
    pushStack(nextMode, thisElement);
  }

  public void endElement(String uri, String localName, String qName) throws SAXException {
    StackEntry lastEntry = popSimpleElement();
    SimpleElement simpleEl = lastEntry.m_element;
    ParseMode lastMode = lastEntry.m_mode;
    assertThat(simpleEl.m_name.equals(qName));
    switch (lastMode) {
      case STATUS_GROUP_MODE:
        m_currentChangeType = null;
        break;
      case STATUS_GROUP_ENTRY_MODE:
        handleStatusGroupEntry(m_currentChangeType, simpleEl);
        break;
      case ITEM_MODE:
        handleItem(simpleEl);
        break;
      case LOG_MODE:
        handleChange(simpleEl);
        break;
    }
  }
}
