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

public class BzrDiffProvider implements DiffProvider
{
    private BzrVcs m_vcs;

    public BzrDiffProvider( BzrVcs vcs )
    {
        m_vcs = vcs;
    }

    public VcsRevisionNumber getCurrentRevision( VirtualFile file )
    {
        try
        {
            VirtualFile root = VcsUtil.getVcsRootFor( m_vcs.getProject(), file );
            BazaarRevision bzrRev = m_vcs.getBzrClient().revno( IJUtil.toBranchLocation( root ) );
            return new BzrRevisionNumber( bzrRev );
        }
        catch ( BazaarException e )
        {
            throw IJUtil.notYetHandled( e );
        }
    }

    public ItemLatestState getLastRevision( VirtualFile file )
    {
        return new ItemLatestState( getCurrentRevision( file ), file.exists() );
    }

    public ContentRevision createFileContent( VcsRevisionNumber revNo, VirtualFile file )
    {
        if ( file == null )
        {
            return null;
        }
        return new BzrContentRevision( m_vcs, IJUtil.toFilePath( file ), (BzrRevisionNumber)revNo );
    }
}
