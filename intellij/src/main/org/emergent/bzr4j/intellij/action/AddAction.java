package org.emergent.bzr4j.intellij.action;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.AbstractVcsHelper;
import com.intellij.openapi.vcs.CompositeVcsException;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.VcsShowConfirmationOption;
import com.intellij.openapi.vcs.changes.VcsDirtyScopeManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.intellij.BzrBundle;
import org.emergent.bzr4j.intellij.BzrVcs;
import org.emergent.bzr4j.utils.BazaarIOException;
import org.emergent.bzr4j.utils.BzrUtil;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Action for adding new files under VCS
 *
 * @author Eugeny Schava
 */
public class AddAction extends BasicAction
{
    protected String getActionName( AbstractVcs vcs )
    {
        return BzrBundle.message( "action.name.add.files", vcs.getName() );
    }

    protected boolean isEnabled( Project project, BzrVcs vcs, VirtualFile file )
    {
//      return SvnStatusUtil.fileCanBeAdded(project, file);
        return true;
    }

    protected boolean needsFiles()
    {
        return true;
    }

    protected void batchPerform( final Project project, BzrVcs activeVcs, VirtualFile[] files,
            DataContext context )
            throws VcsException
    {
        Collection<VcsException> cex = new LinkedList<VcsException>();
        for ( VirtualFile vFile : files )
        {
            try
            {
                doPerform( project, activeVcs, vFile, context );
            }
            catch ( VcsException e )
            {
                cex.add( e );
            }
        }
        if ( !cex.isEmpty() )
            throw new CompositeVcsException( cex );
    }

    protected boolean isBatchAction()
    {
        return true;
    }

    protected void perform( Project project, BzrVcs activeVcs, VirtualFile file,
            DataContext context )
            throws VcsException
    {
        batchPerform( project, activeVcs, new VirtualFile[] { file }, context );
    }

    private void doPerform( Project project, BzrVcs activeVcs, VirtualFile file,
            DataContext context )
            throws VcsException
    {
        try
        {
            addFileUnderVcsConfirm( activeVcs, new File( file.getPath() ) );
            VcsDirtyScopeManager.getInstance( project ).fileDirty( file );
        }
        catch ( IOException e )
        {
            VcsException ve = new VcsException( e );
            ve.setVirtualFile( file );
            throw ve;
        }
    }

    private static void addFileUnderVcsConfirm( BzrVcs bzr, File dstFile ) throws IOException
    {
        VcsShowConfirmationOption.Value confirmationValue = bzr.getAddConfirmation().getValue();

        if ( confirmationValue != VcsShowConfirmationOption.Value.DO_NOTHING_SILENTLY )
        {
            List<VirtualFile> addedVFiles = new LinkedList<VirtualFile>();
            addedVFiles.add( LocalFileSystem.getInstance().refreshAndFindFileByIoFile( dstFile ) );

            Collection<VirtualFile> filesToProcess;

            if ( confirmationValue == VcsShowConfirmationOption.Value.SHOW_CONFIRMATION )
            {
                final AbstractVcsHelper vcsHelper =
                        AbstractVcsHelper.getInstance( bzr.getProject() );

                filesToProcess = vcsHelper.selectFilesToProcess( addedVFiles,
                        "Add files",
                        null,
                        "Add file",
                        "Add file {0} to repository?",
                        bzr.getAddConfirmation() );
            }
            else
            {
                filesToProcess = addedVFiles;
            }

            if ( filesToProcess != null )
            {
                for ( VirtualFile fileToAdd : filesToProcess )
                {
                    File f = new File( fileToAdd.getPath() );
                    bzr.getBzrClient().setWorkDir(
                            BzrUtil.getRootBranch( f ) );
                    try
                    {
                        bzr.getBzrClient().add( new File[]{f} );
                    }
                    catch ( BazaarException e )
                    {
                        throw new BazaarIOException( e );
                    }
                }
            }
        }
    }
}
