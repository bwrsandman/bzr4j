package org.emergent.bzr4j.core;

import static org.emergent.bzr4j.utils.BzrCoreUtil.unixFilePath;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

/**
 * @author Guillermo Gonzalez
 *
 */
public class BazaarStatus implements IBazaarStatus
{
    protected final EnumSet<BazaarStatusKind> statuses = EnumSet.noneOf( BazaarStatusKind.class );

    protected final File branchRoot;

    protected final File file;

    protected File m_previousFile;

    public static IBazaarStatus create( File path, File branchRoot, BazaarStatusKind... status )
    {
        final List<BazaarStatusKind> statuses = new ArrayList<BazaarStatusKind>( 1 );
        for ( BazaarStatusKind kind : status )
        {
            statuses.add( kind );
        }
        return new BazaarStatus( statuses, path, branchRoot );
    }

    public BazaarStatus( File path, File branchRoot )
    {
        this.branchRoot = branchRoot;
        this.file = path;
    }

    public BazaarStatus( final List<BazaarStatusKind> statuses, File path, File branchRoot )
    {
        this( path, branchRoot );
        this.statuses.addAll( statuses );
    }

    public boolean contains( BazaarStatusKind kind )
    {
        return statuses.contains( kind );
    }

    public String getAbsolutePath()
    {
        return unixFilePath( new File( getBranchRoot(), getPath() ) );
    }

    public File getBranchRoot()
    {
        return branchRoot;
    }

    public File getFile()
    {
        return file;
    }

    public String getNewKind()
    {
        return null;
    }

    public String getOldKind()
    {
        return null;
    }

    public String getPath()
    {
        if ( file != null )
            return unixFilePath( file );
        else
            return "";
    }

    public File getPreviousFile()
    {
        return m_previousFile;
    }

    public String getPreviousPath()
    {
        return getPreviousFile() == null ? null : getPreviousFile().getPath();
    }

    public String getShortStatus()
    {
        final StringBuilder versioned = new StringBuilder();
        final StringBuilder content = new StringBuilder();
        final StringBuilder execute = new StringBuilder();

        for ( BazaarStatusKind kind : statuses )
        {
            if ( kind.getCategory() == BazaarStatusKind.Category.VERSIONED )
            {
                versioned.append( kind.toChar() );
            }
            if ( kind.getCategory() == BazaarStatusKind.Category.CONTENT )
            {
                content.append( kind.toChar() );
            }
            if ( kind.getCategory() == BazaarStatusKind.Category.EXECUTABLE )
            {
                execute.append( kind.toChar() );
            }
        }

        return versioned.append( content.toString() ).append( execute.toString() ).toString();
    }

    public Collection<? extends BazaarStatusKind> getStatuses()
    {
        return statuses;
    }

    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append( getShortStatus() ).append( " " );
        sb.append( getPath() ).append( " " );
        return sb.toString();
    }

}
