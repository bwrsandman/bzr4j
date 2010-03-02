package jetbrains.buildServer.buildTriggers.vcs.bazaar;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.vcs.IncludeRule;
import jetbrains.buildServer.vcs.VcsChange;
import jetbrains.buildServer.vcs.VcsChangeInfo;
import org.emergent.bzr4j.core.cli.BzrExecException;
import org.emergent.bzr4j.core.utils.BzrCoreUtil;
import org.emergent.bzr4j.core.xmloutput.XmlOutputHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * @author Patrick Woodworth
 */
public class TCUtil {

  private static final Logger LOG = Logger.getInstance(TCUtil.class.getName());

  public static List<VcsChange> getVcsChanges(Settings settings, ChangeRev prev, ChangeRev cur, IncludeRule includeRule)
      throws BzrExecException {
    List<ModifiedFile> modifiedFiles = getModifiedFiles(settings, prev, cur);
    // changeset full version will be set into VcsChange structure and
    // stored in database (note that getContent method will be invoked with this version)
    return TCUtil.toVcsChanges(modifiedFiles, prev.getFullVersion(), cur.getFullVersion(), includeRule);
  }

  public static List<ModifiedFile> getModifiedFiles(Settings settings, ChangeRev prev, ChangeRev cur)
      throws BzrExecException {

    final List<ModifiedFile> retval = new ArrayList<ModifiedFile>();

    TreeSet<String> beforeList = getFileListing(settings, prev.getId());
    final TreeSet<String> afterList = getFileListing(settings, cur.getId());

    final TreeSet<String> removedList = new TreeSet<String>(beforeList);
    removedList.removeAll(afterList);

    final TreeSet<String> changedList = new TreeSet<String>();

    for (String path : removedList) {
      retval.add(new ModifiedFile(VcsChangeInfo.Type.REMOVED, path));
    }

    BzrTeamcityExec handler = new BzrTeamcityExec(settings,"xmlstatus", "--rev", prev.getId() + ".." + cur.getId());

    handler.exectc(new XmlOutputHandler() {
      @Override
      public void handleAdded(String kind, String path) {
        for (Iterator<String> iter = afterList.iterator(); iter.hasNext(); ) {
          String pathb = iter.next();
          if (pathb.startsWith(path)) {
            changedList.add(pathb);
            iter.remove();
          }
        }
      }

      @Override
      public void handleModified(String kind, String path) {
        for (Iterator<String> iter = afterList.iterator(); iter.hasNext(); ) {
          String pathb = iter.next();
          if (pathb.startsWith(path)) {
            changedList.add(pathb);
            iter.remove();
          }
        }
      }

      @Override
      public void handleRenamed(String kind, String path, String oldPath) {
        for (Iterator<String> iter = afterList.iterator(); iter.hasNext(); ) {
          String pathb = iter.next();
          if (pathb.startsWith(path)) {
            changedList.add(pathb);
            iter.remove();
          }
        }
      }
    });

    for (String path : changedList) {
      if (beforeList.contains(path)) {
        retval.add(new ModifiedFile(VcsChangeInfo.Type.CHANGED, path));
      } else {
        retval.add(new ModifiedFile(VcsChangeInfo.Type.ADDED, path));
      }
    }

    return retval;
  }

  public static TreeSet<String> getFileListing(Settings settings, String rev) throws BzrExecException {

    BzrTeamcityExec handler = new BzrTeamcityExec(settings,"xmlls", "-r", rev);

    final TreeSet<String> retval = new TreeSet<String>();

    handler.exectc(new XmlOutputHandler() {
      @Override
      public void handleItem(String id, String kind, String path, String statusKind) {
        retval.add(path);
      }
    });

    return retval;
  }


  public static List<VcsChange> toVcsChanges(
      final List<ModifiedFile> modifiedFiles, String prevVer, String curVer, final IncludeRule includeRule) {
    List<VcsChange> files = new ArrayList<VcsChange>();
    for (ModifiedFile mf : modifiedFiles) {
      String normalizedPath = BzrCoreUtil.normalizeSeparator(mf.getPath());
      if (!normalizedPath.startsWith(includeRule.getFrom()))
        continue; // skip files which do not match include rule

      VcsChangeInfo.Type changeType = mf.getChangeType();
      LOG.debug(String.format("modified (%s): \"%s\"", changeType, mf.getPath()));
      files.add(new VcsChange(changeType, null, normalizedPath, normalizedPath, prevVer, curVer));
    }
    return files;
  }
}
