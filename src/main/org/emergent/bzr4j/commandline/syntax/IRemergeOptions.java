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
public interface IRemergeOptions
{

    public final static String COMMAND = "remerge"; //$NON-NLS-N$

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
     * Select a particular merge algorithm.
     */
    public static final KeywordOption MERGE_TYPE = new KeywordOption( "--merge-type", "ARG" );
            //$NON-NLS-N$

    /**
     * Show base revision text in conflicts.
     */
    public static final Option SHOW_BASE = new Option( "--show-base" ); //$NON-NLS-N$

    public final static String HELP =
            "Redo a merge.\\n\\nUse this if you want to try a different merge technique while resolving\\nconflicts.  Some merge techniques are better than others, and remerge \\nlets you try different ones on different files.\\n\\nThe m_options for remerge have the same meaning and defaults as the ones for\\nmerge.  The difference is that remerge can (only) be run when there is a\\npending merge, and it lets you specify particular files.\\n\\n:Examples:\\n    Re-do the merge of all conflicted files, and show the base text in\\n    conflict regions, in addition to the usual THIS and OTHER texts::\\n  \\n        bzr remerge --show-base\\n\\n    Re-do the merge of \"foobar\", using the weave merge algorithm, with\\n    additional processing to reduce the size of conflict regions::\\n  \\n        bzr remerge --merge-type weave --reprocess foobar"
            ; //$NON-NLS-N$
}
