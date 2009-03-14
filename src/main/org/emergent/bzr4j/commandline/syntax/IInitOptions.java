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
public interface IInitOptions
{

    public final static String COMMAND = "init"; //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Specify a format for this branch. See "help formats".
     */
    public static final KeywordOption FORMAT = new KeywordOption( "--format", "ARG" ); //$NON-NLS-N$

    /**
     * Create the path leading up to the branch if it does not already exist.
     */
    public static final Option CREATE_PREFIX = new Option( "--create-prefix" ); //$NON-NLS-N$

    /**
     * Never change revnos or the existing log. Append revisions to it only.
     */
    public static final Option APPEND_REVISIONS_ONLY = new Option( "--append-revisions-only" );
            //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    public final static String HELP =
            "Make a directory into a versioned branch.\\n\\nUse this to create an empty branch, or before importing an\\nexisting project.\\n\\nIf there is a repository in a parent directory of the location, then \\nthe history of the branch will be stored in the repository.  Otherwise\\ninit creates a standalone branch which carries its own history\\nin the .bzr directory.\\n\\nIf there is already a branch at the location but it has no working tree,\\nthe tree can be populated with \'bzr checkout\'.\\n\\nRecipe for importing a tree of files::\\n\\n    cd ~/project\\n    bzr init\\n    bzr add .\\n    bzr status\\n    bzr commit -m \"imported project\""
            ; //$NON-NLS-N$
}
