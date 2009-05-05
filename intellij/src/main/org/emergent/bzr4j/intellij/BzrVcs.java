package org.emergent.bzr4j.intellij;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.CommittedChangesProvider;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsConfiguration;
import com.intellij.openapi.vcs.VcsShowConfirmationOption;
import com.intellij.openapi.vcs.annotate.AnnotationProvider;
import com.intellij.openapi.vcs.checkin.CheckinEnvironment;
import com.intellij.openapi.vcs.diff.DiffProvider;
import com.intellij.openapi.vcs.history.VcsHistoryProvider;
import com.intellij.openapi.vcs.rollback.RollbackEnvironment;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.emergent.bzr4j.commandline.CommandLineClient;
import org.emergent.bzr4j.commandline.internal.Commander;
import org.emergent.bzr4j.core.BazaarClientPreferences;
import org.emergent.bzr4j.core.BazaarPreference;
import org.emergent.bzr4j.core.IBazaarClient;
import org.emergent.bzr4j.intellij.gui.BzrVcsConfigurable;
import org.emergent.bzr4j.intellij.providers.BzrAnnotationProvider;
import org.emergent.bzr4j.intellij.providers.BzrChangeProvider;
import org.emergent.bzr4j.intellij.providers.BzrCheckinEnvironment;
import org.emergent.bzr4j.intellij.providers.BzrCommittedChangesProvider;
import org.emergent.bzr4j.intellij.providers.BzrDiffProvider;
import org.emergent.bzr4j.intellij.providers.BzrHistoryProvider;
import org.emergent.bzr4j.intellij.providers.BzrRollbackEnvironment;
import org.emergent.bzr4j.utils.BzrUtil;
import org.emergent.bzr4j.utils.LogUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Patrik Beno
 */
public class BzrVcs extends AbstractVcs implements Disposable
{
    private static final LogUtil LOG = LogUtil.getLogger( BzrVcs.class );

    public static final String VCS_NAME = "Bazaar";

    private Project project;

    private BzrLocalFileOperationsHandler m_vfsHandler;

    private AnnotationProvider annotationProvider;

    private VcsHistoryProvider historyProvider;

    private BzrChangeProvider changeProvider;

    private RollbackEnvironment rollbackEnvironment;

    private CheckinEnvironment checkinEnvironment;

    private CommittedChangesProvider committedChangesProvider;

    private VcsShowConfirmationOption addConfirmation;

    private VcsShowConfirmationOption myDeleteConfirmation;

    private IBazaarClient bzrclient;

    private BzrVcsConfigurable configurable;

    private BzrDiffProvider diffProvider;

    private Disposable activationDisposable;
    
    private static final Map<Project,BzrVcs> sm_instances =
            Collections.synchronizedMap( new HashMap<Project,BzrVcs>() );

    public static BzrVcs getInstance( Project project )
    {
        return sm_instances.get( project );
    }

    public BzrVcs( Project project )
    {
        super( project );

        ApplicationInfo appInfo = ApplicationInfo.getInstance();
        Properties ijprops = new Properties();
        ijprops.setProperty( "intellij.version.build", appInfo.getBuildNumber() );
        ijprops.setProperty( "intellij.version.major", appInfo.getMajorVersion() );
        ijprops.setProperty( "intellij.version.minor", appInfo.getMinorVersion() );
        ijprops.setProperty( "intellij.version.name", appInfo.getVersionName() );
        LogUtil.dumpImportantData( ijprops );

        this.project = project;
        ProjectLevelVcsManager vcsManager = ProjectLevelVcsManager.getInstance( project );
        addConfirmation = vcsManager.getStandardConfirmation( VcsConfiguration.StandardConfirmation.ADD, this );

        myDeleteConfirmation = vcsManager.getStandardConfirmation( VcsConfiguration.StandardConfirmation.REMOVE, this );

        sm_instances.put( project, this );
    }

    @NonNls
    public String getName()
    {
        return VCS_NAME;
    }

    @NonNls
    public String getDisplayName()
    {
        return getName();
    }

    public Project getProject()
    {
        return project;
    }

    public void dispose()
    {
    }

    public BzrVcsConfigurable getConfigurable()
    {
        if ( configurable == null )
        {
            configurable = new BzrVcsConfigurable( getProject() );
        }
        return configurable;
    }

    public void activate()
    {
        super.activate();
        m_vfsHandler = new BzrLocalFileOperationsHandler( this );
        LocalFileSystem.getInstance()
                .registerAuxiliaryFileOperationsHandler( m_vfsHandler );
        CommandProcessor.getInstance().addCommandListener( m_vfsHandler );

        activationDisposable = new Disposable()
        {
                public void dispose()
                {
                }
        };

        bzrclient = new CommandLineClient();
    }

    public void deactivate()
    {
        LOG.info( "BzrVcs.deactivate()" );
        LocalFileSystem.getInstance().unregisterAuxiliaryFileOperationsHandler( m_vfsHandler );
        CommandProcessor.getInstance().removeCommandListener( m_vfsHandler );
        assert activationDisposable != null;
        Disposer.dispose( activationDisposable );
        activationDisposable = null;
        super.deactivate();
    }

    @Nullable
    public BzrRevisionNumber parseRevisionNumber( String revisionNumberString )
    {
        return new BzrRevisionNumber( BzrUtil.parseRevisionNumber( revisionNumberString ));
    }

    public boolean fileIsUnderVcs( FilePath filePath ) {
        return true;
    }
    
    public boolean isVersionedDirectory( VirtualFile dir )
    {
        while (dir != null)
        {
            final VirtualFile versionFile = dir.findChild( ".bzr" );
            if (versionFile != null && versionFile.isDirectory())
                return true;
            dir = dir.getParent();
        }
        return false;
    }

    public IBazaarClient getBzrClient()
    {
        return bzrclient;
    }

    public VcsShowConfirmationOption getAddConfirmation()
    {
        return addConfirmation;
    }

    public VcsShowConfirmationOption getDeleteConfirmation() {
        return myDeleteConfirmation;
    }

    public IBazaarClient getBzrClient( File file )
    {
        IBazaarClient retval = getBzrClient();
        retval.setWorkDir( file );
        return retval;
    }

    public Commander getCommander( final File workDir )
    {
        Commander retval = new Commander()
        {
            public File getDefaultWorkDir()
            {
                return workDir;
            }

            @Override
            public String getBzrExePath()
            {
                return BzrVcsSettings.getInstance().getExecutablePath();
            }
        };
        return retval;
    }

    @Nullable
    public DiffProvider getDiffProvider()
    {
        if ( diffProvider == null )
        {
            diffProvider = new BzrDiffProvider( this );
        }

        return diffProvider;
    }

    @Nullable
    public AnnotationProvider getAnnotationProvider()
    {
        if ( annotationProvider == null )
        {
            annotationProvider = new BzrAnnotationProvider( this );
        }

        return annotationProvider;
    }

    @Nullable
    public VcsHistoryProvider getVcsHistoryProvider()
    {
        if ( historyProvider == null )
        {
            historyProvider = new BzrHistoryProvider( this );
        }
        return historyProvider;
    }

    @Nullable
    public VcsHistoryProvider getVcsBlockHistoryProvider()
    {
        return getVcsHistoryProvider();
    }

    @Nullable
    public BzrChangeProvider getChangeProvider()
    {
        if ( changeProvider == null )
        {
            changeProvider = new BzrChangeProvider( this );
        }
        return changeProvider;
    }

    @Nullable
    public RollbackEnvironment getRollbackEnvironment()
    {
        if ( rollbackEnvironment == null )
        {
            rollbackEnvironment = new BzrRollbackEnvironment( this );
        }

        return rollbackEnvironment;
    }

    @Nullable
    public CheckinEnvironment getCheckinEnvironment()
    {
        if ( checkinEnvironment == null )
        {
            checkinEnvironment = new BzrCheckinEnvironment( this );
        }
        return checkinEnvironment;
    }

    public CommittedChangesProvider getCommittedChangesProvider()
    {
        if ( committedChangesProvider == null )
        {
            committedChangesProvider = new BzrCommittedChangesProvider( this );
        }
        return committedChangesProvider;
    }
}
