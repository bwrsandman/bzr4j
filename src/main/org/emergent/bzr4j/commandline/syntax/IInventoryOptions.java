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
public interface IInventoryOptions
{

    public final static String COMMAND = "inventory"; //$NON-NLS-N$

    /**
     * List entries of a particular kind: file, directory, symlink.
     */
    public static final KeywordOption KIND = new KeywordOption( "--kind", "ARG" ); //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * Show internal object ids.
     */
    public static final Option SHOW_IDS = new Option( "--show-ids" ); //$NON-NLS-N$

    /**
     * Return content as XML
     */
    public static final Option XML = new Option( "--xml" );

    /**
     * See "help revisionspec" for details.
     */
    public static final KeywordOption REVISION = new KeywordOption( "--revision", "ARG" );
            //$NON-NLS-N$

    public final static String HELP =
            "Show inventory of the current working copy or a revision.\\n\\nIt is possible to limit the output to a particular entry\\ntype using the --kind option.  For example: --kind file.\\n\\nIt is also possible to restrict the list of files to a specific\\nset. For example: bzr inventory --show-ids this/file"
            ; //$NON-NLS-N$
}
