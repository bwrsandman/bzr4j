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
public interface IBranchOptions
{

    public final static String COMMAND = "branch"; //$NON-NLS-N$

    /**
     * See "help revisionspec" for details.
     */
    public static final KeywordOption REVISION = new KeywordOption( "--revision", "ARG" );
            //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    public final static String HELP =
            "Create a new copy of a branch.\\n\\nIf the TO_LOCATION is omitted, the last component of the FROM_LOCATION will\\nbe used.  In other words, \"branch ../foo/bar\" will attempt to create ./bar.\\nIf the FROM_LOCATION has no / or path separator embedded, the TO_LOCATION\\nis derived from the FROM_LOCATION by stripping a leading scheme or drive\\nidentifier, if any. For example, \"branch lp:foo-bar\" will attempt to\\ncreate ./foo-bar.\\n\\nTo retrieve the branch as of a particular revision, supply the --revision\\nparameter, as in \"branch foo/bar -r 5\"."
            ; //$NON-NLS-N$
}
