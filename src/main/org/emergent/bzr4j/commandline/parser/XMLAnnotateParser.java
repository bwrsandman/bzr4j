/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.parser;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.emergent.bzr4j.utils.LogUtil;

import java.util.logging.Logger;

/**
 * I'm a parser for the output generated by the command: xmlannotate (using
 * bzr-xmloutput plugin)
 *
 * @author Guillermo Gonzalez
 *
 */
public class XMLAnnotateParser extends XMLParser
{

    private final static LogUtil LOGGER = LogUtil.getLogger( XMLAnnotateParser.class.getName() );

    // List<IBazaarAnnotation> lines;
    private final List<String> revisionByLine = new ArrayList<String>();

    private final List<String> authorByLine = new ArrayList<String>();

    private final List<String> dateByLine = new ArrayList<String>();

    private final List<String> fidByLine = new ArrayList<String>();

    private final List<String> lines = new ArrayList<String>();

    File branchRoot;

    File file;

    private static final String ANNOTATE = "annotation";

    private static final String FILE = "file";

    private static String BRANCH_ROOT = "workingtree-root";

    private static final String ENTRY = "entry";

    private static final String REVISION = "revno";

    private static final String FID = "fid";

    private static final String AUTHOR = "author";

    private static final String DATE = "date";

    public void parse( String xml ) throws XmlPullParserException, IOException
    {
        parser = new KXmlParser();
        try
        {
            parser.setInput( new StringReader( xml ) );
            int eventType = parser.getEventType();
            while ( eventType != XmlPullParser.END_DOCUMENT )
            {
                if ( eventType == XmlPullParser.START_TAG && ANNOTATE.equals( parser.getName() ) )
                {
                    branchRoot = new File( parser.getAttributeValue( null, BRANCH_ROOT ) );
                    file = new File( parser.getAttributeValue( null, FILE ) );
                    parseAnnotate();
                }
                eventType = parser.next();
            }
        }
        catch ( XmlPullParserException e )
        {
            LOGGER.error( e.getMessage(), e );
            throw e;
        }
        catch ( IOException e )
        {
            LOGGER.error( e.getMessage(), e );
            throw e;
        }
    }

    /**
     * @throws IOException
     * @throws XmlPullParserException
     *
     */
    private void parseAnnotate() throws XmlPullParserException, IOException
    {
        revisionByLine.clear(); // this seems to be the intention here (to clear the list.)
        authorByLine
                .clear();   // re-instantiating had the same end effect. without the implicit clarity on why it was done.
        dateByLine.clear();
        lines.clear();
        fidByLine.clear();
        String revno, fid, author, date, line;
        int eventType = parser.next();
        // while (eventType != XmlPullParser.END_DOCUMENT &&
        // group.equals(parser.getName())) {
        while ( eventType != XmlPullParser.END_DOCUMENT || (eventType == XmlPullParser.END_TAG
                && ANNOTATE.equals( parser.getName() )) )
        {
            if ( eventType == XmlPullParser.START_TAG && ENTRY.equals( parser.getName() ) )
            {
                revno = fid = author = date = line = null;
                for ( int i = 0; i < parser.getAttributeCount(); i++ )
                {
                    if ( REVISION.equals( parser.getAttributeName( i ) ) )
                    {
                        revno = parser.getAttributeValue( i );
                    }
                    else if ( FID.equals( parser.getAttributeName( i ) ) )
                    {
                        fid = parser.getAttributeValue( i );
                    }
                    else if ( AUTHOR.equals( parser.getAttributeName( i ) ) )
                    {
                        author = parser.getAttributeValue( i );
                    }
                    else if ( DATE.equals( parser.getAttributeName( i ) ) )
                    {
                        date = parser.getAttributeValue( i );
                    }
                }
                line = parser.nextText();
                revisionByLine.add( revno );
                fidByLine.add( fid );
                authorByLine.add( author );
                dateByLine.add( date );
                lines.add( line );
            }
            eventType = parser.next();
        }
    }

    public String[] getAuthors()
    {
        return authorByLine.toArray( new String[0] );
    }

    public String[] getDates()
    {
        return dateByLine.toArray( new String[0] );
    }

    public String[] getFileIds()
    {
        return fidByLine.toArray( new String[0] );
    }

    public String[] getRevisions()
    {
        return revisionByLine.toArray( new String[0] );
    }

    public String[] getLines()
    {
        return lines.toArray( new String[0] );
    }

    public File getFile()
    {
        return new File( branchRoot, file.getPath() );
    }

    public File getBranchRoot()
    {
        return branchRoot;
    }
}
