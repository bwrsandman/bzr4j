/**
 *
 */
package org.emergent.bzr4j.commandline.commands;

import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

/**
 * @author Guillermo Gonzalez <guillo.gonzo AT gmail DOT com>
 *
 */
public class MultiFileCommandTest extends MultiFileCommand
{

    public MultiFileCommandTest()
    {
        super( new File( "tmp/m_workDir" ),
                new File[]{new File( "test.txt" ), new File( "test_2.txt" ),
                        new File( "test2.txt" )} );
    }

    @Test
    public final void testGetArguments()
    {
        List<String> arguments = getArguments();
        for ( int i = 0; i < arguments.size(); i++ )
        {
            org.testng.Assert.assertEquals( arguments.get( i ), m_resources[i].getPath() );
        }
    }

    @Override
    public String getCommand()
    {
        return "MultFileCommandTest";
    }


}
