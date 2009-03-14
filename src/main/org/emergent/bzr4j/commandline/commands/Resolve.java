/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import java.io.File;
import java.util.List;

import org.emergent.bzr4j.commandline.syntax.IResolveOptions;

/**
 * @author Guillermo Gonzalez
 *
 */
public class Resolve extends MultiFileCommand
{

    public Resolve( final File workDir, final List<File> files )
    {
        super( workDir, files.toArray( new File[0] ) );
    }

    public Resolve( final File workDir, final File[] resources )
    {
        super( workDir, resources );
    }

    @Override
    public String getCommand()
    {
        return IResolveOptions.COMMAND;
    }

}
