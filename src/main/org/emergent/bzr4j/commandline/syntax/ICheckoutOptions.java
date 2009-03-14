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
public interface ICheckoutOptions
{

    public final static String COMMAND = "checkout"; //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Get file contents from this tree.
     */
    public static final KeywordOption FILES_FROM = new KeywordOption( "--files-from", "ARG" );
            //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * Perform a lightweight checkout. Lightweight checkouts depend on access to the branch for every operation. Normal checkouts can
     * perform common operations like diff and status without such access, and also support local commits.
     */
    public static final Option LIGHTWEIGHT = new Option( "--lightweight" ); //$NON-NLS-N$

    /**
     * See "help revisionspec" for details.
     */
    public static final KeywordOption REVISION = new KeywordOption( "--revision", "ARG" );
            //$NON-NLS-N$

    public final static String HELP =
            "Create a new checkout of an existing branch.\\n\\nIf BRANCH_LOCATION is omitted, checkout will reconstitute a working tree for\\nthe branch found in \'.\'. This is useful if you have removed the working tree\\nor if it was never created - i.e. if you pushed the branch to its current\\nlocation using SFTP.\\n\\nIf the TO_LOCATION is omitted, the last component of the BRANCH_LOCATION will\\nbe used.  In other words, \"checkout ../foo/bar\" will attempt to create ./bar.\\nIf the BRANCH_LOCATION has no / or path separator embedded, the TO_LOCATION\\nis derived from the BRANCH_LOCATION by stripping a leading scheme or drive\\nidentifier, if any. For example, \"checkout lp:foo-bar\" will attempt to\\ncreate ./foo-bar.\\n\\nTo retrieve the branch as of a particular revision, supply the --revision\\nparameter, as in \"checkout foo/bar -r 5\". Note that this will be immediately\\nout of date [so you cannot commit] but it may be useful (i.e. to examine old\\ncode.)"
            ; //$NON-NLS-N$
}
