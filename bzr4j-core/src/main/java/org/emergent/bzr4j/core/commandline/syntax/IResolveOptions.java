/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.syntax;

import org.emergent.bzr4j.commandline.commands.options.Option;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface IResolveOptions
{

    public final static String COMMAND = "resolve"; //$NON-NLS-N$

    /**
     * Resolve all conflicts in this tree.
     */
    public static final Option ALL = new Option( "--all" ); //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    public final static String HELP =
            "Mark a conflict as resolved.\\n\\nMerge will do its best to combine the changes in two branches, but there\\nare some kinds of problems only a human can fix.  When it encounters those,\\nit will mark a conflict.  A conflict means that you need to fix something,\\nbefore you should commit.\\n\\nOnce you have fixed a problem, use \"bzr resolve\" to automatically mark\\ntext conflicts as fixed, resolve FILE to mark a specific conflict as\\nresolved, or \"bzr resolve --all\" to mark all conflicts as resolved.\\n\\nSee also bzr conflicts."
            ; //$NON-NLS-N$
}
