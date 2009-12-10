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
public interface IMergeOptions
{

    public final static String COMMAND = "merge"; //$NON-NLS-N$

    /**
     * If the destination is already completely merged into the source, pull from the source rather than merging. When this happens, you do
     * not need to commit the result.
     */
    public static final Option PULL = new Option( "--pull" ); //$NON-NLS-N$

    /**
     * Remember the specified location as a default.
     */
    public static final Option REMEMBER = new Option( "--remember" ); //$NON-NLS-N$

    /**
     * Merge even if the destination tree has uncommitted changes.
     */
    public static final Option FORCE = new Option( "--force" ); //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Reprocess to reduce spurious conflicts.
     */
    public static final Option REPROCESS = new Option( "--reprocess" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * Apply uncommitted changes from a working copy, instead of branch changes.
     */
    public static final Option UNCOMMITTED = new Option( "--uncommitted" ); //$NON-NLS-N$

    /**
     * Select a particular merge algorithm.
     */
    public static final KeywordOption MERGE_TYPE = new KeywordOption( "--merge-type", "ARG" );
            //$NON-NLS-N$

    /**
     * Branch to merge into, rather than the one containing the working directory.
     */
    public static final KeywordOption DIRECTORY = new KeywordOption( "--directory", "ARG" );
            //$NON-NLS-N$

    /**
     * Show base revision text in conflicts.
     */
    public static final Option SHOW_BASE = new Option( "--show-base" ); //$NON-NLS-N$

    /**
     * Select changes introduced by the specified revision. See also "help revisionspec".
     */
    public static final KeywordOption CHANGE = new KeywordOption( "--change", "ARG" ); //$NON-NLS-N$

    /**
     * See "help revisionspec" for details.
     */
    public static final KeywordOption REVISION = new KeywordOption( "--revision", "ARG" );
            //$NON-NLS-N$

    public final static String HELP =
            "Perform a three-way merge.\\n\\nThe branch is the branch you will merge from.  By default, it will merge\\nthe latest revision.  If you specify a revision, that revision will be\\nmerged.  If you specify two revisions, the first will be used as a BASE,\\nand the second one as OTHER.  Revision numbers are always relative to the\\nspecified branch.\\n\\nBy default, bzr will try to merge in all new work from the other\\nbranch, automatically determining an appropriate base.  If this\\nfails, you may need to give an explicit base.\\n\\nMerge will do its best to combine the changes in two branches, but there\\nare some kinds of problems only a human can fix.  When it encounters those,\\nit will mark a conflict.  A conflict means that you need to fix something,\\nbefore you should commit.\\n\\nUse bzr resolve when you have fixed a problem.  See also bzr conflicts.\\n\\nIf there is no default branch set, the first merge will set it. After\\nthat, you can omit the branch to use the default.  To change the\\ndefault, use --remember. The value will only be saved if the remote\\nlocation can be accessed.\\n\\nThe results of the merge are placed into the destination working\\ndirectory, where they can be reviewed (with bzr diff), tested, and then\\ncommitted to record the result of the merge.\\n\\nmerge refuses to run if there are any uncommitted changes, unless\\n--force is given.\\n\\n:Examples:\\n    To merge the latest revision from bzr.dev::\\n\\n        bzr merge ../bzr.dev\\n\\n    To merge changes up to and including revision 82 from bzr.dev::\\n\\n        bzr merge -r 82 ../bzr.dev\\n\\n    To merge the changes introduced by 82, without previous changes::\\n\\n        bzr merge -r 81..82 ../bzr.dev"
            ; //$NON-NLS-N$
}
