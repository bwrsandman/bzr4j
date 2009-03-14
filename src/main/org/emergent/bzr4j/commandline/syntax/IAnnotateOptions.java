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
public interface IAnnotateOptions
{

    public final static String COMMAND = "xmlannotate"; //$NON-NLS-N$

    /**
     * Show annotations on all lines.
     */
    public static final Option ALL = new Option( "--all" ); //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * Show commit date in annotations.
     */
    public static final Option LONG = new Option( "--long" ); //$NON-NLS-N$

    /**
     * Show internal object ids.
     */
    public static final Option SHOW_IDS = new Option( "--show-ids" ); //$NON-NLS-N$

    /**
     * See "help revisionspec" for details.
     */
    public static final KeywordOption REVISION = new KeywordOption( "--revision", "ARG" );
            //$NON-NLS-N$

    public final static String HELP =
            "Show the origin of each line in a file.\\n\\nThis prints out the given file with an annotation on the left side\\nindicating which revision, author and date introduced the change.\\n\\nIf the origin is the same for a run of consecutive lines, it is \\nshown only at the top, unless the --all option is given."
            ; //$NON-NLS-N$

}
