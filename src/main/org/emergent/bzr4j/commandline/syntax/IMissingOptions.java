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
public interface IMissingOptions
{

    public final static String COMMAND = "xmlmissing"; //$NON-NLS-N$

    /**
     * Use specified log format.
     */
    public static final KeywordOption LOG_FORMAT = new KeywordOption( "--log-format", "ARG" );
            //$NON-NLS-N$

    /**
     * Reverse the order of revisions.
     */
    public static final Option REVERSE = new Option( "--reverse" ); //$NON-NLS-N$

    /**
     * Same as --mine-only.
     */
    public static final Option THIS = new Option( "--this" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * Same as --theirs-only.
     */
    public static final Option OTHER = new Option( "--other" ); //$NON-NLS-N$

    /**
     * Display changes in the local branch only.
     */
    public static final Option MINE_ONLY = new Option( "--mine-only" ); //$NON-NLS-N$

    /**
     * Show internal object ids.
     */
    public static final Option SHOW_IDS = new Option( "--show-ids" ); //$NON-NLS-N$

    /**
     * Display changes in the remote branch only.
     */
    public static final Option THEIRS_ONLY = new Option( "--theirs-only" ); //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    public final static String HELP =
            "Show unmerged/unpulled revisions between two branches.\\n\\nOTHER_BRANCH may be local or remote."
            ; //$NON-NLS-N$
}
