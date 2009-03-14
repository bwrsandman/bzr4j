/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import java.io.File;

import org.emergent.bzr4j.commandline.syntax.IStatusOptions;

/**
 * @author Guillermo Gonzalez
 *
 */
public class Status extends MultiFileCommand implements IStatusOptions
{

    public Status( final File workDir, final File[] files )
    {
        super( workDir, files );
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
