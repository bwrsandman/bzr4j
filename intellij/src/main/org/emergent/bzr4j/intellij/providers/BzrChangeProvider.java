package org.emergent.bzr4j.intellij.providers;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManagerGate;
import com.intellij.openapi.vcs.changes.ChangeProvider;
import com.intellij.openapi.vcs.changes.ChangelistBuilder;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.changes.CurrentContentRevision;
import com.intellij.openapi.vcs.changes.VcsDirtyScope;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcsUtil.VcsUtil;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.BazaarRevision;
import org.emergent.bzr4j.core.BazaarStatusKind;
import org.emergent.bzr4j.core.BazaarTreeStatus;
import org.emergent.bzr4j.core.IBazaarClient;
import org.emergent.bzr4j.core.IBazaarStatus;
import org.emergent.bzr4j.intellij.BzrContentRevision;
import org.emergent.bzr4j.intellij.BzrVcs;
import org.emergent.bzr4j.intellij.utils.IJConstants;
import org.emergent.bzr4j.intellij.utils.IJUtil;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Patrik Beno
 */
public class BzrChangeProvider implements ChangeProvider
{
    private static final Logger LOG = Logger.getInstance( "Bzr4IntelliJ" );

    private BzrVcs m_bzr;

    public BzrChangeProvider( BzrVcs bzr )
    {
        m_bzr = bzr;
    }

    public void doCleanup( List<VirtualFile> files )
    {
    }

    public void getChanges( VcsDirtyScope scope, ChangelistBuilder builder,
            ProgressIndicator indicator, ChangeListManagerGate gate ) throws VcsException
    {
        Set<FilePath> files = getAllFilesInDirtyScope( scope );
        Map<FilePath, Set<FilePath>> roots = sortByRoots( files );

        for ( FilePath root : roots.keySet() )
        {
            try
            {
                Set<FilePath> paths = roots.get( root );
                if ( root == null )
                {
                    LOG.warn( String.format( "Ignoring path %s", root ) );
                    continue;
                }

                File rootFile = root.getIOFile();

                IBazaarClient bzrClient = m_bzr.getBzrClient();
                bzrClient.setWorkDir( rootFile );

                RootContext rootCtx = new RootContext(builder, rootFile, paths);

                Map<String, String> ignoredMap = bzrClient.ignored();
                processIgnored( rootCtx, ignoredMap.keySet() );

                if (paths.isEmpty())
                    continue;

                String statusTarget = ".";
                if (!IJConstants.DISABLE_STATUS_TARGET_OPTIMIZATION )
                {
//                    Log.info( "Optimizing statusTarget!" );
                    String commonPrefix = IJUtil.getCommonParent( paths );
                    if (commonPrefix != null)
                    {
                        String rootPath = rootFile.getPath() + File.separatorChar;
                        commonPrefix = commonPrefix.replace( '/', File.separatorChar );
                        if (commonPrefix.startsWith( rootPath ))
                        {
                            File prefixFile = new File( commonPrefix );
                            while ((prefixFile != null) && !prefixFile.exists())
                            {
                                prefixFile = prefixFile.getParentFile();
                            }
                            if ((prefixFile != null) && prefixFile.getPath().startsWith( rootPath ))
                            {
                                statusTarget = prefixFile.getPath().substring( rootPath.length() );
                            }
                        }
                        if ( statusTarget.length() < 1)
                            statusTarget = ".";
                    }
                }

                BazaarTreeStatus rootStatii = bzrClient.status( new File[]{new File( statusTarget )} );

                for ( IBazaarStatus status : rootStatii.getStatusAsArray() )
                {
                    processStatus( rootCtx, status );
                }
            }
            catch ( BazaarException e )
            {
                LOG.error( e );
                throw new VcsException( e );
            }
        }
    }

    public boolean isModifiedDocumentTrackingRequired()
    {
        return true;
    }

    private Set<FilePath> getAllFilesInDirtyScope( VcsDirtyScope vcsDirtyScope )
    {
        Set<FilePath> files = new HashSet<FilePath>();
        for ( FilePath filePath : vcsDirtyScope.getDirtyFiles() )
        {
            files.add( filePath );
        }
        for ( FilePath filePath : vcsDirtyScope.getRecursivelyDirtyDirectories() )
        {
            files.add( filePath );
        }
        return files;
    }

    private Map<FilePath, Set<FilePath>> sortByRoots( Set<FilePath> files )
    {
        Map<FilePath, Set<FilePath>> roots = new HashMap<FilePath, Set<FilePath>>();

        for ( FilePath fpath : files )
        {
            FilePath root = VcsUtil.getFilePath( IJUtil.root( fpath ) );
            Set<FilePath> paths = roots.get( root );
            if ( paths == null )
            {
                paths = new HashSet<FilePath>();
                roots.put( root, paths );
            }
            paths.add( fpath );
        }
        return roots;
    }

    private void processStatus( RootContext rootCtx, IBazaarStatus status ) throws BazaarException
    {
//        Log.info( "processStatus (%s):  %s", rootCtx.ioroot, status );
        FilePath fpath = VcsUtil.getFilePath( new File( rootCtx.ioroot, status.getPath() ) );
        if ( status.contains( BazaarStatusKind.CREATED ) )
        {
            ContentRevision currentRevision = CurrentContentRevision.create( fpath );
            Change change = new Change( null, currentRevision, FileStatus.ADDED );
            processChange( rootCtx, change, fpath );
        }
        if ( status.contains( BazaarStatusKind.DELETED ) )
        {
            ContentRevision lastRev = rootCtx.getLastContentRevision( fpath );
            Change change = new Change( lastRev, null, FileStatus.DELETED );
            processChange( rootCtx, change, fpath );
        }
        if ( status.contains( BazaarStatusKind.MODIFIED ) )
        {
            ContentRevision lastRev = rootCtx.getLastContentRevision( fpath );
            ContentRevision curRev = CurrentContentRevision.create( fpath );
            Change change = new Change( lastRev, curRev, FileStatus.MODIFIED );
            processChange( rootCtx, change, fpath );
        }
        if ( status.contains( BazaarStatusKind.RENAMED ) )
        {
            ContentRevision currentRevision = CurrentContentRevision.create( fpath );
            ContentRevision lastRev = rootCtx.getLastContentRevision(
                    VcsUtil.getFilePath( new File( rootCtx.ioroot, status.getPreviousPath() ) ) );
            Change change = new Change( lastRev, currentRevision, FileStatus.MODIFIED );
            processChange( rootCtx, change, fpath );
        }
        if ( status.contains( BazaarStatusKind.UNKNOWN ) )
        {
            ContentRevision currentRevision = CurrentContentRevision.create( fpath );
            Change change = new Change( null, currentRevision, FileStatus.UNKNOWN );
            processChange( rootCtx, change, fpath );
        }
        if ( status.contains( BazaarStatusKind.VERSIONED ) )
        {
            ContentRevision currentRevision = CurrentContentRevision.create( fpath );
            Change change = new Change( currentRevision, currentRevision, FileStatus.NOT_CHANGED );
            processChange( rootCtx, change, fpath );
        }
    }

    private void processChange( RootContext rootCtx, Change change, FilePath fpath )
    {
        ChangelistBuilder builder = rootCtx.builder;
//        FilePath afterRevFile = change.getAfterRevision().getFile();
        if (change.getFileStatus() == FileStatus.UNKNOWN)
        {
//            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath( fpath.getPath() );
            VirtualFile virtualFile = fpath.getVirtualFile();
            processUnversioned( rootCtx, virtualFile );
//            LOG.info( "Marking UNKNOWN: " + fpath );
        }
        else if (change.getFileStatus() == FileStatus.IGNORED)
        {
//            String fpath = afterRevFile.getPath();
            VirtualFile virtualFile = fpath.getVirtualFile();
            builder.processIgnoredFile( virtualFile );
//            LOG.info( "Marking IGNORED: " + fpath );
        }
        else
        {
//            LOG.info( "Marking CHANGED: " + fpath );
            builder.processChange( change );
        }
        rootCtx.paths.remove( fpath );
    }

    private void processUnversioned( RootContext rootCtx, VirtualFile virtualFile )
    {
        rootCtx.builder.processUnversionedFile( virtualFile );
        if (virtualFile.isDirectory())
        {
            for (VirtualFile child : virtualFile.getChildren())
            {
                processUnversioned( rootCtx, child );
            }
        }
    }

    private void processIgnored( RootContext rootCtx, Set<String> ignored )
            throws BazaarException
    {
        File root = rootCtx.ioroot;
        for ( String ignoredpath : ignored )
        {
            String path = new File( root, ignoredpath ).getPath();
//            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath( path );
//            rootCtx.builder.processIgnoredFile( virtualFile );
            FilePath fpath = VcsUtil.getFilePath( path );
            ContentRevision cRev = CurrentContentRevision.create( fpath );
            Change change = new Change( null, cRev, FileStatus.IGNORED );
            processChange( rootCtx, change, fpath );
        }
    }

    private class RootContext
    {
        public Map<File, BazaarRevision> revCache = new HashMap<File, BazaarRevision>();
        public ChangelistBuilder builder;
        public File ioroot;
        public Set<FilePath> paths;

        public RootContext(ChangelistBuilder builder, File root, Set<FilePath> paths)
        {
            this.builder = builder;
            this.ioroot = root;
            this.paths = paths;
            ioroot = root;
        }

        private ContentRevision getLastContentRevision( FilePath file )
                throws BazaarException
        {
//            File root = Utils.root( file.getIOFile() );
            BazaarRevision rev = revCache.get( ioroot );
            if ( rev == null ) {
                IBazaarClient client = m_bzr.getBzrClient();
                client.setWorkDir( ioroot );
                rev = client.revno( ioroot );
                revCache.put( ioroot, rev );
            }
            return new BzrContentRevision( m_bzr, file, rev );
        }
    }
}
