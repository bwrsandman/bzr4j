package org.emergent.bzr4j.utils;

import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Patrick Woodworth
 */
public class BzrConstants
{
    private static final Properties sm_props = new Properties();

    static
    {
        InputStream is = null;
        try
        {
            is = BzrConstants.class.getResourceAsStream( "bzr4j.properties" );
            sm_props.load( is );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (is != null) try { is.close(); } catch (Exception ignored) { }
        }
    }

    public static final String VERSION = sm_props.getProperty( "version", "UNKNOWN" );

    public static final boolean DEBUG = getBoolean( "bzr4j.debug", false );

    public static final boolean EXEC_WITH_SHELL = getBoolean( "bzr4j.exe.useshell", false );

    public static final String EXE_PATH = System.getProperty( "bzr4j.exe.path");

    public static final boolean EXEC_LOG_MULTILINE_ARGS = getBoolean( "bzr4j.exec_log_multiline_args", false );

    public static boolean getBoolean( String key, boolean def )
    {
        return Boolean.parseBoolean( System.getProperty( key, def ? "true" : "false" ) );
    }
}
