/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.core;

import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import org.emergent.bzr4j.commandline.CommandLineClient;
import org.emergent.bzr4j.commandline.syntax.IInfoOptions;
import org.emergent.bzr4j.commandline.syntax.ILogOptions;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.BranchLocation;
import org.emergent.bzr4j.core.BazaarRevision;
import org.emergent.bzr4j.core.BazaarStatusKind;
import org.emergent.bzr4j.core.IBazaarAnnotation;
import org.emergent.bzr4j.core.IBazaarStatus;
import org.emergent.bzr4j.core.IBazaarInfo;
import org.emergent.bzr4j.core.BazaarTreeStatus;
import org.emergent.bzr4j.core.BazaarVersionInfo;
import org.emergent.bzr4j.core.IBazaarItemInfo;
import org.emergent.bzr4j.core.IBazaarLogMessage;
import org.emergent.bzr4j.testUtils.BazaarTest;
import org.emergent.bzr4j.testUtils.Environment;
import org.emergent.bzr4j.testUtils.ExpectedWorkingTree;
import org.emergent.bzr4j.testUtils.FileUtils;
import static org.emergent.bzr4j.testUtils.FileUtils.addContentToFile;
import static org.emergent.bzr4j.testUtils.FileUtils.assertWTEqual;
import org.emergent.bzr4j.utils.BzrUtil;
import static org.emergent.bzr4j.utils.BzrUtil.unixFilePath;
import org.emergent.bzr4j.utils.StringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Guillermo Gonzalez
 *
 */
public class BazaarClientTest extends BazaarTest
{

    @Test
    public final void testAdd() throws Exception
    {
        Environment testEnv;
        testEnv = new Environment( "basicAdd", getTestConfig() );
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

    @Test
    public final void testAnnotate() throws Exception
    {
        Environment testEnv = new Environment( "basicAnnotate", getTestConfig() );
        client.setWorkDir( testEnv.getWorkingTreeLocation() );

        File file = new File( testEnv.getWorkingTreeLocation(), "A/file_in_A" );
        IBazaarAnnotation ann = client.annotate( file );

        // this should be 1 line
        String[] expectedContent = testEnv.getExpectedWorkingTree().getItemContent( "A/file_in_A" )
                .split( System.getProperty( "line.separator" ) );

        Assert.assertEquals( "Line count", expectedContent.length, ann.getNumberOfLines() );
        Assert.assertEquals( "File path", file, ann.getFile() );
        for ( int i = 0; i < expectedContent.length; i++ )
        {
            Assert.assertEquals( "Author", getBzrMail(), ann.getAuthor( i ) );
            Assert.assertEquals( "Code line", expectedContent[i], ann.getline( i ) );
            // TODO: ann.getRevision(i).equals(obj)
        }
    }

    @Test
    public final void testBranch() throws Exception
    {
        Environment testEnv = new Environment( "localBranch/original", getTestConfig() );

        File newBranch = new File( testEnv.getWorkingTreeLocation().getParent(), "branch" );
        client.setWorkDir( newBranch.getParentFile() );
        client.branch( new BranchLocation( testEnv.getWorkingTreeLocation() ), newBranch, null );
        assertWTEqual( testEnv.getWorkingTreeLocation(), newBranch );
    }

    @Test
    public final void testCat() throws Exception
    {
        Environment testEnv = new Environment( "simpleCat", getTestConfig() );
        client.setWorkDir( testEnv.getWorkingTreeLocation() );

        File file = new File( testEnv.getWorkingTreeLocation(), "file_in_root.txt" );
        String expectedContent =
                testEnv.getExpectedWorkingTree().getItemContent( "file_in_root.txt" );
        BufferedReader reader =
                new BufferedReader( new InputStreamReader( client.cat( file, null ) ) );
        String line = null;
        final StringBuilder strBuilder = new StringBuilder();
        while ( (line = reader.readLine()) != null )
        {
            strBuilder.append( line );
        }
        Assert.assertTrue( strBuilder.toString().trim().equals( expectedContent.trim() ) );
    }

    @Test
    public final void testCheckout() throws Exception
    {
        Environment testEnv = new Environment( "checkout/original", getTestConfig() );

        File checkout = new File( testEnv.getWorkingTreeLocation().getParent(), "checkout" );
        client.setWorkDir( checkout.getParentFile() );

        BranchLocation branchLocation =
                new BranchLocation( testEnv.getWorkingTreeLocation().toURI() );
        client.checkout( branchLocation, checkout );
        assertWTEqual( testEnv.getWorkingTreeLocation(), checkout );

        // now do a commit in the checkedout branch, and see if it's
        // commited to the parent branch
        String textToAdd = "this is added text" + System.getProperty( "line.separator" );
        File fileInRoot = new File( checkout, "file_in_root.txt" );
        addContentToFile( fileInRoot, textToAdd );
        client.setWorkDir( checkout );
        client.commit( new File[]{fileInRoot},
                "commit made in the checkedout branch to test client.checkout(lightweight=true)" );
        client.setWorkDir( testEnv.getWorkingTreeLocation().getParentFile() );
        testEnv.checkStatusesExpectedWorkingTree( testEnv.getWorkingTreeLocation() );
        testEnv.checkStatusesExpectedWorkingTree( checkout );
    }

    @Test
    public final void testCommit() throws Exception
    {
        Environment testEnv = new Environment( "basicCommit", getTestConfig() );
        BazaarRevision expectedRevision = BazaarRevision.getRevision( 2 );
        File fileInRoot = setUpCommitTest( testEnv, expectedRevision );

        // commit the changes
        client.commit( new File[]{fileInRoot}, "file_in_root.txt modified by testCommit" );

        // change the expectedWorkingTree to the expected status post commit
        testEnv.getExpectedWorkingTree().setItemRevision( fileInRoot.getName(), expectedRevision );
        Assert.assertEquals( "current revision diffier from expected", expectedRevision,
                client.revno( fileInRoot ) );

        // Check if status is ok
        testEnv.checkStatusesExpectedWorkingTree( testEnv.getWorkingTreeLocation() );
    }

    /**
     * Helper to avoid copy & paste of the commit setup steps
     * @param testEnv
     * @param expectedRevision
     * @return File added in the root
     * @throws Exception
     */
    private File setUpCommitTest( Environment testEnv, BazaarRevision expectedRevision )
            throws Exception
    {
        client.setWorkDir( testEnv.getWorkingTreeLocation() );

        String textToAdd =
                "this is text added to file_in_root.txt" + System.getProperty( "line.separator" );

        // modify a existing file
        File fileInRoot = new File( testEnv.getWorkingTreeLocation(), "file_in_root.txt" );
        addContentToFile( fileInRoot, textToAdd );

        // change the expectedWorkingTree to the expected status
        testEnv.getExpectedWorkingTree()
                .setItemStatus( fileInRoot.getName(), BazaarStatusKind.MODIFIED );
        String currentContent =
                testEnv.getExpectedWorkingTree().getItemContent( fileInRoot.getName() );
        testEnv.getExpectedWorkingTree()
                .setItemContent( fileInRoot.getName(), currentContent + textToAdd );

        // Check if status is ok
        testEnv.checkStatusesExpectedWorkingTree( testEnv.getWorkingTreeLocation() );
        return fileInRoot;
    }

    @Test
    public final void testDiff() throws Exception
    {
        Environment testEnv = new Environment( "Diff", getTestConfig() );
        client.setWorkDir( testEnv.getWorkingTreeLocation() );
        File file = new File( testEnv.getWorkingTreeLocation(), "A/file_in_A" );
        String textToAdd =
                System.getProperty( "line.separator" ) + "content added to test diff" + System
                        .getProperty( "line.separator" );
        addContentToFile( file, textToAdd );
        String aDiff = client.diff( file, null );
        Assert.assertTrue( aDiff.contains( "+content added to test diff" ) );
    }

    @Test
    public final void testInit() throws Exception
    {
        Environment testEnv = new Environment( "simpleInit", getTestConfig() );
        client.setWorkDir( testEnv.getWorkingTreeLocation().getParentFile() );
        try
        {
            client.init( testEnv.getWorkingTreeLocation() );
        }
        catch ( BazaarException bzrCliEx )
        {
            Assert.assertNotNull( bzrCliEx.getMessage() );
            Assert.assertTrue( bzrCliEx.getMessage().contains( "Already a branch:" ) );
        }
    }

    /**
     * Test method for
     * {@link CommandLineClient#log(File, org.emergent.bzr4j.commandline.commands.options.Option...)}.
     */
    @Test
    public final void testLog() throws Exception
    {
        Environment testEnv = new Environment( "log", getTestConfig() );
        client.setWorkDir( testEnv.getWorkingTreeLocation() );
        List<IBazaarLogMessage> logMsgs = client.log( testEnv.getWorkingTreeLocation() );
        Assert.assertEquals( 1, logMsgs.size() );
        for ( IBazaarLogMessage message : logMsgs )
        {
            Assert.assertEquals( "default_branch", message.getBranchNick() );
            Assert.assertEquals( BazaarRevision.getRevision( 1 ).getValue(),
                    message.getRevision().getValue() );
            Assert.assertEquals( getUserName(), message.getCommiter() );
            Assert.assertEquals( "initial import", message.getMessage().trim() );
        }
    }

    @Test
    public final void testMove() throws Exception
    {
        Environment testEnv = new Environment( "simpleMove", getTestConfig() );

        client.setWorkDir( testEnv.getWorkingTreeLocation() );
        // try to move one file
        File orig = new File( testEnv.getWorkingTreeLocation(), "file_in_root.txt" );
        File dest = new File( testEnv.getWorkingTreeLocation(), "A/" );
        client.move( orig, dest );

        testEnv.getExpectedWorkingTree().addItem( "A/file_in_root.txt",
                testEnv.getExpectedWorkingTree().getItemContent( "file_in_root.txt" ) );
        testEnv.getExpectedWorkingTree().removeItem( "file_in_root.txt" );
        testEnv.getExpectedWorkingTree()
                .setItemStatus( "A/file_in_root.txt", BazaarStatusKind.RENAMED );

        testEnv.checkStatusesExpectedWorkingTree( testEnv.getWorkingTreeLocation() );

        // now try to move >1 files
        File[] filesToMove = new File[2];
        filesToMove[0] = new File( testEnv.getWorkingTreeLocation(), "A/B/file_in_B" );
        filesToMove[1] = new File( testEnv.getWorkingTreeLocation(), "A/file_in_A" );
        File destDir = testEnv.getWorkingTreeLocation(); // the branch
        // root
        client.move( filesToMove, destDir );

        testEnv.getExpectedWorkingTree().addItem( "file_in_B",
                testEnv.getExpectedWorkingTree().getItemContent( "A/B/file_in_B" ) );
        testEnv.getExpectedWorkingTree().addItem( "file_in_A",
                testEnv.getExpectedWorkingTree().getItemContent( "A/file_in_A" ) );
        testEnv.getExpectedWorkingTree().removeItem( "A/B/file_in_B" );
        testEnv.getExpectedWorkingTree().removeItem( "A/file_in_A" );
        testEnv.getExpectedWorkingTree().setItemStatus( "file_in_B", BazaarStatusKind.RENAMED );
        testEnv.getExpectedWorkingTree().setItemStatus( "file_in_A", BazaarStatusKind.RENAMED );

        testEnv.checkStatusesExpectedWorkingTree( testEnv.getWorkingTreeLocation() );

        // and the grand finale, try to move a directory
        File origDir = new File( testEnv.getWorkingTreeLocation(), "A/B/C" );
        destDir = new File( testEnv.getWorkingTreeLocation(), "A/E/" );
        client.move( origDir, destDir );

        // null content represents a directory
        testEnv.getExpectedWorkingTree().addItem( "A/E/C", null );
        testEnv.getExpectedWorkingTree().setItemStatus( "A/E/C", BazaarStatusKind.RENAMED );

        testEnv.checkStatusesExpectedWorkingTree( testEnv.getWorkingTreeLocation() );
    }

    @Test
    public final void testNick() throws Exception
    {
        Environment testEnv = new Environment( "simpleNick", getTestConfig() );
        client.setWorkDir( testEnv.getWorkingTreeLocation() );
        String nick = client.nick( null );
        Assert.assertEquals( testEnv.getWorkingTreeLocation().getName().trim(), nick );
    }

    @Test
    public final void testPull() throws Exception
    {
        Environment testEnv = new Environment( "Pull/parent", getTestConfig() );

        File branch = new File( testEnv.getWorkingTreeLocation().getParent(), "branched" );
        client.branch( new BranchLocation( testEnv.getWorkingTreeLocation() ), branch, null );
        // first check if the parent branch and the checkout have the same
        // structure
        assertWTEqual( testEnv.getWorkingTreeLocation(), branch );

        client.setWorkDir( testEnv.getWorkingTreeLocation() );
        // now do a commit in the checkedout branch, and see if it's
        // commited to the parent branch
        String textToAdd = "this is added text" + System.getProperty( "line.separator" );
        File fileInParent = new File( testEnv.getWorkingTreeLocation(), "file_in_root.txt" );
        addContentToFile( fileInParent, textToAdd );
        client.add( new File[]{fileInParent} );
        client.commit( new File[]{fileInParent},
                "commit made in the parent branch to test client.pull" );
        testEnv.getExpectedWorkingTree().addItem( fileInParent.getName(),
                testEnv.getExpectedWorkingTree().getItemContent( fileInParent.getName() )
                        + textToAdd );

        client.setWorkDir( testEnv.getWorkingTreeLocation().getParentFile() );
        testEnv.checkStatusesExpectedWorkingTree( testEnv.getWorkingTreeLocation() );
        testEnv.checkStatusesExpectedWorkingTree( branch );

        client.setWorkDir( branch );
        client.pull( testEnv.getWorkingTreeLocation().toURI() );
        // the revno 2 should be pulled to branched
        Assert.assertEquals( client.revno( testEnv.getWorkingTreeLocation() ),
                client.revno( branch ) );
    }

    @Test
    public final void testPush() throws Exception
    {
        Environment testEnv = new Environment( "push/remote", getTestConfig() );

        File localBranch = new File( testEnv.getWorkingTreeLocation().getParent(), "local" );
        client.branch( new BranchLocation( testEnv.getWorkingTreeLocation() ), localBranch, null );
        assertWTEqual( testEnv.getWorkingTreeLocation(), localBranch );

        client.setWorkDir( localBranch );
        // now do a commit in the checkedout branch, and see if it's
        // commited to the parent branch
        String textToAdd = "this is added text" + System.getProperty( "line.separator" );
        File fileInParent = new File( localBranch, "file_in_root.txt" );
        addContentToFile( fileInParent, textToAdd );
        client.add( new File[]{fileInParent} );
        client.commit( new File[]{fileInParent},
                "commit made in the local branch to test client.push" );
        testEnv.getExpectedWorkingTree().addItem( fileInParent.getName(),
                testEnv.getExpectedWorkingTree().getItemContent( fileInParent.getName() )
                        + textToAdd );

        client.setWorkDir( testEnv.getWorkingTreeLocation().getParentFile() );
        testEnv.checkStatusesExpectedWorkingTree( testEnv.getWorkingTreeLocation() );
        testEnv.checkStatusesExpectedWorkingTree( localBranch );

        client.setWorkDir( localBranch );
        client.push( StringUtil.getAbsoluteURI( testEnv.getWorkingTreeLocation().toURI() ) );

        // the "remote" branch should be in revno 2
        Assert.assertEquals( client.revno( localBranch ),
                client.revno( testEnv.getWorkingTreeLocation() ) );
    }

    @Test
    public final void testRemove() throws Exception
    {
        Environment testEnv = new Environment( "singleFileRemove", getTestConfig() );
        client.setWorkDir( testEnv.getWorkingTreeLocation() );
        // modify a existing file
        File fileInRoot = new File( testEnv.getWorkingTreeLocation(), "file_in_root.txt" );
        Assert.assertTrue( fileInRoot.delete() );
        testEnv.getExpectedWorkingTree()
                .setItemStatus( fileInRoot.getName(), BazaarStatusKind.DELETED );
        // Check if status is ok after modifications
        testEnv.checkStatusesExpectedWorkingTree( testEnv.getWorkingTreeLocation() );
    }

    @Test
    public final void testRevert() throws Exception
    {
        Environment testEnv = new Environment( "singleFileRevert", getTestConfig() );
        client.setWorkDir( testEnv.getWorkingTreeLocation() );
        // modify a existing file
        File fileInRoot = new File( testEnv.getWorkingTreeLocation(), "file_in_root.txt" );
        String textToAdd = "///added text\\\\\\" + System.getProperty( "line.separator" );
        addContentToFile( fileInRoot, textToAdd );
        // change the expectedWorkingTree to the expected status
        testEnv.getExpectedWorkingTree()
                .setItemStatus( fileInRoot.getName(), BazaarStatusKind.MODIFIED );
        String previousContent =
                testEnv.getExpectedWorkingTree().getItemContent( fileInRoot.getName() );
        testEnv.getExpectedWorkingTree()
                .setItemContent( fileInRoot.getName(), previousContent + textToAdd );

        // Check if status is ok after modifications
        testEnv.checkStatusesExpectedWorkingTree( testEnv.getWorkingTreeLocation() );
        // commit the changes
        client.commit( new File[]{fileInRoot}, fileInRoot.getName() + " modified by testRevert" );
        // change the expectedWorkingTree to the expected status post a
        // revert
        testEnv.getExpectedWorkingTree()
                .setItemRevision( fileInRoot.getName(), BazaarRevision.getRevision( 2 ) );
        Assert.assertEquals( "current revision diffier from expected",
                BazaarRevision.getRevision( 2 ), client.revno( fileInRoot ) );
        // Check if status is ok
        testEnv.checkStatusesExpectedWorkingTree( testEnv.getWorkingTreeLocation() );

        // now execute a 'bzr revert'
        client.revert( new File[]{fileInRoot} );
        // setup expected status
        testEnv.getExpectedWorkingTree()
                .setItemRevision( fileInRoot.getName(), BazaarRevision.getRevision( 1 ) );
        testEnv.getExpectedWorkingTree()
                .setItemStatus( fileInRoot.getName(), BazaarStatusKind.UNCHANGED );
        testEnv.getExpectedWorkingTree().setItemContent( fileInRoot.getName(), previousContent );
        // check if all is ok
        testEnv.checkStatusesExpectedWorkingTree( testEnv.getWorkingTreeLocation() );
    }

    /**
     * Test method for
     * {@link org.emergent.bzr4j.commandline.CommandLineClient#revno(java.io.File)}.
     */
    @Test
    public final void testRevno() throws Exception
    {
        Environment testEnv = new Environment( "Revno", getTestConfig() );
        client.setWorkDir( testEnv.getWorkingTreeLocation() );
        BazaarRevision expectedRevision = BazaarRevision.getRevision( 1 );
        testEnv.getExpectedWorkingTree().setItemsRevision( expectedRevision );
        BazaarRevision result = client.revno( testEnv.getWorkingTreeLocation() );
        Assert.assertEquals( Integer.valueOf( expectedRevision.getValue().trim() ),
                Integer.valueOf( result.getValue().trim() ) );
    }

    /**
     * This is a tricky thing to test, because by their nature
     * revision identifiers will change every time you run the test.
     * TODO : Think about ways of making sure it works better, like
     * checking that the user id is in the revision-id
     * @throws Exception
     * @throws BazaarException
     * @throws IOException
     *
     */
    @Test
    public final void testRevisionInfo() throws IOException, BazaarException, Exception
    {
        Environment testEnv = new Environment( "RevisionInfo", getTestConfig() );
        client.setWorkDir( testEnv.getWorkingTreeLocation() );
        BazaarRevision result = client.revisionInfo( testEnv.getWorkingTreeLocation(), null );
        assertNotNull( result );
        assertNotSame( result, BazaarRevision.INVALID );
    }

    @Test
    public final void testStatus() throws Exception
    {
        Environment testEnv = new Environment( "Status", getTestConfig() );
        client.setWorkDir( testEnv.getWorkingTreeLocation() );
        File fileInRoot = new File( testEnv.getWorkingTreeLocation(), "file_in_root.txt" );
        File fileInA = new File( testEnv.getWorkingTreeLocation(), "A/file_in_A" );
        String newContent = "added content" + System.getProperty( "line.separator" );
        addContentToFile( fileInRoot, newContent );
        addContentToFile( fileInA, newContent );
        BazaarTreeStatus status = client.status( new File[]{fileInRoot, fileInA} );
        Assert.assertEquals( "Expected status info for 2 files but..", 2,
                status.getStatus().size() );
        for ( IBazaarStatus elem : status.getStatus() )
        {
            File absoluteFile = new File( elem.getBranchRoot(), elem.getFile().getPath() );
            if ( absoluteFile.equals( fileInRoot ) || absoluteFile.equals( fileInA ) )
            {
                continue;
            }
            else
            {
                Assert.fail( "recieved status for unexpected files!" );
            }
        }
    }

    @Test
    public final void testUnCommit() throws Exception
    {
        Environment testEnv = new Environment( "singleFileRevert", getTestConfig() );
        client.setWorkDir( testEnv.getWorkingTreeLocation() );
        // modify a existing file
        File fileInRoot = new File( testEnv.getWorkingTreeLocation(), "file_in_root.txt" );
        String textToAdd = "///added text\\\\\\" + System.getProperty( "line.separator" );
        addContentToFile( fileInRoot, textToAdd );
        // change the expectedWorkingTree to the expected status
        testEnv.getExpectedWorkingTree()
                .setItemStatus( fileInRoot.getName(), BazaarStatusKind.MODIFIED );
        String previousContent =
                testEnv.getExpectedWorkingTree().getItemContent( fileInRoot.getName() );
        testEnv.getExpectedWorkingTree()
                .setItemContent( fileInRoot.getName(), previousContent + textToAdd );

        // Check if status is ok after modifications
        testEnv.checkStatusesExpectedWorkingTree( testEnv.getWorkingTreeLocation() );
        // commit the changes
        client.commit( new File[]{fileInRoot}, fileInRoot.getName() + " modified by testCommit" );
        // change the expectedWorkingTree to the expected status post a
        // revert
        testEnv.getExpectedWorkingTree()
                .setItemRevision( fileInRoot.getName(), BazaarRevision.getRevision( 2 ) );
        Assert.assertEquals( "current revision diffier from expected",
                BazaarRevision.getRevision( 2 ), client.revno( fileInRoot ) );
        // Check if status is ok
        testEnv.checkStatusesExpectedWorkingTree( testEnv.getWorkingTreeLocation() );

        // now execute a 'bzr uncommit'
        client.unCommit( testEnv.getWorkingTreeLocation() );

        // status should be exactly the same previous the uncommit
        testEnv.checkStatusesExpectedWorkingTree( testEnv.getWorkingTreeLocation() );
    }

    @Test
    public final void testIgnore() throws Exception
    {
        Environment testEnv = new Environment( "Ignore", getTestConfig() );
        client.setWorkDir( testEnv.getWorkingTreeLocation() );
        File newFile = new File( testEnv.getWorkingTreeLocation(), "file_to_ignore" );
        String textToAdd = "///added text\\\\\\" + System.getProperty( "line.separator" );
        addContentToFile( newFile, textToAdd );
        testEnv.getExpectedWorkingTree().addItem( newFile.getName(), textToAdd );
        testEnv.getExpectedWorkingTree()
                .setItemStatus( newFile.getName(), BazaarStatusKind.UNKNOWN );
        testEnv.checkStatusesExpectedWorkingTree( testEnv.getWorkingTreeLocation() );

        client.ignore( newFile.getName() );

        testEnv.getExpectedWorkingTree().removeItem( newFile.getName() );
        testEnv.getExpectedWorkingTree().addItem( ".bzrignore",
                newFile.getName() + "\n" ); // ignore always uses *nix linefeeds
        testEnv.getExpectedWorkingTree().setItemStatus( ".bzrignore", BazaarStatusKind.CREATED );

        testEnv.checkStatusesExpectedWorkingTree( testEnv.getWorkingTreeLocation() );
    }

    @Test
    public final void testIgnored() throws Exception
    {
        Environment testEnv = new Environment( "Ignored", getTestConfig() );
        client.setWorkDir( testEnv.getWorkingTreeLocation() );
        File newFile = new File( testEnv.getWorkingTreeLocation(), "file_to_ignore" );
        String textToAdd = "///added text\\\\\\" + System.getProperty( "line.separator" );
        addContentToFile( newFile, textToAdd );
        testEnv.getExpectedWorkingTree().addItem( newFile.getName(), textToAdd );
        testEnv.getExpectedWorkingTree()
                .setItemStatus( newFile.getName(), BazaarStatusKind.UNKNOWN );
        testEnv.checkStatusesExpectedWorkingTree( testEnv.getWorkingTreeLocation() );

        client.ignore( newFile.getName() );

        testEnv.getExpectedWorkingTree().removeItem( newFile.getName() );
        testEnv.getExpectedWorkingTree().addItem( ".bzrignore",
                newFile.getName() + "\n" ); // ignore always uses *nix line endings
        testEnv.getExpectedWorkingTree().setItemStatus( ".bzrignore", BazaarStatusKind.CREATED );

        testEnv.checkStatusesExpectedWorkingTree( testEnv.getWorkingTreeLocation() );

        // until here, the same code as in testIgnored
        client.commit( new File[]{new File( testEnv.getWorkingTreeLocation(), ".bzrignore" )},
                "Added .bzrignore" );
        testEnv.getExpectedWorkingTree().setItemStatus( ".bzrignore", BazaarStatusKind.NONE );

        testEnv.checkStatusesExpectedWorkingTree( testEnv.getWorkingTreeLocation() );

        Map<String, String> result = client.ignored();
        Assert.assertNotNull( result.get( newFile.getName() ) );
        Assert.assertTrue( result.keySet().size() == 1 );
        Assert.assertTrue( result.get( newFile.getName() ).equals( newFile.getName() ) );
    }

    @Test
    public final void testUnknowns() throws Exception
    {
        Environment testEnv = new Environment( "unknowns", getTestConfig() );
        client.setWorkDir( testEnv.getWorkingTreeLocation() );
        File fileInRoot = new File( testEnv.getWorkingTreeLocation(), "file_in_root_new.txt" );
        File fileInA = new File( testEnv.getWorkingTreeLocation(), "A/file_in_A_new" );
        fileInRoot.createNewFile();
        fileInA.createNewFile();
        String[] unknowns = client.unknowns();
        Assert.assertEquals( "Expected status info for 2 files but..", 2, unknowns.length );
        BazaarTreeStatus status = client.status( null );
        Assert.assertEquals( "Expected status info for 2 files but..", 2,
                status.getStatus().size() );
        for ( IBazaarStatus elem : status.getStatus() )
        {
            if ( !elem.contains( BazaarStatusKind.UNKNOWN ) )
            {
                Assert.fail( "file: " + elem.getAbsolutePath() + " isn't unknown" );
            }
        }
    }

    @Test
    public final void testInfo() throws Exception
    {
        Environment testEnv = new Environment( "info", getTestConfig() );
        IBazaarInfo info = client.info( testEnv.getWorkingTreeLocation(), IInfoOptions.VERBOSE );
        assertNotNull( info );
        assertNotNull( info.getBranchFormat() );
        assertNotNull( info.getBranchHistory() );
        assertNotNull( info.getControlFormat() );
        assertNotNull( info.getLayout() );
        assertNotNull( info.getRepositoryFormat() );
        assertNotNull( info.getWorkingTreeFormat() );
        assertNotNull( info.getFormats() );
        assertNotNull( info.getLocations() );
        assertNotNull( info.getRelatedBranches() );
        assertNotNull( info.getRepositoryStats() );
        assertNotNull( info.getWorkingTreeStats() );
    }

    @Test
    public final void testBind() throws Exception
    {
        Environment testEnv = new Environment( "bind", getTestConfig() );
        client.setWorkDir( testEnv.getWorkingTreeLocation() );
        File branch = new File( testEnv.getWorkingTreeLocation().getParent(), "branch_bind" );
        BranchLocation source = new BranchLocation( testEnv.getWorkingTreeLocation() );
        client.branch( source, branch, null );
        assertWTEqual( testEnv.getWorkingTreeLocation(), branch );
        IBazaarInfo info = client.info( branch );
        assertFalse( testEnv.getWorkingTreeLocation().getAbsolutePath().equals(
                FileUtils.denormalizePath( info.getLocations().getCheckoutOfBranch() ) ) );
        client.setWorkDir( branch );
        client.bind( source );
        info = client.info( branch );
        assertEquals( testEnv.getWorkingTreeLocation().getAbsolutePath(),
                FileUtils.denormalizePath( info.getLocations().getCheckoutOfBranch() ) );
    }

    @Test
    public final void testUnBind() throws Exception
    {
        Environment testEnv = new Environment( "bind", getTestConfig() );
        client.setWorkDir( testEnv.getWorkingTreeLocation() );
        File branch = new File( testEnv.getWorkingTreeLocation().getParent(), "branch_unbind" );
        BranchLocation source = new BranchLocation( testEnv.getWorkingTreeLocation() );
        client.checkout( source, branch );
        assertWTEqual( testEnv.getWorkingTreeLocation(), branch );
        client.setWorkDir( branch );
        IBazaarInfo info = client.info( branch );
        assertEquals( testEnv.getWorkingTreeLocation().getAbsolutePath(),
                FileUtils.denormalizePath( info.getLocations().getCheckoutOfBranch() ) );
        client.unBind();
        info = client.info( branch );
        assertFalse( testEnv.getWorkingTreeLocation().getAbsolutePath().equals(
                FileUtils.denormalizePath( info.getLocations().getCheckoutOfBranch() ) ) );
    }

    @Test
    public final void testSwitchBranch() throws Exception
    {
        Environment testEnv = new Environment( "switch", getTestConfig() );
        client.setWorkDir( testEnv.getWorkingTreeLocation() );
        File branch1 = new File( testEnv.getWorkingTreeLocation().getParent(), "branch1" );
        File branch2 = new File( testEnv.getWorkingTreeLocation().getParent(), "branch2" );
        BranchLocation source = new BranchLocation( testEnv.getWorkingTreeLocation() );
        client.checkout( source, branch1 );
        client.branch( source, branch2, null );
        client.setWorkDir( branch2 );
        File fileToCommit = setUpCommitTest( testEnv, BazaarRevision.getRevision( 2 ) );
        client.commit( new File[]{fileToCommit}, "test commit" );
        client.setWorkDir( branch1 );
        IBazaarInfo info = client.info( new BranchLocation( branch1 ) );
        assertEquals( testEnv.getWorkingTreeLocation().getAbsolutePath(),
                FileUtils.denormalizePath( info.getLocations().getCheckoutOfBranch() ) );
        client.switchBranch( new BranchLocation( branch2 ) );
        IBazaarInfo infoPostSwitch = client.info( new BranchLocation( branch1 ) );
        assertEquals( branch2.getAbsolutePath(),
                FileUtils.denormalizePath( infoPostSwitch.getLocations().getCheckoutOfBranch() ) );
        BazaarRevision expectedRevno = client.revno( new BranchLocation( branch2 ) );
        BazaarRevision actualRevno = client.revno( new BranchLocation( branch1 ) );
        assertEquals( expectedRevno, actualRevno );
    }

    @Test
    public final void testfindMergeBase() throws Exception
    {
        Environment testEnv = new Environment( "findMergeBase/original", getTestConfig() );

        File branch = new File( testEnv.getWorkingTreeLocation().getParent(), "branch" );
        client.branch( new BranchLocation( testEnv.getWorkingTreeLocation() ), branch, null );
        assertWTEqual( testEnv.getWorkingTreeLocation(), branch );

        BazaarRevision revision = client.findMergeBase( new BranchLocation( branch ),
                new BranchLocation( testEnv.getWorkingTreeLocation() ) );
        assertNotNull( revision );
        List<IBazaarLogMessage> logs =
                client.log( new BranchLocation( testEnv.getWorkingTreeLocation() ),
                        ILogOptions.REVISION.with( "1" ), ILogOptions.SHOW_IDS );
        assertEquals( revision.getValue(), logs.get( 0 ).getRevisionId() );
    }

    @Test
    public final void testLs() throws Exception
    {
        Environment testEnv = new Environment( "ls", getTestConfig() );
        File wtPath = testEnv.getWorkingTreeLocation();
        client.setWorkDir( wtPath );
        IBazaarItemInfo[] items = client.ls( wtPath, null );
        ExpectedWorkingTree tree = testEnv.getExpectedWorkingTree();
        for ( IBazaarItemInfo item : items )
        {
            File file = BzrUtil.getRelativeTo( wtPath, new File( item.getPath() ) );
            String path = unixFilePath( file );
            assertNotNull( path, tree.getItem( path ) );
            assertFalse( "".equals( tree.getItem( path ).getPath().trim() ) );
        }
    }

    @Test
    @Ignore
    public final void testMissing()
    {
        Assert.fail( "not Implemented" );
    }

    @Test
    @Ignore
    public final void testUpdate()
    {
        Assert.fail( "not Implemented" );
    }

    @Test
    public final void testMerge() throws Exception
    {
        Environment testEnv = new Environment( "merge", getTestConfig() );

        File branch = new File( testEnv.getWorkingTreeLocation().getParent(), "merge_branched" );
        client.branch( new BranchLocation( testEnv.getWorkingTreeLocation() ), branch, null );
        assertWTEqual( testEnv.getWorkingTreeLocation(), branch );

        String textToAdd = "this is added text" + System.getProperty( "line.separator" );
        File fileInParent = new File( testEnv.getWorkingTreeLocation(), "file_in_root.txt" );
        addContentToFile( fileInParent, textToAdd );
        client.setWorkDir( testEnv.getWorkingTreeLocation() );
        client.add( new File[]{fileInParent} );
        client.commit( new File[]{fileInParent},
                "commit made in the parent branch to test a merge" );

        testEnv.getExpectedWorkingTree().addItem( fileInParent.getName(),
                testEnv.getExpectedWorkingTree().getItemContent( fileInParent.getName() )
                        + textToAdd );

        client.setWorkDir( testEnv.getWorkingTreeLocation().getParentFile() );
        testEnv.checkStatusesExpectedWorkingTree( testEnv.getWorkingTreeLocation() );
        testEnv.checkStatusesExpectedWorkingTree( branch );

        client.setWorkDir( branch );
        client.merge( new BranchLocation(
                StringUtil.getAbsoluteURI( testEnv.getWorkingTreeLocation() ) ) );
        BazaarTreeStatus treeStatus = client.status( new File[]{branch} );
        IBazaarStatus[] status = treeStatus.getStatusAsArray();
        Assert.assertEquals( 1, status.length );
        client.commit( new File[]{branch}, "merge with parent" );
        // both branches should be at revno 2
        Assert.assertEquals( "2", client.revno( branch ).getValue() );
        Assert.assertEquals( client.revno( testEnv.getWorkingTreeLocation() ),
                client.revno( branch ) );
    }

    @Test
    public final void testResolve() throws Exception
    {
        Environment testEnv = new Environment( "resolve", getTestConfig() );

        File branch = new File( testEnv.getWorkingTreeLocation().getParent(), "to_resolve" );
        client.branch( new BranchLocation( testEnv.getWorkingTreeLocation() ), branch, null );
        assertWTEqual( testEnv.getWorkingTreeLocation(), branch );

        String textToAdd = "this is added text" + System.getProperty( "line.separator" );
        File fileInParent = new File( testEnv.getWorkingTreeLocation(), "file_in_root.txt" );
        addContentToFile( fileInParent, textToAdd );
        client.setWorkDir( testEnv.getWorkingTreeLocation() );
        client.add( new File[]{fileInParent} );
        client.commit( new File[]{fileInParent},
                "commit made in the parent branch to test a merge" );

        testEnv.getExpectedWorkingTree().addItem( fileInParent.getName(),
                testEnv.getExpectedWorkingTree().getItemContent( fileInParent.getName() )
                        + textToAdd );

        client.setWorkDir( testEnv.getWorkingTreeLocation().getParentFile() );
        testEnv.checkStatusesExpectedWorkingTree( testEnv.getWorkingTreeLocation() );
        testEnv.checkStatusesExpectedWorkingTree( branch );

        client.setWorkDir( branch );
        File fileInBranch = new File( branch, "file_in_root.txt" );
        addContentToFile( fileInBranch, textToAdd );
        addContentToFile( fileInBranch, textToAdd );
        client.commit( new File[]{fileInBranch},
                "commit made in the branch to produce a conflict" );

        try
        {
            client.merge( new BranchLocation(
                    StringUtil.getAbsoluteURI( testEnv.getWorkingTreeLocation() ) ) );
        }
        catch ( BazaarException e )
        {
            String msg = e.getMessage();
            Assert.assertTrue( "content don't match",
                    msg.contains( "file_in_root.txt" ) && msg.contains( "conflict" ) );
        }
        BazaarTreeStatus treeStatus = client.status( new File[]{branch} );
        Assert.assertEquals( 1, treeStatus.getStatus().size() );
        IBazaarStatus[] status = treeStatus.getStatusAsArray();
        Assert.assertTrue( "status[0] \"" + status[0] + "\" don't have any conflicts",
                status[0].contains( BazaarStatusKind.HAS_CONFLICTS ) );
        List<File> files = new ArrayList<File>( 1 );
        files.add( status[0].getFile() );
        client.resolve( files );
        treeStatus = client.status( new File[]{branch} );
        status = treeStatus.getStatus().toArray( new IBazaarStatus[0] );
        Assert.assertEquals( 1, status.length );
        Assert.assertFalse( "status[0] \"" + status[0] + "\" have a conflict",
                status[0].contains( BazaarStatusKind.HAS_CONFLICTS ) );
        client.commit( new File[]{branch}, "merge with conflict resolution" );
        Assert.assertEquals( "3", client.revno( branch ).getValue() );
    }

    @Test
    @Ignore
    public final void testSend() throws Exception
    {
        Assert.fail( "not Implemented" );
    }

    @Test
    public final void testVersionInfo() throws Exception
    {
        Environment testEnv = new Environment( "version-info", getTestConfig() );
        BazaarVersionInfo info =
                client.versionInfo( new BranchLocation( testEnv.getWorkingTreeLocation() ) );
        assertNotNull( info );
        assertNotNull( info.getBranchNick() );
        assertNotNull( info.getBuildDate() );
        assertNotNull( info.getDate() );
        assertNotNull( info.getRevid() );
        assertNotNull( info.getRevno() );
    }
}
