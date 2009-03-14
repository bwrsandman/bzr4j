package jetbrains.buildServer.buildTriggers.vcs.bzr;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.vcs.IncludeRule;
import jetbrains.buildServer.vcs.VcsChange;
import jetbrains.buildServer.vcs.VcsChangeInfo;
import org.emergent.bzr4j.commandline.internal.Commander;
import org.emergent.bzr4j.commandline.internal.ExecResult;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.utils.BzrUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Patrick Woodworth
 */
public class TCUtil
{
    public static final Logger LOG = Loggers.VCS;

    static final String REVNO_PREFIX = "revno:";

    static final String CHANGESET_PREFIX = "revision-id:";

    static final String USER_PREFIX = "committer:";

    static final String DATE_PREFIX = "timestamp:";

    static final String DATE_FORMAT = "EEE yyyy-MM-dd HH:mm:ss Z";

    static final String SUMMARY_PREFIX = "message:";

    public static List<ChangeSet> parseChangeSets( final String stdout )
    {
        List<ChangeSet> result = new ArrayList<ChangeSet>();
        String[] lines = stdout.split( "\n" );
        ChangeSet current = null;
        int lineNum = 0;
        while ( lineNum < lines.length )
        {
            String line = lines[lineNum];
            lineNum++;

            if ( line.startsWith( REVNO_PREFIX ) )
            { // todo use CHANGESET_PREFIX too
                String revno = line.substring( REVNO_PREFIX.length() ).trim();
//        line = lines[lineNum++];
//        String revid = line.substring(CHANGESET_PREFIX.length()).trim();
                try
                {
                    current = new ChangeSet( revno );
                    result.add( current );
                }
                catch ( IllegalArgumentException e )
                {
                    LOG.warn( "Unable to extract changeset id from the line: " + line );
                }

                continue;
            }

            if ( current == null ) continue;

            if ( line.startsWith( USER_PREFIX ) )
            {
                current.setUser( line.substring( USER_PREFIX.length() ).trim() );
                continue;
            }

            if ( line.startsWith( DATE_PREFIX ) )
            {
                String date = line.substring( DATE_PREFIX.length() ).trim();
                try
                {
                    Date parsedDate =
                            new SimpleDateFormat( DATE_FORMAT, Locale.ENGLISH ).parse( date );
                    current.setTimestamp( parsedDate );
                }
                catch ( ParseException e )
                {
                    LOG.warn( "Unable to parse date: " + date );
                    current = null;
                }

                continue;
            }

            if ( line.startsWith( SUMMARY_PREFIX ) && (lineNum + 1 < lines.length) )
            {
//        String summary = line.substring(SUMMARY_PREFIX.length()).trim();
//        current.setSummary(summary);
                current.setSummary( lines[lineNum++] );

                continue;
            }
        }

        return result;
    }

    public static List<VcsChange> getVcsChanges(
            Commander commander, ChangeSet prev, ChangeSet cur, IncludeRule includeRule )
            throws BazaarException
    {
        List<ModifiedFile> modifiedFiles = getModifiedFiles( commander, prev, cur );
        // changeset full version will be set into VcsChange structure and
        // stored in database (note that getContent method will be invoked with this version)
        return TCUtil.toVcsChanges( modifiedFiles, prev.getFullVersion(), cur.getFullVersion(),
                includeRule );
    }

    public static List<ModifiedFile> getModifiedFiles(
            Commander commander, ChangeSet prev, ChangeSet cur )
            throws BazaarException
    {
        ExecResult eres = commander.status()
                .addOpts( "-SV" )
                .addOpts( "--rev", prev.getId() + ".." + cur.getId() )
                .exec( true );
        List<ModifiedFile> modifiedFiles = TCUtil.parseStatus( eres.getStdout() );
        return modifiedFiles;
//         changeset full version will be set into VcsChange structure and
//         stored in database (note that getContent method will be invoked with this version)
//        return TCUtil.toVcsChanges( modifiedFiles, prev.getFullVersion(), cur.getFullVersion(),
//                includeRule );
    }

    public static List<ModifiedFile> parseStatus( final String stdout )
    {
        List<ModifiedFile> result = new ArrayList<ModifiedFile>();
        String[] lines = stdout.split( "\n" );
        for ( String line : lines )
        {
            if ( line.length() == 0 ) continue;
            if ( line.startsWith( "working tree is out of date" ) ) continue;
            char fmod = line.charAt( 0 );
            char modifier = line.charAt( 1 );
            if ( fmod == 'R' )
            {
                String path = line.substring( 3 ).trim();
                int ptrIdx = path.indexOf( "=>" );
                String oldpath = path.substring( 0, ptrIdx ).trim();
                String newpath = path.substring( ptrIdx + "=>".length() ).trim();
                result.add( new ModifiedFile( ModifiedFile.Status.REMOVED, oldpath ) );
                result.add( new ModifiedFile( ModifiedFile.Status.ADDED, newpath ) );
            }
            else
            {
                String path = line.substring( 3 ).trim();
                ModifiedFile.Status status = toStatus( modifier );
                if ( status == ModifiedFile.Status.UNKNOWN ) continue;
                result.add( new ModifiedFile( status, path ) );
            }
        }
        return result;
    }

    public static ModifiedFile.Status toStatus( final char modifier )
    {
        switch ( modifier )
        {
            case 'A':
                return ModifiedFile.Status.ADDED;
            case 'M':
                return ModifiedFile.Status.MODIFIED;
            case 'R':
                return ModifiedFile.Status.REMOVED;
            default:
                return ModifiedFile.Status.UNKNOWN;
        }
    }

    public static List<VcsChange> toVcsChanges( final List<ModifiedFile> modifiedFiles, String prevVer,
            String curVer, final IncludeRule includeRule )
    {
        List<VcsChange> files = new ArrayList<VcsChange>();
        for ( ModifiedFile mf : modifiedFiles )
        {
            String normalizedPath = BzrUtil.normalizeSeparator( mf.getPath() );
            if ( !normalizedPath.startsWith( includeRule.getFrom() ) )
                continue; // skip files which do not match include rule

            VcsChangeInfo.Type changeType = getChangeType( mf.getStatus() );
            if ( changeType == null )
            {
                Loggers.VCS.warn( "Unable to convert status: " + mf.getStatus()
                        + " to VCS change type" );
                changeType = VcsChangeInfo.Type.NOT_CHANGED;
            }
            files.add( new VcsChange( changeType, mf.getStatus().getName(), normalizedPath,
                    normalizedPath, prevVer, curVer ) );
        }
        return files;
    }

    private static VcsChangeInfo.Type getChangeType( final ModifiedFile.Status status )
    {
        switch ( status )
        {
            case ADDED:
                return VcsChangeInfo.Type.ADDED;
            case MODIFIED:
                return VcsChangeInfo.Type.CHANGED;
            case REMOVED:
                return VcsChangeInfo.Type.REMOVED;
        }
        return null;
    }
}
