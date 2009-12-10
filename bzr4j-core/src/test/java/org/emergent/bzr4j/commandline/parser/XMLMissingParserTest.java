/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.parser;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.emergent.bzr4j.core.IBazaarLogMessage;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.testUtils.ParserTest;
import org.emergent.bzr4j.utils.StringUtil;
import org.testng.annotations.Test;

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

        org.testng.Assert.assertNotNull( missing );
        org.testng.Assert.assertEquals( (Object)missing.size(), 2 );
        List<IBazaarLogMessage> other = missing.get( "OTHER" );
        List<IBazaarLogMessage> mine = missing.get( "MINE" );
        org.testng.Assert.assertNotNull( mine );
        org.testng.Assert.assertEquals( mine.size(), 1, "extra revisions size diffier: " );
        org.testng.Assert.assertNotNull( other );
        org.testng.Assert.assertEquals( other.size(), 1, "missing revisions size diffier: " );
        for ( IBazaarLogMessage logMessage : mine )
        {
            org.testng.Assert.assertEquals( logMessage.getBranchNick(), "quickdiff-integration" );
            org.testng.Assert.assertEquals( logMessage.getCommiter().trim(), "Guillermo Gonzalez <antiSpam@mail.com>" );
            org.testng.Assert.assertEquals( logMessage.getRevision().getValue(), "116" );
            org.testng.Assert.assertEquals( logMessage.getTimeStamp(), "Fri 2007-12-21 19:34:45 -0300" );
            org.testng.Assert.assertEquals( logMessage.getDate(), StringUtil.parseLogDate( "Fri 2007-12-21 19:34:45 -0300" ) );
            org.testng.Assert.assertNotNull( logMessage.getMerged(), null );
            org.testng.Assert.assertEquals( (Object)logMessage.getMerged().size(), 0 );
        }
        for ( IBazaarLogMessage logMessage : other )
        {
            org.testng.Assert.assertEquals( logMessage.getBranchNick(), "quickdiff-integration" );
            org.testng.Assert.assertEquals( logMessage.getCommiter().trim(), "Guillermo Gonzalez <antiSpam@mail.com>" );
            org.testng.Assert.assertEquals( logMessage.getRevision().getValue(), "116" );
            org.testng.Assert.assertEquals( logMessage.getTimeStamp(), "Fri 2007-12-21 19:34:45 -0300" );
            org.testng.Assert.assertEquals( logMessage.getDate(), StringUtil.parseLogDate( "Fri 2007-12-21 19:34:45 -0300" ) );
            org.testng.Assert.assertNotNull( logMessage.getMerged(), null );
            org.testng.Assert.assertEquals( (Object)logMessage.getMerged().size(), 0 );
        }
    }
}
