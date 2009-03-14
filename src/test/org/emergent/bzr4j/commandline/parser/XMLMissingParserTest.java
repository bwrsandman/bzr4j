/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.parser;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.emergent.bzr4j.core.IBazaarLogMessage;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.testUtils.ParserTest;
import org.emergent.bzr4j.utils.StringUtil;

/**
 * @author Guillermo Gonzalez
 *
 */
public class XMLMissingParserTest extends ParserTest
{

    @Test
    public void testSimpleStatus() throws ParseException, BazaarException, IOException
    {
        Map<String, List<IBazaarLogMessage>> missing = null;

        XMLMissingParser missingParser = new XMLMissingParser();
        missing = missingParser.parse( getContentsFrom( missingFile ) );

        Assert.assertNotNull( missing );
        Assert.assertEquals( 2, missing.size() );
        List<IBazaarLogMessage> other = missing.get( "OTHER" );
        List<IBazaarLogMessage> mine = missing.get( "MINE" );
        Assert.assertNotNull( mine );
        Assert.assertEquals( "extra revisions size diffier: ", 1, mine.size() );
        Assert.assertNotNull( other );
        Assert.assertEquals( "missing revisions size diffier: ", 1, other.size() );
        for ( IBazaarLogMessage logMessage : mine )
        {
            Assert.assertEquals( "quickdiff-integration", logMessage.getBranchNick() );
            Assert.assertEquals( "Guillermo Gonzalez <antiSpam@mail.com>",
                    logMessage.getCommiter().trim() );
            Assert.assertEquals( "116", logMessage.getRevision().getValue() );
            Assert.assertEquals( "Fri 2007-12-21 19:34:45 -0300", logMessage.getTimeStamp() );
            Assert.assertEquals( StringUtil.parseLogDate( "Fri 2007-12-21 19:34:45 -0300" ),
                    logMessage.getDate() );
            Assert.assertNotNull( null, logMessage.getMerged() );
            Assert.assertEquals( 0, logMessage.getMerged().size() );
        }
        for ( IBazaarLogMessage logMessage : other )
        {
            Assert.assertEquals( "quickdiff-integration", logMessage.getBranchNick() );
            Assert.assertEquals( "Guillermo Gonzalez <antiSpam@mail.com>",
                    logMessage.getCommiter().trim() );
            Assert.assertEquals( "116", logMessage.getRevision().getValue() );
            Assert.assertEquals( "Fri 2007-12-21 19:34:45 -0300", logMessage.getTimeStamp() );
            Assert.assertEquals( StringUtil.parseLogDate( "Fri 2007-12-21 19:34:45 -0300" ),
                    logMessage.getDate() );
            Assert.assertNotNull( null, logMessage.getMerged() );
            Assert.assertEquals( 0, logMessage.getMerged().size() );
        }
    }
}
