package org.emergent.bzr4j.testUtils;

import org.emergent.bzr4j.commandline.CommandLineClient;
import org.emergent.bzr4j.core.BazaarClientPreferences;
import org.emergent.bzr4j.core.BazaarPreference;
import org.emergent.bzr4j.core.IBazaarClient;
import org.emergent.bzr4j.debug.LogUtil;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

/**
 * configuration parameters that are common to all tests
 */
public final class TestsConfig
{
    private static final LogUtil log = LogUtil.getLogger( TestsConfig.class.getName() );

    private static TestsConfig testsConfig;

    public String clientType;

    public File rootDir;

    public String rootDirName;

    public File workingCopies;

    public IBazaarClient client;

    public File defaultWorkingTreeLocation;

    public ExpectedWorkingTree defaultWorkingTree;

    // default username
    private static String username = "bzr-eclipse-user";

    private static String usermail = "bzr-eclipse-user@bzr.org";

    static
    {
        log.debug( "Getting environment settings" );
        final BazaarClientPreferences prefs = BazaarClientPreferences.getInstance();
        try
        {
            InputStream is = TestsConfig.class.getResourceAsStream( "/tests.properties" );
            Properties fileProps = new Properties();
            if ( is != null )
            {
                fileProps.load( is );
                prefs.setFrom( fileProps );
            }

        }
        catch ( Exception e )
        {
            log.error( e.getMessage(), e );
        }

        if ( prefs.getString( BazaarPreference.TEST_USER_NAME ) != null )
        {
            username = prefs.getString( BazaarPreference.TEST_USER_NAME );
        }
        if ( prefs.getString( BazaarPreference.TEST_USER_MAIL ) != null )
        {
            usermail = prefs.getString( BazaarPreference.TEST_USER_MAIL );
        }
        prefs.set( BazaarPreference.BZR_EMAIL, getUserName() );
        if ( prefs.getString( BazaarPreference.EXECUTABLE ) == null )
        {
            prefs.set( BazaarPreference.EXECUTABLE, "bzr" );
        }
    }

    private TestsConfig() throws Exception
    {
        rootDir = FileUtils.createTempDir( "bazaar_client_tests", "" );

        workingCopies = new File( rootDir, "working_copies" );
        if ( workingCopies.exists() )
        {
            log.info( "working_copies dir already exists, deleting it..." );
            FileUtils.removeDirectoryWithContent( workingCopies );
        }
        workingCopies.mkdirs();

        // build the sample repository that will be imported
        defaultWorkingTreeLocation = new File( workingCopies, "default_branch" );
        defaultWorkingTree = ExpectedStructureFactory.getWorkingTree();
        client = new CommandLineClient();
    }

    /**
     * Create an example file tree.
     * @throws Exception If the working tree cannot be created
     * @see #deleteWorkingTree()
     */
    public void createWorkingTree() throws Exception
    {
        defaultWorkingTreeLocation.mkdir();
        defaultWorkingTree.materialize( defaultWorkingTreeLocation );

        log.debug( "Creating branch:" + defaultWorkingTreeLocation.toString() );
        File[] filesToAdd = defaultWorkingTreeLocation.listFiles();
        client.setWorkDir( defaultWorkingTreeLocation );
        client.init( defaultWorkingTreeLocation );
        client.add( filesToAdd );
        client.commit( new File[]{defaultWorkingTreeLocation}, "initial import" );
    }

    /**
     * Delete the example file tree.
     * @see #createWorkingTree()
     */
    public void deleteWorkingTree()
    {
        FileUtils.removeDirectoryWithContent( rootDir );
    }

    public static TestsConfig getTestsConfig() throws Exception
    {
        if ( testsConfig == null )
        {
            testsConfig = new TestsConfig();
        }
        return testsConfig;
    }

    public static String getUserName()
    {
        return username + " <" + usermail + ">";
    }

    public static String getBzrMail()
    {
        return usermail;
    }
}
