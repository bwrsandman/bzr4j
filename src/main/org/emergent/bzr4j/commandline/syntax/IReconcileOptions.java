/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.syntax;

import org.emergent.bzr4j.commandline.commands.options.Option;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface IReconcileOptions
{

    public final static String COMMAND = "reconcile"; //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    public final static String HELP =
            "Reconcile bzr metadata in a branch.\\n\\nThis can correct data mismatches that may have been caused by\\nprevious ghost operations or bzr upgrades. You should only\\nneed to run this command if \'bzr check\' or a bzr developer \\nadvises you to run it.\\n\\nIf a second branch is provided, cross-branch reconciliation is\\nalso attempted, which will check that data like the tree root\\nid which was not present in very early bzr versions is represented\\ncorrectly in both branches.\\n\\nAt the same time it is run it may recompress data resulting in \\na potential saving in disk space or performance gain.\\n\\nThe branch *MUST* be on a listable system such as local disk or sftp."
            ; //$NON-NLS-N$
}
