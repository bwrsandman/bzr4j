/*
 * Copyright 2000-2007 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.buildServer.buildTriggers.vcs.bzr;

import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.vcs.CheckoutRules;
import jetbrains.buildServer.vcs.ModificationData;
import jetbrains.buildServer.vcs.VcsChange;
import jetbrains.buildServer.vcs.VcsChangeInfo;
import jetbrains.buildServer.vcs.VcsException;
import jetbrains.buildServer.vcs.VcsManager;
import jetbrains.buildServer.vcs.impl.VcsRootImpl;
import jetbrains.buildServer.vcs.patches.PatchBuilderImpl;
import jetbrains.buildServer.buildTriggers.vcs.bzr.BazaarServerVcsSupport;
import jetbrains.buildServer.buildTriggers.vcs.bzr.Settings;
import org.jmock.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Test
public class BazaarVcsSupportTest extends BaseBazaarTestCase
{
    private BazaarServerVcsSupport myVcs;

    private ServerPaths myServerPaths;

    @BeforeMethod
    protected void setUp() throws Exception
    {
        super.setUp();

        Mock vcsManagerMock = new Mock( VcsManager.class );
        vcsManagerMock.stubs().method( "registerVcsSupport" );
        Mock serverMock = new Mock( SBuildServer.class );
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        serverMock.stubs().method( "getExecutor" ).will( myMockSupport.returnValue( executor ) );

        File systemDir = myTempFiles.createTempDir();
        myServerPaths = new ServerPaths( systemDir.getAbsolutePath(), systemDir.getAbsolutePath() );
        assertTrue( new File( myServerPaths.getCachesDir() ).mkdirs() );
        myVcs = new BazaarServerVcsSupport( (VcsManager)vcsManagerMock.proxy(), myServerPaths,
                (SBuildServer)serverMock.proxy() );
    }

    protected String getTestDataPath()
    {
        return TCTestUtils.TESTDATA_PATH;
    }

    public void test_get_current_version() throws Exception
    {
        VcsRootImpl vcsRoot = createVcsRoot();

        assertEquals( myVcs.getCurrentVersion( vcsRoot ), "6:b9deb9a1c6f4" );
        assertEquals( "b9deb9a1c6f4", myVcs.getVersionDisplayName( "6:b9deb9a1c6f4", vcsRoot ) );

        assertEquals( myVcs.getCurrentVersion( createVcsRoot( "test_branch" ) ), "8:04c3ae4c6312" );

        assertEquals( myVcs.getCurrentVersion( createVcsRoot( "name with space" ) ),
                "9:9babcf2d5705" );
    }

    public void test_collect_changes() throws Exception
    {
        VcsRootImpl vcsRoot = createVcsRoot();

        List<ModificationData> changes =
                myVcs.collectBuildChanges( vcsRoot, "0:9875b412a788", "3:9522278aa38d",
                        new CheckoutRules( "" ) );
        assertEquals( 3, changes.size() );

        ModificationData md1 = changes.get( 0 );
        ModificationData md2 = changes.get( 1 );
        ModificationData md3 = changes.get( 2 );
        assertEquals( md1.getVersion(), "1:1d446e82d356" );
        assertEquals( md1.getDescription(), "new file added" );
        List<VcsChange> files1 = md1.getChanges();
        assertEquals( 1, files1.size() );
        assertEquals( VcsChangeInfo.Type.ADDED, files1.get( 0 ).getType() );
        assertEquals( normalizePath( files1.get( 0 ).getRelativeFileName() ), "dir1/file3.txt" );

        assertEquals( md2.getVersion(), "2:7209b1f1d793" );
        assertEquals( md2.getDescription(), "file4.txt added" );
        List<VcsChange> files2 = md2.getChanges();
        assertEquals( 1, files2.size() );
        assertEquals( files2.get( 0 ).getType(), VcsChangeInfo.Type.ADDED );
        assertEquals( normalizePath( files2.get( 0 ).getRelativeFileName() ), "dir1/file4.txt" );

        assertEquals( md3.getVersion(), "3:9522278aa38d" );
        assertEquals( md3.getDescription(), "file removed" );
        List<VcsChange> files3 = md3.getChanges();
        assertEquals( 1, files3.size() );
        assertEquals( files3.get( 0 ).getType(), VcsChangeInfo.Type.REMOVED );
        assertEquals( normalizePath( files3.get( 0 ).getRelativeFileName() ), "dir1/file4.txt" );
    }

    public void test_collect_changes_with_checkout_rules() throws Exception
    {
        VcsRootImpl vcsRoot = createVcsRoot();

        List<ModificationData> changes =
                myVcs.collectBuildChanges( vcsRoot, "0:9875b412a788", "3:9522278aa38d",
                        new CheckoutRules( "-:.\n+:dir1/subdir" ) );
        assertEquals( changes.size(), 0 );

        changes = myVcs.collectBuildChanges( vcsRoot, "0:9875b412a788", "5:1d2cc6f3bc29",
                new CheckoutRules( "-:.\n+:dir1/subdir" ) );
        assertEquals( changes.size(), 1 );
        ModificationData md = changes.get( 0 );
        assertEquals( md.getDescription(), "modified in subdir" );
    }

    @Test(invocationCount = 3)
    public void test_build_patch() throws IOException, VcsException
    {
        setName( "cleanPatch1" );
        VcsRootImpl vcsRoot = createVcsRoot();

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final PatchBuilderImpl builder = new PatchBuilderImpl( output );

        myVcs.buildPatch( vcsRoot, null, "4:b06a290a363b", builder, new CheckoutRules( "" ) );
        builder.close();

        checkPatchResult( output.toByteArray() );
    }

    public void test_build_incremental_patch() throws IOException, VcsException
    {
        setName( "patch1" );
        VcsRootImpl vcsRoot = createVcsRoot();

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final PatchBuilderImpl builder = new PatchBuilderImpl( output );

        myVcs.buildPatch( vcsRoot, "3:9522278aa38d", "4:b06a290a363b", builder,
                new CheckoutRules( "" ) );
        builder.close();

        checkPatchResult( output.toByteArray() );
    }

    public void test_build_incremental_patch_file_with_space() throws IOException, VcsException
    {
        setName( "patch2" );
        VcsRootImpl vcsRoot = createVcsRoot();

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final PatchBuilderImpl builder = new PatchBuilderImpl( output );

        myVcs.buildPatch( vcsRoot, "3:9522278aa38d", "6:b9deb9a1c6f4", builder,
                new CheckoutRules( "" ) );
        builder.close();

        checkPatchResult( output.toByteArray() );
    }

    public void test_get_content() throws IOException, VcsException
    {
        VcsRootImpl vcsRoot = createVcsRoot();

        byte[] content = myVcs.getContent( "dir1/subdir/file2.txt", vcsRoot, "4:b06a290a363b" );
        assertEquals( new String( content ), "bbb" );
        content = myVcs.getContent( "dir1/subdir/file2.txt", vcsRoot, "5:1d2cc6f3bc29" );
        assertEquals( new String( content ), "modified\r\nbbb" );
    }

    public void test_get_content_in_branch() throws IOException, VcsException
    {
        VcsRootImpl vcsRoot = createVcsRoot( "test_branch" );

        byte[] content = myVcs.getContent( "file_in_branch.txt", vcsRoot, "7:376dcf05cd2a" );
        content = myVcs.getContent( "file_in_branch.txt", vcsRoot, "8:04c3ae4c6312" );
        assertEquals( new String( content ), "file from the test_branch\r\nfile modified" );
    }

    public void test_test_connection() throws IOException, VcsException
    {
        VcsRootImpl vcsRoot = createVcsRoot();

        System.out.println( myVcs.testConnection( vcsRoot ) );

        vcsRoot.addProperty( TCConstants.REPOSITORY_PROP, "/some/non/existent/path" );
        try
        {
            myVcs.testConnection( vcsRoot );
            fail( "Exception expected" );
        }
        catch ( VcsException e )
        {
        }
    }

//    public void test_tag() throws IOException, VcsException
//    {
//        VcsRootImpl vcsRoot = createVcsRoot();
//        cleanRepositoryAfterTest();
//
//        String actualTag =
//                myVcs.label( "new:tag", "1:1d446e82d356", vcsRoot, new CheckoutRules( "" ) );
//        assertEquals( actualTag, "new_tag" );
//
//        // check the tag is pushed to the parent repository
//        GeneralCommandLine cli = new GeneralCommandLine();
//        cli.setExePath( vcsRoot.getProperty( Constants.BZR_COMMAND_PATH_PROP ) );
//        cli.directory( vcsRoot.getProperty( Constants.REPOSITORY_PROP ) );
//        cli.addParameter( "tags" );
//        ExecResult res = CommandUtil.runCommand( cli );
//        assertTrue( res.getStdout().contains( "new_tag" ) );
//        assertTrue( res.getStdout().contains( "1:1d446e82d356" ) );
//    }

//    public void test_tag_in_branch() throws IOException, VcsException
//    {
//        VcsRootImpl vcsRoot = createVcsRoot( "test_branch" );
//        cleanRepositoryAfterTest();
//
//        String actualTag =
//                myVcs.label( "branch_tag", "7:376dcf05cd2a", vcsRoot, new CheckoutRules( "" ) );
//        assertEquals( actualTag, "branch_tag" );
//
//        // check the tag is pushed to the parent repository
//        GeneralCommandLine cli = new GeneralCommandLine();
//        cli.setExePath( vcsRoot.getProperty( Constants.BZR_COMMAND_PATH_PROP ) );
//        cli.directory( vcsRoot.getProperty( Constants.REPOSITORY_PROP ) );
//        cli.addParameter( "tags" );
//        ExecResult res = CommandUtil.runCommand( cli );
//        assertTrue( res.getStdout().contains( "branch_tag" ) );
//        assertTrue( res.getStdout().contains( "7:376dcf05cd2a" ) );
//    }

    public void test_collect_changes_in_branch() throws Exception
    {
        VcsRootImpl vcsRoot = createVcsRoot( "test_branch" );

        // fromVersion(6:b9deb9a1c6f4) is not in the branch (it is in the default branch)
        List<ModificationData> changes =
                myVcs.collectBuildChanges( vcsRoot, "6:b9deb9a1c6f4", "7:376dcf05cd2a",
                        new CheckoutRules( "" ) );
        assertEquals( 1, changes.size() );

        ModificationData md1 = changes.get( 0 );
        assertEquals( md1.getVersion(), "7:376dcf05cd2a" );
        assertEquals( md1.getDescription(), "new file added in the test_branch" );
        List<VcsChange> files1 = md1.getChanges();
        assertEquals( 1, files1.size() );
        assertEquals( VcsChangeInfo.Type.ADDED, files1.get( 0 ).getType() );
        assertEquals( normalizePath( files1.get( 0 ).getRelativeFileName() ),
                "file_in_branch.txt" );

        changes = myVcs.collectBuildChanges( vcsRoot, "7:376dcf05cd2a", "8:04c3ae4c6312",
                new CheckoutRules( "" ) );
        assertEquals( 1, changes.size() );

        md1 = changes.get( 0 );
        assertEquals( md1.getVersion(), "8:04c3ae4c6312" );
        assertEquals( md1.getDescription(), "file modified" );
    }

    public void test_full_patch_from_branch() throws IOException, VcsException
    {
        setName( "patch3" );
        VcsRootImpl vcsRoot = createVcsRoot( "test_branch" );

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final PatchBuilderImpl builder = new PatchBuilderImpl( output );

        myVcs.buildPatch( vcsRoot, null, "7:376dcf05cd2a", builder, new CheckoutRules( "" ) );
        builder.close();

        checkPatchResult( output.toByteArray() );
    }

    public void test_incremental_patch_from_branch() throws IOException, VcsException
    {
        setName( "patch4" );
        VcsRootImpl vcsRoot = createVcsRoot( "test_branch" );

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final PatchBuilderImpl builder = new PatchBuilderImpl( output );

        myVcs.buildPatch( vcsRoot, "7:376dcf05cd2a", "8:04c3ae4c6312", builder,
                new CheckoutRules( "" ) );
        builder.close();

        checkPatchResult( output.toByteArray() );
    }

    @Test(enabled = false)
    public void support_anchor_branch_notation() throws IOException
    {
        VcsRootImpl vcsRoot = createVcsRoot();
        String repPath = vcsRoot.getProperty( TCConstants.REPOSITORY_PROP );
        vcsRoot.addProperty( TCConstants.REPOSITORY_PROP, repPath + "#test_branch" );
        Settings settings = BazaarServerVcsSupport.createSettings( new File( myServerPaths.getCachesDir() ), vcsRoot );
        assertEquals( "test_branch", settings.getBranchName() );

        vcsRoot.addProperty( TCConstants.REPOSITORY_PROP, repPath + "#" );
        settings = BazaarServerVcsSupport.createSettings( new File( myServerPaths.getCachesDir() ), vcsRoot );
        assertEquals( "default", settings.getBranchName() );

        vcsRoot.addProperty( TCConstants.REPOSITORY_PROP, repPath );
        settings = BazaarServerVcsSupport.createSettings( new File( myServerPaths.getCachesDir() ), vcsRoot );
        assertEquals( "default", settings.getBranchName() );
    }

    private Object normalizePath( final String path )
    {
        return path.replace( File.separatorChar, '/' );
    }
}
