/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import org.emergent.bzr4j.commandline.syntax.ICatOptions;

import java.io.File;

/**
 * @author Guillermo Gonzalez
 *
 * TODO: Test me
 */
public class Cat extends SingleFileCommand implements ICatOptions
{

    public Cat( final File workDir, final File file )
    {
        super( workDir, file );
    }

    /*
         * (non-Javadoc)
         *
         * @see org.emergent.bzr4j.core.model.ICommand#getCommandName()
         */
    @Override
    public String getCommand()
    {
        return COMMAND;
    }
}
