/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.emergent.bzr4j.core.BazaarStatusKind;
import org.emergent.bzr4j.core.IBazaarLogMessage;
import org.emergent.bzr4j.core.IBazaarStatus;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.testUtils.ParserTest;

/**
 * @author Guillermo Gonzalez
 */
public class XMLLogParserTest extends ParserTest
{

    List<IBazaarLogMessage> logs;

    List<IBazaarLogMessage> logsWithId;

    @Before
    public void loadLogs() throws BazaarException, IOException
    {
        logs = XMLLogParser.parse( getContentsFrom( logFile ) );
        assertNotNull( logs );
        assertFalse( logs.size() == 0 );
        logsWithId = XMLLogParser.parse( getContentsFrom( logIdsFile ) );
        assertNotNull( logsWithId );
        assertFalse( logsWithId.size() == 0 );
    }

    @Test
    public void testSimpleLog()
    {

        assertEquals( "Number of log message diffier", 6, logs.size() );
        int counter = 0;
        for ( IBazaarLogMessage log : logs )
        {
            counter++;
            if ( log.getMerged() != null && log.getMerged().size() > 0 )
            {
                counter += log.getMerged().size();
            }
        }
        assertEquals( "Number of TOTAL log messages diffier", 21, counter );

        assertEquals( "Number of affected files in the first log", 2,
                logs.get( 0 ).getAffectedFiles().size() );

        // now test the merges of an intermnediate log entry
        IBazaarLogMessage fifthLog = logs.get( 4 );
        assertEquals( "Number of affected files at the 5th log ", 3,
                fifthLog.getAffectedFiles().size() );
        IBazaarLogMessage fifthLogFirstChild = fifthLog.getMerged().get( 0 );
        for ( int i = 0; i < fifthLogFirstChild.getAffectedFiles().size(); i++ )
        {
            if ( i == 0 )
            {
                fifthLogFirstChild.getAffectedFiles().get( i ).contains( BazaarStatusKind.CREATED );
            }
            else
            {
                fifthLogFirstChild.getAffectedFiles().get( i )
                        .contains( BazaarStatusKind.MODIFIED );
            }
        }
        assertEquals( "Number of affected files in first merge log at the 5th log ", 33,
                logs.get( 4 ).getMerged().get( 0 ).getAffectedFiles().size() );
    }

    /**
     * Sort an array of IBazaarStatuses alphabetically by path.
     * @param unsorted An array to sort.
     */
    private void sortStatuses( List<IBazaarStatus> unsorted )
    {
        Collections.sort( unsorted, new Comparator<IBazaarStatus>()
        {
            public int compare( IBazaarStatus o1, IBazaarStatus o2 )
            {
                return o1.getPath().compareToIgnoreCase( o2.getPath() );
            }
        } );
    }

    @Test
    public void testAffectedFiles()
    {
        IBazaarLogMessage firstLog = logs.get( 0 );
        List<IBazaarStatus> files = new ArrayList<IBazaarStatus>();
        files.addAll( firstLog.getAffectedFiles() );
        // These statuses originate from a Set, so their order is undefined.  Sort to
        // be able to compare them.
        sortStatuses( files );
        String[] expected = new String[]{"bzrlib/errors.py", "NEWS"};
        assertEquals( expected.length, files.size() );
        for ( int i = 0; i < expected.length; i++ )
        {
            assertEquals( expected[i], files.get( i ).getPath() );
        }

        files.clear();
        files.addAll( firstLog.getAffectedFiles( true ) );
        // These statuses originate from a Set, so their order is undefined.  Sort to
        // be able to compare them.
        sortStatuses( files );
        expected = new String[]{"bzrlib/errors.py", "NEWS"};
        for ( int i = 0; i < expected.length; i++ )
        {
            assertEquals( expected[i], files.get( i ).getPath() );
        }

        // TODO: Create a changeset interface?
    }

    @Test
    public void testParents()
    {
        IBazaarLogMessage firstLog = logsWithId.get( 0 );
        assertEquals( 1, firstLog.getParents().size() );
        IBazaarLogMessage aLog = logsWithId.get( 7 );
        assertEquals( 2, aLog.getParents().size() );
    }

    @Test
    public void testRevisionId()
    {
        IBazaarLogMessage firstLog = logsWithId.get( 0 );
        assertEquals( "guillo.gonzo@gmail.com-20080824030806-cqf1nsf47bgp2bwr",
                firstLog.getRevisionId() );
    }

}
