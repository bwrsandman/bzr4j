/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.testUtils;

import org.emergent.bzr4j.core.BazaarTreeStatus;
import org.emergent.bzr4j.core.BranchLocation;
import org.emergent.bzr4j.debug.LogUtil;

import java.io.File;

/**
 * @author Guillermo Gonzalez
 *
 */
public class Environment
{
    private static final LogUtil log = LogUtil.getLogger( Environment.class.getName() );

    protected String testName;

    protected TestsConfig testsConfig = TestsConfig.getTestsConfig();

    /**
     * <p>
     * the File of the working tree location
     * </p>
     * <p>
     * this is a branch of testConfig.workingTreeLocation
     * </p>
     */
    protected File workingTreeLocation;

    /**
     * the expected layout of the working copy after the next subversion command
     */
    protected ExpectedWorkingTree expectedWorkingTree;

    protected TestConfig config;

    /**
     *
     * @param testName
     * @param config
     * @throws Exception
     */
    public Environment( String testName, TestConfig config ) throws Exception
    {
        this.testName = testName;
        this.config = config;
        this.expectedWorkingTree = config.getExpectedWorkingTree().copy();
        this.workingTreeLocation =
                createStartWorkingCopy( config.getWorkingTreeLocation(), testName );
    }

    /**
     * <p>
     * Create a branch (with workingTree) with the name of the test who is going
     * to use it.
     * </p>
     *
     * @param branch
     * @param testName
     * @return
     * @throws Exception
     */
    protected File createStartWorkingCopy( File branch, String testName ) throws Exception
    {
        workingTreeLocation = new File( testsConfig.workingCopies, testName );
        log.debug( "Creating working copy at " + workingTreeLocation.toString() );
        workingTreeLocation.getParentFile().mkdirs();
        if ( workingTreeLocation.exists() )
            FileUtils.removeDirectoryWithContent( workingTreeLocation );
        config.getClient().setWorkDir( branch.getParentFile() );
        config.getClient().branch( new BranchLocation( branch ), workingTreeLocation, null );
        // check if the working tree have the expected status
        checkStatusesExpectedWorkingTree( getWorkingTreeLocation() );
        return workingTreeLocation;
    }

    /**
     *
     * @throws Exception
     */
    public void checkStatusesExpectedWorkingTree( File wtLocation ) throws Exception
    {
        BazaarTreeStatus treeStatus = config.getClient().status( new File[]{wtLocation} );
        expectedWorkingTree
                .check( treeStatus.getStatusAsArray(), workingTreeLocation.getAbsolutePath() );
    }

    public ExpectedWorkingTree getExpectedWorkingTree()
    {
        return expectedWorkingTree;
    }

    public File getWorkingTreeLocation()
    {
        return workingTreeLocation;
    }

}
