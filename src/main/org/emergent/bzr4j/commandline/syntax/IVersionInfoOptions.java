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
public interface IVersionInfoOptions
{

    public final static String COMMAND = "version-info"; //$NON-NLS-N$

    /**
     * Include all possible information.
     */
    public static final Option ALL = new Option( "--all" ); //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Check if tree is clean.
     */
    public static final Option CHECK_CLEAN = new Option( "--check-clean" ); //$NON-NLS-N$

    /**
     * Include the revision-history.
     */
    public static final Option INCLUDE_HISTORY = new Option( "--include-history" ); //$NON-NLS-N$

    /**
     * Select the output format.
     */
    public static final KeywordOption FORMAT = new KeywordOption( "--format", "ARG" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * Template for the output.
     */
    public static final KeywordOption TEMPLATE = new KeywordOption( "--template", "ARG" );
            //$NON-NLS-N$

    /**
     * Include the last revision for each file.
     */
    public static final Option INCLUDE_FILE_REVISIONS = new Option( "--include-file-revisions" );
            //$NON-NLS-N$

    public final static String HELP =
            "Show version information about this tree.\\n\\nYou can use this command to add information about version into\\nsource code of an application. The output can be in one of the\\nsupported formats or in a custom format based on a template.\\n\\nFor example::\\n\\n  bzr version-info --custom \\\\n    --template=\"#define VERSION_INFO \\\"Project 1.2.3 (r{revno})\\\"\\n\"\\n\\nwill produce a C header file with formatted string containing the\\ncurrent revision number. Other supported variables in templates are:\\n\\n  * {date} - date of the last revision\\n  * {build_date} - current date\\n  * {revno} - revision number\\n  * {revision_id} - revision id\\n  * {branch_nick} - branch nickname\\n  * {clean} - 0 if the source tree contains uncommitted changes,\\n              otherwise 1"
            ; //$NON-NLS-N$
}
