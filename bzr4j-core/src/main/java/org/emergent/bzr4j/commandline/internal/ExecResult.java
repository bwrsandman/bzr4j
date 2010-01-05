package org.emergent.bzr4j.commandline.internal;

import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.debug.LogUtil;
import org.emergent.bzr4j.utils.StringUtil;

import java.io.ByteArrayOutputStream;

/**
 * @author Patrick Woodworth
 */
public final class ExecResult
{
    private static final LogUtil sm_logger = LogUtil.getLogger( ExecResult.class );

    private long m_execId = System.currentTimeMillis();

    private int m_exitCode = -1;

    private ByteArrayOutputStream m_outBaos = new ByteArrayOutputStream();

    private ByteArrayOutputStream m_errBaos = new ByteArrayOutputStream();

    private byte[] m_byteOutCache;

    private byte[] m_byteErrCache;
    
    private static final String OK_STDERR_MSG = "No handlers could be found for logger \"bzr\"";

    public ExecResult()
    {
    }

    public String toString()
    {
        final StringBuffer buf = new StringBuffer();
        buf.append( "ErrCode=" );
        buf.append( getExitCode() );
//        buf.append( "WorkDir=" );
//        buf.append( m_directory );
//        buf.append( "\nArgs---------------\n:\n'" );
//        buf.append( StringUtil.concat( m_cmd, "'\n'" ) );
        buf.append( "'\nStdOut:------------\n" );
        buf.append( getStdout() );
        buf.append( "\nStdErr:------------\n" );
        buf.append( getStderr() );
        buf.append( "\nEnd----------------\n" );
        return buf.toString();
    }

    public int getExitCode()
    {
        return m_exitCode;
    }

    public byte[] getByteOut()
    {
        if (m_byteOutCache == null)
        {
            m_byteOutCache = m_outBaos.toByteArray();
        }
        return m_byteOutCache;
    }

    private byte[] getByteErr()
    {
        if (m_byteErrCache == null)
        {
            m_byteErrCache = m_errBaos.toByteArray();
        }
        return m_byteErrCache;
    }

    public String getStdout()
    {
        return new String( getByteOut() );
    }

    public String getStderr()
    {
        return new String( getByteErr() );
    }

    public long getExecId()
    {
        return m_execId;
    }

    public void validateNonZeroExitCode() throws BazaarException
    {
        if (m_exitCode != 0)
        {
            throw new BazaarException( this );
        }
    }

    public void validateEmptyStdErr() throws BazaarException
    {
        if (getByteErr().length > 0)
        {
            String stderr = getStderr();
            if (!StringUtil.isEmpty( stderr ) && !stderr.trim().equals( OK_STDERR_MSG ))
            {
                throw new BazaarException( this );
            }
            else
            {
                sm_logger.warn( this );
            }
        }
    }

    public ByteArrayOutputStream getOutBaos()
    {
        return m_outBaos;
    }

    public ByteArrayOutputStream getErrBaos()
    {
        return m_errBaos;
    }

    public void setExitCode( int exitCode )
    {
        m_exitCode = exitCode;
    }
}
