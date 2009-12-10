/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.parser;

import java.io.IOException;

import org.emergent.bzr4j.core.IBazaarInfo;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.testUtils.ParserTest;
import org.testng.annotations.Test;

/**
 * @author Guillermo Gonzalez
 */
public class XMLInfoParserTest extends ParserTest
{

    @Test
    public void testSimpleLog() throws BazaarException, IOException
    {
        IBazaarInfo info = null;

        XMLInfoParser parser = new XMLInfoParser();
        info = parser.parse( getContentsFrom( infoFile ) );

        org.testng.Assert.assertNotNull( info );
        org.testng.Assert.assertEquals( info.getLayout(), "Repository tree" );
        org.testng.Assert.assertEquals( (Object)info.getFormats().length, 1 );
        org.testng.Assert.assertEquals( info.getFormats()[0], "dirstate" );
        org.testng.Assert.assertEquals( info.getLocations().getSharedRepository(), "/home/guillo/bazaar/bzr" );
        org.testng.Assert.assertEquals( info.getLocations().getRepositoryBranch(), "." );
        org.testng.Assert.assertEquals( info.getRelatedBranches().getParentBranch(),
                "http://bazaar.launchpad.net/~bzr/bzr/trunk/" );
        org.testng.Assert.assertEquals( info.getControlFormat().trim(), "Meta directory format 1" );
        org.testng.Assert.assertEquals( info.getWorkingTreeFormat().trim(), "Working tree format 4" );
        org.testng.Assert.assertEquals( info.getBranchFormat().trim(), "Branch format 5" );
        org.testng.Assert.assertEquals( info.getRepositoryFormat().trim(), "Knit repository format 1" );
        org.testng.Assert.assertEquals( (Object)info.getWorkingTreeStats().getUnchanged().intValue(), 735 );
        org.testng.Assert.assertEquals( (Object)info.getWorkingTreeStats().getModified().intValue(), 0 );
        org.testng.Assert.assertEquals( (Object)info.getWorkingTreeStats().getAdded().intValue(), 0 );
        org.testng.Assert.assertEquals( (Object)info.getWorkingTreeStats().getRemoved().intValue(), 0 );
        org.testng.Assert.assertEquals( (Object)info.getWorkingTreeStats().getRenamed().intValue(), 0 );
        org.testng.Assert.assertEquals( (Object)info.getWorkingTreeStats().getUnknown().intValue(), 3 );
        org.testng.Assert.assertEquals( (Object)info.getWorkingTreeStats().getIgnored().intValue(), 73 );
        org.testng.Assert.assertEquals( (Object)info.getWorkingTreeStats().getVersionedSubdirectories().intValue(), 58 );
        org.testng.Assert.assertEquals( (Object)info.getBranchHistory().getRevisions().intValue(), 2866 );
        org.testng.Assert.assertEquals( (Object)info.getBranchHistory().getCommitters().intValue(), 108 );
        org.testng.Assert.assertEquals( (Object)info.getBranchHistory().getDaysOld().intValue(), 824 );
        //TODO: date assertions

        org.testng.Assert.assertEquals( (Object)info.getRepositoryStats().getRevisionCount().intValue(), 13764 );
        org.testng.Assert.assertEquals( (Object)info.getRepositoryStats().getSize().longValue(), 63614L );
    }

}
