/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

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
        File relative = BzrUtil.getRelativeTo( base, file );
        Assert.assertEquals( new File( "src_folder/file" ), relative );
    }

    @Test
    public void testGetRelativeWindows()
    {
        File base = new File( "c:\\bzr\\project\\trunk" );
        File file = new File( "c:\\bzr\\project\\trunk\\src_folder\\file" );
        File relative = BzrUtil.getRelativeTo( base, file );
        if ( File.separatorChar == '\\' )
            Assert.assertEquals( new File( "src_folder\\file" ), relative );
        else
            Assert.assertEquals( new File( "src_folder/file" ), relative );
    }

    @Test
    public void testUriEscapesMetaChar()
    {
        File base = new File( "/home/file%07with%08funkyescapes" );
        String uri = base.toURI().toString();
        Assert.assertEquals( "file%2507with%2508funkyescapes",
                uri.substring( uri.lastIndexOf( '/' ) + 1 ) );

    }
}
