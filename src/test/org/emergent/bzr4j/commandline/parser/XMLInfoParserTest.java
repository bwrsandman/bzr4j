/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.parser;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.emergent.bzr4j.core.IBazaarInfo;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.testUtils.ParserTest;

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

        Assert.assertNotNull( info );
        Assert.assertEquals( "Repository tree", info.getLayout() );
        Assert.assertEquals( 1, info.getFormats().length );
        Assert.assertEquals( "dirstate", info.getFormats()[0] );
        Assert.assertEquals( "/home/guillo/bazaar/bzr", info.getLocations().getSharedRepository() );
        Assert.assertEquals( ".", info.getLocations().getRepositoryBranch() );
        Assert.assertEquals( "http://bazaar.launchpad.net/~bzr/bzr/trunk/",
                info.getRelatedBranches().getParentBranch() );
        Assert.assertEquals( "Meta directory format 1", info.getControlFormat().trim() );
        Assert.assertEquals( "Working tree format 4", info.getWorkingTreeFormat().trim() );
        Assert.assertEquals( "Branch format 5", info.getBranchFormat().trim() );
        Assert.assertEquals( "Knit repository format 1", info.getRepositoryFormat().trim() );
        Assert.assertEquals( 735, info.getWorkingTreeStats().getUnchanged().intValue() );
        Assert.assertEquals( 0, info.getWorkingTreeStats().getModified().intValue() );
        Assert.assertEquals( 0, info.getWorkingTreeStats().getAdded().intValue() );
        Assert.assertEquals( 0, info.getWorkingTreeStats().getRemoved().intValue() );
        Assert.assertEquals( 0, info.getWorkingTreeStats().getRenamed().intValue() );
        Assert.assertEquals( 3, info.getWorkingTreeStats().getUnknown().intValue() );
        Assert.assertEquals( 73, info.getWorkingTreeStats().getIgnored().intValue() );
        Assert.assertEquals( 58,
                info.getWorkingTreeStats().getVersionedSubdirectories().intValue() );
        Assert.assertEquals( 2866, info.getBranchHistory().getRevisions().intValue() );
        Assert.assertEquals( 108, info.getBranchHistory().getCommitters().intValue() );
        Assert.assertEquals( 824, info.getBranchHistory().getDaysOld().intValue() );
        //TODO: date assertions

        Assert.assertEquals( 13764, info.getRepositoryStats().getRevisionCount().intValue() );
        Assert.assertEquals( 63614L, info.getRepositoryStats().getSize().longValue() );
    }

}
