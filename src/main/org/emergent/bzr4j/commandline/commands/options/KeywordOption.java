/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands.options;

/**
 * @basedOn Options system in org.eclipse.team.core
 * @author Guillermo Gonzalez
 *
 */
public class KeywordOption extends Option
{
    private String argument;

    public KeywordOption( String option )
    {
        super( option );
        this.argument = null;
    }

    public KeywordOption( String option, String argument )
    {
        super( option );
        this.argument = argument;
    }

    /**
     * clone this option and set the specified argument.
     *
     * @param argument
     * @return a cloned Option with the specified argument set.
     */
    public KeywordOption with( String argument )
    {
        final KeywordOption newOption = new KeywordOption( getOption() );
        newOption.argument = argument;
        return newOption;
    }

    public KeywordOption setArgument( String argument )
    {
        this.argument = argument;
        return this;
    }

    public String toString()
    {
        if ( argument != null && argument.length() != 0 )
        {
            return option + "=" + argument; //$NON-NLS-1$
            // return option + "=\"" + argument + '"'; //$NON-NLS-1$
        }
        else
        {
            return option + "=?";
        }
    }

    public String getArgument()
    {
        return argument;
    }
}
