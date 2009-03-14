/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.syntax;

import org.emergent.bzr4j.commandline.commands.options.KeywordOption;
import org.emergent.bzr4j.commandline.commands.options.Option;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface IUnCommitOptions
{

    public final static String COMMAND = "uncommit"; //$NON-NLS-N$

    /**
     * Don't actually make changes.
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

    /**
     * Say yes to all questions.
     */
    public static final Option FORCE = new Option( "--force" ); //$NON-NLS-N$

    /**
     * See "help revisionspec" for details.
     */
    public static final KeywordOption REVISION = new KeywordOption( "--revision", "ARG" );
            //$NON-NLS-N$

    public final static String HELP =
            "Remove the last committed revision.\\n\\n--verbose will print out what is being removed.\\n--dry-run will go through all the motions, but not actually\\nremove anything.\\n\\nIf --revision is specified, uncommit revisions to leave the branch at the\\nspecified revision.  For example, \"bzr uncommit -r 15\" will leave the\\nbranch at revision 15.\\n\\nIn the future, uncommit will create a revision bundle, which can then\\nbe re-applied."
            ; //$NON-NLS-N$
}
