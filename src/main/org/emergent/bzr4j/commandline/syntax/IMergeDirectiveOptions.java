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
public interface IMergeDirectiveOptions
{

    public final static String COMMAND = "merge-directive"; //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Instead of printing the directive, email to this address.
     */
    public static final KeywordOption MAIL_TO = new KeywordOption( "--mail-to", "ARG" );
            //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * GPG-sign the directive.
     */
    public static final Option SIGN = new Option( "--sign" ); //$NON-NLS-N$

    /**
     * The type of patch to include in the directive.
     */
    public static final KeywordOption PATCH_TYPE = new KeywordOption( "--patch-type", "ARG" );
            //$NON-NLS-N$

    /**
     * Message to use when committing this merge.
     */
    public static final KeywordOption MESSAGE = new KeywordOption( "--message", "ARG" );
            //$NON-NLS-N$

    /**
     * See "help revisionspec" for details.
     */
    public static final KeywordOption REVISION = new KeywordOption( "--revision", "ARG" );
            //$NON-NLS-N$

    public final static String HELP =
            "Generate a merge directive for auto-merge tools.\\n\\nA directive requests a merge to be performed, and also provides all the\\ninformation necessary to do so.  This means it must either include a\\nrevision bundle, or the location of a branch containing the desired\\nrevision.\\n\\nA submit branch (the location to merge into) must be supplied the first\\ntime the command is issued.  After it has been supplied once, it will\\nbe remembered as the default.\\n\\nA public branch is optional if a revision bundle is supplied, but required\\nif --diff or --plain is specified.  It will be remembered as the default\\nafter the first use."
            ; //$NON-NLS-N$
}
