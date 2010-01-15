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
public interface IPullOptions
{

    public final static String COMMAND = "pull"; //$NON-NLS-N$

    /**
     * Show logs of pulled revisions.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Remember the specified location as a default.
     */
    public static final Option REMEMBER = new Option( "--remember" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * Branch to pull into, rather than the one containing the working directory.
     */
    public static final KeywordOption DIRECTORY = new KeywordOption( "--directory", "ARG" );
            //$NON-NLS-N$

    /**
     * Ignore differences between branches and overwrite unconditionally.
     */
    public static final Option OVERWRITE = new Option( "--overwrite" ); //$NON-NLS-N$

    /**
     * See "help revisionspec" for details.
     */
    public static final KeywordOption REVISION = new KeywordOption( "--revision", "ARG" );
            //$NON-NLS-N$

    public final static String HELP =
            "Turn this branch into a mirror of another branch.\\n\\nThis command only works on branches that have not diverged.  Branches are\\nconsidered diverged if the destination branch\'s most recent commit is one\\nthat has not been merged (directly or indirectly) into the parent.\\n\\nIf branches have diverged, you can use \'bzr merge\' to integrate the changes\\nfrom one into the other.  Once one branch has merged, the other should\\nbe able to pull it again.\\n\\nIf you want to forget your local changes and just update your branch to\\nmatch the remote one, use pull --overwrite.\\n\\nIf there is no default location set, the first pull will set it.  After\\nthat, you can omit the location to use the default.  To change the\\ndefault, use --remember. The value will only be saved if the remote\\nlocation can be accessed."
            ; //$NON-NLS-N$
}
