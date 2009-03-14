/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import java.io.File;
import java.util.List;

import org.emergent.bzr4j.commandline.syntax.IBindOptions;
import org.emergent.bzr4j.core.BranchLocation;

/**
 * @author Guillermo Gonzalez
 *
 */
public class Bind extends SingleFileCommand implements IBindOptions
{

    private BranchLocation location;

    public Bind( File workDir, BranchLocation location )
    {
        super( workDir );
        this.location = location;
    }

    @Override
    public String getCommand()
    {
        return COMMAND;
    }

    @Override
    protected List<String> getArguments()
    {
        if ( location == null )
        {
            return getEmptyArguments();
        }
        return getArguments( location.toString() );
    }

}
