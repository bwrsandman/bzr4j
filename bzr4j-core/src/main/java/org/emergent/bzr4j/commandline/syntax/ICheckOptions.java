/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.syntax;

import org.emergent.bzr4j.commandline.commands.options.Option;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface ICheckOptions
{

    public final static String COMMAND = "check"; //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    public final static String HELP =
            "Validate consistency of branch history.\\n\\nThis command checks various invariants about the branch storage to\\ndetect data corruption or bzr bugs.\\n\\nOutput fields:\\n\\n    revisions: This is just the number of revisions checked.  It doesn\'t\\n        indicate a problem.\\n    versionedfiles: This is just the number of versionedfiles checked.  It\\n        doesn\'t indicate a problem.\\n    unreferenced ancestors: Texts that are ancestors of other texts, but\\n        are not properly referenced by the revision ancestry.  This is a\\n        subtle problem that Bazaar can work around.\\n    unique file texts: This is the total number of unique file contents\\n        seen in the checked revisions.  It does not indicate a problem.\\n    repeated file texts: This is the total number of repeated texts seen\\n        in the checked revisions.  Texts can be repeated when their file\\n        entries are modified, but the file contents are not.  It does not\\n        indicate a problem."
            ; //$NON-NLS-N$
}
