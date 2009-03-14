/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import java.io.File;
import java.util.List;

import org.emergent.bzr4j.commandline.internal.Command;
import org.emergent.bzr4j.commandline.syntax.IBranchOptions;
import org.emergent.bzr4j.core.BranchLocation;
import org.emergent.bzr4j.utils.StringUtil;

/**
 * @author Guillermo Gonzalez
 *
 * TODO: Test me
 */
public class Branch extends Command implements IBranchOptions
{

    private final BranchLocation m_remoteBranch;

    private final File m_localBranch;

    /**
     * @param branchToClone
     * @param localBranch
     */
    public Branch( BranchLocation branchToClone, File localBranch )
    {
        super();
        m_remoteBranch = branchToClone;
        m_localBranch = localBranch;
        setCheckExitValue( false );
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
        String fromLocation = this.m_remoteBranch.toString();
        String toLocation = StringUtil.getAbsoluteURI( this.m_localBranch ).toString();
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
