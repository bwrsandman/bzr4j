package org.emergent.bzr4j.commandline.commands;

import java.util.List;

import org.emergent.bzr4j.commandline.internal.Command;
import org.emergent.bzr4j.commandline.syntax.IFindMergeBaseOptions;
import org.emergent.bzr4j.core.BranchLocation;

public class FindMergeBase extends Command implements IFindMergeBaseOptions
{

    private BranchLocation branch;

    private BranchLocation other;

    public FindMergeBase( BranchLocation branch, BranchLocation other )
    {
        this.branch = branch;
        this.other = other;
    }

    @Override
    public String getCommand()
    {
        return COMMAND;
    }

    @Override
    protected List<String> getArguments()
    {
        return getArguments( branch.toString(), other.toString() );
    }
}
