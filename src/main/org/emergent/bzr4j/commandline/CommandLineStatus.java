/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline;

import static org.emergent.bzr4j.utils.BzrUtil.unixFilePath;

import java.io.File;

import org.emergent.bzr4j.core.BazaarStatus;
import org.emergent.bzr4j.core.BazaarStatusKind;
import org.emergent.bzr4j.core.IBazaarStatus;

/**
 * @author Guillermo Gonzalez
 *
 */
public class CommandLineStatus extends BazaarStatus
{

    private File previousFile;

    private String oldKind, newKind;

    public CommandLineStatus( BazaarStatusKind statusKind, File path, File previousPath,
            String newKind, String oldKind, File branchRoot )
    {
        super( path, branchRoot );
        this.previousFile = previousPath;
        this.newKind = newKind;
        this.oldKind = oldKind;
        this.statuses.add( statusKind );
    }

    public final String getNewKind()
    {
        if ( statuses.contains( BazaarStatusKind.KIND_CHANGED ) )
        {
            return newKind;
        }
        return "";
    }

    public final String getOldKind()
    {
        if ( statuses.contains( BazaarStatusKind.KIND_CHANGED ) )
        {
            return oldKind;
        }
        return "";
    }

    public final String getPreviousPath()
    {
        if ( previousFile != null )
            return unixFilePath( previousFile );
        else
            return "";
    }

    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append( super.toString() );
        if ( !"".equals( getNewKind() ) )
            sb.append( "newkind: " ).append( getNewKind() );
        if ( !"".equals( getOldKind() ) )
            sb.append( "oldkind: " ).append( getOldKind() );
        if ( !"".equals( getPreviousPath() ) )
            sb.append( "prevPath: " ).append( getPreviousPath() );
        return sb.toString();
    }

    public boolean equals( Object obj )
    {
        if ( obj == null )
        {
            return false;
        }
        else if ( obj instanceof IBazaarStatus )
        {
            // this comparison is done only by path (bazaar spit out duplicated
            // status for one file i.e: when a file was modified and has
            // conflicts)
            boolean equalPath = ((IBazaarStatus)obj).getPath().equals( this.getPath() );
            return equalPath && statuses.containsAll( ((IBazaarStatus)obj).getStatuses() );
        }
        else
        {
            return super.equals( obj );
        }

    }

    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((newKind == null) ? 0 : newKind.hashCode());
        result = PRIME * result + ((oldKind == null) ? 0 : oldKind.hashCode());
        result = PRIME * result + ((previousFile == null) ? 0 : previousFile.hashCode());
        result = PRIME * result + ((file == null) ? 0 : file.hashCode());
        result = PRIME * result + ((branchRoot == null) ? 0 : branchRoot.hashCode());
        return result;
    }

    public File getPreviousFile()
    {
        return previousFile;
    }

    public final void merge( IBazaarStatus status )
    {
        if ( status.contains( BazaarStatusKind.KIND_CHANGED ) )
        {
            this.oldKind = status.getOldKind();
            this.newKind = status.getNewKind();
        }
        if ( status.contains( BazaarStatusKind.RENAMED ) )
        {
            previousFile = status.getPreviousFile();
        }
        statuses.addAll( status.getStatuses() );
    }

}
