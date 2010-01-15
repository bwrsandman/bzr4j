package org.emergent.bzr4j.commandline.internal;

import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.debug.DebugLogRecord;
import org.emergent.bzr4j.debug.LogUtil;
import org.emergent.bzr4j.utils.BzrConstants;
import org.emergent.bzr4j.utils.BzrCoreUtil;
import org.emergent.bzr4j.utils.StreamRelay;
import org.emergent.bzr4j.utils.StringUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;

public final class CommandExecutor
{
    public CommandExecutor()
    {
    }

    private List<String> quoteArgs( List<String> cmdList, boolean inline )
    {
        if (!inline)
            cmdList = new ArrayList<String>( cmdList );

        for ( ListIterator<String> iter = cmdList.listIterator(); iter.hasNext(); )
        {
            iter.set( StringUtil.maybeQuote( iter.next() ) );
        }
        return cmdList;
    }

    private void shellTransform( ProcessBuilder pb )
    {
        quoteArgs( pb.command(), true );
        String concated = StringUtil.concat( pb.command().toArray(), " " );
        String[] newCmdArray = null;
        if ( BzrCoreUtil.isWindows() )
        {
            newCmdArray = new String[] { "cmd.exe", "/s", "/c", "\"" + concated + "\"" };
        }
        else
        {
            newCmdArray = new String[] { "/bin/sh", "-c", concated };
        }
        pb.command( newCmdArray );
    }

    public void execAndWait( ExecResult retval, ProcessBuilder pb ) throws BazaarException
    {
        execAndWait( retval, pb, BzrConstants.EXEC_WITH_SHELL );
    }

    public void execAndWait( ExecResult retval, ProcessBuilder pb, boolean useShell ) throws BazaarException
    {
        if (useShell)
        {
            shellTransform( pb );
        }

        Process proc = null;

        BufferedInputStream outrdr = null;
        BufferedInputStream errrdr = null;
        boolean success = false;
        logArguments( retval.getExecId(), pb );
        long sTime = System.currentTimeMillis();
        try
        {
            proc = pb.start();

            outrdr = new BufferedInputStream( proc.getInputStream() );
            errrdr = new BufferedInputStream( proc.getErrorStream() );

            long m_execId = retval.getExecId();

            StreamRelay stdoutrun = new StreamRelay( "out_redirect(" + m_execId + ")", outrdr,
                    retval.getOutBaos() );
            StreamRelay stderrrun = new StreamRelay( "err_redirect(" + m_execId + ")", errrdr,
                    retval.getErrBaos() );
            stdoutrun.start();
            stderrrun.start();
            try
            {
                proc.getOutputStream().close();
            }
            catch (Exception ex)
            {
                // noop
            }
            retval.setExitCode( proc.waitFor() );
            stdoutrun.join();
            stderrrun.join();
            Thread.yield();
            success = true;
        }
        catch (Throwable e)
        {
            throw new BazaarException( retval, e );
        }
        finally
        {
            logFinished( retval.getExecId(), System.currentTimeMillis() - sTime, success );
            if (proc != null) try {  proc.destroy(); } catch (Exception ex) { }
            if (outrdr != null) try { outrdr.close(); } catch (Exception e) { }
            if (errrdr != null) try { errrdr.close(); } catch (Exception e) { }
        }
    }

    private static void logArguments( final long execid, final ProcessBuilder pb )
    {
        LogUtil.LOG.log( new DebugLogRecord( Level.FINE, null ) {

            @Override
            public String getLoggerName()
            {
                return LogUtil.LOG.getName();
            }

            @Override
            public String getSourceClassName()
            {
                return "exec";
            }

            @Override
            public String getSourceMethodName()
            {
                return "" + execid;
            }

            public String getMessage()
            {
                String[] cmdStr = pb.command().toArray( new String[pb.command().size()] );
                File cmdDir = pb.directory();

                if (!BzrConstants.EXEC_LOG_MULTILINE_ARGS )
                {
                    return String.format( "[%s] : '%s'", cmdDir, StringUtil.concat( cmdStr, "' '" ) );
                }
                else
                {
                    return String.format( "[%s] : \n'%s'", cmdDir, StringUtil.concat( cmdStr, "'\n'" ) );
                }
            }
        } );
    }

    private static void logFinished( final long execid, final long duration, final boolean success )
    {
        LogUtil.LOG.log( new DebugLogRecord( Level.FINER, null ) {

            @Override
            public String getLoggerName()
            {
                return LogUtil.LOG.getName();
            }

            @Override
            public String getSourceClassName()
            {
                return "exec";
            }

            @Override
            public String getSourceMethodName()
            {
                return "" + execid;
            }

            public String getMessage()
            {
                return String.format( "finished in %.1f seconds with %s.", duration / 1000.0, success ? "success" : "error" );
            }
        } );
    }
}
