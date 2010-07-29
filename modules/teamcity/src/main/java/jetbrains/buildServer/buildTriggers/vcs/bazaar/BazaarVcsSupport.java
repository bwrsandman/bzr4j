/*
 * Copyright 2000-2007 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.buildServer.buildTriggers.vcs.bazaar;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.Used;
import jetbrains.buildServer.buildTriggers.vcs.AbstractVcsPropertiesProcessor;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.vcs.BuildPatchByCheckoutRules;
import jetbrains.buildServer.vcs.BuildPatchPolicy;
import jetbrains.buildServer.vcs.CheckoutRules;
import jetbrains.buildServer.vcs.CollectChangesByIncludeRules;
import jetbrains.buildServer.vcs.CollectChangesPolicy;
import jetbrains.buildServer.vcs.IncludeRule;
import jetbrains.buildServer.vcs.IncludeRuleChangeCollector;
import jetbrains.buildServer.vcs.LabelingSupport;
import jetbrains.buildServer.vcs.ModificationData;
import jetbrains.buildServer.vcs.RootMerger;
import jetbrains.buildServer.vcs.ServerVcsSupport;
import jetbrains.buildServer.vcs.TestConnectionSupport;
import jetbrains.buildServer.vcs.UrlSupport;
import jetbrains.buildServer.vcs.VcsChange;
import jetbrains.buildServer.vcs.VcsChangeInfo;
import jetbrains.buildServer.vcs.VcsException;
import jetbrains.buildServer.vcs.VcsFileContentProvider;
import jetbrains.buildServer.vcs.VcsManager;
import jetbrains.buildServer.vcs.VcsModification;
import jetbrains.buildServer.vcs.VcsPersonalSupport;
import jetbrains.buildServer.vcs.VcsRoot;
import jetbrains.buildServer.vcs.VcsSupportCore;
import jetbrains.buildServer.vcs.VcsSupportUtil;
import jetbrains.buildServer.vcs.patches.PatchBuilder;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.cli.BzrAbstractResult;
import org.emergent.bzr4j.core.cli.BzrExecException;
import org.emergent.bzr4j.core.cli.BzrStandardResult;
import org.emergent.bzr4j.core.cli.BzrXmlResult;
import org.emergent.bzr4j.core.utils.IOUtil;
import org.emergent.bzr4j.core.xmloutput.XmlOutputHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Bazaar VCS plugin for TeamCity works as follows:
 * <ul>
 * <li>clones repository to internal storage
 * <li>before any operation with working copy of repository pulls changes from the original repository
 * <li>executes corresponding bzr command
 * </ul>
 *
 * <p>Working copy of repository is created in the $TEAMCITY_DATA_PATH/system/caches/bzr_&lt;hash&gt; folder.
 * <p>Personal builds (remote runs) are not yet supported, they require corresponding functionality from the IDE.
 */
public class BazaarVcsSupport extends ServerVcsSupport {

  public static final Logger LOGVCS = Loggers.VCS;

  private static final Logger LOG = Logger.getInstance(BazaarVcsSupport.class.getName());

  private VcsManager m_vcsManager;

  private File myDefaultWorkFolderParent;

  public BazaarVcsSupport(
      @NotNull final VcsManager vcsManager,
      @NotNull ServerPaths paths,
      @NotNull SBuildServer server) {
    m_vcsManager = vcsManager;
    myDefaultWorkFolderParent = new File(paths.getCachesDir(), "bazaar");
    server.getExecutor().scheduleAtFixedRate(
        new CleanupManager(), 0, TCConstants.OLD_WORK_DIRS_CLEANUP_PERIOD, TimeUnit.SECONDS);
  }

  private class CleanupManager implements Runnable {
    public void run() {
      removeOldWorkFolders();
    }
  }

  ///////////////////////////////////////
  // VcsSupportConfig implementation
  ///////////////////////////////////////

  @NotNull
  public String getName() {
    return TCConstants.VCS_NAME;
  }

  @NotNull
  @Used("jsp")
  public String getDisplayName() {
    return "Bazaar";
  }

  @Nullable
  public PropertiesProcessor getVcsPropertiesProcessor() {
    return new MyPropertiesProcessor();
  }

  @NotNull
  public String getVcsSettingsJspFilePath() {
    return "bazaarSettings.jsp";
  }

  @NotNull
  public String describeVcsRoot(VcsRoot vcsRoot) {
    return "bzr: " + vcsRoot.getProperty(TCConstants.REPOSITORY_PROP);
  }

  @Nullable
  public Map<String, String> getDefaultVcsProperties() {
    return Collections.singletonMap(TCConstants.BZR_COMMAND_PATH_PROP, "bzr");
  }

  @Nullable
  public String getVersionDisplayName(@NotNull String version, @NotNull VcsRoot root) throws VcsException {
//    LOG.debug("getVersionDisplayName()");
    return new ChangeRev(version).getId();
  }

  @NotNull
  public Comparator<String> getVersionComparator() {
    return new VcsSupportUtil.StringVersionComparator();
  }

  @Override
  public boolean isAgentSideCheckoutAvailable() {
    return false;
  }

  ///////////////////////////////////////
  // VcsSupportCore implementation
  ///////////////////////////////////////

  @NotNull
  public String getCurrentVersion(@NotNull VcsRoot root) throws VcsException {
//    LOG.debug(String.format("getCurrentVersion(%s)", root.getProperty(TCConstants.REPOSITORY_PROP)));
    // we will return full version of the most recent change as current version
//    syncClonedRepository(root);
    Settings settings = createSettings(root);
    final Lock lock = acquireLockOnWorkingDir(settings);
    try {
//      BzrTeamcityExec handler = new BzrTeamcityExec(settings,"revno");
      BzrTeamcityExec handler = new BzrTeamcityExec(true, settings, "revno", settings.getRepositoryUrl());
      BzrStandardResult result = handler.exectc();
      return result.getStdOutAsString().trim();
    } catch (BzrExecException e) {
      throw new VcsException(e);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public boolean isCurrentVersionExpensive() {
    return false;
  }

  @Override
  public boolean allowSourceCaching() {
    return false; // todo what's best? depends on whether we have local cache clone maybe?
  }

  public boolean sourcesUpdatePossibleIfChangesNotFound(@NotNull VcsRoot root) {
    return true;
  }

  ///////////////////////////////////////
  // VcsSupportContext implementation
  ///////////////////////////////////////  

  @NotNull
  @Override
  public VcsSupportCore getCore() {
    return super.getCore();
  }

  @Override
  public VcsPersonalSupport getPersonalSupport() {
    return super.getPersonalSupport(); // todo implement
  }

  @Override
  public LabelingSupport getLabelingSupport() {
    return new MyLabelingSupport();
  }

  @NotNull
  public VcsFileContentProvider getContentProvider() {
    return new MyVcsFileContentProvider();
  }

  @NotNull
  public CollectChangesPolicy getCollectChangesPolicy() {
    return new MyCollectChangesPolicy();
  }

  @NotNull
  public BuildPatchPolicy getBuildPatchPolicy() {
    return new MyBuildPatchByCheckoutRules();
  }
  
  @Override
  public TestConnectionSupport getTestConnectionSupport() {
    return new MyTestConnectionSupport();
  }

  @Override
  public RootMerger getRootMerger() {
    return super.getRootMerger(); // todo implement
  }

  @Override
  public UrlSupport getUrlSupport() {
    return super.getUrlSupport(); // todo implement
  }

  ///////////////////////////////////////
  // private implementation
  ///////////////////////////////////////  
  

  private Lock acquireLockOnWorkingDir(File workDir) {
    Lock retval = LockUtil.getDirLock(workDir);
    retval.lock();
    return retval;
  }

  private Lock acquireLockOnWorkingDir(Settings settings) {
    return acquireLockOnWorkingDir(settings.getLocalRepositoryDir());
  }

  // updates current working copy of repository by pulling changes from the repository specified in VCS root

  private void syncClonedRepository(final VcsRoot root) throws VcsException {
    syncClonedRepository(root, null);
  }

  private void syncClonedRepository(final VcsRoot root, String version) throws VcsException {
    Settings settings = createSettings(root);
    final Lock lock = acquireLockOnWorkingDir(settings);
    try {
      final File workDir = settings.getLocalRepositoryDir();
//      if (version != null && !"-1".equals(version)) // todo get this working
//        return;
      BzrTeamcityExec handler = null;
      if (settings.hasCopyOfRepository()) {
        handler = new BzrTeamcityExec(settings, "pull", "-q", "--overwrite", settings.getRepositoryUrl());
      } else {
        handler = new BzrTeamcityExec(true, settings, "clone", "-q", "--no-tree", settings.getRepositoryUrl(), workDir.getAbsolutePath());
      }
      BzrAbstractResult result = handler.exectc();
    } catch (BzrExecException e) {
      throw new VcsException(e);
    } finally {
      lock.unlock();
    }
  }

  private void removeOldWorkFolders() {
    File workFoldersParent = myDefaultWorkFolderParent;
    if (!workFoldersParent.isDirectory())
      return;

    Set<File> workDirs = new HashSet<File>();
    File[] files = workFoldersParent.listFiles(new FileFilter() {
      public boolean accept(final File file) {
        return file.isDirectory() && file.getName().startsWith(Settings.DEFAULT_WORK_DIR_PREFIX);
      }
    });
    if (files != null) {
      for (File f : files) {
        workDirs.add(IOUtil.toCanonical(f));
      }
    }

    for (VcsRoot vcsRoot : m_vcsManager.getAllRegisteredVcsRoots()) {
      if (getName().equals(vcsRoot.getVcsName())) {
        try {
          Settings s = createSettings(vcsRoot);
          workDirs.remove(IOUtil.toCanonical(s.getLocalRepositoryDir()));
        } catch (VcsException e) {
          LOG.info(e);
        }
      }
    }

    for (File f : workDirs) {
      final Lock lock = acquireLockOnWorkingDir(f);
      try {
        FileUtil.delete(f);
      } finally {
        lock.unlock();
      }
    }
  }

  private Settings createSettings(final VcsRoot vcsRoot) throws VcsException {
    Settings retval = createSettings(myDefaultWorkFolderParent, vcsRoot);
    createClonedRepositoryParentDir(myDefaultWorkFolderParent);
    return retval;
  }

  public static Settings createSettings(File defWorkDirParent, VcsRoot vcsRoot) {
    return new Settings(defWorkDirParent, vcsRoot);
  }

  private void createClonedRepositoryParentDir(final File parentDir) throws VcsException {
    if (!parentDir.exists()) {
      if (parentDir.mkdirs()) {

      } else {
        throw new VcsException("Failed to create parent directory for cloned repository: " + parentDir.getAbsolutePath());
      }
    }
  }

  private class MyLabelingSupport implements LabelingSupport {
    public String label(
        @NotNull String label,
        @NotNull String version,
        @NotNull VcsRoot root,
        @NotNull CheckoutRules checkoutRules) throws VcsException {
      Settings settings = createSettings(root);
      final Lock lock = acquireLockOnWorkingDir(settings);
      try {
        (new BzrTeamcityExec(settings,"tag","-r",version,"--force",label)).exectc(true);
        (new BzrTeamcityExec(settings,"push","-q",settings.getPushUrl())).exectc(true);
        return label;
      } catch (BzrExecException e) {
        throw new VcsException(e);
      } finally {
        lock.unlock();
      }
    }
  }

  private class MyPropertiesProcessor extends AbstractVcsPropertiesProcessor {
    public Collection<InvalidProperty> process(Map<String, String> props) {
      List<InvalidProperty> retval = new ArrayList<InvalidProperty>();

      if (isEmpty(props.get(TCConstants.BZR_COMMAND_PATH_PROP)))
        retval.add(new InvalidProperty(TCConstants.BZR_COMMAND_PATH_PROP,
            BzrTeamcityMessages.message("path.to.bzr.command.must.be.specified")));

      if (isEmpty(props.get(TCConstants.REPOSITORY_PROP)))
        retval.add(new InvalidProperty(TCConstants.REPOSITORY_PROP,
            BzrTeamcityMessages.message("repository.must.be.specified")));

      return retval;
    }
  }

  private class MyTestConnectionSupport implements TestConnectionSupport {
    @Nullable
    public String testConnection(@NotNull VcsRoot vcsRoot) throws VcsException {
      Settings settings = createSettings(vcsRoot);
      try {
        //syncClonedRepository(vcsRoot);
        BzrTeamcityExec handler = new BzrTeamcityExec(settings,"info");
        handler.addArguments(settings.getRepositoryUrl());
        BzrStandardResult result = handler.exectc();
        return result.getStdOutAsString();
      } catch (BzrExecException e) {
        throw new VcsException(e);
      }
    }
  }

  private class MyVcsFileContentProvider implements VcsFileContentProvider {

    /**
     * Get binary content of the single file in modification set.
     *
     * @param filePath      file path relative to the project root,
     *                      "checkout root" from vcs root should not be included
     * @param versionedRoot current settings.
     * @param version       version returned by {@link jetbrains.buildServer.vcs.VcsModification#getVersion()} or {@link jetbrains.buildServer.vcs.VcsSupportCore#getCurrentVersion(jetbrains.buildServer.vcs.VcsRoot)}.
     * @return specified file content, corresponding to the repository version specified in 'version' parameter.
     * @throws jetbrains.buildServer.vcs.VcsException if some problem occurred.
     * @throws jetbrains.buildServer.vcs.VcsFileNotFoundException when no such file found. If "file not found" condition cannot be
     * detected easily the implementation may throw <tt>jetbrains.buildServer.vcs.VcsException</tt>.
     * In this case some TeamCity functions may work inefficiently, for example the file content cache.
     */
    @NotNull
    public byte[] getContent(
        @NotNull String filePath,
        @NotNull VcsRoot versionedRoot,
        @NotNull String version) throws VcsException {
      Settings settings = createSettings(versionedRoot);
      final Lock lock = acquireLockOnWorkingDir(settings);
      try {
        syncClonedRepository(versionedRoot, version);
        return TCUtil.doCat(settings, version, filePath);
      } catch (IOException e) {
        LOG.warn(String.format("Failed to load content of file '%s'", filePath), e);
        throw new VcsException("Failed to load content of file", e);
      } catch (BazaarException e) {
        LOG.warn(String.format("Failed to load content of file '%s'", filePath), e);
        throw new VcsException("Failed to load content of file", e);
      } finally {
        lock.unlock();
      }
    }

    /**
     * Get binary content of the single file in modification set.
     *
     * @param vcsModification modification set.
     * @param change specified change of file.
     * @param contentType specified what content should be return - before modicication or after it.
     * @param vcsRoot current settings.
     * @return content of the file specified by VcsChange, by version, specified in 'contentType' parameter.
     * @throws jetbrains.buildServer.vcs.VcsException if some problem occurred.
     * @throws jetbrains.buildServer.vcs.VcsFileNotFoundException when no such file found. If "file not found" condition cannot be
     * detected easily the implementation may throw <tt>jetbrains.buildServer.vcs.VcsException</tt>.
     * In this case some TeamCity functions may work inefficiently, for example the file content cache.
     */
    @NotNull
    public byte[] getContent(
        @NotNull VcsModification vcsModification,
        @NotNull VcsChangeInfo change,
        @NotNull VcsChangeInfo.ContentType contentType,
        @NotNull VcsRoot vcsRoot) throws VcsException {
      String version = contentType == VcsChangeInfo.ContentType.AFTER_CHANGE
          ? change.getAfterChangeRevisionNumber()
          : change.getBeforeChangeRevisionNumber();
      return getContent(change.getRelativeFileName(), vcsRoot, version);
    }
  }

  private class MyCollectChangesPolicy implements CollectChangesByIncludeRules {
    @NotNull
    public IncludeRuleChangeCollector getChangeCollector(
        @NotNull final VcsRoot root,
        @NotNull final String fromVersion,
        @Nullable final String currentVersionOrig) throws VcsException {

      return new IncludeRuleChangeCollector() {
        @NotNull
        public List<ModificationData> collectChanges(@NotNull final IncludeRule includeRule) throws VcsException {
          String currentVersion = currentVersionOrig;
          if (currentVersion == null)
            currentVersion = "-1";
//          LOG.info(
//              String.format("Collecting changes for %s; from version %s to version %s;\n%s",
//                  TCUtil.getBranchName(root),
//                  fromVersion, currentVersion, root.convertToPresentableString())
//          );
          LOG.info(String.format("Collecting changes for %s; from version %s to version %s with rule %s",
                  TCUtil.getBranchName(root), fromVersion, currentVersion, includeRule.toDescriptiveString()));

          Settings settings = createSettings(root);
          final Lock lock = acquireLockOnWorkingDir(settings);
          try {
            syncClonedRepository(root, currentVersion);
            // first obtain changes between specified versions
            List<ModificationData> result = new ArrayList<ModificationData>();

            String fromId = new ChangeRev(fromVersion).getId();
            BzrTeamcityExec handler = new BzrTeamcityExec(
                settings, "xmllog", "--forward", "-r", fromVersion + ".." + currentVersion);

            final List<ChangeSet> changeSets = new ArrayList<ChangeSet>();

            XmlOutputHandler resultHandler = new XmlOutputHandler() {
              @Override
              public void handleLog(String revno, String committer, String branchNick, Date timestamp, String message) {
                ChangeSet cset = new ChangeSet(revno, timestamp, committer, message);
                changeSets.add(cset);
              }
            };

            handler.exectc(BzrXmlResult.createBzrXmlResult(resultHandler));

            if (changeSets.isEmpty()) {
              return result;
            }

            ChangeRev prev = new ChangeRev(fromVersion);
            for (ChangeSet cur : changeSets) {
              if (cur.getId().equals(fromId))
                continue; // skip already reported changeset

              LOG.debug(String.format("COLLECTCHANGES: %s..%s", prev.getId(), cur.getId()));
              List<VcsChange> files = TCUtil.getVcsChanges(settings, prev, cur, includeRule);
//              if (files.isEmpty())
//                continue;
              ModificationData md = new ModificationData(cur.getTimestamp(), files, cur.getSummary(),
                  cur.getUser(), root, cur.getFullVersion(), cur.getId());
              result.add(md);
              prev = cur;
            }

            return result;
          } catch (BzrExecException e) {
            throw new VcsException(e);
          } finally {
            lock.unlock();
          }
        }

        public void dispose() throws VcsException {
        }
      };
    }
  }

  private class MyBuildPatchByCheckoutRules implements BuildPatchByCheckoutRules {
    public void buildPatch(@NotNull final VcsRoot root,
                           @Nullable final String fromVersion,
                           @NotNull final String toVersion,
                           @NotNull final PatchBuilder builder,
                           @NotNull final CheckoutRules checkoutRules) throws IOException, VcsException {
      Settings settings = createSettings(root);
      final Lock lock = acquireLockOnWorkingDir(settings);
      try {
        syncClonedRepository(root, toVersion);
        if (fromVersion == null) {
          LOG.debug("building full patch: " + toVersion);
          buildFullPatch(settings, new ChangeRev(toVersion), builder, checkoutRules);
        } else {
          LOG.debug("building delta patch: " + fromVersion + " " + toVersion);
          buildIncrementalPatch(settings, new ChangeRev(fromVersion), new ChangeRev(toVersion), builder, checkoutRules);
        }
      } catch (BazaarException e) {
        LOG.error(e);
        throw new VcsException(e);
      } finally {
        lock.unlock();
      }
    }

    private void buildIncrementalPatch(
        Settings settings,
        ChangeRev fromVer,
        ChangeRev toVer,
        PatchBuilder builder,
        CheckoutRules checkoutRules) throws VcsException, IOException, BazaarException {

      LOG.debug(String.format("BUILDINCPATCH: %s..%s", fromVer.getId(), toVer.getId()));
      List<ModifiedFile> modifiedFiles = TCUtil.getModifiedFiles(settings, fromVer, toVer);

      List<String> notDeletedFiles = new ArrayList<String>();

      Map<String, String> m_resolvedMappings = new HashMap<String, String>();

      for (ListIterator<ModifiedFile> iter = modifiedFiles.listIterator(); iter.hasNext(); ) {
        ModifiedFile modified = iter.next();
        String relPath = modified.getPath();
        String mappedPath = checkoutRules.map(relPath);
//        LOG.debug(String.format("INCMAPPED: '%s' => '%s'", relPath, mappedPath));
        if (mappedPath == null) {
          iter.remove();
        } else {
          m_resolvedMappings.put(relPath, mappedPath);
          switch (modified.getChangeType()) {
            case ADDED:
            case CHANGED:
              notDeletedFiles.add(relPath);
              break;
          }
        }
      }

      File parentDir = TCUtil.doCat(settings, toVer.getId(), notDeletedFiles);

      try {
        for (ModifiedFile modified : modifiedFiles) {
          String relPath = modified.getPath();
          String mappedPath = m_resolvedMappings.get(relPath);
//          if (mappedPath == null)
//            continue;

          final File virtualFile = new File(mappedPath);
          switch (modified.getChangeType()) {
            case DIRECTORY_REMOVED:
              builder.deleteDirectory(virtualFile, false);
              break;
            case DIRECTORY_ADDED:
            case DIRECTORY_CHANGED:
              builder.createDirectory(virtualFile);
              break;
            case REMOVED:
              builder.deleteFile(virtualFile, false);
              break;
            case ADDED:
            case CHANGED:
              InputStream is = null;
              try {
                File realFile = new File(parentDir, relPath);
//                File realFile = TCUtil.doCat(settings, toVer.getId(), parentDir, relPath);
                is = new BufferedInputStream( new FileInputStream(realFile) );
                builder.changeOrCreateBinaryFile(virtualFile, null, is, realFile.length()); // todo handle perms
              } finally {
                IOUtil.close(is);
              }
              break;
          }
        }
      } finally {
        if (parentDir != null)
          FileUtil.delete(parentDir);
      }
    }

    // builds patch by exporting files using specified version

    private void buildFullPatch(
        final Settings settings,
        @NotNull final ChangeRev toVer,
        final PatchBuilder builder,
        CheckoutRules checkoutRules) throws IOException, VcsException, BazaarException {

      File tempDir = FileUtil.createTempDirectory("bazaar", toVer.getId());
      try {
        final File repRoot = new File(tempDir, "rep");
        String deNorm = IOUtil.deNormalizeSeparator(repRoot.getAbsolutePath());
        BzrTeamcityExec handler = new BzrTeamcityExec(settings,"export","-q","--format=dir","-r", toVer.getId(), deNorm, "." );
        handler.exectc(true);
        buildPatchFromDirectory(repRoot, repRoot, builder, checkoutRules, new FileFilter() {
          public boolean accept(final File file) {
            return !(file.isDirectory() && ".bzr".equals(file.getName()));
          }
        });
      } finally {
        FileUtil.delete(tempDir);
      }
    }

    private void buildPatchFromDirectory(
        File curDir,
        File repRoot,
        PatchBuilder builder,
        CheckoutRules checkoutRules,
        FileFilter filter) throws IOException {

      File[] files = curDir.listFiles(filter);
      if (files == null)
        return;
      for (File realFile : files) {
        String relPath = realFile.getAbsolutePath().substring(repRoot.getAbsolutePath().length() + 1);
        String mappedPath = checkoutRules.map(relPath);
        if (realFile.isDirectory()) {
          if (mappedPath != null) {
            final File virtualFile = new File(mappedPath);
            builder.createDirectory(virtualFile);
          }
          buildPatchFromDirectory(realFile, repRoot, builder, checkoutRules, filter);
        } else {
          if (mappedPath == null)
            continue;
//          LOG.debug(String.format("DIRMAPPED: '%s' => '%s'", relPath, mappedPath));
          final File virtualFile = new File(mappedPath);
          FileInputStream is = null;
          try {
            is = new FileInputStream(realFile);
            builder.createBinaryFile(virtualFile, null, is, realFile.length());
          } finally {
            IOUtil.close(is);
          }
        }
      }
    }
  }
}
