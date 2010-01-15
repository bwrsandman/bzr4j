/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.syntax;

import org.emergent.bzr4j.commandline.commands.options.Option;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface ISwitchOptions
{

    public final static String COMMAND = "switch"; //$NON-NLS-N$

    /**
     * Switch even if local commits will be lost.
     */
    public static final Option FORCE = new Option( "--force" ); //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    public final static String HELP =
            "Set the branch of a checkout and update.\\n\\nFor lightweight checkouts, this changes the branch being referenced.\\nFor heavyweight checkouts, this checks that there are no local commits\\nversus the current bound branch, then it makes the local branch a mirror\\nof the new location and binds to it.\\n\\nIn both cases, the working tree is updated and uncommitted changes\\nare merged. The user can commit or revert these as they desire.\\n\\nPending merges need to be committed or reverted before using switch."
            ; //$NON-NLS-N$
}
