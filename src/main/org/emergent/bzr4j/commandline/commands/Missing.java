/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import org.emergent.bzr4j.commandline.syntax.IMissingOptions;
import org.emergent.bzr4j.core.BranchLocation;

import java.io.File;
import java.util.List;

/**
 * @author Guillermo Gonzalez
 *
 */
public class Missing extends SingleFileCommand implements IMissingOptions
{

    final private BranchLocation location;

    public Missing( final File workDir, final BranchLocation otherBranch )
    {
        super( workDir, null );
        this.location = otherBranch;
    }

    @Override
    protected List<String> getArguments()
    {
        return getArguments( location.toString() );
    }

    public String getCommand()
    {
        return COMMAND;
    }

}
