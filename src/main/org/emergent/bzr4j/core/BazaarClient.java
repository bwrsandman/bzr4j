/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.core;

import org.emergent.bzr4j.commandline.commands.options.Option;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @author Guillermo Gonzalez
 *
 */
public abstract class BazaarClient implements IBazaarClient
{

    protected File m_workDir;

    public final void setWorkDir( final File workDir )
    {
        m_workDir = workDir;
    }

    public InputStream cat( File file, IBazaarRevisionSpec revision, Option... options )
            throws BazaarException
    {
        return cat( file, revision, null, options );
    }

    public void move( File orig, File dest, Option... options ) throws BazaarException
    {
        move( new File[]{orig}, dest, options );
    }

    public Map<String, List<IBazaarLogMessage>> missing( File workdir, URI otherBranch,
            Option... options ) throws BazaarException
    {
        return missing( workdir, new BranchLocation( otherBranch ), options );
    }

    public void pull( URI location, Option... options ) throws BazaarException
    {
        pull( new BranchLocation( location ), options );
    }

    public void push( URI location, Option... options ) throws BazaarException
    {
        push( new BranchLocation( location ), options );
    }

    public BazaarRevision revno( File location ) throws BazaarException
    {
        return revno( new BranchLocation( location ) );
    }

    public IBazaarInfo info( File location, Option... options ) throws BazaarException
    {
        return info( new BranchLocation( location ), options );
    }

    protected void notImplementedYet() throws BazaarException
    {
        throw new BazaarException( "Not implemented" );
    }
}
