/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.syntax;

import org.emergent.bzr4j.commandline.commands.options.Option;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface IWaitUntilSignalledOptions
{

    public final static String COMMAND = "wait-until-signalled"; //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    public final static String HELP =
            "Test helper for test_start_and_stop_bzr_subprocess_send_signal.\\n\\nThis just prints a line to signal when it is ready, then blocks on stdin."
            ; //$NON-NLS-N$
}
