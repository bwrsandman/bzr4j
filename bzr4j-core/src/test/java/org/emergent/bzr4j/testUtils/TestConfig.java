/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.testUtils;

import org.emergent.bzr4j.core.IBazaarClient;

import java.io.File;

/**
 * @author Guillermo Gonzalez
 *
 */
public class TestConfig
{
    private IBazaarClient client;

    /**
     * the location of the base branch/workingTree root, (tests should do a
     * branch of this to work with)
     */
    private File workingTreeLocation;

    private ExpectedWorkingTree expectedWorkingTree;

    /**
     *
     * @param client
     * @param workingTreeLocation
     * @param expectedWorkingTree
     */
    public TestConfig( IBazaarClient client, File workingTreeLocation,
            ExpectedWorkingTree expectedWorkingTree )
    {
        super();
        this.client = client;
        this.workingTreeLocation = workingTreeLocation;
        this.expectedWorkingTree = expectedWorkingTree;
    }

    /**
     * @return Returns the client.
     */
    public IBazaarClient getClient()
    {
        return client;
    }

    /**
     * @return Returns the expectedWorkingTree.
     */
    public ExpectedWorkingTree getExpectedWorkingTree()
    {
        return expectedWorkingTree;
    }

    /**
     * which is shared by all tests(which conatins a working tree).
     *
     * @return Returns the branch/workingTree root
     */
    public File getWorkingTreeLocation()
    {
        return workingTreeLocation;
    }

}
