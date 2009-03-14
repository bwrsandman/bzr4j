package org.emergent.bzr4j.commandline.commands;

import org.emergent.bzr4j.commandline.syntax.ISendOptions;
import org.emergent.bzr4j.core.BranchLocation;

import java.io.File;
import java.util.List;

/**
 *
 * @author Guillermo Gonzalez
 *
 */

public class Send extends SingleFileCommand implements ISendOptions
{

    private final BranchLocation submitBranch;

    public Send( final File workDir, final BranchLocation submitBranch )
    {
        super( workDir );
        this.submitBranch = submitBranch;
    }

    @Override
    protected List<String> getArguments()
    {
        List<String> args = super.getArguments();
        args.add( submitBranch.toString() );
        return args;
    }

    @Override
    public String getCommand()
    {
        return COMMAND;
    }

}
