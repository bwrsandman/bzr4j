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
 * over a single file/directory
 * </p>
 *
 * @author Guillermo Gonzalez
 */
public abstract class SingleFileCommand extends Command
{

    protected final File file;

    protected SingleFileCommand( File workDir, File file )
    {
        super( workDir );
        this.file = file;
    }

    protected SingleFileCommand( File workDir )
    {
        super( workDir );
        this.file = null;
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

        if ( file != null )
        {
            args.add( getRelativeToWorkDir( file ).getPath() );
        }

        return args;
    }

}
