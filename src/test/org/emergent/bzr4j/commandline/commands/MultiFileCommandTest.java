/**
 *
 */
package org.emergent.bzr4j.commandline.commands;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

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
            Assert.assertEquals( m_resources[i].getPath(), arguments.get( i ) );
        }
    }

    @Override
    public String getCommand()
    {
        return "MultFileCommandTest";
    }


}
