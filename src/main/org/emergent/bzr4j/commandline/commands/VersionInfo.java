/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.emergent.bzr4j.commandline.syntax.IVersionInfoOptions;
import org.emergent.bzr4j.core.BranchLocation;

/**
 * @author Guillermo Gonzalez <guillo.gonzo@gmail.com>
 *
 */
public class VersionInfo extends SingleFileCommand implements IVersionInfoOptions
{

    private BranchLocation location;

    public VersionInfo( File workDir, BranchLocation location )
    {
        super( workDir );
        this.location = location;
    }

    @Override
    protected List<String> getArguments()
    {
        List<String> args = new ArrayList<String>();
        args.add( location.toString() );
        return args;
    }

    @Override
    public String getCommand()
    {
        return COMMAND;
    }

}
