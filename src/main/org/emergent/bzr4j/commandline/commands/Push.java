/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import org.emergent.bzr4j.commandline.syntax.IPushOptions;
import org.emergent.bzr4j.core.BranchLocation;

import java.io.File;
import java.util.List;

/**
 * @author Guillermo Gonzalez
 *
 */
public class Push extends SingleFileCommand implements IPushOptions
{

    private BranchLocation location;

    public Push( File workDir, BranchLocation location )
    {
        super( workDir );
        this.location = location;
    }

    @Override
    protected List<String> getArguments()
    {
        return getArguments( location.toString() );
    }

    @Override
    public String getCommand()
    {
        return COMMAND;
    }

}
