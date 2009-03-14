/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import java.io.File;
import java.util.List;

import org.emergent.bzr4j.commandline.CommandLineException;
import org.emergent.bzr4j.commandline.syntax.IMoveOptions;
import org.emergent.bzr4j.core.BazaarException;

/**
 * @author Guillermo Gonzalez
 *
 */
public class Move extends MultiFileCommand implements IMoveOptions
{

    private final File toPath;

    public Move( final File workDir, final File[] files, final File toPath )
    {
        super( workDir, files );
        this.toPath = toPath;
    }

    /*
         * (non-Javadoc)
         *
         * @see org.emergent.bzr4j.core.commands.Command#execute()
         */
    @Override
    public void execute() throws BazaarException
    {
        if ( checkArguments() )
        {
            super.execute();
        }
        else
        {
            throw new CommandLineException( "unexpected arguments", getCommand() );
        }
    }

    /*
         * (non-Javadoc)
         *
         * @see org.emergent.bzr4j.core.commands.Command#getArguments()
         */
    @Override
    protected List<String> getArguments()
    {
        List<String> args = super.getArguments();
        args.add( toPath.getAbsolutePath() );
        return args;
    }

    /*
         * (non-Javadoc)
         *
         * @see org.emergent.bzr4j.core.commands.Command#getCommand()
         */
    @Override
    public String getCommand()
    {
        return COMMAND;
    }

    // helpers
    private boolean checkArguments()
    {
        if ( m_resources.length > 1 )
        {
            return toPath.isDirectory();
        }
        return true;
    }

}
