/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.parser;

import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.BazaarStatusKind;
import org.emergent.bzr4j.core.IBazaarLogMessage;
import org.emergent.bzr4j.core.IBazaarStatus;
import org.emergent.bzr4j.testUtils.ParserTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Guillermo Gonzalez
 */
public class XMLLogParserTest extends ParserTest
{

    List<IBazaarLogMessage> logs;

    List<IBazaarLogMessage> logsWithId;

    @BeforeMethod
    public void loadLogs() throws BazaarException, IOException
    {
        logs = XMLLogParser.parse( getContentsFrom( logFile ) );
        org.testng.Assert.assertNotNull( logs );
        org.testng.Assert.assertFalse( logs.size() == 0 );
        logsWithId = XMLLogParser.parse( getContentsFrom( logIdsFile ) );
        org.testng.Assert.assertNotNull( logsWithId );
        org.testng.Assert.assertFalse( logsWithId.size() == 0 );
    }

    @Test
    public void testSimpleLog()
    {
        org.testng.Assert.assertEquals( logs.size(), 21, "Number of log message differs" );
        int counter = 0;
        for ( IBazaarLogMessage log : logs )
        {
            counter++;
            if ( log.getMerged() != null && log.getMerged().size() > 0 )
            {
                counter += log.getMerged().size();
            }
        }
        org.testng.Assert.assertEquals( counter, 36, "Number of TOTAL log messages differs" );

        org.testng.Assert.assertEquals( logs.get( 0 ).getAffectedFiles().size(), 2,
                "Number of affected files in the first log" );

        // now test the merges of an intermnediate log entry
        IBazaarLogMessage fifthLog = logs.get( 4 );
        org.testng.Assert.assertEquals( fifthLog.getAffectedFiles().size(), 3,
                "Number of affected files at the 5th log " );
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
//        for (int ii = 0; ii < logs.size(); ii++)
//        {
//            List<IBazaarLogMessage> merged = logs.get( ii ).getMerged();
//            if (merged.size() > 0)
//                System.out.printf( "logs[%d]: %d\n", ii, merged.get( 0 ).getAffectedFiles().size() );
//        }
        org.testng.Assert.assertEquals( logs.get( 5 ).getMerged().get( 0 ).getAffectedFiles().size(), 33,
                "Number of affected files in first merge log at the 5th log " );
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
        org.testng.Assert.assertEquals( (Object)files.size(), expected.length );
        for ( int i = 0; i < expected.length; i++ )
        {
            org.testng.Assert.assertEquals( files.get( i ).getPath(), expected[i] );
        }

        files.clear();
        files.addAll( firstLog.getAffectedFiles( true ) );
        // These statuses originate from a Set, so their order is undefined.  Sort to
        // be able to compare them.
        sortStatuses( files );
        expected = new String[]{"bzrlib/errors.py", "NEWS"};
        for ( int i = 0; i < expected.length; i++ )
        {
            org.testng.Assert.assertEquals( files.get( i ).getPath(), expected[i] );
        }

        // TODO: Create a changeset interface?
    }

    @Test
    public void testParents()
    {
        IBazaarLogMessage firstLog = logsWithId.get( 0 );
        org.testng.Assert.assertEquals( (Object)firstLog.getParents().size(), 1 );
        IBazaarLogMessage aLog = logsWithId.get( 7 );
        org.testng.Assert.assertEquals( (Object)aLog.getParents().size(), 2 );
    }

    @Test
    public void testRevisionId()
    {
        IBazaarLogMessage firstLog = logsWithId.get( 0 );
        org.testng.Assert.assertEquals( firstLog.getRevisionId(),
                "guillo.gonzo@gmail.com-20080824030806-cqf1nsf47bgp2bwr" );
    }

}
