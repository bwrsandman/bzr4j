/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import org.emergent.bzr4j.commandline.syntax.IUnBindOptions;

import java.io.File;

/**
 * @author Guillermo Gonzalez
 *
 */
public class UnBind extends SingleFileCommand implements IUnBindOptions
{

    public UnBind( File workDir )
    {
        super( workDir );

    }

    @Override
    public String getCommand()
    {
        // TODO Auto-generated method stub
        return COMMAND;
    }

}
