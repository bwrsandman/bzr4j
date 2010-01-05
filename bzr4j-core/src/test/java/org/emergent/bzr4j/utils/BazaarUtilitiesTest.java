/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.utils;

import org.testng.annotations.Test;

import java.io.File;

/**
 * @author Guillermo Gonzalez <guillo.gonzo AT gmail DOT com>
 *
 */
public class BazaarUtilitiesTest
{

    @Test
    public void testGetRelativeUnix()
    {
        File base = new File( "/home/user/bzr/project/trunk" );
        File file = new File( "/home/user/bzr/project/trunk/src_folder/file" );
        File relative = BzrCoreUtil.getRelativeTo( base, file );
        org.testng.Assert.assertEquals( relative, new File( "src_folder/file" ) );
    }

    @Test
    public void testGetRelativeWindows()
    {
        File base = new File( "c:\\bzr\\project\\trunk" );
        File file = new File( "c:\\bzr\\project\\trunk\\src_folder\\file" );
        File relative = BzrCoreUtil.getRelativeTo( base, file );
        if ( File.separatorChar == '\\' )
            org.testng.Assert.assertEquals( relative, new File( "src_folder\\file" ) );
        else
            org.testng.Assert.assertEquals( relative, new File( "src_folder/file" ) );
    }

    @Test
    public void testUriEscapesMetaChar()
    {
        File base = new File( "/home/file%07with%08funkyescapes" );
        String uri = base.toURI().toString();
        org.testng.Assert.assertEquals( uri.substring( uri.lastIndexOf( '/' ) + 1 ), "file%2507with%2508funkyescapes" );

    }
}
