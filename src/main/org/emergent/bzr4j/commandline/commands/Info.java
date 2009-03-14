/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.emergent.bzr4j.commandline.syntax.IInfoOptions;
import org.emergent.bzr4j.core.BranchLocation;

/**
 * @author Guillermo Gonzalez
 *
 */
public class Info extends SingleFileCommand implements IInfoOptions
{

    private String location;

    public Info( final File workDir, final BranchLocation location )
    {
        super( workDir );
        this.location = location.toString();
    }

    protected List<String> getArguments()
    {
        List<String> args = new ArrayList<String>();
        args.add( location );
        return args;
    }

    @Override
    public String getCommand()
    {
        return COMMAND;
    }

}
