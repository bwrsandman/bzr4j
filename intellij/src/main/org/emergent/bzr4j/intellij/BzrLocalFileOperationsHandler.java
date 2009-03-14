package org.emergent.bzr4j.intellij;

import com.intellij.openapi.command.CommandEvent;
import com.intellij.openapi.command.CommandListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.AbstractVcsHelper;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.VcsShowConfirmationOption;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.VcsDirtyScopeManager;
import com.intellij.openapi.vfs.LocalFileOperationsHandler;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.RefreshQueue;
import com.intellij.openapi.vfs.newvfs.RefreshSession;
import org.emergent.bzr4j.commandline.commands.options.Option;
import org.emergent.bzr4j.commandline.syntax.IAddOptions;
import org.emergent.bzr4j.commandline.syntax.IMoveOptions;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.intellij.utils.IJUtil;
import org.emergent.bzr4j.utils.BazaarIOException;
import org.emergent.bzr4j.utils.LogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BzrLocalFileOperationsHandler implements LocalFileOperationsHandler, CommandListener
{
    private static final LogUtil sm_logger = LogUtil.getLogger( BzrLocalFileOperationsHandler.class );

    private static class AddedFileInfo
    {
        private final Project myProject;

        private final VirtualFile myDir;

        private final String myName;

        public AddedFileInfo( Project project, VirtualFile dir, String name )
        {
            myProject = project;
            myDir = dir;
            myName = name;
        }
    }

    private static class MovedFileInfo
    {
        private final Project myProject;

        private final File mySrc;

        private final File myDst;

        private MovedFileInfo( Project project, File src, File dst )
        {
            myProject = project;
            mySrc = src;
            myDst = dst;
        }
    }

    private List<AddedFileInfo> myAddedFiles = new ArrayList<AddedFileInfo>();

    private List<MovedFileInfo> myMovedFiles = new ArrayList<MovedFileInfo>();

    private Map<Project, List<VcsException>> myMoveExceptions =
            new HashMap<Project, List<VcsException>>();

    private List<VirtualFile> myFilesToRefresh = new ArrayList<VirtualFile>();

//    private BzrVcs m_vcs;

    public BzrLocalFileOperationsHandler(BzrVcs vcs)
    {
//        m_vcs = vcs;
    }

    @Nullable
    public File copy( final VirtualFile file, final VirtualFile toDir, final String copyName )
            throws IOException
    {
        return null;
    }

    public boolean move( VirtualFile file, VirtualFile toDir ) throws IOException
    {
        sm_logger.info( "move(" + file + "," + toDir + ");" );
        File srcFile = IJUtil.getIOFile( file );
        File dstFile = new File( IJUtil.getIOFile( toDir ), file.getName() );
        BzrVcs vcs = IJUtil.getBzrVcs( file );
        if (vcs == null)
            return false;
        try
        {
            if ( IJUtil.isVersioned( vcs, file ))
            {
                myMovedFiles.add( new MovedFileInfo( vcs.getProject(), srcFile, dstFile ) );
                myFilesToRefresh.add( file.getParent() );
                myFilesToRefresh.add( toDir );
            }
        }
        catch ( BazaarException e )
        {
            throw new BazaarIOException( e );
        }
        return false;
    }

    public boolean rename( VirtualFile file, String newname ) throws IOException
    {
        sm_logger.info( "rename(" + file + "," + newname + ");" );
        BzrVcs bzr = IJUtil.getBzrVcs( file );
        if ( bzr == null ) { return false; }

        File orig = new File( file.getPath() );
        File newFile = new File( file.getParent().getPath(), newname );

        // If the file isn't under Bzr control yet, let IntelliJ handle it normally.
        try
        {
            if ( IJUtil.isVersioned( bzr, file ))
            {
                myMovedFiles.add( new MovedFileInfo( bzr.getProject(), orig, newFile ) );
                myFilesToRefresh.add( file.getParent() );
            }
        }
        catch ( BazaarException e )
        {
            throw new BazaarIOException( e );
        }
        return false;
    }

    private boolean doMove( @NotNull BzrVcs vcs, final File src, final File dst, boolean after )
            throws BazaarException
    {
        List<Option> options = new ArrayList<Option>();
        if ( after )
        {
            options.add( IMoveOptions.AFTER );
        }
        vcs.getBzrClient().move( src, dst, options.toArray( new Option[options.size()] ) );
        return true;
    }

    public boolean createFile( VirtualFile dir, String name ) throws IOException
    {
        return createItem( dir, name, false );
    }

    public boolean createDirectory( VirtualFile dir, String name ) throws IOException
    {
        return createItem( dir, name, true );
    }

    public boolean delete( VirtualFile file ) throws IOException
    {
        myFilesToRefresh.add( file.getParent() );
        return false;
    }

    /**
     * add file or directory:
     * <p/>
     * parent directory is:
     * unversioned: do nothing, return false
     * versioned:
     * entry is:
     * null: create entry, schedule for addition
     * missing: do nothing, return false
     * deleted, 'do' mode: try to create entry and it schedule for addition if kind is the same, otherwise do nothing, return false.
     * deleted: 'undo' mode: try to revert non-recursively, if kind is the same, otherwise do nothing, return false.
     * anything else: return false.
     */
    private boolean createItem( VirtualFile dir, String name, boolean directory ) throws IOException
    {
        sm_logger.info( "createItem(" + dir + "," + name + ")" );
        BzrVcs vcs = IJUtil.getBzrVcs( dir );
        myAddedFiles.add( new AddedFileInfo( vcs.getProject(), dir, name ) );
        return false;
    }

    public void commandStarted( CommandEvent event )
    {
//      myUndoingMove = false;
//      final Project project = event.getProject();
//      if (project == null) return;
//      myMoveExceptions.remove(project);
    }

    public void beforeCommandFinished( CommandEvent event )
    {
    }

    public void commandFinished( CommandEvent event )
    {
        final Project project = event.getProject();
        if ( project == null ) return;
        if ( myAddedFiles.size() > 0 )
        {
            processAddedFiles( project );
        }
        processMovedFiles( project );

        if ( myFilesToRefresh.size() > 0 )
        {
            refreshFiles( project );
        }
    }

    private void refreshFiles( final Project project )
    {
        final List<VirtualFile> toRefreshFiles = new ArrayList<VirtualFile>();
        final List<VirtualFile> toRefreshDirs = new ArrayList<VirtualFile>();
        for ( VirtualFile file : myFilesToRefresh )
        {
            if ( file.isDirectory() )
            {
                sm_logger.info( "Refreshing dir: " + file.getPath() );
                toRefreshDirs.add( file );
            }
            else
            {
                sm_logger.info( "Refreshing file: " + file.getPath() );
                toRefreshFiles.add( file );
            }
        }
        // if refresh asynchronously, local changes would also be notified that they are dirty asynchronously,
        // and commit could be executed while not all changes are visible
        final RefreshSession session =
                RefreshQueue.getInstance().createSession( false, true, new Runnable()
                {
                    public void run()
                    {
                        if ( project.isDisposed() ) return;
                        filterOutInvalid( toRefreshFiles );
                        filterOutInvalid( toRefreshDirs );

                        final VcsDirtyScopeManager vcsDirtyScopeManager =
                                VcsDirtyScopeManager.getInstance( project );
                        vcsDirtyScopeManager.filesDirty( toRefreshFiles, toRefreshDirs );
                    }
                } );
        session.addAllFiles( myFilesToRefresh );
        session.launch();
        myFilesToRefresh.clear();
    }

    private void filterOutInvalid( final Collection<VirtualFile> files )
    {
        for ( Iterator<VirtualFile> iterator = files.iterator(); iterator.hasNext(); )
        {
            final VirtualFile file = iterator.next();
            if ( !file.isValid() )
            {
                sm_logger.info( "Refresh root is not valid: " + file.getPath() );
                iterator.remove();
            }
        }
    }

    private void processAddedFiles( Project project )
    {
        final BzrVcs vcs = BzrVcs.getInstance( project );
        List<VirtualFile> addedVFiles = new ArrayList<VirtualFile>();
        for ( Iterator<AddedFileInfo> it = myAddedFiles.iterator(); it.hasNext(); )
        {
            AddedFileInfo addedFileInfo = it.next();
            if ( addedFileInfo.myProject == project )
            {
                it.remove();
                VirtualFile addedFile = addedFileInfo.myDir.findChild( addedFileInfo.myName );
                if ( addedFile != null )
                {
                    boolean isIgnored = ChangeListManager.getInstance( addedFileInfo.myProject )
                            .isIgnoredFile( addedFile );
                    if ( !isIgnored )
                    {
                        addedVFiles.add( addedFile );
                    }
                }
            }
        }
        if ( addedVFiles.size() == 0 ) return;
        final VcsShowConfirmationOption.Value value = vcs.getAddConfirmation().getValue();
        if ( value != VcsShowConfirmationOption.Value.DO_NOTHING_SILENTLY )
        {
            final AbstractVcsHelper vcsHelper = AbstractVcsHelper.getInstance( project );
            Collection<VirtualFile> filesToProcess;
            if ( value == VcsShowConfirmationOption.Value.DO_ACTION_SILENTLY )
            {
                filesToProcess = addedVFiles;
            }
            else
            {
                final String singleFilePrompt;
                if ( addedVFiles.size() == 1 && addedVFiles.get( 0 ).isDirectory() )
                {
                    singleFilePrompt = BzrBundle.message( "confirmation.text.add.dir" );
                }
                else
                {
                    singleFilePrompt = BzrBundle.message( "confirmation.text.add.file" );
                }
                filesToProcess = vcsHelper.selectFilesToProcess( addedVFiles,
                        BzrBundle.message( "confirmation.title.add.multiple.files" ),
                        null,
                        BzrBundle.message( "confirmation.title.add.file" ), singleFilePrompt,
                        vcs.getAddConfirmation() );
            }
            if ( filesToProcess != null )
            {
                final List<VcsException> exceptions = new ArrayList<VcsException>();
                for ( VirtualFile file : filesToProcess )
                {
                    final File ioFile = new File( file.getPath() );
                    try
                    {
                        vcs.getBzrClient().add( new File[]{ioFile}, IAddOptions.NO_RECURSE );
                        myFilesToRefresh.add( file );
                    }
                    catch ( BazaarException e )
                    {
                        //noinspection ThrowableInstanceNeverThrown
                        exceptions.add( new VcsException( e ) );
                    }
                }
                if ( !exceptions.isEmpty() )
                {
                    vcsHelper.showErrors( exceptions,
                            BzrBundle.message( "add.files.errors.title" ) );
                }
            }
        }
    }

    private void processMovedFiles( final Project project )
    {
        List<VcsException> exceptionList = new ArrayList<VcsException>();
        BzrVcs vcs = BzrVcs.getInstance( project );
        for ( Iterator<MovedFileInfo> iterator = myMovedFiles.iterator(); iterator.hasNext(); )
        {
            try
            {
                MovedFileInfo movedFileInfo = iterator.next();
                iterator.remove();
                if ( movedFileInfo.myProject == project )
                {
                    doMove( vcs, movedFileInfo.mySrc, movedFileInfo.myDst, true );
                }
            }
            catch ( BazaarException e )
            {
                //noinspection ThrowableInstanceNeverThrown
                exceptionList.add( new VcsException( e ) );
            }
        }
        if (!exceptionList.isEmpty()) {
            AbstractVcsHelper.getInstance(project).showErrors(exceptionList,
                    BzrBundle.message("move.files.errors.title"));
        }
    }

    public void undoTransparentActionStarted()
    {
    }

    public void undoTransparentActionFinished()
    {
    }

//    private void refreshFiles( final Project project )
//    {
//        Utils.refreshFiles( myFilesToRefresh, project, false );
//        myFilesToRefresh.clear();
//    }

}
