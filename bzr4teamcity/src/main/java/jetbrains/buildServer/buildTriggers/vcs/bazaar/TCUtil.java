package jetbrains.buildServer.buildTriggers.vcs.bazaar;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.vcs.IncludeRule;
import jetbrains.buildServer.vcs.VcsChange;
import jetbrains.buildServer.vcs.VcsChangeInfo;
import org.emergent.bzr4j.core.BazaarItemKind;
import org.emergent.bzr4j.core.BazaarStatusType;
import org.emergent.bzr4j.core.cli.BzrHandlerException;
import org.emergent.bzr4j.core.cli.BzrHandlerResult;
import org.emergent.bzr4j.core.IBazaarLogMessage;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.IBazaarStatus;
import org.emergent.bzr4j.core.xmloutput.XmlOutputParser;
import org.emergent.bzr4j.core.xmloutput.XmlStatusResult;
import org.emergent.bzr4j.core.utils.BzrCoreUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Patrick Woodworth
 */
public class TCUtil {

  private static final Logger LOG = Logger.getInstance(TCUtil.class.getName());

  public static List<ChangeSet> parseChangeSets(BzrHandlerResult result) {
    List<ChangeSet> retval = new ArrayList<ChangeSet>();
    try {
      List<IBazaarLogMessage> protorevs = XmlOutputParser.parseXmlLog(result,false);
      for (IBazaarLogMessage lm : protorevs) {
        ChangeSet cset = new ChangeSet(lm.getRevision().getValue(),lm.getDate(),lm.getCommiter(),lm.getMessage());
        retval.add(cset);
      }
    } catch (BazaarException e) {
      Loggers.VCS.error(e);
    }
    return retval;
  }

  public static List<VcsChange> getVcsChanges(Settings settings, ChangeRev prev, ChangeRev cur, IncludeRule includeRule)
      throws BzrHandlerException {
    List<ModifiedFile> modifiedFiles = getModifiedFiles(settings, prev, cur);
    // changeset full version will be set into VcsChange structure and
    // stored in database (note that getContent method will be invoked with this version)
    return TCUtil.toVcsChanges(modifiedFiles, prev.getFullVersion(), cur.getFullVersion(), includeRule);
  }

  public static List<ModifiedFile> getModifiedFiles(Settings settings, ChangeRev prev, ChangeRev cur)
      throws BzrHandlerException {
//    BzrTeamcityHandler handler = new BzrTeamcityHandler(settings,"status","-SV","--rev", prev.getId() + ".." + cur.getId());
    BzrTeamcityHandler handler = new BzrTeamcityHandler(settings,"xmlstatus", "--rev", prev.getId() + ".." + cur.getId());
    BzrHandlerResult result = handler.exectc(true);
//    List<ModifiedFile> modifiedFiles = TCUtil.parseStatus(result.getStdout());
//    return modifiedFiles;
    try {
      return TCUtil.parseStatus(result);
    } catch (BazaarException e) {
      throw new BzrHandlerException(e);
    }
//         changeset full version will be set into VcsChange structure and
//         stored in database (note that getContent method will be invoked with this version)
//        return TCUtil.toVcsChanges( modifiedFiles, prev.getFullVersion(), cur.getFullVersion(),
//                includeRule );
  }

  public static List<ModifiedFile> parseStatus(BzrHandlerResult result) throws BazaarException {
    List<ModifiedFile> retval = new ArrayList<ModifiedFile>();
    XmlStatusResult parser = XmlOutputParser.parseXmlStatus(result);
    Set<IBazaarStatus> statii = parser.getStatusSet();
    for (IBazaarStatus bzrStatus : statii) {
      if (bzrStatus.contains(BazaarStatusType.DELETED)) {
        retval.add(new ModifiedFile(ModifiedFile.Status.REMOVED, bzrStatus.getPath(), bzrStatus.getNewKind()));
      } else if (bzrStatus.contains(BazaarStatusType.CREATED)) {
        retval.add(new ModifiedFile(ModifiedFile.Status.ADDED, bzrStatus.getPath(), bzrStatus.getNewKind()));
      } else if (bzrStatus.contains(BazaarStatusType.RENAMED)) {
        retval.add(new ModifiedFile(ModifiedFile.Status.REMOVED, bzrStatus.getPreviousPath(), bzrStatus.getNewKind()));
        retval.add(new ModifiedFile(ModifiedFile.Status.ADDED, bzrStatus.getPath(), bzrStatus.getNewKind()));
      } else if (bzrStatus.contains(BazaarStatusType.MODIFIED)) {
        retval.add(new ModifiedFile(ModifiedFile.Status.MODIFIED, bzrStatus.getPath(), bzrStatus.getNewKind()));
      }
    }
    return retval;
  }

  public static List<VcsChange> toVcsChanges(final List<ModifiedFile> modifiedFiles, String prevVer,
      String curVer, final IncludeRule includeRule) {
    List<VcsChange> files = new ArrayList<VcsChange>();
    for (ModifiedFile mf : modifiedFiles) {
      String normalizedPath = BzrCoreUtil.normalizeSeparator(mf.getPath());
      if (!normalizedPath.startsWith(includeRule.getFrom()))
        continue; // skip files which do not match include rule

      VcsChangeInfo.Type changeType = getChangeType(mf);
      LOG.debug(String.format("modified (%s): \"%s\"", changeType, mf.getPath()));
      if (changeType == null) {
        Loggers.VCS.warn("Unable to convert status: " + mf.getStatus() + " to VCS change type");
        changeType = VcsChangeInfo.Type.NOT_CHANGED;
      }
      files.add(new VcsChange(changeType, mf.getStatus().getName(), normalizedPath,
          normalizedPath, prevVer, curVer));
    }
    return files;
  }

  private static VcsChangeInfo.Type getChangeType(final ModifiedFile mf) {
    final ModifiedFile.Status status = mf.getStatus();
    if (BazaarItemKind.directory.equals(mf.getKind())) {
      switch (status) {
        case ADDED:
          return VcsChangeInfo.Type.DIRECTORY_ADDED;
        case MODIFIED:
          return VcsChangeInfo.Type.DIRECTORY_CHANGED;
        case REMOVED:
          return VcsChangeInfo.Type.DIRECTORY_REMOVED;
      }
    } else {
      switch (status) {
        case ADDED:
          return VcsChangeInfo.Type.ADDED;
        case MODIFIED:
          return VcsChangeInfo.Type.CHANGED;
        case REMOVED:
          return VcsChangeInfo.Type.REMOVED;
      }
    }
    return null;
  }
}
