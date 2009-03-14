package org.emergent.bzr4j.intellij;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.IBazaarLogMessage;
import org.emergent.bzr4j.intellij.utils.IJUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class BzrFileRevision implements VcsFileRevision
{

    private BzrVcs m_bzr;

    private File m_file;

    private IBazaarLogMessage m_message;

    public BzrFileRevision( BzrVcs bzr, File file, IBazaarLogMessage message )
    {
        m_bzr = bzr;
        m_file = file;
        m_message = message;
    }

    public BzrRevisionNumber getRevisionNumber()
    {
        return new BzrRevisionNumber( m_message.getRevision() );
    }

    public String getBranchName()
    {
        return m_message.getBranchNick();
    }

    public Date getRevisionDate()
    {
        return m_message.getDate();
    }

    public String getAuthor()
    {
        return m_message.getCommiter();
    }

    public String getCommitMessage()
    {
        return m_message.getMessage();
    }

    public void loadContent() throws VcsException
    {
    }

    public byte[] getContent() throws IOException
    {
        try
        {
            m_bzr.getBzrClient( IJUtil.root( m_file ) );
            InputStream stream = m_bzr.getBzrClient().cat( m_file, m_message.getRevision() );
            return FileUtil.adaptiveLoadBytes( stream );

        }
        catch ( BazaarException e )
        {
            throw IJUtil.notYetHandled( e );
        }
    }
}
