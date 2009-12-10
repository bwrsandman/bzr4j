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

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.AbstractVcsHelper;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.ReadonlyStatusHandler;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Processor;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.BazaarStatusKind;
import org.emergent.bzr4j.core.BazaarTreeStatus;
import org.emergent.bzr4j.core.IBazaarStatus;
import org.emergent.bzr4j.intellij.BzrBundle;
import org.emergent.bzr4j.intellij.BzrVcs;
import org.emergent.bzr4j.intellij.utils.IJUtil;
import org.emergent.bzr4j.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ResolveAction extends BasicAction
{
    private static final LogUtil LOG = LogUtil.getLogger( ResolveAction.class );

    protected String getActionName( AbstractVcs vcs )
    {
        return BzrBundle.message( "action.name.resolve.conflict" );
    }

    protected boolean needsAllFiles()
    {
        return true;
    }

    protected boolean isEnabled( Project project, BzrVcs vcs, VirtualFile file )
    {
        if ( file.isDirectory() ) return true;
        try
        {
            BazaarTreeStatus status = IJUtil.createBzrClient().status( new File[] { new File( file.getPath() ) } );
            IBazaarStatus[] stat = status.getStatusAsArray();
            if (stat != null && stat.length > 0)
            {
                return stat[0].contains( BazaarStatusKind.HAS_CONFLICTS );
            }
        }
        catch ( BazaarException e )
        {
            LOG.error( "diffing", e );
        }
//    SVNStatus status;
//    try {
//      SvnVcs.SVNStatusHolder statusValue = vcs.getCachedStatus(file);
//      if (statusValue != null) {
//        status = statusValue.getStatus();
//      } else {
//        SVNStatusClient stClient = vcs.createStatusClient();
//        status = stClient.doStatus(new File(file.getPath()), false);
//      }
//      if (status != null && status.getContentsStatus() == SVNStatusType.STATUS_CONFLICTED) {
//        SVNInfo info;
//        SvnVcs.SVNInfoHolder infoValue = vcs.getCachedInfo(file);
//        if (infoValue != null) {
//          info = infoValue.getInfo();
//        } else {
//          SVNWCClient wcClient = vcs.createWCClient();
//          info = wcClient.doInfo(new File(file.getPath()), SVNRevision.WORKING);
//          vcs.cacheInfo(file, info);
//        }
//        return info != null && info.getConflictNewFile() != null &&
//               info.getConflictOldFile() != null &&
//               info.getConflictWrkFile() != null;
//      }
//    }
//    catch (SVNException e) {

//    }
        return false;
    }

    protected boolean needsFiles()
    {
        return true;
    }

    protected void perform( Project project, BzrVcs activeVcs, VirtualFile file, DataContext context )
            throws VcsException
    {
        batchPerform( project, activeVcs, new VirtualFile[]{file}, context );
    }

    protected void batchPerform( final Project project, final BzrVcs activeVcs,
            final VirtualFile[] files, DataContext context ) throws VcsException
    {
        boolean hasDirs = false;
        for ( VirtualFile file : files )
        {
            if ( file.isDirectory() )
            {
                hasDirs = true;
            }
        }
        final List<VirtualFile> fileList = new ArrayList<VirtualFile>();
        if ( !hasDirs )
        {
            for ( VirtualFile file : files )
            {
                addIfWritable( file, project, fileList );
            }
        }
        else
        {
            ProgressManager.getInstance().runProcessWithProgressSynchronously( new Runnable()
            {
                public void run()
                {
                    for ( VirtualFile file : files )
                    {
                        if ( file.isDirectory() )
                        {
                            ProjectLevelVcsManager.getInstance( project )
                                    .iterateVcsRoot( file, new Processor<FilePath>()
                                    {
                                        public boolean process( final FilePath filePath )
                                        {
                                            ProgressManager.checkCanceled();
                                            VirtualFile fileOrDir = filePath.getVirtualFile();
                                            if ( fileOrDir != null && !fileOrDir.isDirectory()
                                                    && isEnabled( project, activeVcs, fileOrDir )
                                                    && !fileList.contains( fileOrDir ) )
                                            {
                                                addIfWritable( fileOrDir, project, fileList );
                                            }
                                            return true;
                                        }
                                    } );
                        }
                        else
                        {
                            if ( !fileList.contains( file ) )
                            {
                                fileList.add( file );
                            }
                        }
                    }
                }
            }, BzrBundle.message( "progress.searching.for.files.with.conflicts" ), true, project );
        }
        AbstractVcsHelper.getInstance( project ).showMergeDialog(
                fileList, BzrVcs.getInstance( project ).getMergeProvider() );
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

    protected boolean isBatchAction()
    {
        return true;
    }
}
