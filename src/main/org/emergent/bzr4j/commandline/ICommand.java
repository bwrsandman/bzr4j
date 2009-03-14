/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline;

import org.emergent.bzr4j.commandline.commands.options.Option;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface ICommand
{
    public String getCommandError();

    public int getEstimatedWork();

    public void setOption( Option option );
}
