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
public interface IInitRepositoryOptions
{

    public final static String COMMAND = "init-repository"; //$NON-NLS-N$

    /**
     * Branches in the repository will default to not having a working tree.
     */
    public static final Option NO_TREES = new Option( "--no-trees" ); //$NON-NLS-N$

    /**
     * Specify a format for this repository. See "bzr help formats" for details.
     */
    public static final KeywordOption FORMAT = new KeywordOption( "--format", "ARG" ); //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    public final static String HELP =
            "Create a shared repository to hold branches.\\n\\nNew branches created under the repository directory will store their\\nrevisions in the repository, not in the branch directory.\\n\\nIf the --no-trees option is used then the branches in the repository\\nwill not have working trees by default.\\n\\n:Examples:\\n    Create a shared repositories holding just branches::\\n\\n        bzr init-repo --no-trees repo\\n        bzr init repo/trunk\\n\\n    Make a lightweight checkout elsewhere::\\n\\n        bzr checkout --lightweight repo/trunk trunk-checkout\\n        cd trunk-checkout\\n        (add files here)"
            ; //$NON-NLS-N$
}
