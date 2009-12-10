/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.parser;

import java.io.IOException;
import java.text.ParseException;
import java.util.Set;

import org.emergent.bzr4j.core.IPlugin;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.testUtils.ParserTest;
import org.testng.annotations.Test;

/**
 * @author Guillermo Gonzalez
 *
 */
public class XMLPluginParserTest extends ParserTest
{

    @Test
    public void testParse() throws ParseException, BazaarException, IOException
    {
        XMLPluginParser parser = new XMLPluginParser();
        Set<IPlugin> plugins = parser.parse( getContentsFrom( pluginsFile ) );

        org.testng.Assert.assertNotNull( plugins );
        org.testng.Assert.assertEquals( (Object)plugins.size(), 2 );
        for ( IPlugin plugin : plugins )
        {
            if ( "xmloutput".equals( plugin.getName() ) )
            {
                org.testng.Assert.assertEquals( plugin.getName(), "xmloutput" );
                Object[] expected = new String[]{"0", "4", "2"};
                org.testng.Assert.assertEquals( plugin.getVersion(), expected );
                org.testng.Assert.assertEquals( plugin.getPath(), "/Users/guillermo/.bazaar/plugins/xmloutput" );
                org.testng.Assert.assertEquals( plugin.getDescription(),
                        "This plugin provides xml output for status, log, annotate,\n"
                                + "			missing, info, version and plugins adding a --xml option to\n"
                                + "			each" );
            }
            else if ( "launchpad".equals( plugin.getName() ) )
            {
                org.testng.Assert.assertEquals( plugin.getName(), "launchpad" );
                Object[] expected = new String[]{"unknown",};
                org.testng.Assert.assertEquals( plugin.getVersion(), expected );
                org.testng.Assert.assertEquals( plugin.getPath(),
                        "/Users/guillermo/Projects/bazaar/bzr/dev/bzrlib/plugins/launchpad" );
                org.testng.Assert.assertEquals( plugin.getDescription(), "Launchpad.net integration plugin for Bazaar." );
            }
            else
            {
                org.testng.Assert.fail( "I have a unexpected plugin: " + plugin.getName() );
            }
        }
    }

    @Test
    public void testParseMissingDocTag() throws ParseException, BazaarException, IOException
    {
        XMLPluginParser parser = new XMLPluginParser();
        Set<IPlugin> plugins = parser.parse( getContentsFrom( pluginsFileNoDoc ) );
        org.testng.Assert.assertNotNull( plugins );
        org.testng.Assert.assertEquals( (Object)plugins.size(), 3 );
    }
}
