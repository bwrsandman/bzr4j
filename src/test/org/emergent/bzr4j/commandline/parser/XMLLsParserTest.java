/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.parser;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.emergent.bzr4j.core.BazaarItemKind;
import org.emergent.bzr4j.core.BazaarStatusKind;
import org.emergent.bzr4j.core.IBazaarItemInfo;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.testUtils.ParserTest;

/**
 * @author Guillermo Gonzalez
 */
public class XMLLsParserTest extends ParserTest
{

    @Test
    public void testparse() throws BazaarException, IOException
    {
        List<IBazaarItemInfo> items = XMLLsParser.parse( getContentsFrom( lsFile ) );

        Assert.assertNotNull( items );
        Assert.assertFalse( items.size() == 0 );
        Assert.assertEquals( "Number of items diffier", 45, items.size() );
        int fileCounter = 0;
        int dirCounter = 0;
        int symlinkCounter = 0;
        for ( IBazaarItemInfo item : items )
        {
            fileCounter += BazaarItemKind.FILE.equals( item.getKind() ) ? 1 : 0;
            dirCounter += BazaarItemKind.DIRECTORY.equals( item.getKind() ) ? 1 : 0;
            symlinkCounter += BazaarItemKind.SYMLINK.equals( item.getKind() ) ? 1 : 0;
        }
        Assert.assertEquals( "Number of files diffier", 44, fileCounter );
        Assert.assertEquals( "Number of directories diffier", 1, dirCounter );
        Assert.assertEquals( "Number of synlinks diffier", 0, symlinkCounter );

        int versionedCounter = 0;
        int ignoredCounter = 0;
        int unknownCounter = 0;
        for ( IBazaarItemInfo item : items )
        {
            versionedCounter += BazaarStatusKind.VERSIONED.equals( item.getStatusKind() ) ? 1 : 0;
            ignoredCounter += BazaarStatusKind.IGNORED.equals( item.getStatusKind() ) ? 1 : 0;
            unknownCounter += BazaarStatusKind.UNKNOWN.equals( item.getStatusKind() ) ? 1 : 0;
        }
        Assert.assertEquals( "Number of versioned items diffier", 21, versionedCounter );
        Assert.assertEquals( "Number of ignored items diffier", 19, ignoredCounter );
        Assert.assertEquals( "Number of unknown items diffier", 5, unknownCounter );
    }

}
