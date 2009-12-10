package org.emergent.bzr4j.intellij.providers;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeList;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.checkin.CheckinEnvironment;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcsUtil.VcsUtil;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.intellij.utils.IJUtil;
import org.emergent.bzr4j.utils.BzrUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BzrCheckinEnvironment implements CheckinEnvironment
{
    private final Project m_project;

    public BzrCheckinEnvironment( @NotNull Project project )
    {
        m_project = project;
    }

    @Nullable
    public RefreshableOnComponent createAdditionalOptionsPanel( CheckinProjectPanel panel )
    {
        return null; // todo: create "local commit" option panel
    }

    @Nullable
    public String getDefaultMessageFor( FilePath[] filesToCheckin )
    {
        return null;
    }

    public String prepareCheckinMessage( String text )
    {
        return text;
    }

    @Nullable
    @NonNls
    public String getHelpId()
    {
        return null;
    }

    public String getCheckinOperationName()
    {
        return "Commit";
    }

    public boolean showCheckinDialogInAnyCase()
    {
        return false;
    }


    public List<VcsException> commit( List<Change> changes, String preparedComment )
    {
        return commit( changes, preparedComment, null );
    }

    /**
     * {@inheritDoc}
     * @since 8.0M1.9678
     */
    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Nullable
    public List<VcsException> commit( List<Change> changes, String preparedComment, Object parameters )
    {
        Set<File> roots = getBzrRoots( changes );

        if ( roots.size() > 1 )
        {
            return Arrays.asList( new VcsException( String.format(
                    "Too many VCS roots. Cannot commit to multiple VCS roots at once. Roots (%s): %s",
                    roots.size(), roots ) ) );
        }

        List<File> files = new ArrayList<File>( changes.size() );
        for ( Change change : changes )
        {
            ContentRevision revision = change.getAfterRevision();
            if ( revision == null )
            {
                revision = change.getBeforeRevision();
            }

            files.add( revision.getFile().getIOFile() );
        }

        try
        {
            IJUtil.createBzrClient().setWorkDir( IJUtil.root( files.iterator().next() ) );
            IJUtil.createBzrClient().commit( files.toArray( new File[files.size()] ), preparedComment );
            return null;
        }
        catch ( Exception e )
        {
            return Arrays.asList( new VcsException( e ) );
        }
    }

    @Nullable
    public List<VcsException> scheduleMissingFileForDeletion( List<FilePath> files )
    {
//        throw new UnsupportedOperationException( "scheduleMissingFileForDeletion: " + files );
        // should be automatic with bzr4j
        return null;
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Nullable
    public List<VcsException> scheduleUnversionedFilesForAddition( List<VirtualFile> files )
    {
        List<VcsException> exceptions = new ArrayList<VcsException>();
        File[] ary = new File[files.size()];
        for ( int i = 0; i < files.size(); i++ )
        {
            ary[i] = new File( files.get( i ).getPath() );
        }
        try
        {
            IJUtil.createBzrClient().add( ary );
        }
        catch ( BazaarException e )
        {
            exceptions.add( new VcsException( e.getMessage(), e ) );
        }
        // @todo trigger an update of the Unversioned folder. 
        return exceptions;
    }

    private Set<File> getBzrRoots( List<Change> changes )
    {
        // 1) get roots as suggested by IDEA
        Set<VirtualFile> vroots = new HashSet<VirtualFile>();
        for ( Change change : changes )
        {
            ContentRevision rev = change.getAfterRevision() != null ? change.getAfterRevision()
                    : change.getBeforeRevision();
            FilePath f = rev.getFile();
            VirtualFile root = VcsUtil.getVcsRootFor( m_project, f );
            vroots.add( root );
        }

        // 2) normalize for m_bzr workspace: ask m_bzr about all IDEA VCS roots
        Set<File> roots = new HashSet<File>();
        for ( VirtualFile vfile : vroots )
        {
            File root = BzrUtil.getRootBranch( new File( vfile.getPath() ) );
            roots.add( root );
        }
        return roots;
    }

    public boolean keepChangeListAfterCommit( ChangeList changeList )
    {
        return false;
    }
}
