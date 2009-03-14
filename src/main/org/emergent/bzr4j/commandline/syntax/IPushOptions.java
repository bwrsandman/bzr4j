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
public interface IPushOptions
{

    public final static String COMMAND = "push"; //$NON-NLS-N$

    /**
     * Remember the specified location as a default.
     */
    public static final Option REMEMBER = new Option( "--remember" ); //$NON-NLS-N$

    /**
     * Create the path leading up to the branch if it does not already exist.
     */
    public static final Option CREATE_PREFIX = new Option( "--create-prefix" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * By default push will fail if the target directory exists, but does not already have a control directory. This flag will allow push to
     * proceed.
     */
    public static final Option USE_EXISTING_DIR = new Option( "--use-existing-dir" ); //$NON-NLS-N$

    /**
     * Branch to push from, rather than the one containing the working directory.
     */
    public static final KeywordOption DIRECTORY = new KeywordOption( "--directory", "ARG" );
            //$NON-NLS-N$

    /**
     * Ignore differences between branches and overwrite unconditionally.
     */
    public static final Option OVERWRITE = new Option( "--overwrite" ); //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    public final static String HELP =
            "Update a mirror of this branch.\\n\\nThe target branch will not have its working tree populated because this\\nis both expensive, and is not supported on remote file systems.\\n\\nSome smart servers or protocols *may* put the working tree in place in\\nthe future.\\n\\nThis command only works on branches that have not diverged.  Branches are\\nconsidered diverged if the destination branch\'s most recent commit is one\\nthat has not been merged (directly or indirectly) by the source branch.\\n\\nIf branches have diverged, you can use \'bzr push --overwrite\' to replace\\nthe other branch completely, discarding its unmerged changes.\\n\\nIf you want to ensure you have the different changes in the other branch,\\ndo a merge (see bzr help merge) from the other branch, and commit that.\\nAfter that you will be able to do a push without \'--overwrite\'.\\n\\nIf there is no default push location set, the first push will set it.\\nAfter that, you can omit the location to use the default.  To change the\\ndefault, use --remember. The value will only be saved if the remote\\nlocation can be accessed."
            ; //$NON-NLS-N$
}
