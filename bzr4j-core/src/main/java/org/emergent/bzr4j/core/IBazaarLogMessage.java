/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.core;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface IBazaarLogMessage
{

    public BazaarRevision getRevision();

    public String getCommiter();

    public String getAuthor();

    public String getBranchNick();

    public String getTimeStamp();// Tue 2007-03-27 19:28:33 -0300

    public String getMessage();

    public Date getDate();

    /**
     *
     * @return a unmodifiable List of {@link IBazaarStatus} respresenting the affected paths
     */
    public List<IBazaarStatus> getAffectedFiles();

    /**
     *
     * @param includeMerges include the affected paths in the nested merges (if there any)
     * @return a unmodifiable List of {@link IBazaarStatus} respresenting the affected paths
     */
    public List<IBazaarStatus> getAffectedFiles( boolean includeMerges );

    /**
     *
     * @return a unmodifiable and ordered (by revno) list of {@link IBazaarLogMessage}
     */
    public List<IBazaarLogMessage> getMerged();

    /**
     * if the current log message has merged logs, returns a value > 0
     *
     * @return > 0 if the current log has merges
     */
    public int hasMerge();

    public String getRevisionId();

    /**
     *
     * @return a unmodifiable list of parent revision id's
     */
    public List<String> getParents();

    public static final class LogMessageComparator implements Comparator<IBazaarLogMessage>
    {
        public int compare( IBazaarLogMessage log1, IBazaarLogMessage log2 )
        {
            return log2.getRevision().compareTo( log1.getRevision() );
        }
    }

    ;

}
