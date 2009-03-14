/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands.options;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * BaseOption subtype for local options that vary from command to command.
 *
 * @basedOn Options system in org.eclipse.team.core
 * @author Guillermo Gonzalez
 */
public class Option extends BaseOption
{

    public Option( String option )
    {
        super( option );
    }

    public Option[] addTo( Option[] options )
    {
        // ensure natural ordering of the array
        Arrays.sort( options );
        if ( Arrays.binarySearch( options, (Option)this ) <= 0 )
        {
            Option[] newOptions = new Option[options.length + 1];
            System.arraycopy( options, 0, newOptions, 0, options.length );
            newOptions[options.length] = (Option)this;
            return newOptions;
        }
        else
        {
            return options;
        }
    }

    /**
     * Remove this option from the specified array.
     * if this option isn't in the array, the same array is returned;
     *
     * @param options
     * @return a Option[] without this option in it
     */
    public Option[] removeFrom( final Option[] options )
    {
        // ensure natural ordering of the array
        Arrays.sort( options );
        if ( Arrays.binarySearch( options, this ) <= 0 )
        {
            return options;
        }
        else
        {
            final List<Option> newOptions = new ArrayList<Option>();
            for ( Option option : options )
            {
                if ( !option.equals( this ) )
                {
                    newOptions.add( option );
                }
            }
            return newOptions.toArray( new Option[newOptions.size()] );
        }
    }

}
