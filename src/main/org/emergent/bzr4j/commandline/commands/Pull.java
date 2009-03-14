/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import org.emergent.bzr4j.commandline.syntax.IPullOptions;
import org.emergent.bzr4j.core.BranchLocation;

import java.io.File;
import java.util.List;

/**
 * @author Guillermo Gonzalez
 *
 */
public class Pull extends SingleFileCommand implements IPullOptions
{

    final private BranchLocation location;

    public Pull( final File workDir, final BranchLocation location )
    {
        super( workDir, null );
        this.location = location;
    }

    /*
         * (non-Javadoc)
         *
         * @see org.emergent.bzr4j.core.commands.Command#getArguments()
         */
    @Override
    protected List<String> getArguments()
    {
        return getArguments( location.toString() );
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
