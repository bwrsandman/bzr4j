/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.syntax;

import org.emergent.bzr4j.commandline.commands.options.Option;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface IBreakLockOptions
{

    public final static String COMMAND = "break-lock"; //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    public final static String HELP =
            "Break a dead lock on a repository, branch or working directory.\\n\\nCAUTION: Locks should only be broken when you are sure that the process\\nholding the lock has been stopped.\\n\\nYou can get information on what locks are open via the \'bzr info\' command.\\n\\n:Examples:\\n    bzr break-lock"
            ; //$NON-NLS-N$
}
