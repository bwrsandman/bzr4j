/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.syntax;

import org.emergent.bzr4j.commandline.commands.options.Option;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface IJoinOptions
{

    public final static String COMMAND = "join"; //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * Join by reference.
     */
    public static final Option REFERENCE = new Option( "--reference" ); //$NON-NLS-N$

    public final static String HELP =
            "Combine a subtree into its containing tree.\\n\\nThis command is for experimental use only.  It requires the target tree\\nto be in dirstate-with-subtree format, which cannot be converted into\\nearlier formats.\\n\\nThe TREE argument should be an independent tree, inside another tree, but\\nnot part of it.  (Such trees can be produced by \"bzr split\", but also by\\nrunning \"bzr branch\" with the target inside a tree.)\\n\\nThe result is a combined tree, with the subtree no longer an independant\\npart.  This is marked as a merge of the subtree into the containing tree,\\nand all history is preserved.\\n\\nIf --reference is specified, the subtree retains its independence.  It can\\nbe branched by itself, and can be part of multiple projects at the same\\ntime.  But operations performed in the containing tree, such as commit\\nand merge, will recurse into the subtree."
            ; //$NON-NLS-N$
}
