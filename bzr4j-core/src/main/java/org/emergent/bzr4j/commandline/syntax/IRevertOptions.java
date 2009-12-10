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
public interface IRevertOptions
{

    public final static String COMMAND = "revert"; //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * Remove pending merge marker, without changing any files.
     */
    public static final Option FORGET_MERGES = new Option( "--forget-merges" ); //$NON-NLS-N$

    /**
     * Do not save backups of reverted files.
     */
    public static final Option NO_BACKUP = new Option( "--no-backup" ); //$NON-NLS-N$

    /**
     * See "help revisionspec" for details.
     */
    public static final KeywordOption REVISION = new KeywordOption( "--revision", "ARG" );
            //$NON-NLS-N$

    public final static String HELP =
            "Revert files to a previous revision.\\n\\nGiving a list of files will revert only those files.  Otherwise, all files\\nwill be reverted.  If the revision is not specified with \'--revision\', the\\nlast committed revision is used.\\n\\nTo remove only some changes, without reverting to a prior version, use\\nmerge instead.  For example, \"merge . --revision -2..-3\" will remove the\\nchanges introduced by -2, without affecting the changes introduced by -1.\\nOr to remove certain changes on a hunk-by-hunk basis, see the Shelf plugin.\\n\\nBy default, any files that have been manually changed will be backed up\\nfirst.  (Files changed only by merge are not backed up.)  Backup files have\\n\'.~#~\' appended to their name, where # is a number.\\n\\nWhen you provide files, you can use their current pathname or the pathname\\nfrom the target revision.  So you can use revert to \"undelete\" a file by\\nname.  If you name a directory, all the contents of that directory will be\\nreverted.\\n\\nAny files that have been newly added since that revision will be deleted,\\nwith a backup kept if appropriate.  Directories containing unknown files\\nwill not be deleted.\\n\\nThe working tree contains a list of pending merged revisions, which will\\nbe included as parents in the next commit.  Normally, revert clears that\\nlist as well as reverting the files.  If any files are specified, revert\\nleaves the pending merge list alone and reverts only the files.  Use \"bzr\\nrevert .\" in the tree root to revert all files but keep the merge record,\\nand \"bzr revert --forget-merges\" to clear the pending merge list without\\nreverting any files."
            ; //$NON-NLS-N$
}
