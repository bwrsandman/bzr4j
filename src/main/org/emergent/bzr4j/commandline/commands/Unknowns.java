/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import org.emergent.bzr4j.commandline.syntax.IUnknownsOptions;

import java.io.File;

/**
 * @author Guillermo Gonzalez
 *
 */
public class Unknowns extends SingleFileCommand implements IUnknownsOptions
{

    public Unknowns( final File workDir, final File file )
    {
        super( workDir, file );
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
