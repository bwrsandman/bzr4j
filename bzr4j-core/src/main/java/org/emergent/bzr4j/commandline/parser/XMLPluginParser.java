/**
 *
 */
package org.emergent.bzr4j.commandline.parser;

import org.emergent.bzr4j.commandline.CommandLinePlugin;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.IPlugin;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

/**
 * I'm a parser for the output of: 'bzr plugins --xml' command. <br>
 *
 * @author Guillermo Gonzalez <guillo.gonzo AT gmail DOT com> 
 *
 */
public class XMLPluginParser extends XMLParser
{

    private static final String PLUGINS = "plugins"; //$NON-NLS-1$

    private static final String PLUGIN = "plugin"; //$NON-NLS-1$

    private final static String NAME = "name";

    private final static String VERSION = "version";

    private final static String PATH = "path";

    private final static String DOC = "doc";

    private Set<IPlugin> plugins;

    public Set<IPlugin> parse( String xml ) throws BazaarException
    {
        parser = new KXmlParser();
        plugins = new HashSet<IPlugin>();
        try
        {
            parser.setInput( new StringReader( xml ) );
            parser.nextTag();
            parser.require( KXmlParser.START_TAG, null, PLUGINS );
            while ( parser.nextTag() == XmlPullParser.START_TAG )
            {
                parser.require( KXmlParser.START_TAG, null, PLUGIN );
                parser.nextTag();
                parser.require( KXmlParser.START_TAG, null, NAME );
                String name = parser.nextText();
                parser.nextTag();
                parser.require( KXmlParser.START_TAG, null, VERSION );
                String version = parser.nextText();
                parser.nextTag();
                parser.require( KXmlParser.START_TAG, null, PATH );
                String path = parser.nextText();
                parser.nextTag();
                String doc = null;
                try
                {
                    parser.require( KXmlParser.START_TAG, null, DOC );
                    doc = parser.nextText();
                    parser.nextTag();
                }
                catch ( XmlPullParserException e ) {}
                plugins.add( new CommandLinePlugin( doc != null ? doc.trim() : null,
                        name != null ? name.trim() : null, path != null ? path.trim() : null,
                        version != null ? version.trim() : null ) );
            }
//			parser.require(KXmlParser.END_TAG, null, PLUGINS);
        }
        catch ( XmlPullParserException e )
        {
            throw new BazaarException( e );
        }
        catch ( IOException e )
        {
            throw new BazaarException( e );
        }
        return plugins;
    }

}
