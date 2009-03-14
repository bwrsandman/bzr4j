package org.emergent.bzr4j.commandline.parser;

import org.emergent.bzr4j.core.BazaarItemInfo;
import org.emergent.bzr4j.core.BazaarItemKind;
import org.emergent.bzr4j.core.BazaarStatusKind;
import org.emergent.bzr4j.core.IBazaarItemInfo;
import org.emergent.bzr4j.utils.LogUtil;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class XMLLsParser extends XMLParser
{

    private final static LogUtil LOGGER = LogUtil.getLogger( XMLLsParser.class.getName() );

    private final static String LIST = "list";

    private final static String KIND = "kind";

    private final static String PATH = "path";

    private final static String ID = "id";

    private final static String ITEM = "item";

    private final static String STATUS_KIND = "status_kind";

    public static List<IBazaarItemInfo> parse( String xml )
    {
        KXmlParser parser = new KXmlParser();
        try
        {
            parser.setInput( new StringReader( xml ) );
            return new XMLLsParser().parse( parser );
        }
        catch ( XmlPullParserException e )
        {
            LOGGER.error( e.getMessage(), e );
        }
        return new ArrayList<IBazaarItemInfo>( 0 );
    }

    private List<IBazaarItemInfo> parse( KXmlParser parser )
    {
        this.parser = parser;

        List<IBazaarItemInfo> result = new ArrayList<IBazaarItemInfo>();

        try
        {
            int eventType = parser.getEventType();

            while ( eventType != XmlPullParser.END_DOCUMENT )
            {

                if ( eventType == XmlPullParser.START_TAG && !parser.getName().equals( LIST )
                        && parser.getName().equals( ITEM ) )
                {
                    IBazaarItemInfo item = parseItem();
                    if ( item != null )
                    {
                        result.add( item );
                    }
                }

                eventType = parser.next();
            }
        }
        catch ( XmlPullParserException e )
        {
            LOGGER.error( e.getMessage(), e );
        }
        catch ( IOException e )
        {
            LOGGER.error( e.getMessage(), e );
        }
        return result;
    }

    private IBazaarItemInfo parseItem() throws XmlPullParserException, IOException
    {

        BazaarItemKind kind = null;
        String id = null;
        String path = null;
        BazaarStatusKind type = null;

        int eventType = parser.getEventType();
        do
        {
            switch ( eventType )
            {
                case XmlPullParser.START_TAG:
                    if ( KIND.equals( parser.getName() ) )
                    {
                        kind = BazaarItemKind.fromString( parser.nextText() );
                    }
                    if ( PATH.equals( parser.getName() ) )
                    {
                        path = parser.nextText();
                    }
                    if ( ID.equals( parser.getName() ) )
                    {
                        id = parser.nextText();
                    }
                    if ( STATUS_KIND.equals( parser.getName() ) )
                    {
                        type = BazaarStatusKind.fromString( parser.nextText() );
                    }
                    break;
            }
        }
        while ( !(XmlPullParser.END_TAG == (eventType = parser.next()) && ITEM
                .equals( parser.getName() )) );

        return new BazaarItemInfo( kind, id, path, type );

    }

}
