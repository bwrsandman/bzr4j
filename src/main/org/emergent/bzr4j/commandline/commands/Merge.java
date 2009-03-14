/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import java.io.File;
import java.util.List;

import org.emergent.bzr4j.commandline.syntax.IMergeOptions;
import org.emergent.bzr4j.core.BranchLocation;

/**
 * @author Guillermo Gonzalez <guillo.gonzo AT gmail DOT com>
 *
 */
public class Merge extends SingleFileCommand
{

    private final BranchLocation location;

    public Merge( final File workDir, final BranchLocation location )
    {
        super( workDir, null );
        this.location = location;
    }

    @Override
    protected List<String> getArguments()
    {
        return getArguments( location.toString() );
    }

    public String getCommand()
    {
        return IMergeOptions.COMMAND;
    }

}
