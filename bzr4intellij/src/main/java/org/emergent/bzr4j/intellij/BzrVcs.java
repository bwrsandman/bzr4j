package org.emergent.bzr4j.intellij;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.CommittedChangesProvider;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsConfiguration;
import com.intellij.openapi.vcs.VcsShowConfirmationOption;
import com.intellij.openapi.vcs.annotate.AnnotationProvider;
import com.intellij.openapi.vcs.checkin.CheckinEnvironment;
import com.intellij.openapi.vcs.diff.DiffProvider;
import com.intellij.openapi.vcs.history.VcsHistoryProvider;
import com.intellij.openapi.vcs.merge.MergeProvider;
import com.intellij.openapi.vcs.rollback.RollbackEnvironment;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.emergent.bzr4j.intellij.config.BzrVcsSettings;
import org.emergent.bzr4j.intellij.gui.BzrVcsConfigurable;
import org.emergent.bzr4j.intellij.providers.BzrAnnotationProvider;
import org.emergent.bzr4j.intellij.providers.BzrChangeProvider;
import org.emergent.bzr4j.intellij.providers.BzrCheckinEnvironment;
import org.emergent.bzr4j.intellij.providers.BzrCommittedChangesProvider;
import org.emergent.bzr4j.intellij.providers.BzrDiffProvider;
import org.emergent.bzr4j.intellij.providers.BzrHistoryProvider;
import org.emergent.bzr4j.intellij.providers.BzrMergeProvider;
import org.emergent.bzr4j.intellij.providers.BzrRollbackEnvironment;
import org.emergent.bzr4j.utils.BzrUtil;
import org.emergent.bzr4j.utils.LogUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Properties;

/**
 * @author Patrik Beno
 */
public class BzrVcs extends AbstractVcs implements Disposable
{
    private static final LogUtil LOG = LogUtil.getLogger( BzrVcs.class );

    public static final String VCS_NAME = "Bazaar";

    private BzrLocalFileOperationsHandler m_vfsHandler;

    private final BzrAnnotationProvider m_annotationProvider;

    private final BzrDiffProvider m_diffProvider;

    private final BzrCheckinEnvironment m_checkinEnvironment;

    private final BzrChangeProvider m_changeProvider;

    private final BzrHistoryProvider m_vcsHistoryProvider;

    private final BzrRollbackEnvironment m_rollbackEnvironment;

    private final BzrCommittedChangesProvider m_committedChangesProvider;

    private final BzrVcsSettings m_settings;

    private final BzrVcsConfigurable m_configurable;

    private final BzrMergeProvider m_mergeProvider;

    private VcsShowConfirmationOption m_addConfirmation;

    private VcsShowConfirmationOption m_deleteConfirmation;

    private Disposable m_activationDisposable;

    public static BzrVcs getInstance( @NotNull Project project )
    {
        return (BzrVcs)ProjectLevelVcsManager.getInstance( project ).findVcsByName( VCS_NAME );
    }

    public BzrVcs(
            @NotNull final Project project,
            @NotNull final BzrAnnotationProvider bzrAnnotationProvider,
            @NotNull final BzrDiffProvider bzrDiffProvider,
            @NotNull final BzrCheckinEnvironment checkinEnvironment,
            @NotNull final BzrChangeProvider changeProvider,
            @NotNull final BzrHistoryProvider vcsHistoryProvider,
            @NotNull final BzrRollbackEnvironment rollbackEnvironment,
            @NotNull final BzrCommittedChangesProvider committedChangesProvider,
            @NotNull final BzrVcsSettings bzrSettings
    )
    {
        super( project, VCS_NAME );
        m_annotationProvider = bzrAnnotationProvider;
        m_diffProvider = bzrDiffProvider;
        m_checkinEnvironment = checkinEnvironment;
        m_changeProvider = changeProvider;
        m_vcsHistoryProvider = vcsHistoryProvider;
        m_rollbackEnvironment = rollbackEnvironment;
        m_committedChangesProvider = committedChangesProvider;
        m_settings = bzrSettings;

//        myRevSelector = new GitRevisionSelector();
        m_configurable = new BzrVcsConfigurable(/* mySettings, */ myProject );
//        myUpdateEnvironment = new GitUpdateEnvironment(myProject, this, mySettings);
        m_mergeProvider = new BzrMergeProvider( myProject );
//        myCommittedChangeListProvider = new GitCommittedChangeListProvider(myProject);
//        myOutgoingChangesProvider = new GitOutgoingChangesProvider(myProject);
//        myTreeDiffProvider = new GitTreeDiffProvider(myProject);

        ApplicationInfo appInfo = ApplicationInfo.getInstance();
        Properties ijprops = new Properties();
        ijprops.setProperty( "intellij.version.build", "" + appInfo.getBuild().getBuildNumber() );
        ijprops.setProperty( "intellij.version.major", appInfo.getMajorVersion() );
        ijprops.setProperty( "intellij.version.minor", appInfo.getMinorVersion() );
        ijprops.setProperty( "intellij.version.name", appInfo.getVersionName() );
        if (m_settings.isExtraLoggingEnabled())
            LogUtil.dumpImportantData( ijprops );

        ProjectLevelVcsManager vcsManager = ProjectLevelVcsManager.getInstance( project );
        m_addConfirmation = vcsManager.getStandardConfirmation( VcsConfiguration.StandardConfirmation.ADD, this );

        m_deleteConfirmation = vcsManager.getStandardConfirmation( VcsConfiguration.StandardConfirmation.REMOVE, this );
    }

    @NonNls
    public String getDisplayName()
    {
        return getName();
    }

    public void dispose()
    {
    }

    public BzrVcsConfigurable getConfigurable()
    {
        return m_configurable;
    }

    public void activate()
    {
        super.activate();
        m_vfsHandler = new BzrLocalFileOperationsHandler( this );
        LocalFileSystem.getInstance().registerAuxiliaryFileOperationsHandler( m_vfsHandler );
        CommandProcessor.getInstance().addCommandListener( m_vfsHandler );

        m_activationDisposable = new Disposable()
        {
            public void dispose()
            {
            }
        };
    }

    public void deactivate()
    {
        LOG.info( "BzrVcs.deactivate()" );
        LocalFileSystem.getInstance().unregisterAuxiliaryFileOperationsHandler( m_vfsHandler );
        CommandProcessor.getInstance().removeCommandListener( m_vfsHandler );
        assert m_activationDisposable != null;
        Disposer.dispose( m_activationDisposable );
        m_activationDisposable = null;
        super.deactivate();
    }

    @Nullable
    public BzrRevisionNumber parseRevisionNumber( String revisionNumberString )
    {
        return new BzrRevisionNumber( BzrUtil.parseRevisionNumber( revisionNumberString ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVersionedDirectory(VirtualFile dir) {
      return dir.isDirectory() && BzrIdeaUtil.bzrRootOrNull(dir) != null;
    }


    public VcsShowConfirmationOption getAddConfirmation()
    {
        return m_addConfirmation;
    }

    public VcsShowConfirmationOption getDeleteConfirmation()
    {
        return m_deleteConfirmation;
    }

    @NotNull
    public DiffProvider getDiffProvider()
    {
        return m_diffProvider;
    }

    @NotNull
    public AnnotationProvider getAnnotationProvider()
    {
        return m_annotationProvider;
    }

    @NotNull
    public VcsHistoryProvider getVcsHistoryProvider()
    {
        return m_vcsHistoryProvider;
    }

    @NotNull
    public VcsHistoryProvider getVcsBlockHistoryProvider()
    {
        return getVcsHistoryProvider();
    }

    @NotNull
    public BzrChangeProvider getChangeProvider()
    {
        return m_changeProvider;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public MergeProvider getMergeProvider()
    {
        return m_mergeProvider;
    }

    @NotNull
    public RollbackEnvironment getRollbackEnvironment()
    {
        return m_rollbackEnvironment;
    }

    @NotNull
    public CheckinEnvironment getCheckinEnvironment()
    {
        return m_checkinEnvironment;
    }

    @NotNull
    public CommittedChangesProvider getCommittedChangesProvider()
    {
        return m_committedChangesProvider;
    }
}
