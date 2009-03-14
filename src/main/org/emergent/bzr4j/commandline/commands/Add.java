package org.emergent.bzr4j.commandline.commands;

import org.emergent.bzr4j.commandline.syntax.IAddOptions;

import java.io.File;

/**
 * @author Guillermo Gonzalez
 *
 * TODO: need Test
 */

public class Add extends MultiFileCommand implements IAddOptions
{

    public Add( final File workDir, final File[] files )
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
