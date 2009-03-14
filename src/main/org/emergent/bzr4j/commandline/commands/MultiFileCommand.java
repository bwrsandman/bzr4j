/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import org.emergent.bzr4j.commandline.internal.Command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * An abstract class which define some common behaivor for commands who operate
 * over multiple files/directories
 * </p>
 *
 * @author Guillermo Gonzalez
 */
public abstract class MultiFileCommand extends Command
{

    protected final File[] m_resources;

    public MultiFileCommand( File workDir, final File[] files )
    {
        super( workDir );
        this.m_resources = files;
    }

    @Override
    protected boolean isNoop()
    {
        return m_resources.length < 1;
    }

    /*
         * (non-Javadoc)
         *
         * @see org.emergent.bzr4j.core.commands.Command#getArguments()
         */
    @Override
    protected List<String> getArguments()
    {

        List<String> args = new ArrayList<String>();
        for ( File f : m_resources )
        {
            args.add( getRelativeToWorkDir( f ).getPath() );
        }

        return args;
    }

    /*
         * (non-Javadoc)
         *
         * @see org.emergent.bzr4j.core.model.ICommand#getEstimatedWork()
         */
    public int getEstimatedWork()
    {
        return this.m_resources.length;
    }

}
