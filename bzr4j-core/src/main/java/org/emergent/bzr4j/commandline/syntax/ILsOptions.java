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
public interface ILsOptions
{

    public final static String COMMAND = "xmlls"; //$NON-NLS-N$

    /**
     * Print paths relative to the root of the branch.
     */
    public static final Option FROM_ROOT = new Option( "--from-root" ); //$NON-NLS-N$

    /**
     * Print ignored files.
     */
    public static final Option IGNORED = new Option( "--ignored" ); //$NON-NLS-N$

    /**
     * List entries of a particular kind: file, directory, symlink.
     */
    public static final KeywordOption KIND = new KeywordOption( "--kind", "ARG" ); //$NON-NLS-N$

    /**
     * Print versioned files.
     */
    public static final Option VERSIONED = new Option( "--versioned" ); //$NON-NLS-N$

    /**
     * Print unknown files.
     */
    public static final Option UNKNOWN = new Option( "--unknown" ); //$NON-NLS-N$

    /**
     * Don't recurse into subdirectories.
     */
    public static final Option NON_RECURSIVE = new Option( "--non-recursive" ); //$NON-NLS-N$

    /**
     * See "help revisionspec" for details.
     */
    public static final KeywordOption REVISION = new KeywordOption( "--revision", "ARG" );
            //$NON-NLS-N$

    public final static String HELP = "List files in a tree.\\n    "; //$NON-NLS-N$
}
