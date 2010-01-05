/*
 * Copyright (c) 2010 Emergent.org
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

package org.emergent.bzr4j.intellij;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManagerAdapter;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.annotate.AnnotationProvider;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.ChangeProvider;
import com.intellij.openapi.vcs.checkin.CheckinEnvironment;
import com.intellij.openapi.vcs.diff.DiffProvider;
import com.intellij.openapi.vcs.history.VcsHistoryProvider;
import com.intellij.openapi.vcs.merge.MergeProvider;
import com.intellij.openapi.vcs.rollback.RollbackEnvironment;
import com.intellij.openapi.vcs.update.UpdateEnvironment;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.messages.Topic;
import org.emergent.bzr4j.intellij.provider.BzrChangeProvider;
import org.emergent.bzr4j.intellij.provider.BzrDiffProvider;
import org.emergent.bzr4j.intellij.provider.BzrHistoryProvider;
import org.emergent.bzr4j.intellij.provider.BzrRollbackEnvironment;
import org.emergent.bzr4j.intellij.provider.annotate.BzrAnnotationProvider;
import org.emergent.bzr4j.intellij.provider.commit.BzrCheckinEnvironment;
import org.emergent.bzr4j.intellij.provider.commit.BzrCommitExecutor;
import org.emergent.bzr4j.intellij.provider.update.BzrIntegrateEnvironment;
import org.emergent.bzr4j.intellij.provider.update.BzrUpdateEnvironment;
import org.emergent.bzr4j.intellij.ui.BzrChangesetStatus;
import org.emergent.bzr4j.intellij.ui.BzrCurrentBranchStatus;
import org.emergent.bzr4j.utils.BzrCoreUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.concurrent.ScheduledFuture;

/**
 * @author Patrick Woodworth
 */
public class BzrVcs extends AbstractVcs implements Disposable {

  static final Logger LOG = Logger.getInstance(BzrVcs.class.getName());

  public static final String VCS_NAME = "Bazaar";

  public static final Topic<BzrUpdater> BRANCH_TOPIC = new Topic<BzrUpdater>("bzr4intellij.branch", BzrUpdater.class);
  public static final Topic<BzrUpdater> INCOMING_TOPIC =
      new Topic<BzrUpdater>("bzr4intellij.incoming", BzrUpdater.class);
  public static final Topic<BzrUpdater> OUTGOING_TOPIC =
      new Topic<BzrUpdater>("bzr4intellij.outgoing", BzrUpdater.class);

  public static final Icon BAZAAR_ICON = IconLoader.getIcon("/images/bzr.png");
  public static final Icon INCOMING_ICON = IconLoader.getIcon("/actions/moveDown.png");
  public static final Icon OUTGOING_ICON = IconLoader.getIcon("/actions/moveUp.png");

  private final AnnotationProvider m_annotationProvider;

  private final DiffProvider m_diffProvider;

  private final CheckinEnvironment m_checkinEnvironment;

  private final ChangeProvider m_changeProvider;

  private final VcsHistoryProvider m_vcsHistoryProvider;

  private final RollbackEnvironment m_rollbackEnvironment;

  private final MergeProvider m_mergeProvider;

  private final UpdateEnvironment updateEnvironment;
  private final BzrIntegrateEnvironment integrateEnvironment;

  private final BzrProjectConfigurable m_configurable;

  private Disposable m_activationDisposable;

  private VirtualFileListener virtualFileListener;

  private final BzrCommitExecutor commitExecutor;
  private final BzrCurrentBranchStatus hgCurrentBranchStatus = new BzrCurrentBranchStatus();
  private final BzrChangesetStatus incomingChangesStatus = new BzrChangesetStatus(BzrVcs.INCOMING_ICON);
  private final BzrChangesetStatus outgoingChangesStatus = new BzrChangesetStatus(BzrVcs.OUTGOING_ICON);
  private MessageBusConnection messageBusConnection;
  private ScheduledFuture<?> changesUpdaterScheduledFuture;

  private boolean started;

  public static BzrVcs getInstance(@NotNull Project project) {
    return (BzrVcs)ProjectLevelVcsManager.getInstance(project).findVcsByName(VCS_NAME);
  }

  public BzrVcs(@NotNull final Project project) {
    super(project, VCS_NAME);

    BzrProjectSettings projectSettings = BzrProjectSettings.getInstance(project);
    m_configurable = new BzrProjectConfigurable(projectSettings);
    m_changeProvider = new BzrChangeProvider(project, getKeyInstanceMethod());
    virtualFileListener = new BzrVirtualFileListener(project, this);
    m_rollbackEnvironment = new BzrRollbackEnvironment(project);
    m_diffProvider = new BzrDiffProvider(project);
    m_vcsHistoryProvider = new BzrHistoryProvider(project);
    m_checkinEnvironment = new BzrCheckinEnvironment(project);
    m_annotationProvider = new BzrAnnotationProvider(project);
    commitExecutor = new BzrCommitExecutor(project);

    m_mergeProvider = null;
    updateEnvironment = new BzrUpdateEnvironment(project);
    integrateEnvironment = BzrDebug.EXPERIMENTAL_ENABLED ? new BzrIntegrateEnvironment(project) : null;

//        LogUtil.dumpImportantData(new Properties());
  }

  @NonNls
  public String getDisplayName() {
    return m_configurable.getDisplayName();
  }

  public void dispose() {
  }

  public Configurable getConfigurable() {
    return m_configurable;
  }

  @Nullable
  public BzrRevisionNumber parseRevisionNumber(String revisionNumberString) {
    BzrRevisionNumber retval = null;
    try {
      retval = BzrRevisionNumber.createBzrRevisionNumber(BzrCoreUtil.parseRevisionNumber(revisionNumberString));
      return retval;
    }
    finally {
      LOG.debug("parseRevisionNumber: " + String.valueOf(retval));
    }
  }

  public DiffProvider getDiffProvider() {
    if (!started) {
      return null;
    }
    return m_diffProvider;
  }

  public AnnotationProvider getAnnotationProvider() {
    if (!started) {
      return null;
    }
    return m_annotationProvider;
  }

  public VcsHistoryProvider getVcsHistoryProvider() {
    if (!started) {
      return null;
    }
    return m_vcsHistoryProvider;
  }

  public VcsHistoryProvider getVcsBlockHistoryProvider() {
    if (!started) {
      return null;
    }
    return getVcsHistoryProvider();
  }

  public ChangeProvider getChangeProvider() {
    if (!started) {
      return null;
    }
    return m_changeProvider;
  }

  public MergeProvider getMergeProvider() {
    if (!started) {
      return null;
    }
    return m_mergeProvider;
  }

  public RollbackEnvironment getRollbackEnvironment() {
    if (!started) {
      return null;
    }
    return m_rollbackEnvironment;
  }

  public CheckinEnvironment getCheckinEnvironment() {
    if (!started) {
      return null;
    }
    return m_checkinEnvironment;
  }

  @Override
  public UpdateEnvironment getUpdateEnvironment() {
    if (!started) {
      return null;
    }

    return updateEnvironment;
  }

  @Override
  public UpdateEnvironment getIntegrateEnvironment() {
    if (!started) {
      return null;
    }

    return integrateEnvironment;
  }

  @Override
  public RootsConvertor getCustomConvertor() {
    return BzrRootConverter.INSTANCE;
  }

  @Override
  public boolean isVersionedDirectory(VirtualFile dir) {
    return dir.isDirectory() && BzrUtil.bzrRootOrNull(dir) != null;
  }

  public boolean isStarted() {
    return started;
  }

  @Override
  protected void start() throws VcsException {
    BzrExecutableValidator validator = new BzrExecutableValidator(myProject);
    started = validator.check(BzrGlobalSettings.getInstance());
  }

  @Override
  protected void shutdown() throws VcsException {
    started = false;
  }

  public void activate() {
    if (!started) {
      return;
    }

    LocalFileSystem.getInstance().addVirtualFileListener(virtualFileListener);

    BzrGlobalSettings globalSettings = BzrGlobalSettings.getInstance();
    BzrProjectSettings projectSettings = BzrProjectSettings.getInstance(myProject);

    ChangeListManager.getInstance(myProject).registerCommitExecutor(commitExecutor);

    StatusBar statusBar = WindowManager.getInstance().getStatusBar(myProject);
    if (statusBar != null) {
      statusBar.addCustomIndicationComponent(hgCurrentBranchStatus);
      statusBar.addCustomIndicationComponent(incomingChangesStatus);
      statusBar.addCustomIndicationComponent(outgoingChangesStatus);
    }

    final BzrIncomingStatusUpdater incomingUpdater =
        new BzrIncomingStatusUpdater(incomingChangesStatus, projectSettings);

    final BzrOutgoingStatusUpdater outgoingUpdater =
        new BzrOutgoingStatusUpdater(outgoingChangesStatus, projectSettings);

//        changesUpdaterScheduledFuture = JobScheduler.getScheduler().scheduleWithFixedDelay(
//                new Runnable() {
//                    public void run() {
//                        incomingUpdater.update(myProject);
//                        outgoingUpdater.update(myProject);
//                    }
//                }, 0, globalSettings.getIncomingCheckIntervalSeconds(), TimeUnit.SECONDS);

    MessageBus messageBus = myProject.getMessageBus();
    messageBusConnection = messageBus.connect();

    messageBusConnection.subscribe(BzrVcs.INCOMING_TOPIC, incomingUpdater);
    messageBusConnection.subscribe(BzrVcs.OUTGOING_TOPIC, outgoingUpdater);

    messageBusConnection.subscribe(
        BzrVcs.BRANCH_TOPIC, new BzrCurrentBranchStatusUpdater(hgCurrentBranchStatus)
    );

    messageBusConnection.subscribe(
        FileEditorManagerListener.FILE_EDITOR_MANAGER,
        new FileEditorManagerAdapter() {
          @Override
          public void selectionChanged(FileEditorManagerEvent event) {
            Project project = event.getManager().getProject();
            project.getMessageBus()
                .asyncPublisher(BzrVcs.BRANCH_TOPIC)
                .update(project);
          }
        }
    );

    m_activationDisposable = new Disposable() {
      public void dispose() {
      }
    };
  }

  public void deactivate() {
    if (!started) {
      return;
    }

    LocalFileSystem.getInstance().removeVirtualFileListener(virtualFileListener);

    StatusBar statusBar = WindowManager.getInstance().getStatusBar(myProject);
    if (messageBusConnection != null) {
      messageBusConnection.disconnect();
    }
    if (changesUpdaterScheduledFuture != null) {
      changesUpdaterScheduledFuture.cancel(true);
    }
    if (statusBar != null) {
      statusBar.removeCustomIndicationComponent(incomingChangesStatus);
      statusBar.removeCustomIndicationComponent(outgoingChangesStatus);
      statusBar.removeCustomIndicationComponent(hgCurrentBranchStatus);
    }

    assert m_activationDisposable != null;
    Disposer.dispose(m_activationDisposable);
    m_activationDisposable = null;
  }
}
