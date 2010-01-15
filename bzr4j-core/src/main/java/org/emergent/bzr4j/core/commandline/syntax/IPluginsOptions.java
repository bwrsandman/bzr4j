/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.syntax;

import org.emergent.bzr4j.commandline.commands.options.Option;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface IPluginsOptions
{

    public final static String COMMAND = "xmlplugins"; //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    public final static String HELP =
            "List the installed plugins.\\n\\nThis command displays the list of installed plugins including the\\npath where each one is located and a short description of each.\\n\\nA plugin is an external component for Bazaar that extends the\\nrevision control system, by adding or replacing code in Bazaar.\\nPlugins can do a variety of things, including overriding commands,\\nadding new commands, providing additional network transports and\\ncustomizing log output.\\n\\nSee the Bazaar web site, http://bazaar-vcs.org, for further\\ninformation on plugins including where to find them and how to\\ninstall them. Instructions are also provided there on how to\\nwrite new plugins using the Python programming language."
            ; //$NON-NLS-N$

}
