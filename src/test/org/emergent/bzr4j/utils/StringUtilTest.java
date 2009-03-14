/**
 *
 */
package org.emergent.bzr4j.utils;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URI;

import org.junit.Test;
import org.emergent.bzr4j.core.BranchLocation;

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
        assertEquals( expected.toString(), actual.toString() );
        expected = URI.create( "file:///Users/username/test_dir" );
        uriStr = "file:/Users/username/test_dir";
        actual = StringUtil.getAbsoluteURI( uriStr );
        assertEquals( expected.toString(), actual.toString() );
        uriStr = "file://Users/username/test_dir";
        actual = StringUtil.getAbsoluteURI( uriStr );
        assertEquals( expected.toString(), actual.toString() );
        uriStr = "file:///Users/username/test_dir";
        actual = StringUtil.getAbsoluteURI( uriStr );
        assertEquals( expected.toString(), actual.toString() );
        // uri with spaces (Bug #294061)
        expected = URI.create( "/Users/user%20name/test_dir" );
        uriStr = "/Users/user name/test_dir";
        actual = StringUtil.getAbsoluteURI( uriStr );
        assertEquals( expected, actual );
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
        assertEquals( expected.toString(), actual.toString() );
        file = new File( URI.create( "file:" + path ) );
        actual = StringUtil.getAbsoluteURI( file );
        assertEquals( expected.toString(), actual.toString() );
    }

    /**
     * Test method for {@link org.emergent.bzr4j.utils.StringUtil#getAbsoluteURI(java.net.URI)}.
     */
    @Test
    public final void testGetAbsoluteURIURI()
    {
        URI expected = URI.create( "file:///Users/username/test_dir" );
        URI actual = StringUtil.getAbsoluteURI( URI.create( "file:///Users/username/test_dir" ) );
        assertEquals( expected.toString(), actual.toString() );
        actual = StringUtil.getAbsoluteURI( URI.create( "file://Users/username/test_dir" ) );
        assertEquals( expected.toString(), actual.toString() );
        actual = StringUtil.getAbsoluteURI( URI.create( "file:/Users/username/test_dir" ) );
        assertEquals( expected.toString(), actual.toString() );
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
            assertEquals( expected.toString(), actual.toString() );
        }
        URI expected = URI.create( "file:///Users/username/test_dir" );
        URI actual = StringUtil.getAbsoluteURI( URI.create( "file:///Users/username/test_dir" ) );
        assertEquals( expected.toString(), actual.toString() );
    }

}
