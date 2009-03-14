/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import org.emergent.bzr4j.commandline.syntax.IAnnotateOptions;

import java.io.File;

/**
 * @author Guillermo Gonzalez
 *
 * TODO: Test me
 */
public class Annotate extends SingleFileCommand implements IAnnotateOptions
{

    public Annotate( final File workDir, final File file )
    {
        super( workDir, file );
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
