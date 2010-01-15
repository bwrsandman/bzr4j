/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import org.emergent.bzr4j.commandline.syntax.INickOptions;

import java.io.File;
import java.util.List;

/**
 * @author Guillermo Gonzalez
 *
 */
public class Nick extends SingleFileCommand implements INickOptions
{

    private final String newNick;

    /**
     * This constructor is for setting the nick
     *
     * @param project
     */
    public Nick( final File workDir, final String newNick )
    {
        super( workDir, null );
        this.newNick = newNick;
    }

    /**
     * This constructor is for getting the nick
     *
     * @param project
     */
    public Nick( final File workDir )
    {
        super( workDir, null );
        this.newNick = null;
    }

    /*
         * (non-Javadoc)
         *
         * @see org.emergent.bzr4j.core.commands.Command#getArguments()
         */
    @Override
    protected List<String> getArguments()
    {
        return getArguments( newNick );
    }

    /*
         * (non-Javadoc)
         *
         * @see org.emergent.bzr4j.core.commands.Command#getCommand()
         */
    @Override
    public String getCommand()
    {
        return COMMAND;
    }

}
