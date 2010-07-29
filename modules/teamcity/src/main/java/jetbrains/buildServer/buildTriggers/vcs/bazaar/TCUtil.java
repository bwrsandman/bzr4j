package jetbrains.buildServer.buildTriggers.vcs.bazaar;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.vcs.IncludeRule;
import jetbrains.buildServer.vcs.VcsChange;
import jetbrains.buildServer.vcs.VcsChangeInfo;
import jetbrains.buildServer.vcs.VcsRoot;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.BazaarItemKind;
import org.emergent.bzr4j.core.cli.BzrExecException;
import org.emergent.bzr4j.core.cli.BzrSinkResult;
import org.emergent.bzr4j.core.cli.BzrXmlResult;
import org.emergent.bzr4j.core.utils.BzrCoreUtil;
import org.emergent.bzr4j.core.utils.IOUtil;
import org.emergent.bzr4j.core.utils.StringUtil;
import org.emergent.bzr4j.core.xmloutput.GenericChange;
import org.emergent.bzr4j.core.xmloutput.XmlOutputHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
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

  public static List<GenericChange> getGenericChanges(Settings settings, final ChangeRev prev, ChangeRev cur)
      throws BzrExecException {

    final String startRevno = prev.getId();

    LOG.debug(String.format("getGenericChanges: revnos = '%s..%s'", prev.getId(), cur.getId()));

      final List<GenericChange> retval = new ArrayList<GenericChange>();

      BzrTeamcityExec handler = new BzrTeamcityExec(
          settings, "xmllog", "--forward", "-v", "--show-ids", "-r", prev.getId() + ".." + cur.getId(), ".");

//    final List<ChangeSet> changeSets = new ArrayList<ChangeSet>();

    XmlOutputHandler resultHandler = new XmlOutputHandler() {

      private final List<GenericChange> m_currentRevChanges = new LinkedList<GenericChange>();

      @Override
      public void handleLog(String revno, String committer, String branchNick, Date timestamp, String message) {
//          ChangeSet cset = new ChangeSet(revno, timestamp, committer, message);
//          changeSets.add(cset);
        if (!startRevno.equals(revno)) {
          LOG.debug(String.format("  getGenericChanges: revno = '%s'", revno));
          for (GenericChange change : m_currentRevChanges) {
            LOG.debug(String.format("    getGenericChanges adding (%s): '%s'", change.m_changeType, change.m_path));
          }
          retval.addAll(m_currentRevChanges);
        }
        m_currentRevChanges.clear();
      }

      @Override
      public void handleGenericChange(GenericChange change) {
        m_currentRevChanges.add(change);
      }

    };

    handler.exectc(BzrXmlResult.createBzrXmlResult(resultHandler));

    return retval;
  }

  public static List<ModifiedFile> getModifiedFiles(Settings settings, ChangeRev prev, ChangeRev cur)
      throws BzrExecException {

    LOG.debug(String.format("COLLECTING CHANGES: %s..%s", prev.getId(), cur.getId()));

    final List<ModifiedFile> retval = new ArrayList<ModifiedFile>();

    TreeSet<ListingEntry> beforeList = getFileListing(settings, prev.getId());
    final TreeSet<ListingEntry> afterList = getFileListing(settings, cur.getId());

    final TreeSet<ListingEntry> removedList = new TreeSet<ListingEntry>(beforeList);
    removedList.removeAll(afterList);

    final TreeSet<ListingEntry> keptList = new TreeSet<ListingEntry>(beforeList);
    keptList.removeAll(removedList);

    final TreeSet<ListingEntry> newList = new TreeSet<ListingEntry>(afterList);
    newList.removeAll(keptList);

    final TreeSet<ListingEntry> changedList = new TreeSet<ListingEntry>(newList);

    for (ListingEntry entry : removedList) {
      if (entry.isDirectory()) {
        addModifiedFile(retval, VcsChangeInfo.Type.DIRECTORY_REMOVED, entry.m_path);
      } else {
        addModifiedFile(retval, VcsChangeInfo.Type.REMOVED, entry.m_path);
      }
    }

    List<GenericChange> changes = getGenericChanges(settings, prev, cur);
    for (GenericChange change : changes) {
      if (change.m_kind != BazaarItemKind.file && change.m_kind != BazaarItemKind.directory)
        continue;

      switch (change.m_changeType) {
        case added:
        case modified:
        case renamed:
        case kind_changed:
          ListingEntry entry = new ListingEntry(change.m_kind, change.m_path);
          if (keptList.contains(entry)) {
            changedList.add(entry);
//            afterList.remove(change.m_path);
          }
          break;
      }
    }

    for (ListingEntry entry : changedList) {
      if (keptList.contains(entry)) {
        if (entry.isDirectory()) {
          addModifiedFile(retval, VcsChangeInfo.Type.DIRECTORY_CHANGED, entry.m_path);
        } else {
          addModifiedFile(retval, VcsChangeInfo.Type.CHANGED, entry.m_path);
        }
      } else {
        if (entry.isDirectory()) {
          addModifiedFile(retval, VcsChangeInfo.Type.DIRECTORY_ADDED, entry.m_path);
        } else {
          addModifiedFile(retval, VcsChangeInfo.Type.ADDED, entry.m_path);
        }
      }
    }

    return retval;
  }

  private static void addModifiedFile(Collection<ModifiedFile> files, VcsChangeInfo.Type type, String path) {
    LOG.debug(String.format("  CHANGE (%s) : '%s'", type, path));
    files.add(new ModifiedFile(type, path));
  }

  private static TreeSet<ListingEntry> getFileListing(Settings settings, String rev) throws BzrExecException {

    BzrTeamcityExec handler = new BzrTeamcityExec(settings,"xmlls", "-r", rev);

    final TreeSet<ListingEntry> retval = new TreeSet<ListingEntry>();

    handler.exectc(new XmlOutputHandler() {
      @Override
      public void handleItem(String id, String kind, String path, String statusKind) {
        BazaarItemKind itemKind = BazaarItemKind.fromString(kind);
//        LOG.debug(String.format("LISTING (%9s): '%s'", itemKind, path));
        if (itemKind == BazaarItemKind.file) {
          retval.add(new ListingEntry(itemKind, path));
        } else if (itemKind == BazaarItemKind.directory) {
          retval.add(new ListingEntry(itemKind, path));
        }
      }
    });

    return retval;
  }

  private static List<VcsChange> toVcsChanges(List<ModifiedFile> modifiedFiles, String prevVer, String curVer, IncludeRule includeRule) {
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

  public static String getBranchName(VcsRoot vcsRoot) {
    String retval = vcsRoot.getProperty(TCConstants.BRANCH_NAME_PROP);
    if (StringUtil.isEmpty(retval)) {
      retval = vcsRoot.getProperty(TCConstants.REPOSITORY_PROP);
      while (retval.endsWith("/") || retval.endsWith("\\"))
        retval = retval.substring(0, retval.length() - 1);
      int sepIdx = retval.lastIndexOf('/');
      if (sepIdx > -1)
        retval = retval.substring(sepIdx + 1);
      sepIdx = retval.lastIndexOf('\\');
      if (sepIdx > -1)
        retval = retval.substring(sepIdx + 1);
    }
    return retval;
  }

  public static File doCat(Settings settings, String myRevId, List<String> relPaths) throws IOException,
      BazaarException {

    File tempDir = IOUtil.createTempDirectory("bazaar", "catresult");
    for (String path : relPaths) {
      doCat(settings, myRevId, tempDir, path);
    }

    return tempDir;
  }

  public static File doCat(Settings settings, String myRevId, File tempDir, String path) throws IOException, BazaarException {

    final File parentFile = new File(tempDir, path).getParentFile();
    if (!parentFile.isDirectory() && !parentFile.mkdirs()) {
      throw new BazaarException("Failed to create directory: " + parentFile.getAbsolutePath());
    }

    File outFile = new File(tempDir.getAbsolutePath(), path);
    BzrSinkResult sinkResult = new BzrSinkResult(outFile);
    (new BzrTeamcityExec(settings, "cat", "-r", myRevId, IOUtil.deNormalizeSeparator(path))).exectc(sinkResult);

    return outFile;
  }

  public static byte[] doCat(Settings settings, String myRevId, String path) throws IOException, BazaarException {
    ByteArrayOutputStream outFile = new ByteArrayOutputStream();
    BzrSinkResult sinkResult = new BzrSinkResult(outFile, true);
    (new BzrTeamcityExec(settings, "cat", "-r", myRevId, IOUtil.deNormalizeSeparator(path))).exectc(sinkResult);
    return outFile.toByteArray();
  }
  
  public static class ListingEntry implements Comparable<ListingEntry>{

    public final BazaarItemKind m_kind;
    public final String m_path;

    public ListingEntry(BazaarItemKind kind, String path) {
      m_kind = kind;
      m_path = path;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ListingEntry that = (ListingEntry)o;

      if (m_kind != that.m_kind) return false;
      if (!m_path.equals(that.m_path)) return false;

      return true;
    }

    @Override
    public int hashCode() {
      int result = m_kind.hashCode();
      result = 31 * result + m_path.hashCode();
      return result;
    }

    private String getDirectorySuffixedPath() {
      if (m_kind == BazaarItemKind.directory && !m_path.endsWith("/"))
        return m_path + "/";
      return m_path;
    }

    public int compareTo(ListingEntry o) {
      return getDirectorySuffixedPath().compareTo(o.getDirectorySuffixedPath());
    }

    public boolean isDirectory() {
      return m_kind == BazaarItemKind.directory;
    }
  }
}
