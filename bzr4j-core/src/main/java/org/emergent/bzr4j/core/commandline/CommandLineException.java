/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline;

import org.emergent.bzr4j.core.BazaarException;

/**
 * @author Guillermo Gonzalez
 *
 */
public class CommandLineException extends BazaarException
{

    private static final long serialVersionUID = -8645734268757666362L;

    private static final int ERROR = 0;

    private static final int INFO = 1;

    private final String command;

    public CommandLineException( final String message, final String command )
    {
        super( message );
        this.command = command;
    }

    public CommandLineException( final Throwable cause, final String command )
    {
        super( cause.getMessage(), cause );
        this.setStackTrace( cause.getStackTrace() );
        this.command = command;
    }

    public static CommandLineException wrapException( final Exception e )
    {
        return new CommandLineException( e, "" );
    }

    public String getMessageError()
    {
        final String[] message = getMessage().split( System.getProperty( "line.separator" ) );
        String error = message[ERROR];
        return error;
    }

    public String getMessageInfo()
    {
        final String[] message = getMessage().split( System.getProperty( "line.separator" ) );
        if ( message.length > INFO )
            return message[INFO];
        return getMessage();
    }

    public String getCommand()
    {
        return command;
    }

    public boolean isCommandLine()
    {
        return true;
    }

    /**
     * Create a stacktrace from the current thread, and remove the calls used to
     * generate it
     *
     * @return StackTraceElement[]
     */
    public static StackTraceElement[] getCurrentStackTrace()
    {
        // a custom stackTrace (all calls except
        // Thread.currentThread().getStackTrace() ;) )
        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        System.arraycopy( st, 3, st, 0, st.length - 3 );
        return st;
    }    
}
