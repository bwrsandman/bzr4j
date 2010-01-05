/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.utils;

import org.emergent.bzr4j.testUtils.BazaarTest;
import org.emergent.bzr4j.testUtils.Environment;
import org.testng.annotations.Test;

import java.io.File;

/**
 * @author Guillermo Gonzalez <guillo.gonzo AT gmail DOT com>
 *
 */
public class BazaarUtilitiesTestWithBranch extends BazaarTest
{

    @Test
    public void testGetRelativeTo() throws Exception
    {
        Environment testEnv;
        testEnv = new Environment( "getRelativeToTest", getTestConfig() );
        client.setWorkDir( testEnv.getWorkingTreeLocation() );

        File newDir = new File( testEnv.getWorkingTreeLocation(), "new_Dir" );
        newDir.mkdirs();
        org.testng.Assert.assertEquals( BzrCoreUtil.getRootBranch( newDir ), testEnv.getWorkingTreeLocation() );
    }

    @Test
    public void testGetRootBranch() throws Exception
    {
        Environment testEnv;
        testEnv = new Environment( "getRelativeToTest", getTestConfig() );
        client.setWorkDir( testEnv.getWorkingTreeLocation() );

        File newDir = new File( testEnv.getWorkingTreeLocation(), "new_Dir" );
        newDir.mkdirs();
        org.testng.Assert.assertTrue( BzrCoreUtil.getRootBranch( newDir ).equals(
                testEnv.getWorkingTreeLocation() ) );
    }

}
