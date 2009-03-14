/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.syntax;

import org.emergent.bzr4j.commandline.commands.options.Option;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface ISignMyCommitsOptions
{

    public final static String COMMAND = "sign-my-commits"; //$NON-NLS-N$

    /**
     * Don't actually sign anything, just print the revisions that would be signed.
     */
    public static final Option DRY_RUN = new Option( "--dry-run" ); //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    public final static String HELP =
            "Sign all commits by a given committer.\\n\\nIf location is not specified the local tree is used.\\nIf committer is not specified the default committer is used.\\n\\nThis does not sign commits that already have signatures."
            ; //$NON-NLS-N$
}
