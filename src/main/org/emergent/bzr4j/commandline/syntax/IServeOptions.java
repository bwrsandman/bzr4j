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
public interface IServeOptions
{

    public final static String COMMAND = "serve"; //$NON-NLS-N$

    /**
     * By default the server is a readonly server. Supplying --allow-writes enables write access to the contents of the served directory and
     * below.
     */
    public static final Option ALLOW_WRITES = new Option( "--allow-writes" ); //$NON-NLS-N$

    /**
     * Display more information.
     */
    public static final Option VERBOSE = new Option( "--verbose" ); //$NON-NLS-N$

    /**
     * Only display errors and warnings.
     */
    public static final Option QUIET = new Option( "--quiet" ); //$NON-NLS-N$

    /**
     * Serve contents of this directory.
     */
    public static final KeywordOption DIRECTORY = new KeywordOption( "--directory", "ARG" );
            //$NON-NLS-N$

    /**
     * Listen for connections on nominated port of the form [hostname:]portnumber. Passing 0 as the port number will result in a dynamically
     * allocated port. The default port is 4155.
     */
    public static final KeywordOption PORT = new KeywordOption( "--port", "ARG" ); //$NON-NLS-N$

    /**
     * Serve on stdin/out for use from inetd or sshd.
     */
    public static final Option INET = new Option( "--inet" ); //$NON-NLS-N$

    public final static String HELP = "Run the bzr server."; //$NON-NLS-N$
}
