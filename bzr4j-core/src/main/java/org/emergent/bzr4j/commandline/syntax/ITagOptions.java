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
public interface ITagOptions
{

    public final static String COMMAND = "tag"; //$NON-NLS-N$

    /**
     * Replace existing tags.
     */
    public static final Option FORCE = new Option( "--force" ); //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * Branch in which to place the tag.
     */
    public static final KeywordOption DIRECTORY = new KeywordOption( "--directory", "ARG" );
            //$NON-NLS-N$

    /**
     * See "help revisionspec" for details.
     */
    public static final KeywordOption REVISION = new KeywordOption( "--revision", "ARG" );
            //$NON-NLS-N$

    /**
     * Delete this tag rather than placing it.
     */
    public static final Option DELETE = new Option( "--delete" ); //$NON-NLS-N$

    public final static String HELP =
            "Create, remove or modify a tag naming a revision.\\n\\nTags give human-meaningful names to revisions.  Commands that take a -r\\n(--revision) option can be given -rtag:X, where X is any previously\\ncreated tag.\\n\\nTags are stored in the branch.  Tags are copied from one branch to another\\nalong when you branch, push, pull or merge.\\n\\nIt is an error to give a tag name that already exists unless you pass \\n--force, in which case the tag is moved to point to the new revision."
            ; //$NON-NLS-N$
}
