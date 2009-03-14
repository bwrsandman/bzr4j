/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline.commands;

import org.emergent.bzr4j.commandline.CommandLineException;
import org.emergent.bzr4j.commandline.internal.ExecResult;
import org.emergent.bzr4j.commandline.syntax.ICommitOptions;
import org.emergent.bzr4j.core.BazaarException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * <br>
 * Represents a 'bzr commit [args]' command.<br>
 * <br>
 *
 * @author Guillermo Gonzalez
 *
 */
public class Commit extends MultiFileCommand implements ICommitOptions
{

    private final String message;

    public Commit( final File workDir, final File[] files, final String message )
    {
        super( workDir, files );
        assert message.trim().length() > 0;
        this.message = message;
        setCheckExitValue( false );

    }

    @Override
    public String getCommand()
    {
        return COMMAND;
    }

    @Override
    public void execute() throws BazaarException
    {
        File tempFile = createMessageTempFile();
        try
        {
            setOption( ICommitOptions.FILE.with( tempFile.getPath() ) );
            super.execute();
        }
        catch ( CommandLineException e )
        {
            throw e;
        }
        finally
        {
            tempFile.delete();
        }
    }

    private File createMessageTempFile() throws CommandLineException
    {
        File tempFile = null;
        OutputStreamWriter osw = null;
        try
        {
            tempFile = File.createTempFile( "bzr", "_log_message" );
            osw = new OutputStreamWriter( new FileOutputStream( tempFile ), "utf-8" );
            osw.write( message );
        }
        catch ( IOException e )
        {
            throw CommandLineException.wrapException( e );
        }
        finally
        {
            if ( osw != null )
                try
                {
                    osw.close();
                }
                catch ( IOException e )
                {
                    throw CommandLineException.wrapException( e );
                }
        }
        return tempFile;
    }

}
