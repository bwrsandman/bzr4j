/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.emergent.bzr4j.commandline.syntax.ILogOptions;
import org.emergent.bzr4j.core.BranchLocation;

/**
 * @author Guillermo Gonzalez
 *
 */
public class Log extends SingleFileCommand implements ILogOptions
{

    private String location;

    public Log( final File workDir, final File resource )
    {
        super( workDir, resource );
    }

    public Log( final File workDir, final URI location )
    {
        super( workDir );
        this.location = location.toString();
    }

    public Log( final File workDir, final BranchLocation location )
    {
        super( workDir );
        this.location = location.toString();
    }

    protected List<String> getArguments()
    {
        List<String> args = new ArrayList<String>();
        if ( file != null )
        {
            args.add( getRelativeToWorkDir( file ).getPath() );
        }
        else
        {
            args.add( location );
        }
        return args;
    }

    @Override
    public String getCommand()
    {
        return COMMAND;
    }

}
