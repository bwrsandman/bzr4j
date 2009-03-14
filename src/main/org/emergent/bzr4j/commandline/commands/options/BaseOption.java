/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands.options;

/**
 * Superclass for all bzr command options
 *
 * @basedOn Options system in org.eclipse.team.core
 * @modified *
 * @author Guillermo Gonzalez
 */
public abstract class BaseOption
{

    protected final String option;

    public BaseOption( String option )
    {
        this.option = option;
    }

    /**
     * Returns the option part of this option
     */
    public String getOption()
    {
        return option;
    }

    /**
     * Compares two options for equality.
     *
     * @param other
     *            the other option
     */
    public boolean equals( Object other )
    {
        if ( this == other )
            return true;
        if ( other instanceof BaseOption )
        {
            BaseOption otherOption = (BaseOption)other;
            return option.equals( otherOption.option );
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((option == null) ? 0 : option.hashCode());
        return result;
    }

    /*
         * To make debugging a tad easier.
         */
    public String toString()
    {
        return option;
    }
}
