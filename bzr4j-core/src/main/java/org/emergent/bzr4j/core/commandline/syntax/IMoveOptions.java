/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.syntax;

import org.emergent.bzr4j.commandline.commands.options.Option;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface IMoveOptions
{

    public final static String COMMAND = "move"; //$NON-NLS-N$

    /**
     * Move only the bzr identifier of the file, because the file has already been moved.
     */
    public static final Option AFTER = new Option( "--after" ); //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    public final static String HELP =
            "Move or rename a file.\\n\\n:Usage:\\n    bzr mv OLDNAME NEWNAME\\n\\n    bzr mv SOURCE... DESTINATION\\n\\nIf the last argument is a versioned directory, all the other names\\nare moved into it.  Otherwise, there must be exactly two arguments\\nand the file is changed to a new name.\\n\\nIf OLDNAME does not exist on the filesystem but is versioned and\\nNEWNAME does exist on the filesystem but is not versioned, mv\\nassumes that the file has been manually moved and only updates\\nits internal inventory to reflect that change.\\nThe same is valid when moving many SOURCE files to a DESTINATION.\\n\\nFiles cannot be moved between branches."
            ; //$NON-NLS-N$
}
