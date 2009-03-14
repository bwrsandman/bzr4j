package org.emergent.bzr4j.testUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.Assert;

import org.emergent.bzr4j.core.BazaarRevision;
import org.emergent.bzr4j.core.BazaarStatusKind;
import org.emergent.bzr4j.core.IBazaarStatus;

/**
 * This class describe the expected state of the working copy
 */
public class ExpectedWorkingTree implements ExpectedStructure
{
    /**
     * the map of the items in the working tree.<br>
     * The relative path is the key for the map
     */
    protected Map<String, Item> items = new HashMap<String, Item>();

    /**
     * @param root
     * @throws IOException
     */
    public void materialize( File root ) throws IOException
    {
        // generate all directories first
        Iterator<Item> it = items.values().iterator();
        while ( it.hasNext() )
        {
            Item item = it.next();
            if ( item.myContent == null )
            {
                // is a directory
                File dir = new File( root, item.myPath );
                if ( !dir.exists() )
                    dir.mkdirs();
            }
        }
        // generate all files with the content in the second run
        it = items.values().iterator();
        while ( it.hasNext() )
        {
            Item item = it.next();
            if ( item.myContent != null )
            {
                // is a file
                File file = new File( root, item.myPath );
                PrintWriter pw = new PrintWriter( new FileOutputStream( file ) );
                pw.print( item.myContent );
                pw.close();
            }
        }
    }

    /**
     * Add a new item to the working copy
     *
     * @param path
     *            the path of the item
     * @param content
     *            the content of the item. A null content signifies a directory
     */
    public void addItem( String path, String content )
    {
        new Item( path, content );
    }

    /**
     * Returns the item at a path
     *
     * @param path
     *            the path, where the item is searched
     * @return the found item
     */
    public Item getItem( String path )
    {
        return (Item)items.get( path );
    }

    /**
     * @return the number of items in WT
     */
    public int size()
    {
        return items.size();
    }

    /**
     * Remove the item at a path
     *
     * @param path
     *            the path, where the item is removed
     */
    public void removeItem( String path )
    {
        items.remove( path );
    }

    /**
     * Set text (content) status of the item at a path
     *
     * @param path
     *            the path, where the status is set
     * @param kind
     *            the new text status
     */
    public void setItemStatus( String path, BazaarStatusKind kind )
    {
        if ( kind.getCategory() == BazaarStatusKind.Category.CONTENT )
            ((Item)items.get( path )).contentStatus = kind;
        if ( kind.getCategory() == BazaarStatusKind.Category.VERSIONED )
            ((Item)items.get( path )).versionStatus = kind;
        if ( kind.getCategory() == BazaarStatusKind.Category.EXECUTABLE )
            ((Item)items.get( path )).executeStatus = kind;

    }

    /**
     * Set the revision number of the item at a path
     *
     * @param path
     *            the path, where the revision number is set
     * @param revision
     *            the new revision number
     */
    public void setItemRevision( String path, BazaarRevision revision )
    {
        ((Item)items.get( path )).baseRevision = revision;
    }

    /**
     * Returns the file content of the item at a path
     *
     * @param path
     *            the path, where the content is retrieved
     * @return the content of the file
     */
    public String getItemContent( String path )
    {
        return ((Item)items.get( path )).myContent;
    }

    /**
     * Set the file content of the item at a path
     *
     * @param path
     *            the path, where the content is set
     * @param content
     *            the new content
     */
    public void setItemContent( String path, String content )
    {
        // since having no content signals a directory, changes of removing the
        // content or setting a former not set content is not allowed. That
        // would change the type of the item.
        Assert.assertNotNull( "cannot unset content", content );
        Item i = (Item)items.get( path );
        Assert.assertNotNull( "cannot set content on directory", i.myContent );
        i.myContent = content;
    }

    public void setItemsRevision( BazaarRevision rev )
    {
        // generate all files with the content in the second run
        Iterator<Item> iter = items.values().iterator();
        while ( iter.hasNext() )
        {
            Item item = iter.next();
            item.baseRevision = rev;
        }
    }

    /**
     * Set the expected node kind at a path
     *
     * @param path
     *            the path, where the node kind is set
     * @param nodeKind
     *            the expected node kind
     *//*
	public void setItemNodeKind(String path, BazaarNodeKind nodeKind) {
		Item i = (Item) items.get(path);
		i.nodeKind = nodeKind;
	}*/
    public void check( IBazaarStatus[] tested, String workingTreePath ) throws Exception
    {
        if ( tested == null || tested.length == 0 )
            return;
        for ( IBazaarStatus status : tested )
        {
            // TODO: test path, kinds (old, new), etc
            String path = status.getPath();

            Item item = (Item)items.get( path );
            Assert.assertNotNull( "status not found for " + path, item );
            Assert.assertEquals( "mismatch in status for " + path,
                    getShortStatus( item.versionStatus, item.contentStatus, item.executeStatus ),
                    status
                            .getShortStatus() );

            if ( item.myContent != null && (item.contentStatus != null && !item.contentStatus
                    .equals( BazaarStatusKind.DELETED )) )
            {
                // file
                // if (item.nodeKind != BazaarNodeKind.NONE) {
                // Assert.assertEquals("state says file, working copy not for "
                // + path,
                // item.nodeKind, status.getNodeKind());
                // }
                final File input = new File( workingTreePath, item.myPath );
                final BufferedReader rd = new BufferedReader( new FileReader( input ) );
                final StringBuilder builder = new StringBuilder();
                int c;
                while ( (c = rd.read()) != -1 )
                {
                    builder.append( (char)c );
                }
                rd.close();
                String expected = item.myContent;
                String actual = builder.toString();
                Assert.assertEquals( "content mismatch for " + path, expected, actual );
            }
        }
    }

    /**
     * internal class to discribe a single working copy item
     */
    public class Item
    {
        String myContent;

        String myPath;

        BazaarStatusKind contentStatus = BazaarStatusKind.NONE;

        BazaarStatusKind versionStatus = BazaarStatusKind.NONE;

        BazaarStatusKind executeStatus = BazaarStatusKind.NONE;

        BazaarRevision baseRevision = BazaarRevision.INVALID;

        /**
         * create a new item
         *
         * @param path
         * @param content
         */
        protected Item( String path, String content )
        {
            myPath = path;
            myContent = content;
            items.put( path, this );
        }

        private Item( Item existing, ExpectedWorkingTree wt )
        {
            contentStatus = existing.contentStatus;
            versionStatus = existing.versionStatus;
            executeStatus = existing.executeStatus;
            myPath = existing.myPath;
            myContent = existing.myContent;
            wt.items.put( myPath, this );
        }

        protected Item copy( ExpectedWorkingTree owner )
        {
            return new Item( this, owner );
        }

        public String getPath()
        {
            return myPath;
        }

        public String getContent()
        {
            return myContent;
        }
    }

    public ExpectedWorkingTree copy()
    {
        ExpectedWorkingTree newWt = new ExpectedWorkingTree();
        Iterator<Item> it = items.values().iterator();
        while ( it.hasNext() )
        {
            it.next().copy( newWt );
        }
        return newWt;
    }

    // Helper methods
    private static String getShortStatus( BazaarStatusKind versioned, BazaarStatusKind content,
            BazaarStatusKind execute )
    {
        final StringBuilder sb = new StringBuilder();
        if ( versioned != null )
            sb.append( versioned.toChar() );
        if ( content != null )
            sb.append( content.toChar() );
        if ( execute != null )
            sb.append( execute.toChar() );
        return sb.toString().trim();
    }

}
