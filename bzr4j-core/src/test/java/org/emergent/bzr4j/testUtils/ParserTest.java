/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.testUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Guillermo Gonzalez
 *
 */
public abstract class ParserTest
{

    protected final static String logFile = "org/emergent/bzr4j/commandline/parser/log.xml";

    protected final static String logIdsFile =
            "org/emergent/bzr4j/commandline/parser/log_showids.xml";

    protected final static String statusFile = "org/emergent/bzr4j/commandline/parser/status.xml"
            ;

    protected final static String statusWithConflictsFile =
            "org/emergent/bzr4j/commandline/parser/status_conflicts.xml";

    protected final static String statusWithPendingMergesFile =
            "org/emergent/bzr4j/commandline/parser/status_pending_merges.xml";

    protected final static String annotationFile =
            "org/emergent/bzr4j/commandline/parser/annotation.xml";

    protected final static String infoFile = "org/emergent/bzr4j/commandline/parser/info.xml";

    protected final static String missingFile =
            "org/emergent/bzr4j/commandline/parser/missing.xml";

    protected final static String pluginsFile =
            "org/emergent/bzr4j/commandline/parser/plugins.xml";

    protected final static String pluginsFileNoDoc =
            "org/emergent/bzr4j/commandline/parser/plugins_no_doc.xml";

    protected final static String errorFile = "org/emergent/bzr4j/commandline/parser/error.xml";

    protected final static String lsFile = "org/emergent/bzr4j/commandline/parser/ls.xml";

    private static BufferedReader getReaderFor( String file )
    {
        InputStream in = ParserTest.class.getClassLoader().getResourceAsStream( file );
        return new BufferedReader( new InputStreamReader( in ) );
    }

    protected static String getContentsFrom( String file ) throws IOException
    {
        final BufferedReader reader = getReaderFor( file );
        final StringBuilder contents = new StringBuilder();
        String line = null;
        try
        {
            while ( (line = reader.readLine()) != null )
            {
                contents.append( line );
                contents.append( System.getProperty( "line.separator" ) );
            }
        }
        finally
        {
            if ( reader != null )
                reader.close();
        }
        return contents.toString();
    }
}
