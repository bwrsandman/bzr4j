/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import java.io.File;

import org.emergent.bzr4j.commandline.syntax.ICatOptions;

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
