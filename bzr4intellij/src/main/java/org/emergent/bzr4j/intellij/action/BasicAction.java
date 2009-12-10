/**
 * @copyright
 * ====================================================================
 * Copyright (c) 2003-2004 QintSoft.  All rights reserved.
 *
 * This software is licensed as described in the file COPYING, which
 * you should have received as part of this distribution.  The terms
 * are also available at http://subversion.tigris.org/license-1.html.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 *
 * This software consists of voluntary contributions made by many
 * individuals.  For exact contribution history, see the revision
 * history and logs, available at http://svnup.tigris.org/.
 * ====================================================================
 * @endcopyright
 */
/*
 * Copyright 2000-2005 JetBrains s.r.o.
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
package org.emergent.bzr4j.intellij.action;

import com.intellij.history.LocalHistory;
import com.intellij.history.LocalHistoryAction;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.AbstractVcsHelper;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.TransactionRunnable;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.VcsDirtyScopeManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.emergent.bzr4j.intellij.BzrVcs;
import org.emergent.bzr4j.utils.LogUtil;

import java.util.Arrays;
import java.util.List;

public abstract class BasicAction extends AnAction
{
    private static final LogUtil sm_logger = LogUtil.getLogger( BasicAction.class );

    protected BasicAction()
    {
    }

//    protected BasicAction( String text )
//    {
//        super( text );
//    }
//
//    protected BasicAction( String text, String description, Icon icon )
//    {
//        super( text, description, icon );
//    }

    public void actionPerformed( AnActionEvent event )
    {
        if ( sm_logger.isDebugEnabled() )
        {
            sm_logger.debug( "enter: actionPerformed(id='" + ActionManager.getInstance().getId( this )
                    + "')" );
        }
        final DataContext dataContext = event.getDataContext();
        final Project project = PlatformDataKeys.PROJECT.getData( dataContext );

        final VirtualFile[] files = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData( dataContext );
        if ( sm_logger.isDebugEnabled() && files != null )
        {
            sm_logger.debug( "files='" + Arrays.asList( files ) + "'" );
        }
        if ( (files == null || files.length == 0) && needsFiles() ) return;

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
    }

    public void update( AnActionEvent e )
    {
        //sm_logger.debug("enter: update class:"+getClass().getName());
        super.update( e );

        Presentation presentation = e.getPresentation();
        final DataContext dataContext = e.getDataContext();

        Project project = PlatformDataKeys.PROJECT.getData( dataContext );
        if ( project == null )
        {
            presentation.setEnabled( false );
            presentation.setVisible( false );
            return;
        }

        if ( !needsFiles() )
        {
            presentation.setEnabled( true );
            presentation.setVisible( true );
            return;
        }

        VirtualFile[] files = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData( dataContext );
        if ( files == null || files.length == 0 )
        {
            presentation.setEnabled( false );
            presentation.setVisible( true );
            return;
        }

        BzrVcs vcs = BzrVcs.getInstance( project );
        if ( !ProjectLevelVcsManager.getInstance( project ).checkAllFilesAreUnder( vcs, files ) )
        {
            presentation.setEnabled( false );
            presentation.setVisible( true );
            return;
        }

        boolean enabled = true;
        if ( !needsAllFiles() )
        {
            enabled = false;
        }

        sm_logger.debug( getClass().getName() + (enabled ? " needsAllFiles" : " needsSingleFile") );
        for ( int i = 0; i < files.length; i++ )
        {
            VirtualFile file = files[i];
            boolean fileEnabled = false;
            try
            {
                fileEnabled = isEnabled( project, vcs, file );
            }
            catch ( Throwable t )
            {
                sm_logger.debug( t );

            }
            sm_logger.debug(
                    "file:" + file.getPath() + (fileEnabled ? " is enabled" : " is not enabled") );
            if ( needsAllFiles() )
            {
                if ( !fileEnabled )
                {
                    sm_logger.debug( "now disabled" );
                    enabled = false;
                    break;
                }
            }
            else
            {
                if ( fileEnabled )
                {
                    sm_logger.debug( "now enabled" );
                    enabled = true;
                }
            }
        }

        presentation.setEnabled( enabled );
        presentation.setVisible( true );
    }

    protected boolean needsAllFiles()
    {
        return needsFiles();
    }

    protected void execute( Project project,
            final BzrVcs activeVcs,
            final VirtualFile file,
            DataContext context,
            AbstractVcsHelper helper ) throws VcsException
    {
        if ( file.isDirectory() )
        {
            perform( project, activeVcs, file, context );
            writeToCache( project, new VirtualFile[] {file} );
        }
        else
        {
            perform( project, activeVcs, file, context );
            writeToCache( project, new VirtualFile[] {file} );
        }
    }

    protected void doVcsRefresh( final Project project, final VirtualFile file )
    {
        VcsDirtyScopeManager.getInstance( project ).fileDirty( file );
    }

    private void batchExecute( Project project,
            final BzrVcs activeVcs,
            final VirtualFile[] file,
            DataContext context,
            AbstractVcsHelper helper ) throws VcsException
    {
        batchPerform( project, activeVcs, file, context );
        writeToCache( project, file );
    }

    protected void writeToCache( final Project project, final VirtualFile[] files )
    {
        ApplicationManager.getApplication().runWriteAction( new Runnable()
        {
            public void run()
            {
                for ( int i = 0; files != null && i < files.length; i++ )
                {
                    files[i].refresh( false, true );
                }
            }
        } );

        for ( int i = 0; files != null && i < files.length; i++ )
        {
            doVcsRefresh( project, files[i] );
        }
    }

    protected abstract String getActionName( AbstractVcs vcs );

    protected abstract boolean isEnabled( Project project, BzrVcs vcs, VirtualFile file );

    protected abstract boolean needsFiles();

    protected abstract void perform( Project project, final BzrVcs activeVcs, VirtualFile file,
            DataContext context ) throws VcsException;

    protected abstract void batchPerform( Project project, final BzrVcs activeVcs,
            VirtualFile[] file, DataContext context ) throws VcsException;

    protected abstract boolean isBatchAction();
}
