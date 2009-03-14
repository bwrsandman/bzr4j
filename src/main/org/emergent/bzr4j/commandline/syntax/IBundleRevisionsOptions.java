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
public interface IBundleRevisionsOptions
{

    public final static String COMMAND = "bundle-revisions"; //$NON-NLS-N$

    /**
     * Branch to generate the submission from, rather than the one containing the working directory.
     */
    public static final KeywordOption FROM = new KeywordOption( "--from", "ARG" ); //$NON-NLS-N$

    /**
     * Remember submit and public branch.
     */
    public static final Option REMEMBER = new Option( "--remember" ); //$NON-NLS-N$

    /**
     * Use the specified output format.
     */
    public static final KeywordOption FORMAT = new KeywordOption( "--format", "ARG" ); //$NON-NLS-N$

    /**
     * Do not include a bundle in the merge directive.
     */
    public static final Option NO_BUNDLE = new Option( "--no-bundle" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * Write directive to this file.
     */
    public static final KeywordOption OUTPUT = new KeywordOption( "--output", "ARG" ); //$NON-NLS-N$

    /**
     * See "help revisionspec" for details.
     */
    public static final KeywordOption REVISION = new KeywordOption( "--revision", "ARG" );
            //$NON-NLS-N$

    /**
     * Do not include a preview patch in the merge directive.
     */
    public static final Option NO_PATCH = new Option( "--no-patch" ); //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    public final static String HELP =
            "Create a merge-directive for submiting changes.\\n\\nA merge directive provides many things needed for requesting merges:\\n\\n* A machine-readable description of the merge to perform\\n\\n* An optional patch that is a preview of the changes requested\\n\\n* An optional bundle of revision data, so that the changes can be applied\\n  directly from the merge directive, without retrieving data from a\\n  branch.\\n\\nIf --no-bundle is specified, then public_branch is needed (and must be\\nup-to-date), so that the receiver can perform the merge using the\\npublic_branch.  The public_branch is always included if known, so that\\npeople can check it later.\\n\\nThe submit branch defaults to the parent, but can be overridden.  Both\\nsubmit branch and public branch will be remembered if supplied.\\n\\nIf a public_branch is known for the submit_branch, that public submit\\nbranch is used in the merge instructions.  This means that a local mirror\\ncan be used as your actual submit branch, once you have set public_branch\\nfor that mirror.\\n\\nTwo formats are currently supported: \"4\" uses revision bundle format 4 and\\nmerge directive format 2.  It is significantly faster and smaller than\\nolder formats.  It is compatible with Bazaar 0.19 and later.  It is the\\ndefault.  \"0.9\" uses revision bundle format 0.9 and merge directive\\nformat 1.  It is compatible with Bazaar 0.12 - 0.18."
            ; //$NON-NLS-N$
}
