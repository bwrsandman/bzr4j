/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import org.emergent.bzr4j.commandline.CommandLineException;
import org.emergent.bzr4j.commandline.internal.Command;
import org.emergent.bzr4j.commandline.syntax.IPluginsOptions;
import org.emergent.bzr4j.core.BazaarException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Guillermo Gonzalez <guillo.gonzo AT gmail DOT com>
 *
 */
public class Plugins extends Command implements IPluginsOptions
{

    public Plugins()
    {
        super();
    }

    @Override
    public void execute() throws BazaarException
    {
        super.execute();
        String err = getStandardError();
        if ( !"".equals( err ) )
        {
            throw new CommandLineException( err, COMMAND );
        }
    }

    @Override
    public String getCommand()
    {
        return IPluginsOptions.COMMAND;
    }

    @Override
    protected List<String> getArguments()
    {
        return new ArrayList<String>();
    }

}
