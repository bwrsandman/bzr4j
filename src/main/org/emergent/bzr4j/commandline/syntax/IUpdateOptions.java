/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.syntax;

import org.emergent.bzr4j.commandline.commands.options.Option;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface IUpdateOptions
{

    public final static String COMMAND = "update"; //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    public final static String HELP =
            "Update a tree to have the latest code committed to its branch.\\n\\nThis will perform a merge into the working tree, and may generate\\nconflicts. If you have any local changes, you will still \\nneed to commit them after the update for the update to be complete.\\n\\nIf you want to discard your local changes, you can just do a \\n\'bzr revert\' instead of \'bzr commit\' after the update."
            ; //$NON-NLS-N$
}
