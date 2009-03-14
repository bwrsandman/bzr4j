/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import org.emergent.bzr4j.commandline.syntax.ILsOptions;

import java.io.File;

/**
 * @author Guillermo Gonzalez
 *
 */
public class Ls extends SingleFileCommand implements ILsOptions
{

    public Ls( final File workDir )
    {
        super( workDir, workDir );
    }

    /*
         * (non-Javadoc)
         *
         * @see org.emergent.bzr4j.core.model.ICommand#getCommandName()
         */
    public String getCommand()
    {
        return COMMAND;
    }

}
