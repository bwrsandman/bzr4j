/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.parser;

import java.io.IOException;
import java.util.List;

import org.emergent.bzr4j.core.BazaarItemKind;
import org.emergent.bzr4j.core.BazaarStatusKind;
import org.emergent.bzr4j.core.IBazaarItemInfo;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.testUtils.ParserTest;
import org.testng.annotations.Test;

/**
 * @author Guillermo Gonzalez
 */
public class XMLLsParserTest extends ParserTest
{

    @Test
    public void testparse() throws BazaarException, IOException
    {
        List<IBazaarItemInfo> items = XMLLsParser.parse( getContentsFrom( lsFile ) );

        org.testng.Assert.assertNotNull( items );
        org.testng.Assert.assertFalse( items.size() == 0 );
        org.testng.Assert.assertEquals( items.size(), 45, "Number of items diffier" );
        int fileCounter = 0;
        int dirCounter = 0;
        int symlinkCounter = 0;
        for ( IBazaarItemInfo item : items )
        {
            fileCounter += BazaarItemKind.FILE.equals( item.getKind() ) ? 1 : 0;
            dirCounter += BazaarItemKind.DIRECTORY.equals( item.getKind() ) ? 1 : 0;
            symlinkCounter += BazaarItemKind.SYMLINK.equals( item.getKind() ) ? 1 : 0;
        }
        org.testng.Assert.assertEquals( fileCounter, 44, "Number of files diffier" );
        org.testng.Assert.assertEquals( dirCounter, 1, "Number of directories diffier" );
        org.testng.Assert.assertEquals( symlinkCounter, 0, "Number of synlinks diffier" );

        int versionedCounter = 0;
        int ignoredCounter = 0;
        int unknownCounter = 0;
        for ( IBazaarItemInfo item : items )
        {
            versionedCounter += BazaarStatusKind.VERSIONED.equals( item.getStatusKind() ) ? 1 : 0;
            ignoredCounter += BazaarStatusKind.IGNORED.equals( item.getStatusKind() ) ? 1 : 0;
            unknownCounter += BazaarStatusKind.UNKNOWN.equals( item.getStatusKind() ) ? 1 : 0;
        }
        org.testng.Assert.assertEquals( versionedCounter, 21, "Number of versioned items diffier" );
        org.testng.Assert.assertEquals( ignoredCounter, 19, "Number of ignored items diffier" );
        org.testng.Assert.assertEquals( unknownCounter, 5, "Number of unknown items diffier" );
    }

}
