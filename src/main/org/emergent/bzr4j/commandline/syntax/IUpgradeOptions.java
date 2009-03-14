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
public interface IUpgradeOptions
{

    public final static String COMMAND = "upgrade"; //$NON-NLS-N$

    /**
     * Upgrade to a specific format. See "bzr help formats" for details.
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
            "Upgrade branch storage to current format.\\n\\nThe check command or bzr developers may sometimes advise you to run\\nthis command. When the default format has changed you may also be warned\\nduring other operations to upgrade."
            ; //$NON-NLS-N$
}
