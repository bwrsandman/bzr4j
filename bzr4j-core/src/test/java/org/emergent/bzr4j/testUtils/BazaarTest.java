/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.testUtils;

import java.io.IOException;

import org.emergent.bzr4j.core.IBazaarClient;
import org.emergent.bzr4j.core.BazaarException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 * @author Guillermo Gonzalez
 */

public abstract class BazaarTest
{
    protected IBazaarClient client;

    protected static TestsConfig testsConfig;

    private TestConfig testConfig = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        testsConfig = TestsConfig.getTestsConfig();
        testsConfig.createWorkingTree();
    }

    @BeforeMethod
    public void setUp()
    {
        // get the client
        client = testsConfig.client;
        client.setWorkDir( testsConfig.rootDir );
    }

    /*
         * Note: This method use init, add and commit.
         *
         */
    public TestConfig getTestConfig() throws IOException, BazaarException
    {
        if ( testConfig == null )
        {
            testConfig = new TestConfig( client, testsConfig.defaultWorkingTreeLocation,
                    testsConfig.defaultWorkingTree );
        }
        return testConfig;
    }

    protected static String getUserName()
    {
        return TestsConfig.getUserName();
    }

    protected static String getBzrMail()
    {
        return TestsConfig.getBzrMail();
    }

    @AfterClass
    public static void tearDownAfterClass()
    {
        if ( testsConfig != null )
        {
            testsConfig.deleteWorkingTree();
        }
    }
}
