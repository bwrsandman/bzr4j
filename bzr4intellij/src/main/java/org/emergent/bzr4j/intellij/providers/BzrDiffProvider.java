// Copyright 2008 Victor Iacoban
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software distributed under
// the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific language governing permissions and
// limitations under the License.
package org.emergent.bzr4j.intellij.providers;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.diff.DiffProvider;
import com.intellij.openapi.vcs.diff.ItemLatestState;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcsUtil.VcsUtil;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.BazaarRevision;
import org.emergent.bzr4j.intellij.BzrContentRevision;
import org.emergent.bzr4j.intellij.BzrRevisionNumber;
import org.emergent.bzr4j.intellij.BzrVcs;
import org.emergent.bzr4j.intellij.utils.IJUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BzrDiffProvider implements DiffProvider
{
    private static final Set<FileStatus> ourGoodStatuses = new HashSet<FileStatus>();

    static {
      ourGoodStatuses.addAll( Arrays.asList(
                FileStatus.NOT_CHANGED,
                FileStatus.DELETED,
                FileStatus.MODIFIED,
                FileStatus.MERGE,
                FileStatus.MERGED_WITH_CONFLICTS
        ));
    }

    /**
     * The context project
     */
    private final Project myProject;

    /**
     * The status manager for the project
     */
    private final FileStatusManager myStatusManager;

    public BzrDiffProvider( @NotNull Project project )
    {
        myProject = project;
        myStatusManager = FileStatusManager.getInstance(myProject);
    }

    public VcsRevisionNumber getCurrentRevision( VirtualFile file )
    {
        try
        {
            VirtualFile root = VcsUtil.getVcsRootFor( myProject, file );
            BazaarRevision bzrRev = IJUtil.createBzrClient().revno( IJUtil.toBranchLocation( root ) );
            return new BzrRevisionNumber( bzrRev );
        }
        catch ( BazaarException e )
        {
            throw IJUtil.notYetHandled( e );
        }
    }

    public ItemLatestState getLastRevision( VirtualFile file )
    {
        return new ItemLatestState( getCurrentRevision( file ), file.exists(), false );
    }

    public ContentRevision createFileContent( VcsRevisionNumber revNo, VirtualFile file )
    {
        if ( file == null )
        {
            return null;
        }
        return new BzrContentRevision( BzrVcs.getInstance( myProject ), IJUtil.toFilePath( file ), (BzrRevisionNumber)revNo );
    }

    public ItemLatestState getLastRevision( FilePath filePath )
    {
//        if (filePath.isDirectory()) {
//          return null;
//        }
//        final VirtualFile vf = filePath.getVirtualFile();
//        if (vf != null) {
//          if (!ourGoodStatuses.contains(myStatusManager.getStatus(vf))) {
//            return null;
//          }
//        }
//        try {
//          return GitHistoryUtils.getLastRevision(myProject, filePath);
//        }
//        catch (VcsException e) {
//          return null;
//        }
        return null;
    }

    public VcsRevisionNumber getLatestCommittedRevision( VirtualFile vcsRoot )
    {
        return null; // todo implement
    }
}
