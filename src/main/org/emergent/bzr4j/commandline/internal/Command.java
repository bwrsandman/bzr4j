/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.internal;

import static org.emergent.bzr4j.utils.StringUtil.shellQuote;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.emergent.bzr4j.commandline.CmdLineClientTexts;
import org.emergent.bzr4j.commandline.CommandLineException;
import org.emergent.bzr4j.commandline.ICommand;
import org.emergent.bzr4j.commandline.commands.options.Option;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.BazaarClientPreferences;
import org.emergent.bzr4j.core.BazaarPreference;
import org.emergent.bzr4j.utils.BzrUtil;

/**
 * Abstract base class for command requests. Provides a framework for
 * implementing command execution.
 * @author Guillermo Gonzalez
 * @author Patrick Woodworth
 */
public abstract class Command implements ICommand
{
    private static final Object sm_sync = new Object();

    private static HashMap<String, String> DEFAULT_ENVIROMENT = new HashMap<String, String>();

    static
    {
        // Here we put default env variables
        DEFAULT_ENVIROMENT.put( BazaarPreference.BZR_PROGRESS_BAR.name(), "none" ); //$NON-NLS-N$
        // if the executable is not configured, try to find it in the PATH
        // DEFAULT_ENVIROMENT.put("PATH", System.getenv("PATH"));
    }

    private ExecResult m_result = new ExecResult();

    protected File m_workDir;

    protected List<Option> m_options = new ArrayList<Option>();

    protected boolean m_checkExitValue = true;

    protected Command()
    {
        this( null );
    }

    protected Command( File workDir )
    {
        m_checkExitValue = true;
        m_workDir = workDir;
    }

    public void setCheckExitValue( boolean checkExitValue )
    {
        m_checkExitValue = checkExitValue;
    }

    public void runCommand( final List<String> cmdLine, final File workDir )
            throws BazaarException
    {
        assert cmdLine != null;
        assert cmdLine.size() > 0;
        cmdLine.addAll( 0, BazaarClientPreferences.getExecutable() );
        ProcessBuilder pb = new ProcessBuilder( cmdLine );
        setDefaultEnviroment( pb );
        if ( workDir != null )
        {
            pb.directory( workDir );
        }
        CommandExecutor executor = new CommandExecutor();
        synchronized (sm_sync)
        {
            executor.execAndWait( m_result, pb );
            if ( m_checkExitValue && m_result.getExitCode() != 0 )
            {
                throw new BazaarException( m_result );
            }
        }
    }

    /**
     * @return the command stderr
     */
    public ExecResult getExecResult()
    {
        return m_result;
    }

    protected static void setDefaultEnviroment( ProcessBuilder pb )
    {
        Map<String, String> env = pb.environment();
        env.putAll( DEFAULT_ENVIROMENT );
        env.putAll( BazaarClientPreferences.getInstance().getPreferences() );
    }


    /**
     *
     * @return A String that represent the bzr command name
     */
    public abstract String getCommand();

    public File getWorkDir()
    {
        return m_workDir;
    }

    /*
         * (non-Javadoc)
         *
         * @see org.emergent.bzr4j.core.model.ICommand#getEstimatedWork()
         */
    public int getEstimatedWork()
    {
        // defaut estimated work
        return 100;
    }

    protected boolean isNoop()
    {
        return false;
    }

    /**
     * <p>
     * Do the real work of a command
     * </p>
     * <p>
     * Subclases must override this method when needed.<br>
     * Implementors must ensure that the any return value 'll be recorded
     * </p>
     *
     * @throws BazaarException
     */
    public void execute() throws BazaarException
    {
        if (isNoop())
            return;
        List<String> cmdLine = constructCommandInvocationString();
        runCommand( cmdLine, m_workDir );
    }

    /**
     * Constucts the Bzr command invocation string corresponding to the
     * arguments.
     *
     * @return the command invocation string
     * @throws CommandLineException
     */
    private List<String> constructCommandInvocationString() throws CommandLineException
    {

        List<String> arguments = getArguments();
        List<String> commandLine = new ArrayList<String>();
        commandLine.addAll( defaultCmdLine() );
        for ( Option option : m_options )
        {
            if ( option == null )
            {
                // only to show a more detailed error when an option is null
                CommandLineException cle =
                        new CommandLineException( "Command option cannot be null", getCommand() );
                cle.setStackTrace( CommandLineException.getCurrentStackTrace() );
                throw cle;
            }
            String strOption = option.toString();
            if ( strOption.length() > 0 )
            {

                commandLine.add( strOption );
                //commandLine[m_options.indexOf(option) + defaultCmdLineSize()] = strOption;
                //lastIdx += 1;
            }
        }

        commandLine.addAll( arguments );

        return commandLine;
    }

    public void setOption( Option option )
    {
        if ( !this.m_options.contains( option ) )
        {
            this.m_options.add( option );
        }
    }

    public String getCommandError()
    {
        return CmdLineClientTexts.bind( "Command.error", new String[]{getCommand()} );
    }

    public String getStandardError()
    {
        return getExecResult().getStderr();
    }

    public String getStandardOutput()
    {
        return getExecResult().getStdout();
    }

    public String[] getStandardOutputSplit()
    {
        return getExecResult().getStdout().trim().split( System.getProperty( "line.separator" ) );
    }

    // ///////////////////////////////////////
    // Command line Helpers
    /**
     * @return cmdLine String[] with empty option of the specified size
     */
    protected List<String> defaultCmdLine()
    {
        List<String> result = new ArrayList<String>();
        result.add( getCommand() );
        return result;
    }

    /*
        protected int defaultCmdLineSize() {
                String[] defaultCmdLine = new String[] { getExecutable(), getCommand() };
                return defaultCmdLine.length;
        }*/

    protected abstract List<String> getArguments() throws CommandLineException;

    /*
         * Convenience methods
         */
    protected List<String> getArguments( String arg )
    {
        List<String> args = new ArrayList<String>();
        if ( arg != null )
        {
            args.add( arg );
        }
        return args;
    }

    protected List<String> getArguments( String arg1, String arg2 )
    {
        List<String> args = getArguments( arg1 );
        args.add( arg2 );
        return args;
    }

    protected List<String> getEmptyArguments()
    {
        return new ArrayList<String>();
    }

    /**
     * @return a the cmd line that is going to be executed
     * @throws CommandLineException
     */
    protected String getCmdLineAsString() throws CommandLineException
    {
        final StringBuilder cmdLine = new StringBuilder();
        for ( String part : BazaarClientPreferences.getExecutable() )
        {
            cmdLine.append( shellQuote( part ) );
            cmdLine.append( " " );
        }
        for ( String part : constructCommandInvocationString() )
        {
            cmdLine.append( shellQuote( part ) );
            cmdLine.append( " " );
        }
        return cmdLine.toString().trim();
    }

    protected File getRelativeToWorkDir( File file )
    {
        return BzrUtil.getRelativeTo( m_workDir, file );
    }

}
