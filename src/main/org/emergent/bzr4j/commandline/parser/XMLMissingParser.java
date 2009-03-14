/**
 *
 */
package org.emergent.bzr4j.commandline.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kxml2.io.KXmlParser;
import org.emergent.bzr4j.core.IBazaarLogMessage;
import org.emergent.bzr4j.core.BazaarException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * @author Guillermo Gonzalez <guillo.gonzo@gmail.com>
 *
 */
public class XMLMissingParser extends XMLParser
{

    private static final String OTHER = "OTHER"; //$NON-NLS-1$

    private static final String MINE = "MINE"; //$NON-NLS-1$

    private final static String MISSING = "missing";

    private final static String LAST_LOCATION = "last_location";

    private final static String MISSING_REVISIONS = "missing_revisions";

    private final static String EXTRA_REVISIONS = "extra_revisions";

    public Map<String, List<IBazaarLogMessage>> parse( String xml ) throws BazaarException
    {
        final Map<String, List<IBazaarLogMessage>> missingOutput =
                new HashMap<String, List<IBazaarLogMessage>>( 2 );
        parser = new KXmlParser();
        try
        {
            parser.setInput( new StringReader( xml ) );
            parser.nextTag();
            parser.require( KXmlParser.START_TAG, null, MISSING );
            parser.nextTag();
            parser.require( KXmlParser.START_TAG, null, LAST_LOCATION );
            parser.nextText();// last location
            parser.nextTag();
            try
            {
                parser.require( KXmlParser.START_TAG, null, EXTRA_REVISIONS );
                missingOutput.put( MINE, parseLogs( EXTRA_REVISIONS ) );
            }
            catch ( XmlPullParserException e )
            {
                // do nothing, we don't have any new revisions
            }
            parser.nextTag(); // end_tag <logs>
            parser.nextTag();
            try
            {
                parser.require( KXmlParser.START_TAG, null, MISSING_REVISIONS );
                missingOutput.put( OTHER, parseLogs( MISSING_REVISIONS ) );
            }
            catch ( XmlPullParserException e )
            {
                // do nothing, the other branch don't have any new revisions
            }

        }
        catch ( XmlPullParserException e )
        {
            throw new BazaarException( e );
        }
        catch ( IOException e )
        {
            throw new BazaarException( e );
        }
        return missingOutput;
    }

    private List<IBazaarLogMessage> parseLogs( final String section ) throws BazaarException
    {
        // more efficient to reference a stack variable(within a method) instead of a class variable. everytime you do an add etc.
        final List<IBazaarLogMessage> logs = new ArrayList<IBazaarLogMessage>();
        final XMLLogParser logParser = new XMLLogParser();
        logParser.parser = parser;
        try
        {
            int eventType = parser.nextTag();
            // iterate over all tags (actually only care about first level <log/> tags)
            while ( eventType != XmlPullParser.END_TAG && !section.equals( parser.getName() ) )
            {
                if ( eventType == XmlPullParser.START_TAG && XMLLogParser.LOG
                        .equals( parser.getName() ) )
                {
                    IBazaarLogMessage log = logParser.parseLog();
                    if ( log != null )
                    {
                        logs.add( log );
                    }
                }
                eventType = parser.next();
            }
        }
        catch ( XmlPullParserException e )
        {
            throw new BazaarException( e );
        }
        catch ( IOException e )
        {
            throw new BazaarException( e );
        }
        return logs;
    }
}
