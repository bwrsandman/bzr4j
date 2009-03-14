/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.parser;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.emergent.bzr4j.core.BazaarStatusKind;
import org.emergent.bzr4j.core.IBazaarLogMessage;
import org.emergent.bzr4j.core.IBazaarStatus;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.testUtils.ParserTest;

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

        Assert.assertNotNull( statuses );
        Assert.assertFalse( statuses.size() == 0 );
        Assert.assertEquals( "statuses size diffier: ", 20, statuses.size() );
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
        Assert.assertEquals( 8, modified );
        Assert.assertEquals( 4, added );
        Assert.assertEquals( 1, renamed );
        Assert.assertEquals( 1, kindChanged );
        Assert.assertEquals( 7, unknown );
    }

    @Test
    public void testStatusWithConflicts() throws BazaarException, IOException
    {
        Set<IBazaarStatus> statuses = null;

        XMLStatusParser statusParser = new XMLStatusParser();
        statusParser.parse( getContentsFrom( statusWithConflictsFile ) );
        statuses = statusParser.getStatusSet();

        Assert.assertNotNull( statuses );
        Assert.assertFalse( statuses.size() == 0 );
        Assert.assertEquals( "statuses size diffier: ", 20, statuses.size() );
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
        Assert.assertEquals( 8, modified );
        Assert.assertEquals( 4, added );
        Assert.assertEquals( 1, renamed );
        Assert.assertEquals( 1, kindChanged );
        Assert.assertEquals( 7, unknown );
        Assert.assertEquals( 1, conflicts );
    }

    @Test
    public void testStatusWithPendingMerges() throws BazaarException, IOException
    {
        Set<IBazaarStatus> statuses = null;

        XMLStatusParser statusParser = new XMLStatusParser();
        statusParser.parse( getContentsFrom( statusWithPendingMergesFile ) );
        statuses = statusParser.getStatusSet();

        Assert.assertNotNull( statuses );
        Assert.assertFalse( statuses.size() == 0 );
        Assert.assertEquals( "statuses size diffier: ", 17, statuses.size() );
        int modified = 0, added = 0, unknown = 0, deleted = 0;
        for ( IBazaarStatus status : statuses )
        {
            modified = status.contains( BazaarStatusKind.MODIFIED ) ? modified + 1 : modified;
            added = status.contains( BazaarStatusKind.CREATED ) ? added + 1 : added;
            unknown = status.contains( BazaarStatusKind.UNKNOWN ) ? unknown + 1 : unknown;
            deleted = status.contains( BazaarStatusKind.DELETED ) ? deleted + 1 : deleted;
        }
        Assert.assertEquals( 8, modified );
        Assert.assertEquals( 1, added );
        Assert.assertEquals( 5, unknown );
        Assert.assertEquals( 3, deleted );

        List<IBazaarLogMessage> pendingMerges = statusParser.getPendingMerges();
        Assert.assertEquals( 2, pendingMerges.size() );
        for ( IBazaarLogMessage pending : pendingMerges )
        {
            Assert.assertNotNull( pending.getRevisionId() );
            Assert.assertNotNull( pending.getTimeStamp() );
        }
    }
}
