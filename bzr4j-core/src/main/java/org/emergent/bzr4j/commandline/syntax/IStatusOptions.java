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
public interface IStatusOptions
{

    public final static String COMMAND = "xmlstatus"; //$NON-NLS-N$

    /**
     * Use short status indicators.
     */
    public static final Option SHORT = new Option( "--short" ); //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only show versioned files.
     */
    public static final Option VERSIONED = new Option( "--versioned" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * Show internal object ids.
     */
    public static final Option SHOW_IDS = new Option( "--show-ids" ); //$NON-NLS-N$

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
            "Display status summary.\\n\\nThis reports on versioned and unknown files, reporting them\\ngrouped by state.  Possible states are:\\n\\nadded\\n    Versioned in the working copy but not in the previous revision.\\n\\nremoved\\n    Versioned in the previous revision but removed or deleted\\n    in the working copy.\\n\\nrenamed\\n    Path of this file changed from the previous revision;\\n    the text may also have changed.  This includes files whose\\n    parent directory was renamed.\\n\\nmodified\\n    Text has changed since the previous revision.\\n\\nkind changed\\n    File kind has been changed (e.g. from file to directory).\\n\\nunknown\\n    Not versioned and not matching an ignore pattern.\\n\\nTo see ignored files use \'bzr ignored\'.  For details on the\\nchanges to file texts, use \'bzr diff\'.\\n\\nNote that --short or -S gives status flags for each item, similar\\nto Subversion\'s status command. To get output similar to svn -q,\\nuse bzr -SV.\\n\\nIf no arguments are specified, the status of the entire working\\ndirectory is shown.  Otherwise, only the status of the specified\\nfiles or directories is reported.  If a directory is given, status\\nis reported for everything inside that directory.\\n\\nIf a revision argument is given, the status is calculated against\\nthat revision, or between two revisions if two are provided."
            ; //$NON-NLS-N$

}
