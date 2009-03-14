/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import org.emergent.bzr4j.commandline.syntax.IUpdateOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Guillermo Gonzalez
 *
 * TODO: Test me
 */
public class Update extends SingleFileCommand implements IUpdateOptions
{

    public Update( final File workDir )
    {
        super( workDir );
    }

    public Update( final File workDir, File location )
    {
        super( workDir, location );
    }

    @Override
    protected List<String> getArguments()
    {
        List<String> args = new ArrayList<String>();
        if ( file != null )
        {
            args.add( file.getPath() );
        }
        return args;
    }

    public String getCommand()
    {
        return COMMAND;
    }

}
