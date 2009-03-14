package org.emergent.bzr4j.intellij;

import org.emergent.bzr4j.testUtils.BazaarTest;
import org.emergent.bzr4j.testUtils.Environment;
import static org.emergent.bzr4j.testUtils.FileUtils.addContentToFile;
import org.emergent.bzr4j.core.BazaarStatusKind;
import org.junit.Test;

import java.io.File;

/**
 * @author Patrick Woodworth
 */
public class UtilsTest extends BazaarTest
{
//    static
//    {
//        PropertyConfigurator.configure( "test-log4j.properties" );
//    }
    

    @Test
    public final void testIsVersioned() throws Exception
    {
        Environment testEnv;
        testEnv = new Environment( "testIsVersioned", getTestConfig() );
        client.setWorkDir( testEnv.getWorkingTreeLocation() );

        File newDir = new File( testEnv.getWorkingTreeLocation(), "new_Dir" );
        newDir.mkdirs();
        File newFile = new File( newDir, "new_File.txt" );
        addContentToFile( newFile, "a new File" );

        client.add( new File[]{newDir} );
        testEnv.getExpectedWorkingTree().addItem( "new_Dir", null );
        testEnv.getExpectedWorkingTree().setItemStatus( "new_Dir", BazaarStatusKind.CREATED );
        testEnv.getExpectedWorkingTree().addItem( "new_Dir/new_File.txt", "a new File" );
        testEnv.getExpectedWorkingTree()
                .addItem( "new_Dir/a file with spaces in its name.txt", "a file with spaces" );
        testEnv.getExpectedWorkingTree()
                .setItemStatus( "new_Dir/new_File.txt", BazaarStatusKind.CREATED );

        testEnv.checkStatusesExpectedWorkingTree( testEnv.getWorkingTreeLocation() );
    }
}
