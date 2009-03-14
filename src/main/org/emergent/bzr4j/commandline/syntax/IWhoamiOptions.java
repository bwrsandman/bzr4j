/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.syntax;

import org.emergent.bzr4j.commandline.commands.options.Option;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface IWhoamiOptions
{

    public final static String COMMAND = "whoami"; //$NON-NLS-N$

    /**
     * Display email address only.
     */
    public static final Option EMAIL = new Option( "--email" ); //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * Set identity for the current branch instead of globally.
     */
    public static final Option BRANCH = new Option( "--branch" ); //$NON-NLS-N$

    public final static String HELP =
            "Show or set bzr user id.\\n\\n:Examples:\\n    Show the email of the current user::\\n\\n        bzr whoami --email\\n\\n    Set the current user::\\n\\n        bzr whoami \"Frank Chu <fchu@example.com>\""
            ; //$NON-NLS-N$
}
