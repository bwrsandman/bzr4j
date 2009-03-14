/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import java.io.File;

import org.emergent.bzr4j.commandline.syntax.IDiffOptions;

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
