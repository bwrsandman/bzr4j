package org.emergent.bzr4j.intellij.action;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.AbstractVcsHelper;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.ReadonlyStatusHandler;
import com.intellij.openapi.vfs.VirtualFile;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.intellij.BzrVcs;
import org.emergent.bzr4j.intellij.utils.IJUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Action for updating the whole project under VCS
 *
 * @author Dao Duc Duy
 */
public class UpdateAction extends BasicAction
{
    protected String getActionName( AbstractVcs vcs )
    {
        return "Update code";
    }

    protected boolean isEnabled( Project project, BzrVcs vcs, VirtualFile file )
    {
        return true;
    }

    protected boolean needsFiles()
    {
        return false;
    }

    protected void batchPerform( final Project project, BzrVcs activeVcs, VirtualFile[] files,
            DataContext context )
            throws VcsException
    {
//        Collection<VcsException> cex = new LinkedList<VcsException>();
//        try
//        {
            doPerform( project, activeVcs );
//        }
//        catch ( VcsException e )
//        {
//            cex.add( e );
//        }
//        if ( !cex.isEmpty() )
//            throw new CompositeVcsException( cex );
    }

    protected boolean isBatchAction()
    {
        return true;
    }

    protected void perform( Project project, BzrVcs activeVcs, VirtualFile file,
            DataContext context )
            throws VcsException
    {

    }

    private void doPerform( final Project project, final BzrVcs activeVcs )
            throws VcsException
    {
        final VirtualFile rootPath = project.getBaseDir();
        try
        {
            IJUtil.createBzrClient().update( new File( rootPath.getPath() ) );
        }
        catch( BazaarException ex )
        {
            // Conflict found
            // Update cache before going to merging
            writeToCache( project, new VirtualFile[] {rootPath} );
            final List<VirtualFile> fileList = getConflictsFromException( rootPath, project, ex );

            AbstractVcsHelper.getInstance( project )
                    .showMergeDialog( fileList, BzrVcs.getInstance( project ).getMergeProvider() );
        }
        finally
        {
            rootPath.refresh( false, true );
        }

    }

    private void addIfWritable( final VirtualFile fileOrDir, final Project project,
            final List<VirtualFile> fileList )
    {
        final ReadonlyStatusHandler.OperationStatus operationStatus =
                ReadonlyStatusHandler.getInstance( project ).ensureFilesWritable( fileOrDir );
        if ( !operationStatus.hasReadonlyFiles() )
        {
            fileList.add( fileOrDir );
        }
    }

    private List<VirtualFile> getConflictsFromException( VirtualFile rootPath, Project project, BazaarException ex )
    {
        List<VirtualFile> fileList = new ArrayList<VirtualFile>();
        String detailMessage = ex.getMessage();
        String header = "Text conflict in ";
        VirtualFile file;

        for ( String s : detailMessage.split( "\n" ) )
        {
            if ( s.contains( header ) )
            {
                file = LocalFileSystem.getInstance().findFileByPath(
                        rootPath.getPath() + "/" + s.replaceAll( header, "" ).replaceAll( "\r", "" ));
                addIfWritable( file, project, fileList );
            }
        }

        return fileList;
    }
}
