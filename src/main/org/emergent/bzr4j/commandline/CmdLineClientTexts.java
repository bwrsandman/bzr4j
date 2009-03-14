/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 *
 */
public class CmdLineClientTexts
{

    private static final String BUNDLE_NAME =
            CmdLineClientTexts.class.getPackage().getName() + ".cmdLineClient"; //$NON-NLS-1$

    private static ResourceBundle bundle = null;

    private static ResourceBundle getResourceBundle()
    {
        if ( bundle == null )
        {
            bundle = ResourceBundle.getBundle( BUNDLE_NAME );
        }
        return bundle;
    }

    /**
     * Lookup the message with the given ID in this catalog and bind its
     * substitution locations with the given string.
     *
     * @param id
     * @param binding
     * @return the message with substitutions applied
     */
    public static String bind( String id, String binding )
    {
        return bind( id, new String[]{binding} );
    }

    /**
     * Lookup the message with the given ID in this catalog and bind its
     * substitution locations with the given strings.
     *
     * @param id
     * @param binding1
     * @param binding2
     * @return the message with substitutions applied
     */
    public static String bind( String id, String binding1, String binding2 )
    {
        return bind( id, new String[]{binding1, binding2} );
    }

    /**
     * Gets a string from the resource bundle. We don't want to crash because of
     * a missing String.
     *
     * @param key
     * @return string from the resource bundle or the key if not found.
     */
    public static String bind( String key )
    {
        try
        {
            return getResourceBundle().getString( key );
        }
        catch ( MissingResourceException e )
        {
            return key;
        }
        catch ( NullPointerException e )
        {
            return "!" + key + "!"; //$NON-NLS-1$  //$NON-NLS-2$
        }
    }

    /**
     * Gets a string from the resource bundle and binds it with the given
     * arguments. If the key is not found, return the key.
     *
     * @param key
     * @param args
     * @return string with substitutions from the resource bundle or the key if
     *         not found.
     */
    public static String bind( String key, Object[] args )
    {
        try
        {
            return MessageFormat.format( bind( key ), args );
        }
        catch ( MissingResourceException e )
        {
            return key;
        }
        catch ( NullPointerException e )
        {
            return "!" + key + "!"; //$NON-NLS-1$  //$NON-NLS-2$
        }
    }
}
