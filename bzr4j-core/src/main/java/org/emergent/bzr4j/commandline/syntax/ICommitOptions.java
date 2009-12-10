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
public interface ICommitOptions
{

    public final static String COMMAND = "commit"; //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Set the author's name, if it's different from the committer.
     */
    public static final KeywordOption AUTHOR = new KeywordOption( "--author", "ARG" ); //$NON-NLS-N$

    /**
     * Commit even if nothing has changed.
     */
    public static final Option UNCHANGED = new Option( "--unchanged" ); //$NON-NLS-N$

    /**
     * Mark a bug as being fixed by this revision.
     */
    public static final KeywordOption FIXES = new KeywordOption( "--fixes", "ARG" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * When no message is supplied, show the diff along with the status summary in the message editor.
     */
    public static final Option SHOW_DIFF = new Option( "--show-diff" ); //$NON-NLS-N$

    /**
     * Refuse to commit if there are unknown files in the working tree.
     */
    public static final Option STRICT = new Option( "--strict" ); //$NON-NLS-N$

    /**
     * Take commit message from this file.
     */
    public static final KeywordOption FILE = new KeywordOption( "--file", "msgfile" ); //$NON-NLS-N$

    /**
     * Description of the new revision.
     */
    public static final KeywordOption MESSAGE = new KeywordOption( "--message", "ARG" );
            //$NON-NLS-N$

    public static final Option MESSAGE_SHORT = new Option( "-m" ); //$NON-NLS-N$

    /**
     * Perform a local commit in a bound branch. Local commits are not pushed to the master branch until a normal commit is performed.
     */
    public static final Option LOCAL = new Option( "--local" ); //$NON-NLS-N$

    public final static String HELP =
            "Commit changes into a new revision.\\n\\nIf no arguments are given, the entire tree is committed.\\n\\nIf selected files are specified, only changes to those files are\\ncommitted.  If a directory is specified then the directory and everything \\nwithin it is committed.\\n\\nIf author of the change is not the same person as the committer, you can\\nspecify the author\'s name using the --author option. The name should be\\nin the same format as a committer-id, e.g. \"John Doe <jdoe@example.com>\".\\n\\nA selected-file commit may fail in some cases where the committed\\ntree would be invalid. Consider::\\n\\n  bzr init foo\\n  mkdir foo/bar\\n  bzr add foo/bar\\n  bzr commit foo -m \"committing foo\"\\n  bzr mv foo/bar foo/baz\\n  mkdir foo/bar\\n  bzr add foo/bar\\n  bzr commit foo/bar -m \"committing bar but not baz\"\\n\\nIn the example above, the last commit will fail by design. This gives\\nthe user the opportunity to decide whether they want to commit the\\nrename at the same time, separately first, or not at all. (As a general\\nrule, when in doubt, Bazaar has a policy of Doing the Safe Thing.)\\n\\nNote: A selected-file commit after a merge is not yet supported."
            ; //$NON-NLS-N$
}
