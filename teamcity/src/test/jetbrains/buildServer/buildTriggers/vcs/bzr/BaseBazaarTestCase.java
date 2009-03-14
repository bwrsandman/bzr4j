package jetbrains.buildServer.buildTriggers.vcs.bzr;

import jetbrains.buildServer.MockSupport;
import jetbrains.buildServer.TempFiles;
import jetbrains.buildServer.vcs.impl.VcsRootImpl;
import jetbrains.buildServer.vcs.patches.PatchTestCase;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.IOException;

/**
 * @author Pavel.Sher
 *         Date: 31.07.2008
 */
public abstract class BaseBazaarTestCase extends PatchTestCase
{
    protected TempFiles myTempFiles;

    protected MockSupport myMockSupport;

    @Override
    @BeforeMethod
    protected void setUp() throws Exception
    {
        super.setUp();

        myMockSupport = new MockSupport();
        myMockSupport.setUpMocks();
        myTempFiles = new TempFiles();
    }

    @AfterMethod
    protected void tearDown() throws Exception
    {
        myMockSupport.tearDownMocks();
        myTempFiles.cleanup();
    }

    protected VcsRootImpl createVcsRoot() throws IOException
    {
        VcsRootImpl vcsRoot = new VcsRootImpl( 1, TCConstants.VCS_NAME );
        vcsRoot.addProperty( TCConstants.BZR_COMMAND_PATH_PROP,
                new File( TCTestUtils.EXE_PATH ).getAbsolutePath() );
        String repPath = getRepositoryPath();
        File repository = LocalRepositoryUtil.prepareRepository( repPath );
        vcsRoot.addProperty( TCConstants.REPOSITORY_PROP, repository.getAbsolutePath() );
        return vcsRoot;
    }

    protected VcsRootImpl createVcsRoot( @NotNull String branchName ) throws IOException
    {
        VcsRootImpl vcsRoot = createVcsRoot();
        vcsRoot.addProperty( TCConstants.BRANCH_NAME_PROP, branchName );
        return vcsRoot;
    }

    private String getRepositoryPath()
    {
        return new File( TCTestUtils.TESTDATA_PATH + "/rep1" ).getAbsolutePath();
    }

    protected void cleanRepositoryAfterTest()
    {
        LocalRepositoryUtil.forgetRepository( getRepositoryPath() );
    }
}
