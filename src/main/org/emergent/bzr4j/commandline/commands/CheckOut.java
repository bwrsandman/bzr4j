/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import java.io.File;
import java.util.List;

import org.emergent.bzr4j.commandline.internal.Command;
import org.emergent.bzr4j.commandline.syntax.ICheckoutOptions;
import org.emergent.bzr4j.core.BranchLocation;

/**
 * @author Guillermo Gonzalez
 *
 */
public class CheckOut extends Command implements ICheckoutOptions
{

    private final BranchLocation remoteBranch;

    private final File localBranch;

    /**
     * @param fromLocation
     * @param toLocation
     */
    public CheckOut( BranchLocation fromLocation, File localBranch )
    {
        this.remoteBranch = fromLocation;
        this.localBranch = localBranch;
    }

    public String getCommand()
    {
        return COMMAND;
    }

    /*
         * (non-Javadoc)
         *
         * @see org.emergent.bzr4j.core.commands.Command#getArguments()
         */
    @Override
    protected List<String> getArguments()
    {
        String fromLocation = this.remoteBranch.toString();
        String toLocation = this.localBranch.getPath();
        return getArguments( fromLocation, toLocation );
    }

    /*
         * (non-Javadoc)
         *
         * @see org.emergent.bzr4j.core.model.ICommand#getEstimatedWork()
         */
    public int getEstimatedWork()
    {
        // TODO: search info about this value, and how can be defined
        return 100;
    }

}
