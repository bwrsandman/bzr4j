package org.emergent.bzr4j.intellij;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.BazaarRevision;
import org.emergent.bzr4j.intellij.utils.IJUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Patrick Woodworth
 */
public class BzrContentRevision implements ContentRevision
{
    private BzrVcs m_bzrvcs;

    private FilePath m_file;

    private BzrRevisionNumber m_revision;

    private String content;


    public BzrContentRevision( BzrVcs bzr, FilePath file, BazaarRevision revision )
    {
        this( bzr, file, new BzrRevisionNumber(  revision ) );
    }

    public BzrContentRevision( BzrVcs bzr, FilePath file, BzrRevisionNumber revision )
    {
        m_bzrvcs = bzr;
        m_file = file;
        m_revision = revision;
    }

    @Nullable
    public String getContent() throws VcsException
    {
        if ( content != null )
        {
            return content;
        }

        try
        {
            File ioFile = new File( m_file.getPath() );
            m_bzrvcs.getBzrClient().setWorkDir( IJUtil.root( ioFile ) );
            InputStream stream = m_bzrvcs.getBzrClient().cat( ioFile, m_revision.getBazaarRevision() );
            content = new String( FileUtil.adaptiveLoadBytes( stream ) );
            return content;
        }
        catch ( BazaarException e )
        {
            throw IJUtil.notYetHandled( e );
        }
        catch ( IOException e )
        {
            throw IJUtil.notYetHandled( e );
        }

    }

    @NotNull
    public FilePath getFile()
    {
        return m_file;
    }

    @NotNull
    public VcsRevisionNumber getRevisionNumber()
    {
        return m_revision;
    }
}
