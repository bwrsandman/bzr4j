/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.emergent.bzr4j.commandline.syntax.IRevnoOptions;
import org.emergent.bzr4j.core.BranchLocation;

/**
 * @author Guillermo Gonzalez
 *
 */
public class Revno extends SingleFileCommand implements IRevnoOptions
{

    private String location;

    public Revno( final File workDir, final BranchLocation location )
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

    /*
         * (non-Javadoc)
         *
         * @see org.emergent.bzr4j.core.model.ICommand#getCommandName()
         */
    @Override
    public String getCommand()
    {
        return COMMAND;
    }

    public String getStandardOutput()
    {
        return super.getStandardOutputSplit()[0].trim();
    }

}
