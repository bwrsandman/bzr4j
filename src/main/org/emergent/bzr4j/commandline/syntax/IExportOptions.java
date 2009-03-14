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
public interface IExportOptions
{

    public final static String COMMAND = "export"; //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Type of file to export to.
     */
    public static final KeywordOption FORMAT = new KeywordOption( "--format", "ARG" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * Name of the root directory inside the exported file.
     */
    public static final KeywordOption ROOT = new KeywordOption( "--root", "ARG" ); //$NON-NLS-N$

    /**
     * See "help revisionspec" for details.
     */
    public static final KeywordOption REVISION = new KeywordOption( "--revision", "ARG" );
            //$NON-NLS-N$

    public final static String HELP =
            "Export current or past revision to a destination directory or archive.\\n\\nIf no revision is specified this exports the last committed revision.\\n\\nFormat may be an \"exporter\" name, such as tar, tgz, tbz2.  If none is\\ngiven, try to find the format with the extension. If no extension\\nis found exports to a directory (equivalent to --format=dir).\\n\\nIf root is supplied, it will be used as the root directory inside\\ncontainer formats (tar, zip, etc). If it is not supplied it will default\\nto the exported filename. The root option has no effect for \'dir\' format.\\n\\nIf branch is omitted then the branch containing the current working\\ndirectory will be used.\\n\\nNote: Export of tree with non-ASCII filenames to zip is not supported.\\n\\n  =================       =========================\\n  Supported formats       Autodetected by extension\\n  =================       =========================\\n     dir                         (none)\\n     tar                          .tar\\n     tbz2                    .tar.bz2, .tbz2\\n     tgz                      .tar.gz, .tgz\\n     zip                          .zip\\n  =================       ========================="
            ; //$NON-NLS-N$
}
