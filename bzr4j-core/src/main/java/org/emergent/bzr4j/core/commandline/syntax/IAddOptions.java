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
public interface IAddOptions
{

    public final static String COMMAND = "add"; //$NON-NLS-N$

    /**
     * Show what would be done, but don't actually do anything.
     */
    public static final Option DRY_RUN = new Option( "--dry-run" ); //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Don't recursively add the contents of directories.
     */
    public static final Option NO_RECURSE = new Option( "--no-recurse" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * Lookup file ids from this tree.
     */
    public static final KeywordOption FILE_IDS_FROM = new KeywordOption( "--file-ids-from", "ARG" );
            //$NON-NLS-N$

    public final static String HELP =
            "Add specified files or directories.\\n\\nIn non-recursive mode, all the named items are added, regardless\\nof whether they were previously ignored.  A warning is given if\\nany of the named files are already versioned.\\n\\nIn recursive mode (the default), files are treated the same way\\nbut the behaviour for directories is different.  Directories that\\nare already versioned do not give a warning.  All directories,\\nwhether already versioned or not, are searched for files or\\nsubdirectories that are neither versioned or ignored, and these\\nare added.  This search proceeds recursively into versioned\\ndirectories.  If no names are given \'.\' is assumed.\\n\\nTherefore simply saying \'bzr add\' will version all files that\\nare currently unknown.\\n\\nAdding a file whose parent directory is not versioned will\\nimplicitly add the parent, and so on up to the root. This means\\nyou should never need to explicitly add a directory, they\'ll just\\nget added when you add a file in the directory.\\n\\n--dry-run will show which files would be added, but not actually \\nadd them.\\n\\n--file-ids-from will try to use the file ids from the supplied path.\\nIt looks up ids trying to find a matching parent directory with the\\nsame filename, and then by pure path. This option is rarely needed\\nbut can be useful when adding the same logical file into two\\nbranches that will be merged later (without showing the two different\\nadds as a conflict). It is also useful when merging another project\\ninto a subdirectory of this one."
            ; //$NON-NLS-N$
}
