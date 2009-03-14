/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import java.io.File;

import org.emergent.bzr4j.commandline.syntax.IIgnoredOptions;

/**
 * @author Guillermo Gonzalez
 *
 */
public class Ignored extends SingleFileCommand implements IIgnoredOptions
{

    /**
     * @param workDir
     *            file - file in the branch or the branch root
     */
    public Ignored( final File workDir )
    {
        super( workDir );
    }

    /*
         * (non-Javadoc)
         *
         * @see org.emergent.bzr4j.commandline.internal.Command#getCommand()
         */
    @Override
    public String getCommand()
    {
        return COMMAND;
    }

}
