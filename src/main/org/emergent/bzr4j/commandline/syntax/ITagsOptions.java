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
public interface ITagsOptions
{

    public final static String COMMAND = "tags"; //$NON-NLS-N$

    /**
     * Sort tags by different criteria.
     */
    public static final KeywordOption SORT = new KeywordOption( "--sort", "ARG" ); //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * Branch whose tags should be displayed.
     */
    public static final KeywordOption DIRECTORY = new KeywordOption( "--directory", "ARG" );
            //$NON-NLS-N$

    /**
     * Show internal object ids.
     */
    public static final Option SHOW_IDS = new Option( "--show-ids" ); //$NON-NLS-N$

    public final static String HELP =
            "List tags.\\n\\nThis command shows a table of tag names and the revisions they reference."
            ; //$NON-NLS-N$
}
