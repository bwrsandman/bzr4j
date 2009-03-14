/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import java.io.File;

import org.emergent.bzr4j.commandline.syntax.IUnCommitOptions;

/**
 * @author Guillermo Gonzalez
 *
 * TODO: Test me
 */
public class UnCommit extends SingleFileCommand implements IUnCommitOptions
{

    public UnCommit( final File workDir, final File branch )
    {
        super( workDir, branch );
    }

    public String getCommand()
    {
        return COMMAND;
    }

}
