/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.syntax;

import org.emergent.bzr4j.commandline.commands.options.Option;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface IHelpOptions
{

    public final static String COMMAND = "help"; //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * Show help on all commands.
     */
    public static final Option LONG = new Option( "--long" ); //$NON-NLS-N$

    public final static String HELP = "Show help on a command or other topic.\\n    ";
            //$NON-NLS-N$
}
