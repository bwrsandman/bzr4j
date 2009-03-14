/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.syntax;

import org.emergent.bzr4j.commandline.commands.options.Option;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface IFileIdOptions
{

    public final static String COMMAND = "file-id"; //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    public final static String HELP =
            "Print file_id of a particular file or directory.\\n\\nThe file_id is assigned when the file is first added and remains the\\nsame through all revisions where the file exists, even when it is\\nmoved or renamed."
            ; //$NON-NLS-N$
}
