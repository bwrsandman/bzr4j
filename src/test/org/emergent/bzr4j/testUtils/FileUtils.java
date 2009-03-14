/*******************************************************************************
 * Copyright (c) 2004, 2006 Subclipse project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Subclipse project committers - initial API and implementation
 ******************************************************************************/
package org.emergent.bzr4j.testUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Assert;

/**
 * Utility class used to copy an entire directory or files
 */
public class FileUtils
{

    /**
     * Recursively copy all files from one directory to another.
     *
     * @param src
     *            file or directory to copy from.
     * @param dest
     *            file or directory to copy to.
     * @throws IOException
     */
    public static void copyFiles( File src, File dest ) throws IOException
    {
        if ( !src.exists() )
        {
            return;
        }

        if ( src.isDirectory() )
        {
            dest.mkdirs();

            String[] list = src.list();
            for ( int i = 0; i < list.length; i++ )
            {
                File src1 = new File( src, list[i] );
                File dest1 = new File( dest, list[i] );
                copyFiles( src1, dest1 );
            }

        }
        else
        {
            copyFile( src, dest );
        }
    }

    /**
     * copy a file from a source to a destination
     *
     * @param src
     * @param dest
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void copyFile( File src, File dest ) throws FileNotFoundException, IOException
    {

        if ( dest.exists() )
        {
            dest.delete();
        }

        // this part comes from org.apache.tools.ant.util.FileUtils

        FileInputStream in = null;
        FileOutputStream out = null;
        try
        {
            in = new FileInputStream( src );
            out = new FileOutputStream( dest );

            byte[] buffer = new byte[8 * 1024];
            int count = 0;
            do
            {
                out.write( buffer, 0, count );
                count = in.read( buffer, 0, buffer.length );
            }
            while ( count != -1 );
        }
        finally
        {
            if ( out != null )
            {
                out.close();
            }
            if ( in != null )
            {
                in.close();
            }
        }
    }

    /**
     * Remove a directory with all files and directories it may contain.
     *
     * @param localTmp The directory to remove.
     */
    public static void removeDirectoryWithContent( File localTmp )
    {
        if ( localTmp.isDirectory() )
        {
            File[] content = localTmp.listFiles();
            for ( int i = 0; i < content.length; i++ )
                removeDirectoryWithContent( content[i] );
        }
        localTmp.delete();
    }

    /**
     * Create a temporary directory with the given prefix and suffix.
     * @param suffix This will be appended to the end of the directory name.
     * @param prefix This will be prepended to the beginning of the directory name.
     * @return A reference to a new temporary directory
     * @throws IOException If directory creation fails
     */
    public static File createTempDir( String suffix, String prefix ) throws IOException
    {
        File tmpDir;
        File tmpFile = File.createTempFile( "bazaar_client_tests", "" );
        tmpFile.delete();
        tmpDir = new File( tmpFile.getCanonicalPath() );
        tmpDir.mkdirs();
        return tmpDir;
    }

    public static void addContentToFile( File file, String content ) throws FileNotFoundException
    {
        PrintWriter fileWriter = new PrintWriter( new FileOutputStream( file, true ) );
        fileWriter.print( content );
        fileWriter.close();
    }

    /**
     * Asserts that the layout of the WT of both branches is the same
     *
     * @param anotherBranch
     * @param branch
     */
    public static void assertWTEqual( File anotherBranch, File branch )
    {
        String[] newList = branch.list();
        String[] origList = anotherBranch.list();
        for ( int i = 0; i < origList.length; i++ )
        {
            Assert.assertEquals( origList[i], newList[i] );
        }
    }

    public static String denormalizePath( String path )
    {
        String retval = path;
        if (path != null && path.length() > 0 && path.indexOf( "://" ) == -1)
        {
            retval = (new File( path )).getAbsolutePath();
        }
        return retval;
    }
}
