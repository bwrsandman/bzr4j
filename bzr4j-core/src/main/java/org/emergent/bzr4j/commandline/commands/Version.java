/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import org.emergent.bzr4j.commandline.internal.Command;
import org.emergent.bzr4j.commandline.syntax.IVersionOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Guillermo Gonzalez
 *
 * TODO: test me
 */
public class Version extends Command implements IVersionOptions
{

    /*
         * (non-Javadoc)
         *
         * @see org.emergent.bzr4j.commandline.internal.Command#getArguments()
         */
    @Override
    protected List<String> getArguments()
    {
        return new ArrayList<String>();
    }

    /*
         * (non-Javadoc)
         *
         * @see org.emergent.bzr4j.commandline.internal.Command#getCommand()
         */
    @Override
    public String getCommand()
    {
        return COMMAND;
    }

}
