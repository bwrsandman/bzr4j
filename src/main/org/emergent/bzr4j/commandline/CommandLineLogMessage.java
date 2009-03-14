/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.emergent.bzr4j.core.BazaarRevision;
import org.emergent.bzr4j.core.IBazaarLogMessage;
import org.emergent.bzr4j.core.IBazaarStatus;
import org.emergent.bzr4j.utils.BazaarRuntimeException;
import org.emergent.bzr4j.utils.StringUtil;

/**
 * @author Guillermo Gonzalez
 *
 * TODO: need revision
 */
public class CommandLineLogMessage implements IBazaarLogMessage
{

    private final String timestamp;

    private final List<IBazaarStatus> files;

    private final String commiter;

    private final String author;

    private final String revno;

    private final String nick;

    private final String message;

    private final List<IBazaarLogMessage> merged;

    private final String revisionId;

    private final List<String> parents;

    /**
     *
     * @param revno
     * @param commiter
     * @param author
     * @param nick
     * @param timestamp
     * @param message
     * @param resourceStatus
     * @param merged
     * @param revisionId
     * @param parents
     */
    public CommandLineLogMessage( final String revno, final String commiter, final String author,
            final String nick, final String timestamp, final String message,
            final List<IBazaarStatus> resourceStatus,
            final List<IBazaarLogMessage> merged, final String revisionId,
            final List<String> parents )
    {
        this.revno = revno;
        this.commiter = commiter;
        this.nick = nick;
        this.timestamp = timestamp;
        this.message = message;
        this.files = Collections.unmodifiableList(
                resourceStatus == null ? Collections.EMPTY_LIST : resourceStatus );
        this.merged =
                Collections.unmodifiableList( merged == null ? Collections.EMPTY_LIST : merged );
        this.author = author;
        this.revisionId = revisionId;
        this.parents =
                Collections.unmodifiableList( parents == null ? Collections.EMPTY_LIST : parents );
    }

    public CommandLineLogMessage( final String revno, final String commiter, final String nick,
            final String timestamp, final String message, final List<IBazaarStatus> resourceStatus,
            final List<IBazaarLogMessage> merged, final String revisionId,
            final List<String> parents )
    {
        this( revno, commiter, null, nick, timestamp, message, resourceStatus, merged, revisionId,
                parents );
    }

    public String getBranchNick()
    {
        return nick;
    }

    public String getCommiter()
    {
        return commiter;
    }

    public String getMessage()
    {
        return message;
    }

    public BazaarRevision getRevision()
    {
        return BazaarRevision.getRevision( "revno:", revno );
    }

    public String getTimeStamp()
    {
        return timestamp;
    }

    public Date getDate()
    {
        try
        {
            // FIXME: investigate why the call to parse died if the timeStamp
            // conatins the day (three letters)
            // ¿maybe a locale issue?
            return StringUtil.parseLogDate( timestamp );
        }
        catch ( ParseException e )
        {
            throw new BazaarRuntimeException( e );
        }
    }

    public List<IBazaarStatus> getAffectedFiles()
    {
        return files;
    }

    public List<IBazaarLogMessage> getMerged()
    {
        return merged;
    }

    public List<IBazaarStatus> getAffectedFiles( boolean includeMerges )
    {
        if ( includeMerges )
        {
            List<IBazaarStatus> all = new ArrayList<IBazaarStatus>();
            all.addAll( files );
            for ( IBazaarLogMessage log : getMerged() )
            {
                for ( IBazaarStatus status : log.getAffectedFiles() )
                {
                    if ( !all.contains( status ) )
                    {
                        all.add( status );
                    }
                }
            }
            return all;
        }
        else
        {
            return getAffectedFiles();
        }
    }

    public String getAuthor()
    {
        if ( author == null )
            return getCommiter();
        return author;
    }

    public List<String> getParents()
    {
        return parents;
    }

    public String getRevisionId()
    {
        return revisionId;
    }

    public int hasMerge()
    {
        final List<IBazaarLogMessage> merges = getMerged();
        if ( merges != null )
        {
            return merges.size();
        }
        else
        {
            return 0;
        }
    }

}
