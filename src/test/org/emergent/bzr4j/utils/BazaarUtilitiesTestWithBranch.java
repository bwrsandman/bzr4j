/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.utils;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.emergent.bzr4j.testUtils.BazaarTest;
import org.emergent.bzr4j.testUtils.Environment;

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
        Assert.assertEquals( testEnv.getWorkingTreeLocation(),
                BzrUtil.getRootBranch( newDir ) );
    }

    @Test
    public void testGetRootBranch() throws Exception
    {
        Environment testEnv;
        testEnv = new Environment( "getRelativeToTest", getTestConfig() );
        client.setWorkDir( testEnv.getWorkingTreeLocation() );

        File newDir = new File( testEnv.getWorkingTreeLocation(), "new_Dir" );
        newDir.mkdirs();
        Assert.assertTrue( BzrUtil.getRootBranch( newDir ).equals(
                testEnv.getWorkingTreeLocation() ) );
    }

}
