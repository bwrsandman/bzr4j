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
package org.emergent.bzr4j.intellij.providers;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vcs.VcsBundle;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.merge.MergeData;
import com.intellij.openapi.vcs.merge.MergeProvider;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcsUtil.VcsRunnable;
import com.intellij.vcsUtil.VcsUtil;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.IBazaarClient;
import org.emergent.bzr4j.intellij.BzrVcs;
import org.emergent.bzr4j.intellij.utils.IJUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author lesya
 * @author yole
 */
public class BzrMergeProvider implements MergeProvider
{
    private final Project myProject;

    public BzrMergeProvider( final Project project )
    {
        myProject = project;
    }

    @NotNull
    public MergeData loadRevisions( final VirtualFile file ) throws VcsException
    {
        final MergeData data = new MergeData();
        VcsRunnable runnable = new VcsRunnable()
        {
            public void run() throws VcsException
            {
                BzrVcs vcs = BzrVcs.getInstance( myProject );
//                File theFile = new File( file.getPath() );
                File oldFile = new File( file.getPath() + ".BASE" );
                File newFile = new File( file.getPath() + ".OTHER" );
                File workingFile = new File( file.getPath() + ".THIS" );
//                try
//                {
//                    BazaarRevision bzrRev = vcs.getBzrClient().revno( new BranchLocation( new File( VcsUtil.getVcsRootFor( myProject, file ).getPath() ) ) );
//                    data.LAST_REVISION_NUMBER = new BzrRevisionNumber( bzrRev );
//                }
//                catch ( BazaarException e )
//                {
//                    throw new VcsException( e );
//                }

//        SVNWCClient client;
//        try {
//          client = vcs.createWCClient();
//          SVNInfo info = client.doInfo(new File(file.getPath()), SVNRevision.WORKING);
//          if (info != null) {
//            oldFile = info.getConflictOldFile();
//            newFile = info.getConflictNewFile();
//            workingFile = info.getConflictWrkFile();
//            data.LAST_REVISION_NUMBER = new SvnRevisionNumber(info.getRevision());
//          }
//        }
//        catch (SVNException e) {
//          throw new VcsException(e);
//        }
//        if (oldFile == null || newFile == null || workingFile == null) {
//          ByteArrayOutputStream bos = new ByteArrayOutputStream();
//          try {
////              return new BzrContentRevision( vcs, new File( file.getPath() ), revisionNumber.asString() );
//
////            client.doGetFileContents(new File(file.getPath()), SVNRevision.UNDEFINED, SVNRevision.BASE, true, bos);
//              InputStream stream =
//                      vcs.getBzrClient().cat( file, getRevisionNumber().getBazaarRevision() );
//              data.ORIGINAL = FileUtil.adaptiveLoadBytes( stream );
//              data.LAST = data.ORIGINAL;
//          }
//          catch ( BzrException e) {
//            //
//          }
////          data.ORIGINAL = bos.toByteArray();
////          data.LAST = bos.toByteArray();
//          data.CURRENT = readFile(new File(file.getPath()));
//        }
//        else {
          data.ORIGINAL = readFile(oldFile);
          data.LAST = readFile(newFile);
          data.CURRENT = readFile(workingFile);
//        }
            }
        };
        //noinspection UnresolvedPropertyKey
        VcsUtil.runVcsProcessWithProgress( runnable,
                VcsBundle.message( "multiple.file.merge.loading.progress.title" ), false,
                myProject );

        return data;
    }

    private static byte[] readFile( File workingFile ) throws VcsException
    {
        try
        {
            return FileUtil.loadFileBytes( workingFile );
        }
        catch ( IOException e )
        {
            throw new VcsException( e );
        }
    }

    public void conflictResolvedForFile( VirtualFile file )
    {
        BzrVcs vcs = BzrVcs.getInstance( myProject );
        try
        {
            IBazaarClient client = IJUtil.createBzrClient();
            List<File> arg = Arrays.asList( new File( file.getPath() ) );
            client.resolve( arg );
        }
        catch ( BazaarException e )
        {
//        e.printStackTrace();
        }
        // the .mine/.r## files have been deleted
        final VirtualFile parent = file.getParent();
        if ( parent != null )
        {
            parent.refresh( true, false );
        }
    }

    public boolean isBinary( final VirtualFile file )
    {
//    BzrVcs vcs = BzrVcs.getInstance(myProject);
        try
        {
//      SVNWCClient client = vcs.createWCClient();
//      File ioFile = new File(file.getPath());
//      SVNPropertyData svnPropertyData = client.doGetProperty(ioFile, SVNProperty.MIME_TYPE, SVNRevision.UNDEFINED, SVNRevision.WORKING);
//      if (svnPropertyData != null && SVNProperty.isBinaryMimeType(SVNPropertyValue.getPropertyAsString(svnPropertyData.getValue()))) {
//        return true;
//      }
            byte[] bytes = readFile( new File( file.getPath() ) );
            for ( byte aByte : bytes )
            {
                if ( aByte == 0 )
                    return true;
            }
        }
        catch ( VcsException e )
        {
            //
        }
        return false;
    }
}
