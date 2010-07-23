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
import jetbrains.buildServer.CollectChangesByIncludeRule;
import jetbrains.buildServer.Used;
import jetbrains.buildServer.buildTriggers.vcs.AbstractVcsPropertiesProcessor;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.vcs.BuildPatchByCheckoutRules;
import jetbrains.buildServer.vcs.CheckoutRules;
import jetbrains.buildServer.vcs.CollectChangesPolicy;
import jetbrains.buildServer.vcs.IncludeRule;
import jetbrains.buildServer.vcs.LabelingSupport;
import jetbrains.buildServer.vcs.ModificationData;
import jetbrains.buildServer.vcs.VcsChange;
import jetbrains.buildServer.vcs.VcsChangeInfo;
import jetbrains.buildServer.vcs.VcsException;
import jetbrains.buildServer.vcs.VcsFileContentProvider;
import jetbrains.buildServer.vcs.VcsManager;
import jetbrains.buildServer.vcs.VcsModification;
import jetbrains.buildServer.vcs.VcsRoot;
import jetbrains.buildServer.vcs.VcsSupport;
import jetbrains.buildServer.vcs.VcsSupportUtil;
import jetbrains.buildServer.vcs.patches.PatchBuilder;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.cli.BzrAbstractResult;
import org.emergent.bzr4j.core.cli.BzrExecException;
import org.emergent.bzr4j.core.cli.BzrSinkResult;
import org.emergent.bzr4j.core.cli.BzrStandardResult;
import org.emergent.bzr4j.core.cli.BzrXmlResult;
import org.emergent.bzr4j.core.utils.IOUtil;
import org.emergent.bzr4j.core.xmloutput.XmlOutputHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
public class BazaarVcsSupport extends VcsSupport
    implements CollectChangesByIncludeRule {

  public static final Logger LOGVCS = Loggers.VCS;

  private static final Logger LOG = Logger.getInstance(BazaarVcsSupport.class.getName());

  private static final int OLD_WORK_DIRS_CLEANUP_PERIOD = 600;

  private VcsManager m_vcsManager;

  private File myDefaultWorkFolderParent;

  public BazaarVcsSupport(
      @NotNull final VcsManager vcsManager,
      @NotNull ServerPaths paths,
      @NotNull SBuildServer server) {
    vcsManager.registerVcsSupport(this);
    m_vcsManager = vcsManager;
    server.getExecutor().scheduleAtFixedRate(new Runnable() {
      public void run() {
        removeOldWorkFolders();
      }
    }, 0, OLD_WORK_DIRS_CLEANUP_PERIOD, TimeUnit.SECONDS);
    myDefaultWorkFolderParent = new File(paths.getCachesDir(), "bazaar");
  }

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
    return new AbstractVcsPropertiesProcessor() {
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
    };
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
    LOG.debug("getVersionDisplayName()");
    return new ChangeRev(version).getId();
  }

  @NotNull
  public Comparator<String> getVersionComparator() {
    return new VcsSupportUtil.StringVersionComparator();
  }

  @NotNull
  public String getCurrentVersion(@NotNull VcsRoot root) throws VcsException {
    LOG.debug("getCurrentVersion()");
    // we will return full version of the most recent change as current version
    syncClonedRepository(root);
    Settings settings = createSettings(root);
    try {
      BzrTeamcityExec handler = new BzrTeamcityExec(settings,"revno");
      BzrStandardResult result = handler.exectc();
      return result.getStdOutAsString().trim();
    } catch (BzrExecException e) {
      throw new VcsException(e);
    }
  }

  @Override
  public boolean sourcesUpdatePossibleIfChangesNotFound(@NotNull VcsRoot root) {
    return true;
  }

  @NotNull
  public VcsFileContentProvider getContentProvider() {
    return super.getContentProvider();
  }

  @NotNull
  public CollectChangesPolicy getCollectChangesPolicy() {
    return super.getCollectChangesPolicy();
  }

  @NotNull
  public BuildPatchByCheckoutRules getBuildPatchPolicy() {
    return super.getBuildPatchPolicy();
  }

  public boolean isTestConnectionSupported() {
    return true;
  }

  @Nullable
  public String testConnection(@NotNull final VcsRoot vcsRoot) throws VcsException {
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

  @Override
  public boolean ignoreServerCachesFor(@NotNull final VcsRoot root) {
    // since a copy of repository for each VCS root is already stored on disk
    // we do not need separate cache for our patches
    return true;
  }

  protected void lockWorkDir(@NotNull File workDir) {
    getWorkDirLock(workDir).lock();
  }

  protected void unlockWorkDir(@NotNull File workDir) {
    getWorkDirLock(workDir).unlock();
  }

  private Lock getWorkDirLock(final File workDir) {
    Lock lock = BzrTeamcityExec.getWorkDirLock(workDir);
    return lock;
  }

  public List<ModificationData> collectBuildChanges(
      VcsRoot root,
      @NotNull String fromVersion,
      @NotNull String currentVersion,
      CheckoutRules checkoutRules) throws VcsException {
    syncClonedRepository(root);
    return VcsSupportUtil.collectBuildChanges(root, fromVersion, currentVersion, checkoutRules, this);
  }

  public List<ModificationData> collectBuildChanges(
      final VcsRoot root,
      final String fromVersion,
      final String currentVersion,
      final IncludeRule includeRule) throws VcsException {
    LOG.info(
        String.format("Collecting changes for %s; from version %s to version %s;\n%s",
            root.getProperty(TCConstants.BRANCH_NAME_PROP),
            fromVersion, currentVersion, root.convertToPresentableString())
    );
    try {
      // first obtain changes between specified versions
      List<ModificationData> result = new ArrayList<ModificationData>();
      Settings settings = createSettings(root);

      String fromId = new ChangeRev(fromVersion).getId();
      BzrTeamcityExec handler = new BzrTeamcityExec(settings, "xmllog", "-r", fromVersion + ".." + currentVersion);

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

        List<VcsChange> files = TCUtil.getVcsChanges(settings, prev, cur, includeRule);
        if (files.isEmpty()) continue;
        ModificationData md = new ModificationData(cur.getTimestamp(), files, cur.getSummary(),
            cur.getUser(), root, cur.getFullVersion(), cur.getId());
        result.add(md);
        prev = cur;
      }

      return result;
    } catch (BzrExecException e) {
      throw new VcsException(e);
    }
  }

  @NotNull
  public byte[] getContent(
      @NotNull final VcsModification vcsModification,
      @NotNull final VcsChangeInfo change,
      @NotNull final VcsChangeInfo.ContentType contentType,
      @NotNull final VcsRoot vcsRoot) throws VcsException {
    String version = contentType == VcsChangeInfo.ContentType.AFTER_CHANGE
        ? change.getAfterChangeRevisionNumber()
        : change.getBeforeChangeRevisionNumber();
    return getContent(change.getRelativeFileName(), vcsRoot, version);
  }

  @NotNull
  public byte[] getContent(@NotNull String filePath, @NotNull VcsRoot vcsRoot, @NotNull String version)
      throws VcsException {
    syncClonedRepository(vcsRoot);
    Settings settings = createSettings(vcsRoot);
    File parentDir = null;
    try {
      parentDir = doCat(settings, version, Collections.singletonList(filePath));
      File file = new File(parentDir, filePath);
      if (file.isFile()) {
        return FileUtil.loadFileBytes(file);
      } else {
        LOG.warn("Unable to obtain content of the file: " + filePath);
      }
    } catch (IOException e) {
      throw new VcsException("Failed to load content of file", e);
    } catch (BazaarException e) {
      throw new VcsException("Failed to load content of file", e);
    } finally {
      if (parentDir != null)
        FileUtil.delete(parentDir);
    }
    return new byte[0];
  }

  public void buildPatch(
      @NotNull final VcsRoot root,
      @Nullable final String fromVersion,
      @NotNull final String toVersion,
      @NotNull final PatchBuilder builder,
      @NotNull final CheckoutRules checkoutRules) throws IOException, VcsException {
    syncClonedRepository(root);
    Settings settings = createSettings(root);
    try {
      if (fromVersion == null) {
        LOG.debug("building full patch: " + toVersion);
        buildFullPatch(settings, new ChangeRev(toVersion), builder, checkoutRules);
      } else {
        LOG.debug("building delta patch: " + fromVersion + " " + toVersion);
        buildIncrementalPatch(settings, new ChangeRev(fromVersion), new ChangeRev(toVersion), builder);
      }
    } catch (BazaarException e) {
      LOG.error(e);
      throw new VcsException(e);
    }
  }

  @Override
  public LabelingSupport getLabelingSupport() {
    return new LabelingSupport() {
      public String label(@NotNull String label, @NotNull String version, @NotNull VcsRoot root,
          @NotNull CheckoutRules checkoutRules)
          throws VcsException {
        try {
          Settings settings = createSettings(root);
          (new BzrTeamcityExec(settings,"tag","-r",version,"--force",label)).exectc(true);
          (new BzrTeamcityExec(settings,"push","-q",settings.getPushUrl())).exectc(true);
          return label;
        } catch (BzrExecException e) {
          throw new VcsException(e);
        }
      }
    };
  }

  // builds patch from version to version

  private void buildIncrementalPatch(final Settings settings, @NotNull final ChangeRev fromVer,
      @NotNull final ChangeRev toVer, final PatchBuilder builder)
      throws VcsException, IOException, BazaarException, BzrExecException {
    List<ModifiedFile> modifiedFiles = TCUtil.getModifiedFiles(settings, fromVer, toVer);

    List<String> notDeletedFiles = new ArrayList<String>();
    for (ModifiedFile f : modifiedFiles) {
      if (f.getChangeType() != VcsChangeInfo.Type.REMOVED) {
        notDeletedFiles.add(f.getPath());
      }
    }

    if (notDeletedFiles.isEmpty())
      return;

    File parentDir = doCat(settings, toVer.getId(), notDeletedFiles);

    try {
      for (ModifiedFile f : modifiedFiles) {
        final File virtualFile = new File(f.getPath());
        if (f.getChangeType() == VcsChangeInfo.Type.REMOVED) {
          builder.deleteFile(virtualFile, true);
        } else {
          File realFile = new File(parentDir, f.getPath());
          FileInputStream is = new FileInputStream(realFile);
          try {
            builder.changeOrCreateBinaryFile(virtualFile, null, is, realFile.length());
          } finally {
            is.close();
          }
        }
      }
    }
    finally {
      FileUtil.delete(parentDir);
    }
  }

  private File doCat(Settings settings, String myRevId, List<String> relPaths)
      throws IOException, BazaarException, BzrExecException {

    File tempDir = IOUtil.createTempDirectory("bazaar", "catresult");
    for (String path : relPaths) {
      final File parentFile = new File(tempDir, path).getParentFile();
      if (!parentFile.isDirectory() && !parentFile.mkdirs()) {
        throw new BazaarException("Failed to create directory: " + parentFile.getAbsolutePath());
      }
    }

    for (String p : relPaths) {
      BzrSinkResult sinkResult = new BzrSinkResult(new File(tempDir.getAbsolutePath(), p));
      (new BzrTeamcityExec(settings, "cat", "-r", myRevId, IOUtil.deNormalizeSeparator(p))).exectc(sinkResult);
    }

    return tempDir;
  }

  // builds patch by exporting files using specified version

  private void buildFullPatch(
      final Settings settings, @NotNull final ChangeRev toVer,
      final PatchBuilder builder, CheckoutRules checkoutRules)
      throws IOException, VcsException, BazaarException, BzrExecException {
    File tempDir = FileUtil.createTempDirectory("bazaar", toVer.getId());
    try {
      final File repRoot = new File(tempDir, "rep");
      BzrTeamcityExec handler = new BzrTeamcityExec(settings,"export","-q","--format=dir","-r",toVer.getId(),
          IOUtil.deNormalizeSeparator(repRoot.getAbsolutePath()));
      handler.exectc(true);
      buildPatchFromDirectory(builder, repRoot, new FileFilter() {
        public boolean accept(final File file) {
          return !(file.isDirectory() && ".bzr".equals(file.getName()));
        }
      }, checkoutRules);
    }
    finally {
      FileUtil.delete(tempDir);
    }
  }

  private void buildPatchFromDirectory(
      final PatchBuilder builder, final File repRoot,
      final FileFilter filter, CheckoutRules checkoutRules) throws IOException {
    buildPatchFromDirectory(repRoot, builder, repRoot, filter, checkoutRules);
  }

  private void buildPatchFromDirectory(
      File curDir, final PatchBuilder builder,
      final File repRoot, final FileFilter filter, CheckoutRules checkoutRules) throws IOException {
    File[] files = curDir.listFiles(filter);
    if (files != null) {
      for (File realFile : files) {
        String relPath = realFile.getAbsolutePath().substring(repRoot.getAbsolutePath().length());
        String mappedPath = checkoutRules.map(relPath);
        if (mappedPath != null) {
          final File virtualFile = new File(mappedPath);
          if (realFile.isDirectory()) {
            builder.createDirectory(virtualFile);
            buildPatchFromDirectory(realFile, builder, repRoot, filter, checkoutRules);
          } else {
            final FileInputStream is = new FileInputStream(realFile);
            try {
              builder.createBinaryFile(virtualFile, null, is, realFile.length());
            }
            finally {
              is.close();
            }
          }
        }
      }
    }
  }

  // updates current working copy of repository by pulling changes from the repository specified in VCS root

  private void syncClonedRepository(final VcsRoot root) throws VcsException {
    Settings settings = createSettings(root);
    File workDir = settings.getLocalRepositoryDir();
//    lockWorkDir(workDir);
    try {
      BzrTeamcityExec handler = null;
      if (settings.hasCopyOfRepository()) {
        handler = new BzrTeamcityExec(settings, "pull", "-q", "--overwrite", settings.getRepositoryUrl());
      } else {
        handler = new BzrTeamcityExec(settings, "clone","-q", settings.getRepositoryUrl(), workDir.getAbsolutePath());
      }
      BzrAbstractResult result = handler.exectc();
    } catch (BzrExecException e) {
      throw new VcsException(e);
    } finally {
//      unlockWorkDir(workDir);
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
      lockWorkDir(f);
      try {
        FileUtil.delete(f);
      }
      finally {
        unlockWorkDir(f);
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
    if (!parentDir.exists() && !parentDir.mkdirs()) {
      throw new VcsException("Failed to create parent directory for cloned repository: " + parentDir.getAbsolutePath());
    }
  }
}
