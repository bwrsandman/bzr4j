/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.parser;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.text.ParseException;
import java.util.Set;

import org.junit.Test;
import org.emergent.bzr4j.core.IPlugin;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.testUtils.ParserTest;

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

        assertNotNull( plugins );
        assertEquals( 2, plugins.size() );
        for ( IPlugin plugin : plugins )
        {
            if ( "xmloutput".equals( plugin.getName() ) )
            {
                assertEquals( "xmloutput", plugin.getName() );
                assertArrayEquals( new String[]{"0", "4", "2"}, plugin.getVersion() );
                assertEquals( "/Users/guillermo/.bazaar/plugins/xmloutput", plugin.getPath() );
                assertEquals( "This plugin provides xml output for status, log, annotate,\n"
                        + "			missing, info, version and plugins adding a --xml option to\n"
                        + "			each", plugin.getDescription() );
            }
            else if ( "launchpad".equals( plugin.getName() ) )
            {
                assertEquals( "launchpad", plugin.getName() );
                assertArrayEquals( new String[]{"unknown",}, plugin.getVersion() );
                assertEquals( "/Users/guillermo/Projects/bazaar/bzr/dev/bzrlib/plugins/launchpad",
                        plugin.getPath() );
                assertEquals( "Launchpad.net integration plugin for Bazaar.",
                        plugin.getDescription() );
            }
            else
            {
                fail( "I have a unexpected plugin: " + plugin.getName() );
            }
        }
    }

    @Test
    public void testParseMissingDocTag() throws ParseException, BazaarException, IOException
    {
        XMLPluginParser parser = new XMLPluginParser();
        Set<IPlugin> plugins = parser.parse( getContentsFrom( pluginsFileNoDoc ) );
        assertNotNull( plugins );
        assertEquals( 3, plugins.size() );
    }
}
