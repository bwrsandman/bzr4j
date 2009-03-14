/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.syntax;

import org.emergent.bzr4j.commandline.commands.options.Option;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface IRemoveTreeOptions
{

    public final static String COMMAND = "remove-tree"; //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    public final static String HELP =
            "Remove the working tree from a given branch/checkout.\\n\\nSince a lightweight checkout is little more than a working tree\\nthis will refuse to run against one.\\n\\nTo re-create the working tree, use \"bzr checkout\"."
            ; //$NON-NLS-N$
}
