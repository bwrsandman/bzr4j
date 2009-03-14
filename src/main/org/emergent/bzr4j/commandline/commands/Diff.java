/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import org.emergent.bzr4j.commandline.syntax.IDiffOptions;

import java.io.File;

/**
 * @author Guillermo Gonzalez
 *
 * TODO: test me
 */
public class Diff extends SingleFileCommand implements IDiffOptions
{
    public Diff( final File workDir, final File file )
    {
        super( workDir, file );
        setCheckExitValue( false );
    }

    /*
         * (non-Javadoc)
         *
         * @see org.emergent.bzr4j.commandline.internal.Command#getCommand()
         */
    public String getCommand()
    {
        return COMMAND;
    }

}
