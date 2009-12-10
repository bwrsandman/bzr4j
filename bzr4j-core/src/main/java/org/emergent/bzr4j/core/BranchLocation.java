/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.core;

import static org.emergent.bzr4j.utils.StringUtil.getAbsoluteURI;

import java.io.File;
import java.io.Serializable;
import java.net.URI;

/**
 * @author Guillermo Gonzalez
 * @author Patrick Woodworth
 */
public class BranchLocation implements Comparable<BranchLocation>, Serializable
{

    private static final long serialVersionUID = -881174278540933857L;

    public static final String[] REMOTE_SCHEMES =
            new String[]{"http", "sftp", "ftp", "rsync", "https", "bzr+ssh", "bzr+http", "bzr+https"};

    public static final String LP_SCHEME = "lp";

    private final String location;

    public BranchLocation( File location )
    {
        if ( location == null )
        {
            this.location = null;
        }
        else
        {
            this.location = getAbsoluteURI( location ).toString();
        }
    }

    public BranchLocation( URI location )
    {
        if ( location == null )
        {
            this.location = null;
        }
        else
        {
            this.location = getAbsoluteURI( location ).toString();
        }
    }

    public BranchLocation( String location )
    {
        if ( location == null )
        {
            this.location = null;
        }
        else if ( location.startsWith( LP_SCHEME ) )
        {
            this.location = location;
        }
        else
        {
            this.location = getAbsoluteURI( location ).toString();
        }
    }

    public URI getURI()
    {
        if ( location == null )
        {
            return null;
        }
        return getAbsoluteURI( this.location );
    }

    @Override
    public String toString()
    {
        return this.location;
    }

    public int compareTo( BranchLocation other )
    {
        if ( location == null )
        {
            return -1;
        }
        else
        {
            return this.location.compareTo( other.toString() );
        }
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((location == null) ? 0 : location.hashCode());
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        BranchLocation other = (BranchLocation)obj;
        if ( location == null )
        {
            if ( other.location != null )
                return false;
        }
        else if ( !location.equals( other.location ) )
            return false;
        return true;
    }

}
