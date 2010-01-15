/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.core.commandline.parser;

import org.emergent.bzr4j.core.commandline.parser.CommandLineInfo;
import org.emergent.bzr4j.core.commandline.parser.CommandLineInfo.CmdLineBranchHistory;
import org.emergent.bzr4j.core.commandline.parser.CommandLineInfo.CmdLineLocations;
import org.emergent.bzr4j.core.commandline.parser.CommandLineInfo.CmdLineRelatedBranches;
import org.emergent.bzr4j.core.commandline.parser.CommandLineInfo.CmdLineRepositoryStats;
import org.emergent.bzr4j.core.commandline.parser.CommandLineInfo.CmdLineWorkingTreeStats;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.IBazaarInfo;
import org.emergent.bzr4j.core.utils.StringUtil;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Guillermo Gonzalez
 *
 */
class XMLInfoParser extends XmlAbstractParser {

  private static final String INFO = "info";

  private static final String LAYOUT = "layout";

  private static final String FORMATS = "formats";

  private static final String FORMAT = "format";

  // <location>
  private static final String LOCATION = "location";

  private static final String LOCATION_LIGHT_CHECKOUT_ROOT = "light_checkout_root";

  private static final String LOCATION_REPOSITORY_CHECKOUT_ROOT = "repository_checkout_root";

  private static final String LOCATION_CHECKOUT_ROOT = "checkout_root";

  private static final String LOCATION_CHECKOUT_OF_BRANCH = "checkout_of_branch";

  private static final String LOCATION_SHARED_REPOSITORY = "shared_repository";

  private static final String LOCATION_REPOSITORY = "repository";

  private static final String LOCATION_REPOSITORY_BRANCH = "repository_branch";

  private static final String LOCATION_BRANCH_ROOT = "branch_root";

  private static final String LOCATION_BOUND_TO_BRANCH = "bound_to_branch";

  // </location>
  // <related_branches>
  private static final String RELATED_BRANCHES = "related_branches";

  private static final String PUBLIC_BRANCH = "public_branch";

  private static final String PUSH_BRANCH = "push_branch";

  private static final String PARENT_BRANCH = "parent_branch";

  private static final String SUBMIT_BRANCH = "submit_branch";

  // </related_branches>
  // <format>
  private static final String CONTROL = "control";

  private static final String WORKING_TREE = "working_tree";

  private static final String BRANCH = "branch";

  private static final String REPOSITORY = "repository";

  // <workingtree_stats>
  private static final String WORKING_TREE_STATS = "working_tree_stats";

  private static final String UNCHANGED = "unchanged";

  private static final String MODIFIED = "modified";

  private static final String ADDED = "added";

  private static final String REMOVED = "removed";

  private static final String RENAMED = "renamed";

  private static final String UNKNOWN = "unknown";

  private static final String IGNORED = "ignored";

  private static final String VERSIONED_SUBDIRECTORIES = "versioned_subdirectories";
  // </workingtree_stats>

  private static final String BRANCH_HISTORY = "branch_history";

  private static final String REVISIONS = "revisions";

  private static final String COMMITTERS = "committers";

  private static final String DAYS_OLD = "days_old";

  private static final String FIRST_REVISION = "first_revision";

  private static final String LATEST_REVISION = "latest_revision";

  private static final String REPOSITORY_STATS = "repository_stats";

  private static final String SIZE = "size";

  public IBazaarInfo parse(String xml) throws BazaarException {
    try {
      XMLInputFactory factory = XMLInputFactory.newInstance();
      parser = factory.createXMLStreamReader(new StringReader(xml));
      int eventType = parser.getEventType();
      CommandLineInfo info = null;
      while (eventType != XMLStreamConstants.END_DOCUMENT) {
        if (eventType == XMLStreamConstants.START_ELEMENT && INFO.equals(parser.getLocalName())) {
          info = parseInfo();
        }
        eventType = parser.next();
      }
      return info;
    }
    catch (XMLStreamException e) {
      throw new BazaarException(e);
    }
    catch (ParseException e) {
      throw new BazaarException(e);
    }
    catch (IOException e) {
      throw new BazaarException(e);
    }
  }

  private CommandLineInfo parseInfo() throws XMLStreamException, IOException, ParseException {
    int eventType = parser.next();
    String layout = null;
    String workingTreeFormat = null;
    String branchFormat = null;
    String repositoryFormat = null;
    String controlFormat = null;
    List<String> formats = Collections.EMPTY_LIST;
    CmdLineLocations locations = null;
    CmdLineRelatedBranches relatedBranches = null;
    CmdLineWorkingTreeStats workingTreeStats = null;
    CmdLineBranchHistory branchHistory = null;
    CmdLineRepositoryStats repositoryStats = null;
    while (eventType != XMLStreamConstants.END_DOCUMENT || (eventType == XMLStreamConstants.END_ELEMENT
        && INFO.equals(parser.getLocalName()))) {
      if (eventType == XMLStreamConstants.START_ELEMENT && LAYOUT.equals(parser.getLocalName())) {
        layout = parser.getElementText();
      } else if (eventType == XMLStreamConstants.START_ELEMENT && FORMATS.equals(parser.getLocalName())) {
        formats = parseFormats();
      } else if (eventType == XMLStreamConstants.START_ELEMENT && LOCATION.equals(parser.getLocalName())) {
        locations = parseLocations();
      } else if (eventType == XMLStreamConstants.START_ELEMENT && RELATED_BRANCHES
          .equals(parser.getLocalName())) {
        relatedBranches = parseRelatedBranches();
      } else if (eventType == XMLStreamConstants.START_ELEMENT && FORMAT.equals(parser.getLocalName())) {
        int formatEventType = parser.next();
        while (formatEventType != XMLStreamConstants.END_DOCUMENT || (
            formatEventType == XMLStreamConstants.END_ELEMENT && FORMAT
                .equals(parser.getLocalName()))) {
          if (formatEventType == XMLStreamConstants.START_ELEMENT && CONTROL
              .equals(parser.getLocalName())) {
            controlFormat = StringUtil.nullSafeTrim(parser.getElementText());
          } else if (formatEventType == XMLStreamConstants.START_ELEMENT && WORKING_TREE
              .equals(parser.getLocalName())) {
            workingTreeFormat = StringUtil.nullSafeTrim(parser.getElementText());
          } else if (formatEventType == XMLStreamConstants.START_ELEMENT && BRANCH
              .equals(parser.getLocalName())) {
            branchFormat = StringUtil.nullSafeTrim(parser.getElementText());
          } else if (formatEventType == XMLStreamConstants.START_ELEMENT && REPOSITORY
              .equals(parser.getLocalName())) {
            repositoryFormat = StringUtil.nullSafeTrim(parser.getElementText());
          } else if (formatEventType == XMLStreamConstants.END_ELEMENT && FORMAT
              .equals(parser.getLocalName())) {
            break;
          }
          formatEventType = parser.next();
        }
      } else if (eventType == XMLStreamConstants.START_ELEMENT && WORKING_TREE_STATS
          .equals(parser.getLocalName())) {
        workingTreeStats = parseWorkingTreeStats();
      } else if (eventType == XMLStreamConstants.START_ELEMENT && BRANCH_HISTORY
          .equals(parser.getLocalName())) {
        branchHistory = parseBranchHistory();
      } else if (eventType == XMLStreamConstants.START_ELEMENT && REPOSITORY_STATS
          .equals(parser.getLocalName())) {
        repositoryStats = parseRepositoryStats();
      }
      eventType = parser.next();
    }
    return new CommandLineInfo(layout, formats.toArray(new String[0]), branchFormat,
        controlFormat, repositoryFormat, workingTreeFormat, locations, relatedBranches,
        workingTreeStats,
        branchHistory, repositoryStats);
  }

  private List<String> parseFormats() throws XMLStreamException, IOException {
    int eventType = parser.next();
    final List<String> formats = new ArrayList<String>();
    while (eventType != XMLStreamConstants.END_DOCUMENT || (eventType == XMLStreamConstants.END_ELEMENT
        && FORMATS.equals(parser.getLocalName()))) {
      if (eventType == XMLStreamConstants.START_ELEMENT && FORMAT.equals(parser.getLocalName())) {
        formats.add(StringUtil.nullSafeTrim(parser.getElementText()));
      } else if (eventType == XMLStreamConstants.END_ELEMENT && FORMATS.equals(parser.getLocalName())) {
        return formats;
      }
      eventType = parser.next();
    }
    return formats;
  }

  private CmdLineLocations parseLocations() throws XMLStreamException, IOException {
    int eventType = parser.next();
    String lightCheckoutRoot, repositoryCheckoutRoot, checkoutRoot, checkoutOfBranch;
    String sharedRepository, repository, repositoryBranch, branchRoot, boundToBranch;
    lightCheckoutRoot = repositoryCheckoutRoot = checkoutRoot = checkoutOfBranch = null;
    sharedRepository = repository = repositoryBranch = branchRoot = boundToBranch = null;
    while (eventType != XMLStreamConstants.END_DOCUMENT || (eventType == XMLStreamConstants.END_ELEMENT
        && LOCATION.equals(parser.getLocalName()))) {
      if (eventType == XMLStreamConstants.START_ELEMENT && LOCATION_BOUND_TO_BRANCH
          .equals(parser.getLocalName())) {
        boundToBranch = StringUtil.nullSafeTrim(parser.getElementText());
      } else if (eventType == XMLStreamConstants.START_ELEMENT && LOCATION_BRANCH_ROOT
          .equals(parser.getLocalName())) {
        branchRoot = StringUtil.nullSafeTrim(parser.getElementText());
      } else if (eventType == XMLStreamConstants.START_ELEMENT && LOCATION_CHECKOUT_OF_BRANCH
          .equals(parser.getLocalName())) {
        checkoutOfBranch = StringUtil.nullSafeTrim(parser.getElementText());
      } else if (eventType == XMLStreamConstants.START_ELEMENT && LOCATION_CHECKOUT_ROOT
          .equals(parser.getLocalName())) {
        checkoutRoot = StringUtil.nullSafeTrim(parser.getElementText());
      } else if (eventType == XMLStreamConstants.START_ELEMENT && LOCATION_LIGHT_CHECKOUT_ROOT
          .equals(parser.getLocalName())) {
        lightCheckoutRoot = StringUtil.nullSafeTrim(parser.getElementText());
      } else if (eventType == XMLStreamConstants.START_ELEMENT && LOCATION_REPOSITORY
          .equals(parser.getLocalName())) {
        repository = StringUtil.nullSafeTrim(parser.getElementText());
      } else if (eventType == XMLStreamConstants.START_ELEMENT && LOCATION_REPOSITORY_BRANCH
          .equals(parser.getLocalName())) {
        repositoryBranch = StringUtil.nullSafeTrim(parser.getElementText());
      } else if (eventType == XMLStreamConstants.START_ELEMENT && LOCATION_REPOSITORY_CHECKOUT_ROOT
          .equals(parser.getLocalName())) {
        repositoryCheckoutRoot = StringUtil.nullSafeTrim(parser.getElementText());
      } else if (eventType == XMLStreamConstants.START_ELEMENT && LOCATION_SHARED_REPOSITORY
          .equals(parser.getLocalName())) {
        sharedRepository = StringUtil.nullSafeTrim(parser.getElementText());
      } else if (eventType == XMLStreamConstants.END_ELEMENT && LOCATION.equals(parser.getLocalName())) {
        break;
      }
      eventType = parser.next();
    }
    return new CmdLineLocations(lightCheckoutRoot, repositoryCheckoutRoot,
        checkoutRoot, checkoutOfBranch, sharedRepository, repository,
        repositoryBranch, branchRoot, boundToBranch);
  }

  private CmdLineRelatedBranches parseRelatedBranches() throws XMLStreamException, IOException {
    int eventType = parser.next();
    String publicBranch = null, push = null, parent = null, submit = null;
    while (eventType != XMLStreamConstants.END_DOCUMENT || (eventType == XMLStreamConstants.END_ELEMENT
        && RELATED_BRANCHES.equals(parser.getLocalName()))) {
      if (eventType == XMLStreamConstants.START_ELEMENT && PUBLIC_BRANCH.equals(parser.getLocalName())) {
        publicBranch = StringUtil.nullSafeTrim(parser.getElementText());
      } else if (eventType == XMLStreamConstants.START_ELEMENT && PUSH_BRANCH
          .equals(parser.getLocalName())) {
        push = StringUtil.nullSafeTrim(parser.getElementText());
      } else if (eventType == XMLStreamConstants.START_ELEMENT && PARENT_BRANCH
          .equals(parser.getLocalName())) {
        parent = StringUtil.nullSafeTrim(parser.getElementText());
      } else if (eventType == XMLStreamConstants.START_ELEMENT && SUBMIT_BRANCH
          .equals(parser.getLocalName())) {
        submit = StringUtil.nullSafeTrim(parser.getElementText());
      } else if (eventType == XMLStreamConstants.END_ELEMENT && RELATED_BRANCHES
          .equals(parser.getLocalName())) {
        break;
      }
      eventType = parser.next();
    }
    return new CmdLineRelatedBranches(publicBranch, push, parent, submit);
  }

  private CmdLineWorkingTreeStats parseWorkingTreeStats()
      throws XMLStreamException, IOException {
    int eventType = parser.next();
    Integer unchanged, modified, added, removed, renamed, unknown, ignored, versionedSubDirs;
    unchanged = modified = added =
        removed = renamed = unknown = ignored = versionedSubDirs = Integer.valueOf(0);
    while (eventType != XMLStreamConstants.END_DOCUMENT || (eventType == XMLStreamConstants.END_ELEMENT
        && WORKING_TREE_STATS.equals(parser.getLocalName()))) {
      if (eventType == XMLStreamConstants.START_ELEMENT && UNCHANGED.equals(parser.getLocalName())) {
        unchanged = new Integer(parser.getElementText());
      } else if (eventType == XMLStreamConstants.START_ELEMENT && MODIFIED.equals(parser.getLocalName())) {
        modified = new Integer(parser.getElementText());
      } else if (eventType == XMLStreamConstants.START_ELEMENT && ADDED.equals(parser.getLocalName())) {
        added = new Integer(parser.getElementText());
      } else if (eventType == XMLStreamConstants.START_ELEMENT && REMOVED.equals(parser.getLocalName())) {
        removed = new Integer(parser.getElementText());
      } else if (eventType == XMLStreamConstants.START_ELEMENT && RENAMED.equals(parser.getLocalName())) {
        renamed = new Integer(parser.getElementText());
      } else if (eventType == XMLStreamConstants.START_ELEMENT && UNKNOWN.equals(parser.getLocalName())) {
        unknown = new Integer(parser.getElementText());
      } else if (eventType == XMLStreamConstants.START_ELEMENT && IGNORED.equals(parser.getLocalName())) {
        ignored = new Integer(parser.getElementText());
      } else if (eventType == XMLStreamConstants.START_ELEMENT && VERSIONED_SUBDIRECTORIES
          .equals(parser.getLocalName())) {
        versionedSubDirs = new Integer(parser.getElementText());
      } else if (eventType == XMLStreamConstants.END_ELEMENT && WORKING_TREE_STATS
          .equals(parser.getLocalName())) {
        break;
      }
      eventType = parser.next();
    }
    return new CmdLineWorkingTreeStats(added, ignored, modified, removed, renamed, unchanged,
        unknown, versionedSubDirs);
  }

  private CmdLineBranchHistory parseBranchHistory()
      throws XMLStreamException, IOException, ParseException {
    int eventType = parser.next();
    Integer revisions, committers, days;
    revisions = committers = days = Integer.valueOf(0);
    Date firstRevDate = null, lastRevDate = null;
    while (eventType != XMLStreamConstants.END_DOCUMENT || (eventType == XMLStreamConstants.END_ELEMENT
        && BRANCH_HISTORY.equals(parser.getLocalName()))) {
      if (eventType == XMLStreamConstants.START_ELEMENT && REVISIONS.equals(parser.getLocalName())) {
        revisions = new Integer(parser.getElementText());
      } else if (eventType == XMLStreamConstants.START_ELEMENT && COMMITTERS
          .equals(parser.getLocalName())) {
        committers = new Integer(parser.getElementText());
      } else if (eventType == XMLStreamConstants.START_ELEMENT && DAYS_OLD.equals(parser.getLocalName())) {
        days = new Integer(parser.getElementText());
      } else if (eventType == XMLStreamConstants.START_ELEMENT && FIRST_REVISION
          .equals(parser.getLocalName())) {
        final String timestamp = StringUtil.nullSafeTrim(parser.getElementText());
        firstRevDate = StringUtil.parseLogDate(timestamp);
      } else if (eventType == XMLStreamConstants.START_ELEMENT && LATEST_REVISION
          .equals(parser.getLocalName())) {
        final String timestamp = StringUtil.nullSafeTrim(parser.getElementText());
        lastRevDate = StringUtil.parseLogDate(timestamp);
      } else if (eventType == XMLStreamConstants.END_ELEMENT && BRANCH_HISTORY
          .equals(parser.getLocalName())) {
        break;
      }
      eventType = parser.next();
    }
    return new CmdLineBranchHistory(revisions, committers, days, firstRevDate, lastRevDate);
  }

  private CmdLineRepositoryStats parseRepositoryStats() throws XMLStreamException, IOException {
    int eventType = parser.next();
    Integer revisions = null;
    Long size = null;
    while (eventType != XMLStreamConstants.END_DOCUMENT || (eventType == XMLStreamConstants.END_ELEMENT
        && REPOSITORY_STATS.equals(parser.getLocalName()))) {
      if (eventType == XMLStreamConstants.START_ELEMENT && REVISIONS.equals(parser.getLocalName())) {
        revisions = new Integer(parser.getElementText());
      } else if (eventType == XMLStreamConstants.START_ELEMENT && SIZE.equals(parser.getLocalName())) {
        size = new Long(parser.getElementText());
      } else if (eventType == XMLStreamConstants.END_ELEMENT && REPOSITORY_STATS
          .equals(parser.getLocalName())) {
        break;
      }
      eventType = parser.next();
    }
    return new CmdLineRepositoryStats(revisions, size);
  }

}
