/**
 *
 */
package org.emergent.bzr4j.utils;

import org.emergent.bzr4j.core.BranchLocation;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URI;

/**
 * @author Guillermo Gonzalez <guillo.gonzo AT gmail DOT com>
 *
 */
public class StringUtilTest
{

    /**
     * Test method for {@link org.emergent.bzr4j.utils.StringUtil#getAbsoluteURI(java.lang.String)}.
     */
    @Test
    public final void testGetAbsoluteURIString()
    {
        URI expected = URI.create( "/Users/username/test_dir" );
        String uriStr = "/Users/username/test_dir";
        URI actual = StringUtil.getAbsoluteURI( uriStr );
        org.testng.Assert.assertEquals( actual.toString(), expected.toString() );
        expected = URI.create( "file:///Users/username/test_dir" );
        uriStr = "file:/Users/username/test_dir";
        actual = StringUtil.getAbsoluteURI( uriStr );
        org.testng.Assert.assertEquals( actual.toString(), expected.toString() );
        uriStr = "file://Users/username/test_dir";
        actual = StringUtil.getAbsoluteURI( uriStr );
        org.testng.Assert.assertEquals( actual.toString(), expected.toString() );
        uriStr = "file:///Users/username/test_dir";
        actual = StringUtil.getAbsoluteURI( uriStr );
        org.testng.Assert.assertEquals( actual.toString(), expected.toString() );
        // uri with spaces (Bug #294061)
        expected = URI.create( "/Users/user%20name/test_dir" );
        uriStr = "/Users/user name/test_dir";
        actual = StringUtil.getAbsoluteURI( uriStr );
        org.testng.Assert.assertEquals( actual, expected );
    }

    /**
     * Test method for {@link org.emergent.bzr4j.utils.StringUtil#getAbsoluteURI(java.io.File)}.
     */
    @Test
    public final void testGetAbsoluteURIFile()
    {
        String path = "/Users/username/test_dir";
        if ( System.getProperty( "os.name" ).startsWith( "Windows" ) )
        {
            path = "/c:/Users/username/test_dir";
        }
        File file = new File( path );
        URI expected = URI.create( "file://" + path );
        URI actual = StringUtil.getAbsoluteURI( file );
        org.testng.Assert.assertEquals( actual.toString(), expected.toString() );
        file = new File( URI.create( "file:" + path ) );
        actual = StringUtil.getAbsoluteURI( file );
        org.testng.Assert.assertEquals( actual.toString(), expected.toString() );
    }

    /**
     * Test method for {@link org.emergent.bzr4j.utils.StringUtil#getAbsoluteURI(java.net.URI)}.
     */
    @Test
    public final void testGetAbsoluteURIURI()
    {
        URI expected = URI.create( "file:///Users/username/test_dir" );
        URI actual = StringUtil.getAbsoluteURI( URI.create( "file:///Users/username/test_dir" ) );
        org.testng.Assert.assertEquals( actual.toString(), expected.toString() );
        actual = StringUtil.getAbsoluteURI( URI.create( "file://Users/username/test_dir" ) );
        org.testng.Assert.assertEquals( actual.toString(), expected.toString() );
        actual = StringUtil.getAbsoluteURI( URI.create( "file:/Users/username/test_dir" ) );
        org.testng.Assert.assertEquals( actual.toString(), expected.toString() );
    }

    @Test
    public final void testGetAbsoluteURISchemas()
    {
        String[] schemes = BranchLocation.REMOTE_SCHEMES;
        for ( String scheme : schemes )
        {
            URI expected = URI.create( scheme + "://Users/username/test_dir" );
            URI actual =
                    StringUtil.getAbsoluteURI( URI.create( scheme + "://Users/username/test_dir" ) )
                    ;
            org.testng.Assert.assertEquals( actual.toString(), expected.toString() );
        }
        URI expected = URI.create( "file:///Users/username/test_dir" );
        URI actual = StringUtil.getAbsoluteURI( URI.create( "file:///Users/username/test_dir" ) );
        org.testng.Assert.assertEquals( actual.toString(), expected.toString() );
    }

}
