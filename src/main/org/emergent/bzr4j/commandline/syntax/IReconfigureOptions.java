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
public interface IReconfigureOptions
{

    public final static String COMMAND = "reconfigure"; //$NON-NLS-N$

    /**
     * Perform reconfiguration even if local changes will be lost.
     */
    public static final Option FORCE = new Option( "--force" ); //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * The type to reconfigure the directory to.
     */
    public static final KeywordOption TARGET_TYPE = new KeywordOption( "--target_type", "ARG" );
            //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * Branch to bind checkout to.
     */
    public static final KeywordOption BIND_TO = new KeywordOption( "--bind-to", "ARG" );
            //$NON-NLS-N$

    public final static String HELP =
            "Reconfigure the type of a bzr directory.\\n\\nA target configuration must be specified.\\n\\nFor checkouts, the bind-to location will be auto-detected if not specified.\\nThe order of preference is\\n1. For a lightweight checkout, the current bound location.\\n2. For branches that used to be checkouts, the previously-bound location.\\n3. The push location.\\n4. The parent location.\\nIf none of these is available, --bind-to must be specified."
            ; //$NON-NLS-N$
}
