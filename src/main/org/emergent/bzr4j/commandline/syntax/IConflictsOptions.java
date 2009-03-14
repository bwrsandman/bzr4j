/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.syntax;

import org.emergent.bzr4j.commandline.commands.options.Option;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface IConflictsOptions
{

    public final static String COMMAND = "conflicts"; //$NON-NLS-N$

    /**
     * List paths of files with text conflicts.
     */
    public static final Option TEXT = new Option( "--text" ); //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    public final static String HELP =
            "List files with conflicts.\\n\\nMerge will do its best to combine the changes in two branches, but there\\nare some kinds of problems only a human can fix.  When it encounters those,\\nit will mark a conflict.  A conflict means that you need to fix something,\\nbefore you should commit.\\n\\nConflicts normally are listed as short, human-readable messages.  If --text\\nis supplied, the pathnames of files with text conflicts are listed,\\ninstead.  (This is useful for editing all files with text conflicts.)\\n\\nUse bzr resolve when you have fixed a problem.\\n\\nSee also bzr resolve."
            ; //$NON-NLS-N$
}
