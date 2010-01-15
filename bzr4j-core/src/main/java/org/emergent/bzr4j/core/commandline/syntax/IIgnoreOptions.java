/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.syntax;

import org.emergent.bzr4j.commandline.commands.options.Option;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface IIgnoreOptions
{

    public final static String COMMAND = "ignore"; //$NON-NLS-N$

    /**
     * Write out the ignore rules bzr < 0.9 always used.
     */
    public static final Option OLD_DEFAULT_RULES = new Option( "--old-default-rules" );
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
            "Ignore specified files or patterns.\\n\\nTo remove patterns from the ignore list, edit the .bzrignore file.\\n\\nTrailing slashes on patterns are ignored. \\nIf the pattern contains a slash or is a regular expression, it is compared \\nto the whole path from the branch root.  Otherwise, it is compared to only\\nthe last component of the path.  To match a file only in the root \\ndirectory, prepend \'./\'.\\n\\nIgnore patterns specifying absolute paths are not allowed.\\n\\nIgnore patterns may include globbing wildcards such as::\\n\\n  ? - Matches any single character except \'/\'\\n  * - Matches 0 or more characters except \'/\'\\n  /**/ - Matches 0 or more directories in a path\\n  [a-z] - Matches a single character from within a group of characters\\n\\nIgnore patterns may also be Python regular expressions.  \\nRegular expression ignore patterns are identified by a \'RE:\' prefix \\nfollowed by the regular expression.  Regular expression ignore patterns\\nmay not include named or numbered groups.\\n\\nNote: ignore patterns containing shell wildcards must be quoted from \\nthe shell on Unix.\\n\\n:Examples:\\n    Ignore the top level Makefile::\\n\\n        bzr ignore ./Makefile\\n\\n    Ignore class files in all directories::\\n\\n        bzr ignore \"*.class\"\\n\\n    Ignore .o files under the lib directory::\\n\\n        bzr ignore \"lib/**/*.o\"\\n\\n    Ignore .o files under the lib directory::\\n\\n        bzr ignore \"RE:lib/.*\\.o\""
            ; //$NON-NLS-N$
}
