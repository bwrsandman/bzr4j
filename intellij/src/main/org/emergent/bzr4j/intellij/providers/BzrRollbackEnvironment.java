package org.emergent.bzr4j.intellij.providers;

import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.rollback.DefaultRollbackEnvironment;
import com.intellij.openapi.vcs.rollback.RollbackProgressListener;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.commandline.syntax.IRevertOptions;
import org.emergent.bzr4j.intellij.BzrVcs;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

public class BzrRollbackEnvironment extends DefaultRollbackEnvironment
{

    private BzrVcs m_vcs;

    public BzrRollbackEnvironment( BzrVcs mvcs )
    {
        m_vcs = mvcs;
    }

    /**
     * For the 8.0 API
     */
    public List<VcsException> rollbackChanges( List<Change> changes )
    {
        List<VcsException> exceptions = new LinkedList<VcsException>();
        rollbackChanges( changes, exceptions, null );
        return exceptions;
    }

    /**
     * For the 8.0 API
     */
    public List<VcsException> rollbackMissingFileDeletion( List<FilePath> files )
    {
        List<VcsException> exceptions = new LinkedList<VcsException>();
        rollbackMissingFileDeletion( files, exceptions, null );
        return exceptions;
    }

    /**
     * For the 8.1 API
     */
    public void rollbackChanges( List<Change> changes, List<VcsException> vcsExceptions,
            @NotNull RollbackProgressListener listener )
    {
        try
        {
            List<File> files = new ArrayList<File>();
            for ( Change change : changes )
            {
                ContentRevision rev = (change.getAfterRevision() != null)
                        ? change.getAfterRevision()
                        : change.getBeforeRevision();
                String path = rev.getFile().getPath();
                files.add( new File( path ) );
            }
            m_vcs.getBzrClient().revert( files.toArray( new File[files.size()] ), IRevertOptions.NO_BACKUP );
        }
        catch ( BazaarException e )
        {
            //noinspection ThrowableInstanceNeverThrown
            vcsExceptions.add( new VcsException( e ) );
        }
    }

    /**
     * For the 8.1 API
     */
    public void rollbackMissingFileDeletion( List<FilePath> files, List<VcsException> exceptions,
            RollbackProgressListener listener )
    {
        throw new UnsupportedOperationException( "rollbackMissingFileDeletion: " + files );
    }
}
