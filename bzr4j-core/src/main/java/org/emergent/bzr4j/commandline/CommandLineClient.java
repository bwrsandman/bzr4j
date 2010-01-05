/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline;

import org.emergent.bzr4j.commandline.commands.Add;
import org.emergent.bzr4j.commandline.commands.Annotate;
import org.emergent.bzr4j.commandline.commands.Bind;
import org.emergent.bzr4j.commandline.commands.Branch;
import org.emergent.bzr4j.commandline.commands.Bugs;
import org.emergent.bzr4j.commandline.commands.Cat;
import org.emergent.bzr4j.commandline.commands.CheckOut;
import org.emergent.bzr4j.commandline.commands.Commit;
import org.emergent.bzr4j.commandline.commands.Diff;
import org.emergent.bzr4j.commandline.commands.FindMergeBase;
import org.emergent.bzr4j.commandline.commands.Ignore;
import org.emergent.bzr4j.commandline.commands.Ignored;
import org.emergent.bzr4j.commandline.commands.Info;
import org.emergent.bzr4j.commandline.commands.Init;
import org.emergent.bzr4j.commandline.commands.Log;
import org.emergent.bzr4j.commandline.commands.Ls;
import org.emergent.bzr4j.commandline.commands.Merge;
import org.emergent.bzr4j.commandline.commands.Missing;
import org.emergent.bzr4j.commandline.commands.Move;
import org.emergent.bzr4j.commandline.commands.Nick;
import org.emergent.bzr4j.commandline.commands.Pull;
import org.emergent.bzr4j.commandline.commands.Push;
import org.emergent.bzr4j.commandline.commands.Remove;
import org.emergent.bzr4j.commandline.commands.Resolve;
import org.emergent.bzr4j.commandline.commands.Revert;
import org.emergent.bzr4j.commandline.commands.RevisionInfo;
import org.emergent.bzr4j.commandline.commands.Revno;
import org.emergent.bzr4j.commandline.commands.Send;
import org.emergent.bzr4j.commandline.commands.Status;
import org.emergent.bzr4j.commandline.commands.Switch;
import org.emergent.bzr4j.commandline.commands.UnBind;
import org.emergent.bzr4j.commandline.commands.UnCommit;
import org.emergent.bzr4j.commandline.commands.Unknowns;
import org.emergent.bzr4j.commandline.commands.Update;
import org.emergent.bzr4j.commandline.commands.VersionInfo;
import org.emergent.bzr4j.commandline.commands.options.KeywordOption;
import org.emergent.bzr4j.commandline.commands.options.Option;
import org.emergent.bzr4j.commandline.internal.Command;
import org.emergent.bzr4j.commandline.parser.XMLInfoParser;
import org.emergent.bzr4j.commandline.parser.XMLLogParser;
import org.emergent.bzr4j.commandline.parser.XMLLsParser;
import org.emergent.bzr4j.commandline.parser.XMLMissingParser;
import org.emergent.bzr4j.commandline.parser.XMLStatusParser;
import org.emergent.bzr4j.commandline.syntax.ILogOptions;
import org.emergent.bzr4j.commandline.syntax.ILsOptions;
import org.emergent.bzr4j.commandline.syntax.IVersionInfoOptions;
import org.emergent.bzr4j.core.BazaarClient;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.BazaarRevision;
import org.emergent.bzr4j.core.BazaarRevision.Prefix;
import org.emergent.bzr4j.core.BazaarRevisionRange;
import org.emergent.bzr4j.core.BazaarTreeStatus;
import org.emergent.bzr4j.core.BazaarVersionInfo;
import org.emergent.bzr4j.core.BranchLocation;
import org.emergent.bzr4j.core.IBazaarAnnotation;
import org.emergent.bzr4j.core.IBazaarInfo;
import org.emergent.bzr4j.core.IBazaarItemInfo;
import org.emergent.bzr4j.core.IBazaarLogMessage;
import org.emergent.bzr4j.core.IBazaarNotifyListener;
import org.emergent.bzr4j.core.IBazaarPromptUserPassword;
import org.emergent.bzr4j.core.IBazaarRevisionSpec;
import org.emergent.bzr4j.core.IBzrLogMessageHandler;
import org.emergent.bzr4j.debug.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * <p>
 * High level interface to Bazaar Commands
 * </p>
 *
 * @author Guillermo Gonzalez
 * @author Phan Minh Thang
 *
 */
public class CommandLineClient extends BazaarClient
{
    private static final LogUtil sm_logger = LogUtil.getLogger( CommandLineClient.class );

    public CommandLineClient()
    {
        super();
    }

    public void add( final File[] files, final Option... options ) throws BazaarException
    {
        final Add cmd = new Add( m_workDir, files );
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        run( cmd );
    }

    public IBazaarAnnotation annotate( final File file, final Option... options )
            throws BazaarException
    {
        Annotate cmd = new Annotate( m_workDir, file );
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        run( cmd );
        return CommandLineAnnotation.getAnnotationFromXml( cmd.getStandardOutput() );
    }

    public void branch( final BranchLocation fromLocation, final File toLocation,
            final IBazaarRevisionSpec revision, final Option... options )
            throws BazaarException
    {
        final Branch cmd = new Branch( fromLocation, toLocation );
        if ( revision != null )
            cmd.setOption( Branch.REVISION.with( revision.toString() ) );
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        run( cmd );
    }

    /** {@inheritDoc} */
    public List<String> bugs( BranchLocation location, final Option... options ) throws BazaarException
    {
        final Bugs cmd = new Bugs( m_workDir, location );
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        run( cmd );

        if( !"".equals(cmd.getStandardOutput().trim() ) )
        {
            return Arrays.asList( cmd.getStandardOutput().split( "\r\n" ) );
        }

        return new ArrayList<String>();
    }

    public InputStream cat( final File file, final IBazaarRevisionSpec revision,
            final String charsetName, final Option... options ) throws BazaarException
    {
        final Cat cmd = new Cat( m_workDir, file );
        if ( revision == null )
        {
            cmd.setOption( Cat.REVISION.with(
                    BazaarRevision.getRevision( BazaarRevision.Prefix.LAST, "" ).toString() ) );
        }
        else
        {
            cmd.setOption( Cat.REVISION.with( revision.toString() ) );
        }
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        // fallback to shell command execution
        // the xmlrpc can't handle different encodings
        run( cmd );
        return new ByteArrayInputStream( cmd.getExecResult().getByteOut() );
    }

    public void checkout( final BranchLocation fromLocation, final File toLocation,
            final Option... options ) throws BazaarException
    {
        final CheckOut cmd = new CheckOut( fromLocation, toLocation );
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        run( cmd );
    }

    public void commit( final File[] files, String message, final Option... options )
            throws BazaarException
    {
        final Commit cmd = new Commit( m_workDir, files, message );
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        run( cmd );
    }

    public String diff( final File file, final IBazaarRevisionSpec range, final Option... options )
            throws BazaarException
    {
        final Diff cmd = new Diff( m_workDir, file );
        if ( range != null )
        {
            cmd.setOption( Diff.REVISION.with( range.toString() ) );
        }
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        run( cmd );
        return cmd.getStandardOutput();
    }

    public void init( final File location, final Option... options ) throws BazaarException
    {
        final Init cmd = new Init( location );
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        run( cmd );
    }

    public List<IBazaarLogMessage> log( final File location, final Option... options )
            throws BazaarException
    {
        final Log cmd = new Log( m_workDir, location );
        return log( cmd, options );
    }

    public List<IBazaarLogMessage> log( URI location, final Option... options )
            throws BazaarException
    {
        final Log cmd = new Log( m_workDir, location );
        return log( cmd, options );
    }

    public List<IBazaarLogMessage> log( final BranchLocation location, final Option... options )
            throws BazaarException
    {
        final Log cmd = new Log( m_workDir, location );
        return log( cmd, options );
    }

    /**
     * This version of log, is optimized for BIG history fetching, it fetch the history in
     * "groups" of 10000 logs and spawns a thread to parse and call the
     * <code>IBzrLogMessageHandler#handle(IBazaarLogMessage)</code>.
     */
    public void log( final BranchLocation location, final IBzrLogMessageHandler logHandler,
            Option... options ) throws BazaarException
    {
        log( location, logHandler, false, options );
    }

    public void logAsync( final BranchLocation location, final IBzrLogMessageHandler logHandler,
            Option... options ) throws BazaarException
    {
        log( location, logHandler, true, options );
    }

    protected void log( final BranchLocation location, final IBzrLogMessageHandler logHandler,
            final boolean async, Option... options ) throws BazaarException
    {
        int limit = 1;
        int fetchSize = 1000;
        options = ILogOptions.REVISION.removeFrom( options );
        Arrays.sort( options );
        final int limitIdx = Arrays.binarySearch( options, ILogOptions.LIMIT );
        if ( limitIdx >= 0 )
        {
            final String limitStr = ((KeywordOption)options[limitIdx]).getArgument();
            if ( limitStr != null && !"".equals( limitStr ) )
            {
                limit = Integer.valueOf( limitStr );
            }
            options = ILogOptions.LIMIT.removeFrom( options );
        }
        final int verboseIdx = Arrays.binarySearch( options, ILogOptions.VERBOSE );
        if ( verboseIdx >= 0 )
        {
            fetchSize = 100;
        }
        final int iterations = limit / fetchSize;
        final ExecutorService executor = Executors.newFixedThreadPool( 1 );
        final List<Future<? extends Object>> futures =
                new ArrayList<Future<? extends Object>>( iterations );
        int startRev = fetchSize;
        int endRev = 1;
        BazaarRevision lastRevno = revno( location );
        int endRevMaxValue = Integer.valueOf( lastRevno.getValue() );
        for ( int i = 0; i < iterations; i++ )
        {
            startRev = fetchSize * (i + 1);
            if ( startRev > endRevMaxValue )
            {
                break;
            }
            // create the command here because it keep state (m_options, output, etc)
            // FIXME: needs to be fixed in Command class
            final Log cmd = new Log( m_workDir, location );
            for ( Option option : options )
            {
                cmd.setOption( option );
            }
            BazaarRevision start =
                    BazaarRevision.getRevision( Prefix.LAST, String.valueOf( startRev ) );
            BazaarRevision end = BazaarRevision.getRevision( Prefix.LAST, String.valueOf( endRev ) )
                    ;
            cmd.setOption( ILogOptions.REVISION.with(
                    BazaarRevisionRange.getRange( start, end ).toString() ) );
            run( cmd );
            final String xml = cmd.getStandardOutput();
            Runnable logHandlerRunnable = new Runnable()
            {
                public void run()
                {
                    try
                    {
                        logHandler.handle( XMLLogParser.parse( xml ) );
                    }
                    catch ( BazaarException e )
                    {
                        sm_logger.error( "Was an error: ", e );
                    }
                }
            };
            futures.add( executor.submit( logHandlerRunnable ) );
            endRev = startRev + 1;
            if ( endRev > endRevMaxValue )
            {
                endRev = endRevMaxValue;
            }
        }
        if ( !async )
        {
            for ( Future<? extends Object> future : futures )
            {
                try
                {
                    future.get();
                }
                catch ( InterruptedException e )
                {
                    executor.shutdownNow();
                    throw new BazaarException( e );
                }
                catch ( ExecutionException e )
                {
                    executor.shutdownNow();
                    throw new BazaarException( e );
                }
            }
        }
        executor.shutdown();
    }

    private List<IBazaarLogMessage> log( final Command cmd, final Option... options )
            throws BazaarException
    {
        // TODO: implement -v (verbose) in the parser
        // cmd.setOption(Log.VERBOSE);
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        run( cmd );
        return XMLLogParser.parse( cmd.getStandardOutput() );
    }

    public void move( final File[] orig, final File dest, final Option... options )
            throws BazaarException
    {
        final Move cmd = new Move( m_workDir, orig, dest );
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        run( cmd );
    }

    public String nick( final String newNick ) throws BazaarException
    {
        final Nick cmd = new Nick( m_workDir, newNick );
        run( cmd );
        String nick = cmd.getStandardOutput();
        return nick == null ? "" : nick.replaceAll( "\n", "" ).replaceAll( "\r", "" );
    }

    public void pull( final BranchLocation location, final Option... options )
            throws BazaarException
    {
        final Pull cmd = new Pull( m_workDir, location );
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        run( cmd );
    }

    public void push( final BranchLocation location, final Option... options )
            throws BazaarException
    {
        final Push cmd = new Push( m_workDir, location );
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        run( cmd );
    }

    public void remove( final File[] files, final Option... options ) throws BazaarException
    {
        final Remove cmd = new Remove( m_workDir, files );
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        run( cmd );
    }

    public void revert( final File[] files, final Option... options ) throws BazaarException
    {
        final Revert cmd = new Revert( m_workDir, files );
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        run( cmd );
    }

    public BazaarRevision revno( final BranchLocation location ) throws BazaarException
    {
        Revno cmd;
        if ( location != null )
            cmd = new Revno( m_workDir, location );
        else
            cmd = new Revno( m_workDir, new BranchLocation( new File( "." ) ) );
        run( cmd );
        return BazaarRevision.getRevision( BazaarRevision.Prefix.REVNO, cmd.getStandardOutput() );
    }

    public BazaarRevision revisionInfo( final File location, final IBazaarRevisionSpec revision )
            throws BazaarException
    {
        RevisionInfo cmd;
        if ( location != null )
            cmd = new RevisionInfo( location );
        else
            cmd = new RevisionInfo( m_workDir );

        if ( revision != null )
            cmd.setOption( RevisionInfo.REVISION.with( revision.toString() ) );

        run( cmd );
        String revString = cmd.getStandardOutput();
        revString = revString.substring( revString.lastIndexOf( ' ' ) + 1 ).trim();
        return BazaarRevision.getRevision( Prefix.REVID, revString );
    }

    public BazaarTreeStatus status( File[] files, final Option... options )
            throws BazaarException
    {

        if ( files == null )
        {
            files = new File[]{new File( "." )};
        }

        final Status cmd = new Status( m_workDir, files );

        for ( Option option : options )
        {
            cmd.setOption( option );
        }

        run( cmd );

        BazaarTreeStatus status = null;
        if ( cmd.getStandardOutput() != null )
        {
            final XMLStatusParser parser = new XMLStatusParser();
            parser.parse( cmd.getStandardOutput() );
            status = new BazaarTreeStatus( parser.getStatusSet(), parser.getPendingMerges() );
        }
        else
        {
            status = new BazaarTreeStatus();
        }
        return status;
    }

    public void unCommit( final File location, final Option... options )
            throws BazaarException
    {
        final UnCommit cmd = new UnCommit( m_workDir, location );
        // use --force because (for the moment) we can't propmt a comfirm dialog
        cmd.setOption( UnCommit.FORCE );
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        run( cmd );
    }

    public void ignore( final String pattern ) throws BazaarException
    {
        final Ignore cmd = new Ignore( m_workDir, pattern );
        run( cmd );
    }

    public Map<String, String> ignored() throws BazaarException
    {
        final Ignored cmd = new Ignored( m_workDir );
        run( cmd );
        final String[] lines = cmd.getStandardOutputSplit();
        Map<String, String> result = new HashMap<String, String>( lines.length );
        for ( String line : lines )
        {
            int sepIdx = line.lastIndexOf( ' ' );
            if ( sepIdx < 1 ) { continue; }
            final String[] splitted =
                    new String[]{line.substring( 0, sepIdx ), line.substring( sepIdx )};
            result.put( splitted[0].trim(), splitted[1].trim() );
        }
        return result;
    }

    public String[] unknowns() throws BazaarException
    {
        final Unknowns cmd = new Unknowns( m_workDir, null );
        run( cmd );
        return cmd.getStandardOutputSplit();
    }

    public IBazaarItemInfo[] ls( final File workDir, final IBazaarRevisionSpec revision,
            final Option... options ) throws BazaarException
    {
        final Ls cmd = new Ls( workDir );
        if ( revision != null )
        {
            cmd.setOption( ILsOptions.REVISION.with( revision.toString() ) );
        }
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        try
        {
            run( cmd );
            return XMLLsParser.parse( cmd.getStandardOutput() ).toArray( new IBazaarItemInfo[0] );
        }
        catch ( BazaarException e )
        {
            // if get an exception from 'bzr ls', assume that the
            // resource is not under revision control
            return new IBazaarItemInfo[0];
        }
    }

    public String update( final File file, final Option... options ) throws BazaarException
    {
        final Update cmd = new Update( m_workDir, file );
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        run( cmd );
        return cmd.getStandardError();
    }

    public IBazaarInfo info( final BranchLocation location, final Option... options )
            throws BazaarException
    {
        final Info cmd = new Info( m_workDir, location );
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        run( cmd );
        return new XMLInfoParser().parse( cmd.getStandardOutput() );
    }

    public void bind( final BranchLocation location, final Option... options )
            throws BazaarException
    {
        final Bind cmd = new Bind( m_workDir, location );
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        run( cmd );
    }

    public void unBind( final Option... options ) throws BazaarException
    {
        final UnBind cmd = new UnBind( m_workDir );
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        run( cmd );
    }

    public Map<String, List<IBazaarLogMessage>> missing( final File workdir,
            final BranchLocation otherBranch, final Option... options ) throws BazaarException
    {
        final Missing cmd = new Missing( m_workDir, otherBranch );
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        run( cmd );
        if ( cmd.getStandardOutput() == null || cmd.getStandardOutput().trim().equals( "" ) )
            return new HashMap<String, List<IBazaarLogMessage>>( 0 );
        return new XMLMissingParser().parse( cmd.getStandardOutput() );
    }

    public void switchBranch( final BranchLocation location, final Option... options )
            throws BazaarException
    {
        final Switch cmd = new Switch( m_workDir, location );
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        run( cmd );
    }

    public void merge( final BranchLocation remoteBranch, final Option... options )
            throws BazaarException
    {
        final Merge cmd = new Merge( m_workDir, remoteBranch );
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        run( cmd );
    }

    public void resolve( List<File> files, final Option... options ) throws BazaarException
    {
        final Resolve cmd = new Resolve( m_workDir, files );
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        run( cmd );
    }

    public void send( final BranchLocation submitBranch, final Option... options )
            throws BazaarException
    {
        final Send cmd = new Send( m_workDir, submitBranch );
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        run( cmd );
    }

    public BazaarVersionInfo versionInfo( final BranchLocation location, final Option... options )
            throws BazaarException
    {
        final VersionInfo cmd = new VersionInfo( m_workDir, location );
        for ( Option option : options )
        {
            cmd.setOption( option );
        }
        cmd.setOption( IVersionInfoOptions.FORMAT.with( "rio" ) );
        run( cmd );
        return BazaarVersionInfo.parse( cmd.getStandardOutput() );
    }

    public void addNotifyListener( IBazaarNotifyListener listener )
    {
    }

    public void addPasswordCallback( IBazaarPromptUserPassword callback )
    {
        // do nothing (for the moment)
    }

    public void removeNotifyListener( IBazaarNotifyListener listener )
    {
    }

    /**
     * Run the speccified command.<br>
     *
     * @param cmd ICommand to execute
     * @throws BazaarException
     */
    protected void run( final Command cmd ) throws BazaarException
    {
        cmd.execute();
    }

    public BazaarRevision findMergeBase( final BranchLocation branch, final BranchLocation other )
            throws BazaarException
    {
        final FindMergeBase cmd = new FindMergeBase( branch, other );
        run( cmd );
        String output = cmd.getStandardOutput();
        output = output.replace( "merge base is revision ", "" ).trim();
        return BazaarRevision.getRevision( Prefix.REVID, output );
    }
}
