/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.core;

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
import org.testng.annotations.Test;

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

        org.testng.Assert.assertEquals( ann.getNumberOfLines(), expectedContent.length, "Line count" );
        org.testng.Assert.assertEquals( ann.getFile(), file, "File path" );
        for ( int i = 0; i < expectedContent.length; i++ )
        {
            org.testng.Assert.assertEquals( (Object)ann.getAuthor( i ), getBzrMail(), "Author" );
            org.testng.Assert.assertEquals( (Object)ann.getline( i ), expectedContent[i], "Code line" );
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
        org.testng.Assert.assertTrue( strBuilder.toString().trim().equals( expectedContent.trim() ) );
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
        org.testng.Assert.assertEquals( client.revno( fileInRoot ), expectedRevision,
                "current revision diffier from expected" );

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
        org.testng.Assert.assertTrue( aDiff.contains( "+content added to test diff" ) );
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
            org.testng.Assert.assertNotNull( bzrCliEx.getMessage() );
            org.testng.Assert.assertTrue( bzrCliEx.getMessage().contains( "Already a branch:" ) );
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
        org.testng.Assert.assertEquals( (Object)logMsgs.size(), 1 );
        for ( IBazaarLogMessage message : logMsgs )
        {
            org.testng.Assert.assertEquals( message.getBranchNick(), "default_branch" );
            org.testng.Assert.assertEquals( message.getRevision().getValue(), BazaarRevision.getRevision( 1 ).getValue() );
            org.testng.Assert.assertEquals( message.getCommiter(), getUserName() );
            org.testng.Assert.assertEquals( message.getMessage().trim(), "initial import" );
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
        org.testng.Assert.assertEquals( nick, testEnv.getWorkingTreeLocation().getName().trim() );
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
        org.testng.Assert.assertEquals( client.revno( branch ), client.revno( testEnv.getWorkingTreeLocation() ) );
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
        org.testng.Assert.assertEquals( client.revno( testEnv.getWorkingTreeLocation() ), client.revno( localBranch ) );
    }

    @Test
    public final void testRemove() throws Exception
    {
        Environment testEnv = new Environment( "singleFileRemove", getTestConfig() );
        client.setWorkDir( testEnv.getWorkingTreeLocation() );
        // modify a existing file
        File fileInRoot = new File( testEnv.getWorkingTreeLocation(), "file_in_root.txt" );
        org.testng.Assert.assertTrue( fileInRoot.delete() );
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
        org.testng.Assert.assertEquals( client.revno( fileInRoot ), BazaarRevision.getRevision( 2 ),
                "current revision diffier from expected" );
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
        org.testng.Assert.assertEquals( Integer.valueOf( result.getValue().trim() ), Integer.valueOf( expectedRevision.getValue().trim() ) );
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
        org.testng.Assert.assertNotNull( result );
        org.testng.Assert.assertNotSame( BazaarRevision.INVALID, result );
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
        org.testng.Assert.assertEquals( status.getStatus().size(), 2, "Expected status info for 2 files but.." );
        for ( IBazaarStatus elem : status.getStatus() )
        {
            File absoluteFile = new File( elem.getBranchRoot(), elem.getFile().getPath() );
            if ( absoluteFile.equals( fileInRoot ) || absoluteFile.equals( fileInA ) )
            {
                continue;
            }
            else
            {
                org.testng.Assert.fail( "recieved status for unexpected files!" );
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
        org.testng.Assert.assertEquals( client.revno( fileInRoot ), BazaarRevision.getRevision( 2 ),
                "current revision diffier from expected" );
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
        org.testng.Assert.assertNotNull( result.get( newFile.getName() ) );
        org.testng.Assert.assertTrue( result.keySet().size() == 1 );
        org.testng.Assert.assertTrue( result.get( newFile.getName() ).equals( newFile.getName() ) );
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
        org.testng.Assert.assertEquals( unknowns.length, 2, "Expected status info for 2 files but.." );
        BazaarTreeStatus status = client.status( null );
        org.testng.Assert.assertEquals( status.getStatus().size(), 2, "Expected status info for 2 files but.." );
        for ( IBazaarStatus elem : status.getStatus() )
        {
            if ( !elem.contains( BazaarStatusKind.UNKNOWN ) )
            {
                org.testng.Assert.fail( "file: " + elem.getAbsolutePath() + " isn't unknown" );
            }
        }
    }

    @Test
    public final void testInfo() throws Exception
    {
        Environment testEnv = new Environment( "info", getTestConfig() );
        IBazaarInfo info = client.info( testEnv.getWorkingTreeLocation(), IInfoOptions.VERBOSE );
        org.testng.Assert.assertNotNull( info );
        org.testng.Assert.assertNotNull( info.getBranchFormat() );
        org.testng.Assert.assertNotNull( info.getBranchHistory() );
        org.testng.Assert.assertNotNull( info.getControlFormat() );
        org.testng.Assert.assertNotNull( info.getLayout() );
        org.testng.Assert.assertNotNull( info.getRepositoryFormat() );
        org.testng.Assert.assertNotNull( info.getWorkingTreeFormat() );
        org.testng.Assert.assertNotNull( info.getFormats() );
        org.testng.Assert.assertNotNull( info.getLocations() );
        org.testng.Assert.assertNotNull( info.getRelatedBranches() );
        org.testng.Assert.assertNotNull( info.getRepositoryStats() );
        org.testng.Assert.assertNotNull( info.getWorkingTreeStats() );
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
        org.testng.Assert.assertFalse( testEnv.getWorkingTreeLocation().getAbsolutePath().equals(
                FileUtils.denormalizePath( info.getLocations().getCheckoutOfBranch() ) ) );
        client.setWorkDir( branch );
        client.bind( source );
        info = client.info( branch );
        org.testng.Assert.assertEquals( FileUtils.denormalizePath( info.getLocations().getCheckoutOfBranch() ), testEnv.getWorkingTreeLocation().getAbsolutePath() );
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
        org.testng.Assert.assertEquals( FileUtils.denormalizePath( info.getLocations().getCheckoutOfBranch() ), testEnv.getWorkingTreeLocation().getAbsolutePath() );
        client.unBind();
        info = client.info( branch );
        org.testng.Assert.assertFalse( testEnv.getWorkingTreeLocation().getAbsolutePath().equals(
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
        org.testng.Assert.assertEquals( FileUtils.denormalizePath( info.getLocations().getCheckoutOfBranch() ), testEnv.getWorkingTreeLocation().getAbsolutePath() );
        client.switchBranch( new BranchLocation( branch2 ) );
        IBazaarInfo infoPostSwitch = client.info( new BranchLocation( branch1 ) );
        org.testng.Assert.assertEquals( FileUtils.denormalizePath( infoPostSwitch.getLocations().getCheckoutOfBranch() ),
                branch2.getAbsolutePath() );
        BazaarRevision expectedRevno = client.revno( new BranchLocation( branch2 ) );
        BazaarRevision actualRevno = client.revno( new BranchLocation( branch1 ) );
        org.testng.Assert.assertEquals( actualRevno, expectedRevno );
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
        org.testng.Assert.assertNotNull( revision );
        List<IBazaarLogMessage> logs =
                client.log( new BranchLocation( testEnv.getWorkingTreeLocation() ),
                        ILogOptions.REVISION.with( "1" ), ILogOptions.SHOW_IDS );
        org.testng.Assert.assertEquals( logs.get( 0 ).getRevisionId(), revision.getValue() );
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
            org.testng.Assert.assertNotNull( tree.getItem( path ), path );
            org.testng.Assert.assertFalse( "".equals( tree.getItem( path ).getPath().trim() ) );
        }
    }

    @Test(groups = "broken")
    public final void testMissing()
    {
        org.testng.Assert.fail( "not Implemented" );
    }

    @Test(groups = "broken")
    public final void testUpdate()
    {
        org.testng.Assert.fail( "not Implemented" );
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
        org.testng.Assert.assertEquals( (Object)status.length, 1 );
        client.commit( new File[]{branch}, "merge with parent" );
        // both branches should be at revno 2
        org.testng.Assert.assertEquals( client.revno( branch ).getValue(), "2" );
        org.testng.Assert.assertEquals( client.revno( branch ), client.revno( testEnv.getWorkingTreeLocation() ) );
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
            org.testng.Assert.assertTrue( msg.contains( "file_in_root.txt" ) && msg.contains( "conflict" ),
                    "content don't match" );
        }
        BazaarTreeStatus treeStatus = client.status( new File[]{branch} );
        org.testng.Assert.assertEquals( (Object)treeStatus.getStatus().size(), 1 );
        IBazaarStatus[] status = treeStatus.getStatusAsArray();
        org.testng.Assert.assertTrue( status[0].contains( BazaarStatusKind.HAS_CONFLICTS ), "status[0] \"" + status[0] + "\" don't have any conflicts" );
        List<File> files = new ArrayList<File>( 1 );
        files.add( status[0].getFile() );
        client.resolve( files );
        treeStatus = client.status( new File[]{branch} );
        status = treeStatus.getStatus().toArray( new IBazaarStatus[0] );
        org.testng.Assert.assertEquals( (Object)status.length, 1 );
        org.testng.Assert.assertFalse( status[0].contains( BazaarStatusKind.HAS_CONFLICTS ),
                "status[0] \"" + status[0] + "\" have a conflict" );
        client.commit( new File[]{branch}, "merge with conflict resolution" );
        org.testng.Assert.assertEquals( client.revno( branch ).getValue(), "3" );
    }

    @Test(groups = "broken")
    public final void testSend() throws Exception
    {
        org.testng.Assert.fail( "not Implemented" );
    }

    @Test
    public final void testVersionInfo() throws Exception
    {
        Environment testEnv = new Environment( "version-info", getTestConfig() );
        BazaarVersionInfo info =
                client.versionInfo( new BranchLocation( testEnv.getWorkingTreeLocation() ) );
        org.testng.Assert.assertNotNull( info );
        org.testng.Assert.assertNotNull( info.getBranchNick() );
        org.testng.Assert.assertNotNull( info.getBuildDate() );
        org.testng.Assert.assertNotNull( info.getDate() );
        org.testng.Assert.assertNotNull( info.getRevid() );
        org.testng.Assert.assertNotNull( info.getRevno() );
    }
}
