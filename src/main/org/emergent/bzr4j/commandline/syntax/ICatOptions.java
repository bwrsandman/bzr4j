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
public interface ICatOptions
{

    public final static String COMMAND = "cat"; //$NON-NLS-N$

    /**
     * See "help revisionspec" for details.
     */
    public static final KeywordOption REVISION = new KeywordOption( "--revision", "ARG" );
            //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * The path name in the old tree.
     */
    public static final Option NAME_FROM_REVISION = new Option( "--name-from-revision" );
            //$NON-NLS-N$

    public final static String HELP =
            "Write the contents of a file as of a given revision to standard output.\\n\\nIf no revision is nominated, the last revision is used.\\n\\nNote: Take care to redirect standard output when using this command on a\\nbinary file. "
            ; //$NON-NLS-N$
}
