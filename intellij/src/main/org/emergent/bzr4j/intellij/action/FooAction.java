package org.emergent.bzr4j.intellij.action;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.diff.DiffProvider;
import com.intellij.openapi.vcs.diff.ItemLatestState;
import com.intellij.openapi.vfs.VirtualFile;
import java.util.logging.Logger;
import org.emergent.bzr4j.intellij.utils.IJConstants;
import org.emergent.bzr4j.utils.LogUtil;

import java.util.Arrays;

/**
 * @author Patrick Woodworth
 */
public class FooAction extends AnAction
{
    private static final LogUtil sm_logger = LogUtil.getLogger( FooAction.class );

    protected String getActionName( AbstractVcs vcs )
    {
        return "Foo";
    }

    public void actionPerformed( AnActionEvent event )
    {
        if ( sm_logger.isDebugEnabled() )
        {
            sm_logger.debug( "enter: actionPerformed(id='" + ActionManager.getInstance().getId( this ) + "')" );
        }
        final DataContext dataContext = event.getDataContext();
        final Project project = PlatformDataKeys.PROJECT.getData( dataContext );

        final VirtualFile[] files = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData( dataContext );
        if ( sm_logger.isDebugEnabled() && files != null )
        {
            sm_logger.debug( "files='" + Arrays.asList( files ) + "'" );
        }
        if ((files == null || files.length == 0)) 
            return;

        ProjectLevelVcsManager vcsMgr = ProjectLevelVcsManager.getInstance( project );

        for ( VirtualFile file : files )
        {
            sm_logger.info( "file: " + file );
            AbstractVcs vcs = vcsMgr.getVcsFor( file );
            sm_logger.info( "\tvcs: " + vcs );
            DiffProvider changeProvider = vcs.getDiffProvider();
            sm_logger.info( "\tcurRev: " + changeProvider.getCurrentRevision( file ) );
            ItemLatestState lastRev =  changeProvider.getLastRevision( file );
            sm_logger.info( "\tlastRev: " + lastRev.getNumber() );
        }

/*
        final BzrVcs vcs = BzrVcs.getInstance( project );
        if ( !ProjectLevelVcsManager.getInstance( project ).checkAllFilesAreUnder( vcs, files ) )
        {
            return;
        }

        if ( project != null )
        {
            project.save();
        }

        final String actionName = getActionName( vcs );

        final AbstractVcsHelper helper = AbstractVcsHelper.getInstance( project );
        LocalHistoryAction action = LocalHistoryAction.NULL;
        if ( actionName != null )
        {
            action = LocalHistory.startAction( project, actionName );
        }

        try
        {
            List<VcsException> exceptions =
                    helper.runTransactionRunnable( vcs, new TransactionRunnable()
                    {
                        public void run( List<VcsException> exceptions )
                        {
                            VirtualFile badFile = null;
                            try
                            {
                                if ( isBatchAction() )
                                {
                                    batchExecute( project, vcs, files, dataContext, helper );
                                }
                                else
                                {
                                    for ( int i = 0; files != null && i < files.length; i++ )
                                    {
                                        VirtualFile file = files[i];
                                        badFile = file;
                                        execute( project, vcs, file, dataContext, helper );
                                    }
                                }
                            }
                            catch ( VcsException ex )
                            {
                                ex.setVirtualFile( badFile );
                                exceptions.add( ex );
                            }
                        }
                    }, null );

            helper.showErrors( exceptions, actionName != null ? actionName : vcs.getName() );
        }
        finally
        {
            action.finish();
        }
*/
    }

    public void update( AnActionEvent e )
    {
        //sm_logger.debug("enter: update class:"+getClass().getName());
        super.update( e );

        Presentation presentation = e.getPresentation();
        final DataContext dataContext = e.getDataContext();

        if (!IJConstants.FOO_ACTION_ENABLED )
        {
            presentation.setEnabled( false );
            presentation.setVisible( false );
            return;
        }

        Project project = PlatformDataKeys.PROJECT.getData( dataContext );
        if ( project == null )
        {
            presentation.setEnabled( false );
            presentation.setVisible( false );
            return;
        }

        VirtualFile[] files = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData( dataContext );
        if ( files == null || files.length == 0 )
        {
            presentation.setEnabled( false );
            presentation.setVisible( true );
            return;
        }

        presentation.setEnabled( true );
        presentation.setVisible( true );
    }
}
