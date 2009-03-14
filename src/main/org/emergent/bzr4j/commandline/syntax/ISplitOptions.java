/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.syntax;

import org.emergent.bzr4j.commandline.commands.options.Option;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface ISplitOptions
{

    public final static String COMMAND = "split"; //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    public final static String HELP =
            "Split a subdirectory of a tree into a separate tree.\\n\\nThis command will produce a target tree in a format that supports\\nrich roots, like \'rich-root\' or \'rich-root-pack\'.  These formats cannot be\\nconverted into earlier formats like \'dirstate-tags\'.\\n\\nThe TREE argument should be a subdirectory of a working tree.  That\\nsubdirectory will be converted into an independent tree, with its own\\nbranch.  Commits in the top-level tree will not apply to the new subtree."
            ; //$NON-NLS-N$
}
