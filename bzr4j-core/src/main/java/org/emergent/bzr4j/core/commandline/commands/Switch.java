/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import org.emergent.bzr4j.commandline.syntax.ISwitchOptions;
import org.emergent.bzr4j.core.BranchLocation;

import java.io.File;
import java.util.List;

/**
 * @author Guillermo Gonzalez <guillo.gonzo at gmail dot com>
 *
 */
public class Switch extends SingleFileCommand implements ISwitchOptions
{

    private BranchLocation location;

    public Switch( File workDir, BranchLocation location )
    {
        super( workDir );
        this.location = location;
    }

    @Override
    public String getCommand()
    {
        return COMMAND;
    }

    @Override
    protected List<String> getArguments()
    {
        if ( location != null )
        {
            return getArguments( location.toString() );
        }
        else
        {
            return getEmptyArguments();
        }
    }


}
