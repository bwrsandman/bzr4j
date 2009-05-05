package org.emergent.bzr4j.utils;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Patrick Woodworth
 */
@SuppressWarnings({"ThrowableInstanceNeverThrown"})
public class LogUtil
{
    public static final Logger LOG = Logger.getLogger( "org.emergent.bzr4j" );

    private static LogUtil sm_instance = new LogUtil();

    public static String getFileHandlePattern()
    {
        String fhpattern = System.getProperty( "teamcity_logs" );
        if (fhpattern == null)
        {
            fhpattern = System.getProperty( "idea.system.path" );
            if (fhpattern != null)
            {
                if (!fhpattern.endsWith( "/" ))
                    fhpattern += "/";
                fhpattern += "log/bzr4j.%g.log";
            }
        }
        else
        {
            if (!fhpattern.endsWith( "/" ))
                fhpattern += "/";
            fhpattern += "bzr4j.%g.log";
        }

        return fhpattern;
    }

    public static void dumpImportantData( Properties ijprops )
    {
        if (!Boolean.getBoolean( "bzr4j.logging.disable_autoconf" ))
        {
            try
            {
                String fhpattern = getFileHandlePattern();

                if (fhpattern != null)
                {
                    FileHandler fh = new FileHandler( fhpattern, 100 * 1024 * 1024, 2, true );
//                    fh.setFormatter( new SimpleFormatter() );
                    PatternFormatter patForm = new PatternFormatter();
                    patForm.setTimeFormat( "yyyy-MM-dd HH:mm:ss" );
                    patForm.setLogPattern( "%TIME% - %LEVEL% -- %SOURCECLASS%#%SOURCEMETHOD%: %MESSAGE%\n" );
                    patForm.setExceptionPattern( "%TIME% - %LEVEL% -- %MESSAGE% \nException in %SOURCECLASS%: %EXCEPTION% \n%STACKTRACE%" );
//                    patForm.setLogPattern( "%2s - %1s -- %4s#%5s: %3s\n" );
                    fh.setFormatter( patForm );
                    LOG.addHandler( fh );
                    LOG.setLevel( Level.parse( System.getProperty( "bzr4j.logging.level", "ALL" ) ));
                    Properties debugProps = new Properties();
                    if (ijprops != null)
                        debugProps.putAll( ijprops );
                    debugProps.setProperty( "bzr4j.version", BzrConstants.VERSION );
                    String sysPropDump = BzrUtil.dumpSystemProperties( System.getProperties(), "system" );
                    LOG.fine( sysPropDump );
                    LOG.info( BzrUtil.dumpSystemProperties( debugProps, "application" ) );
                }
            }
            catch ( IOException e)
            {
                e.printStackTrace();
            }
        }
    }



    public static LogUtil getLogger( String name )
    {
        return sm_instance;
    }

    public static LogUtil getLogger( Class clazz )
    {
        return sm_instance;
    }

    public void debug( String msg )
    {
        LOG.fine( msg );
    }

    public void error( Object msg, Throwable e )
    {
        LOG.log( Level.SEVERE, String.valueOf(msg), e );
    }

    public void info( Object msg )
    {
        LOG.info( String.valueOf( msg ) );
    }

    public void warn( Object msg )
    {
        LOG.log( Level.WARNING, String.valueOf( msg ) );
    }

    public void warn( Object msg, Throwable e )
    {
        LOG.log( Level.WARNING, String.valueOf( msg ), e );
    }

    public void fine( Object msg )
    {
        LOG.log( Level.FINE, String.valueOf( msg ) );
    }

    public boolean isDebugEnabled()
    {
        return LOG.isLoggable( Level.FINE );
    }

    public void debug( Throwable e )
    {
        LOG.log( Level.FINE, e.getMessage(), e );
    }
}
