/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.syntax;

import org.emergent.bzr4j.commandline.commands.options.Option;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface IRemoveOptions
{

    public final static String COMMAND = "remove"; //$NON-NLS-N$

    /**
     * Remove newly-added files.
     */
    public static final Option NEW = new Option( "--new" ); //$NON-NLS-N$

    /**
     * Only delete files if they can be safely recovered (default).
     */
    public static final Option SAFE = new Option( "--safe" ); //$NON-NLS-N$

    /**
     * Delete all the specified files, even if they can not be recovered and
     * even if they are non-empty directories.
     */
    public static final Option FORCE = new Option( "--force" ); //$NON-NLS-N$

    /**
     * Don't delete any files.
     */
    public static final Option KEEP = new Option( "--keep" ); //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    public final static String HELP =
            "Remove files or directories.\\n\\nThis makes bzr stop tracking changes to the specified files and\\ndelete them if they can easily be recovered using revert.\\n\\nYou can specify one or more files, and/or --new.  If you specify --new,\\nonly \'added\' files will be removed.  If you specify both, then new files\\nin the specified directories will be removed.  If the directories are\\nalso new, they will also be removed."
            ; //$NON-NLS-N$
}
