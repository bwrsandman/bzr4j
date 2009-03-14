package org.emergent.bzr4j.intellij.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.changes.VcsDirtyScopeManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.RefreshQueue;
import com.intellij.openapi.vfs.newvfs.RefreshSession;
import com.intellij.vcsUtil.VcsUtil;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.BazaarStatusKind;
import org.emergent.bzr4j.core.BazaarTreeStatus;
import org.emergent.bzr4j.core.BranchLocation;
import org.emergent.bzr4j.core.IBazaarStatus;
import org.emergent.bzr4j.intellij.BzrVcs;
import org.emergent.bzr4j.utils.BzrUtil;
import org.emergent.bzr4j.utils.IOUtil;
import org.emergent.bzr4j.utils.LogUtil;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Patrik Beno
 */
public class IJUtil
{
    private static final LogUtil sm_logger = LogUtil.getLogger( IJUtil.class );

    public static final String EOL = System.getProperty( "line.separator" );

    public static File root( FilePath file )
    {
        File f = file.getIOFile();
        return BzrUtil.getRootBranch( f != null ? f : new File( file.getPath() ) );
    }

    public static File root( File file )
    {
        return BzrUtil.getRootBranch( file );
    }

    public static boolean isUnknown( BzrVcs bzr, File file ) throws BazaarException
    {
        BazaarTreeStatus tstat = bzr.getBzrClient().status( new File[]{file} );
        IBazaarStatus[] stats = tstat.getStatusAsArray();
        sm_logger.debug( "status array len: " + stats.length );
        File root = BzrUtil.getRootBranch( file );
        File relPath = BzrUtil.getRelativeTo( root, file );
        String nixRel = BzrUtil.unixFilePath( relPath );
        for ( IBazaarStatus status : stats )
        {
            if (nixRel.equals( status.getPath() ) && status.contains( BazaarStatusKind.UNKNOWN ) )
                return true;
        }
        return false;
    }

    public static void refreshFiles( List<VirtualFile> myFilesToRefresh, final Project project,
            boolean async )
    {
        final List<VirtualFile> toRefreshFiles = new ArrayList<VirtualFile>();
        final List<VirtualFile> toRefreshDirs = new ArrayList<VirtualFile>();
        for ( VirtualFile file : myFilesToRefresh )
        {
            if ( file.isDirectory() )
            {
                sm_logger.debug( "Gonna refresh: " + file.getName() );
                toRefreshDirs.add( file );
            }
            else
            {
                sm_logger.debug( "Gonna refresh: " + file.getName() );
                toRefreshFiles.add( file );
            }
        }
        // if refresh asynchronously, local changes would also be notified that they are dirty asynchronously,
        // and commit could be executed while not all changes are visible
        final RefreshSession session =
                RefreshQueue.getInstance().createSession( async, true, new Runnable()
                {
                    public void run()
                    {
                        if ( project.isDisposed() ) return;
                        filterOutInvalid( toRefreshFiles );
                        filterOutInvalid( toRefreshDirs );

                        final VcsDirtyScopeManager vcsDirtyScopeManager =
                                VcsDirtyScopeManager.getInstance( project );
                        vcsDirtyScopeManager.filesDirty( toRefreshFiles, toRefreshDirs );
                    }
                } );
        session.addAllFiles( myFilesToRefresh );
        session.launch();
    }

    private static void filterOutInvalid( final Collection<VirtualFile> files )
    {
        for ( Iterator<VirtualFile> iterator = files.iterator(); iterator.hasNext(); )
        {
            final VirtualFile file = iterator.next();
            if ( !file.isValid() )
            {
                sm_logger.info( "Refresh root is not valid: " + file.getPath() );
                iterator.remove();
            }
        }
    }

//    public static boolean isVersioned( BzrVcs bzr, File file ) throws BazaarException
//    {
//        String ppath = file.getAbsoluteFile().getParentFile().getPath();
//        if (file.exists())
//        {
//            ExecResult retval =
//                    Commander.getInstance().ls()
//                            .addOpts( "--non-recursive", "--unknown" )
//                            .addOpts( "--kind=", file.isDirectory() ? "directory" : "file" )
//                            .addOpts( ppath ).exec(true);
//            String[] flist = retval.getStdout().split( EOL );
//            if (Arrays.binarySearch( flist, file.getName() ) >= 0)
//                return false;
//            return true;
//        }
//        else
//        {
//            ExecResult retval = Commander.getInstance().deleted().exec( true );
//            String[] flist = retval.getStdout().split( EOL );
//            if (Arrays.binarySearch( flist, file.getName() ) >= 0)
//                return false;
//        }
//    }

    public static boolean isVersioned( BzrVcs bzr, VirtualFile file ) throws BazaarException
    {
        FileStatusManager statMgr = FileStatusManager.getInstance(bzr.getProject());
        final FileStatus fileStatus = statMgr.getStatus(file);
        return fileStatus != FileStatus.UNKNOWN;
    }

    public static boolean isIgnored( BzrVcs bzr, VirtualFile file ) throws BazaarException
    {
        FileStatusManager statMgr = FileStatusManager.getInstance(bzr.getProject());
        final FileStatus fileStatus = statMgr.getStatus(file);
        return fileStatus == FileStatus.UNKNOWN;
    }

    public static String getCommonParent(Collection<FilePath> col) {
        String retval = null;
        for (FilePath path : col)
        {
            String next = path.getPath();
            if (retval != null)
            {
                IOUtil.getCommonParent( retval, next );
            }
            else
            {
                retval = next;
            }
        }
        return retval;
    }

    /**
     * Find longest common prefix of two strings.
     */
    public static String longestCommonPrefix(String s, String t) {
        return s.substring (0, longestCommonPrefixLength (s, t));
    }

    public static int longestCommonPrefixLength(String s, String t) {
        int m = Math.min (s.length (), t.length());
        for (int k = 0; k < m; ++k)
            if (s.charAt (k) != t.charAt (k))
                return k;
        return m;
    }

    @Nullable
    public static BzrVcs getBzrVcs( VirtualFile file )
    {
        for ( Project project : ProjectManager.getInstance().getOpenProjects() )
        {
            AbstractVcs vcs = ProjectLevelVcsManager.getInstance( project ).getVcsFor( file );
            if ( vcs instanceof BzrVcs )
            {
                sm_logger.debug( String.format( "Found BzrVcs for file %s: %s", file, vcs ) );
                return (BzrVcs)vcs;
            }
        }
        return null;
    }

    public static File getIOFile( VirtualFile vf )
    {
        return new File( vf.getPath() ).getAbsoluteFile();
    }

    public static File toFile( VirtualFile vf )
    {
        return new File( vf.getPath() );
    }

    public static FilePath toFilePath( VirtualFile vf )
    {
        return VcsUtil.getFilePath( vf.getPath() );
    }

    public static FilePath toFilePath( File vf )
    {
        return VcsUtil.getFilePath( vf );
    }

    public static BranchLocation toBranchLocation( File file )
    {
        return new BranchLocation( file );
    }

    public static BranchLocation toBranchLocation( VirtualFile file )
    {
        return new BranchLocation( toFile( file ) );
    }

    public static BranchLocation toBranchLocation( FilePath filePath )
    {
        return new BranchLocation( filePath.getIOFile() );
    }

    public static AssertionError notYetImplemented()
    {
        return new AssertionError();
    }

    public static AssertionError notYetHandled( Throwable t )
    {
        return new AssertionError( t );
    }
}
