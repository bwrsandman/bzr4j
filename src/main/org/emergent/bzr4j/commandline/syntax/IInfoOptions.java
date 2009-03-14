/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.syntax;

import org.emergent.bzr4j.commandline.commands.options.Option;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface IInfoOptions
{

    public final static String COMMAND = "xmlinfo"; //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    public final static String HELP =
            "Show information about a working tree, branch or repository.\\n\\nThis command will show all known locations and formats associated to the\\ntree, branch or repository.  Statistical information is included with\\neach report.\\n\\nBranches and working trees will also report any missing revisions."
            ; //$NON-NLS-N$

}
