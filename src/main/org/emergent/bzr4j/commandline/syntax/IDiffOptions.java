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
public interface IDiffOptions
{

    public final static String COMMAND = "diff"; //$NON-NLS-N$

    /**
     * Branch/tree to compare from.
     */
    public static final KeywordOption OLD = new KeywordOption( "--old", "ARG" ); //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * Set prefixes added to old and new filenames, as two values separated by a colon. (eg "old/:new/").
     */
    public static final KeywordOption PREFIX = new KeywordOption( "--prefix", "ARG" ); //$NON-NLS-N$

    /**
     * Use this command to compare files.
     */
    public static final KeywordOption USING = new KeywordOption( "--using", "ARG" ); //$NON-NLS-N$

    /**
     * Branch/tree to compare to.
     */
    public static final KeywordOption NEW = new KeywordOption( "--new", "ARG" ); //$NON-NLS-N$

    /**
     * See "help revisionspec" for details.
     */
    public static final KeywordOption REVISION = new KeywordOption( "--revision", "ARG" );
            //$NON-NLS-N$

    /**
     * Pass these options to the external diff program.
     */
    public static final KeywordOption DIFF_OPTIONS = new KeywordOption( "--diff-m_options", "ARG" );
            //$NON-NLS-N$

    /**
     * Select changes introduced by the specified revision. See also "help revisionspec".
     */
    public static final KeywordOption CHANGE = new KeywordOption( "--change", "ARG" ); //$NON-NLS-N$

    public final static String HELP =
            "Show differences in the working tree, between revisions or branches.\\n\\nIf no arguments are given, all changes for the current tree are listed.\\nIf files are given, only the changes in those files are listed.\\nRemote and multiple branches can be compared by using the --old and\\n--new m_options. If not provided, the default for both is derived from\\nthe first argument, if any, or the current tree if no arguments are\\ngiven.\\n\\n\"bzr diff -p1\" is equivalent to \"bzr diff --prefix old/:new/\", and\\nproduces patches suitable for \"patch -p1\".\\n\\n:Exit values:\\n    1 - changed\\n    2 - unrepresentable changes\\n    3 - error\\n    0 - no change\\n\\n:Examples:\\n    Shows the difference in the working tree versus the last commit::\\n\\n        bzr diff\\n\\n    Difference between the working tree and revision 1::\\n\\n        bzr diff -r1\\n\\n    Difference between revision 2 and revision 1::\\n\\n        bzr diff -r1..2\\n\\n    Difference between revision 2 and revision 1 for branch xxx::\\n\\n        bzr diff -r1..2 xxx\\n\\n    Show just the differences for file NEWS::\\n\\n        bzr diff NEWS\\n\\n    Show the differences in working tree xxx for file NEWS::\\n\\n        bzr diff xxx/NEWS\\n\\n    Show the differences from branch xxx to this working tree:\\n\\n        bzr diff --old xxx\\n\\n    Show the differences between two branches for file NEWS::\\n\\n        bzr diff --old xxx --new yyy NEWS\\n\\n    Same as \'bzr diff\' but prefix paths with old/ and new/::\\n\\n        bzr diff --prefix old/:new/"
            ; //$NON-NLS-N$
}
