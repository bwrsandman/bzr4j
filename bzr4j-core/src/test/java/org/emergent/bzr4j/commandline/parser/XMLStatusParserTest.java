/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.parser;

import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.BazaarStatusKind;
import org.emergent.bzr4j.core.IBazaarLogMessage;
import org.emergent.bzr4j.core.IBazaarStatus;
import org.emergent.bzr4j.testUtils.ParserTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author Guillermo Gonzalez
 *
 */
public class XMLStatusParserTest extends ParserTest
{

    @Test
    public void testSimpleStatus() throws BazaarException, IOException
    {
        Set<IBazaarStatus> statuses = null;

        XMLStatusParser statusParser = new XMLStatusParser();
        statusParser.parse( getContentsFrom( statusFile ) );
        statuses = statusParser.getStatusSet();

        org.testng.Assert.assertNotNull( statuses );
        org.testng.Assert.assertFalse( statuses.size() == 0 );
        org.testng.Assert.assertEquals( statuses.size(), 20, "statuses size diffier: " );
        int modified = 0, added = 0, renamed = 0, kindChanged = 0, unknown = 0;
        for ( IBazaarStatus status : statuses )
        {
            modified = status.contains( BazaarStatusKind.MODIFIED ) ? modified + 1 : modified;
            added = status.contains( BazaarStatusKind.CREATED ) ? added + 1 : added;
            renamed = status.contains( BazaarStatusKind.RENAMED ) ? renamed + 1 : renamed;
            kindChanged = status.contains( BazaarStatusKind.KIND_CHANGED ) ? kindChanged + 1
                    : kindChanged;
            unknown = status.contains( BazaarStatusKind.UNKNOWN ) ? unknown + 1 : unknown;
        }
        org.testng.Assert.assertEquals( (Object)modified, 8 );
        org.testng.Assert.assertEquals( (Object)added, 4 );
        org.testng.Assert.assertEquals( (Object)renamed, 1 );
        org.testng.Assert.assertEquals( (Object)kindChanged, 1 );
        org.testng.Assert.assertEquals( (Object)unknown, 7 );
    }

    @Test
    public void testStatusWithConflicts() throws BazaarException, IOException
    {
        Set<IBazaarStatus> statuses = null;

        XMLStatusParser statusParser = new XMLStatusParser();
        statusParser.parse( getContentsFrom( statusWithConflictsFile ) );
        statuses = statusParser.getStatusSet();

        org.testng.Assert.assertNotNull( statuses );
        org.testng.Assert.assertFalse( statuses.size() == 0 );
        org.testng.Assert.assertEquals( statuses.size(), 20, "statuses size diffier: " );
        int modified = 0, added = 0, renamed = 0, kindChanged = 0, unknown = 0, conflicts = 0;
        for ( IBazaarStatus status : statuses )
        {
            modified = status.contains( BazaarStatusKind.MODIFIED ) ? modified + 1 : modified;
            added = status.contains( BazaarStatusKind.CREATED ) ? added + 1 : added;
            renamed = status.contains( BazaarStatusKind.RENAMED ) ? renamed + 1 : renamed;
            kindChanged = status.contains( BazaarStatusKind.KIND_CHANGED ) ? kindChanged + 1
                    : kindChanged;
            unknown = status.contains( BazaarStatusKind.UNKNOWN ) ? unknown + 1 : unknown;
            conflicts =
                    status.contains( BazaarStatusKind.HAS_CONFLICTS ) ? conflicts + 1 : conflicts;
        }
        org.testng.Assert.assertEquals( (Object)modified, 8 );
        org.testng.Assert.assertEquals( (Object)added, 4 );
        org.testng.Assert.assertEquals( (Object)renamed, 1 );
        org.testng.Assert.assertEquals( (Object)kindChanged, 1 );
        org.testng.Assert.assertEquals( (Object)unknown, 7 );
        org.testng.Assert.assertEquals( (Object)conflicts, 1 );
    }

    @Test
    public void testStatusWithPendingMerges() throws BazaarException, IOException
    {
        Set<IBazaarStatus> statuses = null;

        XMLStatusParser statusParser = new XMLStatusParser();
        statusParser.parse( getContentsFrom( statusWithPendingMergesFile ) );
        statuses = statusParser.getStatusSet();

        org.testng.Assert.assertNotNull( statuses );
        org.testng.Assert.assertFalse( statuses.size() == 0 );
        org.testng.Assert.assertEquals( statuses.size(), 17, "statuses size diffier: " );
        int modified = 0, added = 0, unknown = 0, deleted = 0;
        for ( IBazaarStatus status : statuses )
        {
            modified = status.contains( BazaarStatusKind.MODIFIED ) ? modified + 1 : modified;
            added = status.contains( BazaarStatusKind.CREATED ) ? added + 1 : added;
            unknown = status.contains( BazaarStatusKind.UNKNOWN ) ? unknown + 1 : unknown;
            deleted = status.contains( BazaarStatusKind.DELETED ) ? deleted + 1 : deleted;
        }
        org.testng.Assert.assertEquals( (Object)modified, 8 );
        org.testng.Assert.assertEquals( (Object)added, 1 );
        org.testng.Assert.assertEquals( (Object)unknown, 5 );
        org.testng.Assert.assertEquals( (Object)deleted, 3 );

        List<IBazaarLogMessage> pendingMerges = statusParser.getPendingMerges();
        org.testng.Assert.assertEquals( (Object)pendingMerges.size(), 2 );
        for ( IBazaarLogMessage pending : pendingMerges )
        {
            org.testng.Assert.assertNotNull( pending.getRevisionId() );
            org.testng.Assert.assertNotNull( pending.getTimeStamp() );
        }
    }
}
