/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import java.io.File;
import java.util.ArrayList;

import org.emergent.bzr4j.commandline.commands.options.Option;
import org.emergent.bzr4j.commandline.syntax.IInitOptions;
import org.emergent.bzr4j.commandline.internal.ExecResult;
import org.emergent.bzr4j.core.BazaarException;

/**
 * @author Guillermo Gonzalez
 *
 * TODO: Test me
 */
public class Init extends SingleFileCommand implements IInitOptions
{

    /**
     *
     * @param branch
     */
    public Init( File branch )
    {
        // the workdir should be the parent of the directory to be crated
        super( branch == null ? null : branch.getParentFile(), branch );
        this.m_options = new ArrayList<Option>();
    }

    @Override
    protected boolean isNoop()
    {
        return file == null;
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

}
