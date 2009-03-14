/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import java.io.File;
import java.util.List;

import org.emergent.bzr4j.commandline.syntax.IIgnoreOptions;

/**
 * @author Guillermo Gonzalez
 *
 */
public class Ignore extends SingleFileCommand implements IIgnoreOptions
{

    final private String pattern;

    /**
     * @param workDir
     *            file - file in the branch or the branch root
     * @param pattern
     */
    public Ignore( final File workDir, final String pattern )
    {
        super( workDir );
        this.pattern = pattern;
    }

    /*
         * (non-Javadoc)
         *
         * @see org.emergent.bzr4j.commandline.internal.Command#getArguments()
         */
    @Override
    protected List<String> getArguments()
    {
        return getArguments( pattern );
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
