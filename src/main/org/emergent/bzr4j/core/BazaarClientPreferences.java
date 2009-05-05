/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.core;

import org.emergent.bzr4j.utils.BzrConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * A singleton for handling some preferences and environment variables for bzr
 * executable
 *
 * @author Guillermo Gonzalez
 *
 * TODO: need test (and check if this are all the available env variables)
 */
public final class BazaarClientPreferences
{
    private static final BazaarClientPreferences instance = new BazaarClientPreferences();

    private static final Properties sm_defaults = new Properties();

    static
    {
        sm_defaults.setProperty( BazaarPreference.EXECUTABLE.toString(), "bzr" );
    }

    private final HashMap<BazaarPreference, String> preferenceMap =
            new HashMap<BazaarPreference, String>();

    /**
     * Default constructor which load some values for the environment (if they
     * are set)
     *
     */
    private BazaarClientPreferences()
    {
        fillFrom( sm_defaults );
        for ( BazaarPreference pref : BazaarPreference.values() )
        {
            if ( getSystemEnv( pref ) != null )
            {
                preferenceMap.put( pref, getSystemEnv( pref ) );
            }
        }
    }

    /**
     * Set all the elements corresponding to preferences from the {@link Properties}
     * in the {@link BazaarClientPreferences} singleton
     *
     * Any value in the properties that is already setted is ignored.
     *
     * @param properties Properties to fill from
     */
    public void fillFrom( Properties properties )
    {
        for ( BazaarPreference pref : BazaarPreference.values() )
        {
            if ( preferenceMap.get( pref ) == null )
            {
                preferenceMap.put( pref, getValue( properties, pref ) );
            }
        }
    }

    public void setFrom( Properties properties )
    {
        for ( BazaarPreference pref : BazaarPreference.values() )
        {
            preferenceMap.put( pref, getValue( properties, pref ) );
        }
    }

    public static BazaarClientPreferences getInstance()
    {
        return instance;
    }

    /**
     * @param key The preference key.
     * @return The value of the preference
     */
    public String getString( BazaarPreference key )
    {
        return (String)preferenceMap.get( key );
    }

    /**
     *
     * @param key The preference key.
     * @param value The new value for the preference.
     */
    public final void set( BazaarPreference key, String value )
    {
        preferenceMap.put( key, value );
    }

    public final void unset( BazaarPreference key )
    {
        preferenceMap.remove( key );
    }

    /**
     * @return the executable
     */
    public static String getExecutablePath()
    {
        String exe = (BzrConstants.EXE_PATH != null)
            ? BzrConstants.EXE_PATH
            : getInstance().getString( BazaarPreference.EXECUTABLE );
        return exe;
    }


    /**
     * @return the executable
     */
    public static List<String> getExecutable()
    {
        String exe = getExecutablePath();
        List<String> output = new ArrayList<String>();
        output.add( exe );
        output.add( "--no-aliases" );
        return output;

    }

    /**
     * returns all setted preferences (except EXECUTABLE)
     *
     * @return Map<String, String>
     */
    public final Map<String, String> getPreferences()
    {
        final HashMap<String, String> newMap =
                new HashMap<String, String>( preferenceMap.size() - 1 );
        for ( BazaarPreference key : preferenceMap.keySet() )
        {
            if ( !BazaarPreference.EXECUTABLE.equals( key ) && !BazaarPreference.BZR_PROGRESS_BAR.equals( key )
                    && preferenceMap.get( key ) != null )
                newMap.put( key.name(), preferenceMap.get( key ) );
        }
        return newMap;
    }

    public final Map<BazaarPreference, String> asMap()
    {
        final HashMap<BazaarPreference, String> newMap =
                new HashMap<BazaarPreference, String>( preferenceMap.size() - 1 );
        for ( BazaarPreference key : preferenceMap.keySet() )
        {
            newMap.put( key, preferenceMap.get( key ) );
        }
        return newMap;
    }

    private static String getSystemEnv( BazaarPreference pref )
    {
        return getValue( null, pref );
    }

    private static String getValue( Properties properties, BazaarPreference pref )
    {
        if ( properties != null )
        {
            return properties.getProperty( pref.toString(), System.getProperty( pref.toString() ) );
        }
        return System.getProperty( pref.toString() );
    }

}
