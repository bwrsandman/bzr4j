/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import org.emergent.bzr4j.commandline.syntax.IRemoveOptions;

import java.io.File;
import java.util.List;

/**
 * @author Guillermo Gonzalez
 *
 * TODO: Test me
 */
public class Remove extends MultiFileCommand implements IRemoveOptions
{

    public Remove( final File workDir, final List<File> resources )
    {
        super( workDir, resources.toArray( new File[0] ) );
    }

    public Remove( final File workDir, final File[] resources )
    {
        super( workDir, resources );
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
