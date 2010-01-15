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
public interface ILogOptions
{

    public final static String COMMAND = "xmllog"; //$NON-NLS-N$

    /**
     * Use specified log format.
     */
    public static final KeywordOption LOG_FORMAT = new KeywordOption( "--log-format", "ARG" );
            //$NON-NLS-N$

    /**
     * Show files changed in each revision.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * Limit the output to the first N revisions.
     */
    public static final KeywordOption LIMIT = new KeywordOption( "--limit", "N" ); //$NON-NLS-N$

    /**
     * Show from oldest to newest.
     */
    public static final Option FORWARD = new Option( "--forward" ); //$NON-NLS-N$

    /**
     * Display timezone as local, original, or utc.
     */
    public static final KeywordOption TIMEZONE = new KeywordOption( "--timezone", "ARG" );
            //$NON-NLS-N$

    /**
     * Show internal object ids.
     */
    public static final Option SHOW_IDS = new Option( "--show-ids" ); //$NON-NLS-N$

    /**
     * See "help revisionspec" for details.
     */
    public static final KeywordOption REVISION = new KeywordOption( "--revision", "ARG" );
            //$NON-NLS-N$

    /**
     * Show revisions whose message matches this regular expression.
     */
    public static final KeywordOption MESSAGE = new KeywordOption( "--message", "ARG" );
            //$NON-NLS-N$

    public final static String HELP =
            "Show log of a branch, file, or directory.\\n\\nBy default show the log of the branch containing the working directory.\\n\\nTo request a range of logs, you can use the command -r begin..end\\n-r revision requests a specific revision, -r ..end or -r begin.. are\\nalso valid.\\n\\n:Examples:\\n    Log the current branch::\\n\\n        bzr log\\n\\n    Log a file::\\n\\n        bzr log foo.c\\n\\n    Log the last 10 revisions of a branch::\\n\\n        bzr log -r -10.. http://server/branch"
            ; //$NON-NLS-N$

}
