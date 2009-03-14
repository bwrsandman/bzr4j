/*
 * Copyright 2000-2007 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.buildServer.buildTriggers.vcs.bzr;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.AgentSideCheckoutAbility;
import jetbrains.buildServer.CollectChangesByIncludeRule;
import jetbrains.buildServer.Used;
import jetbrains.buildServer.buildTriggers.vcs.AbstractVcsPropertiesProcessor;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.vcs.CheckoutRules;
import jetbrains.buildServer.vcs.IncludeRule;
import jetbrains.buildServer.vcs.ModificationData;
import jetbrains.buildServer.vcs.VcsChange;
import jetbrains.buildServer.vcs.VcsChangeInfo;
import jetbrains.buildServer.vcs.VcsException;
import jetbrains.buildServer.vcs.VcsManager;
import jetbrains.buildServer.vcs.VcsModification;
import jetbrains.buildServer.vcs.VcsRoot;
import jetbrains.buildServer.vcs.VcsSupport;
import jetbrains.buildServer.vcs.VcsSupportUtil;
import jetbrains.buildServer.vcs.patches.PatchBuilder;
import org.emergent.bzr4j.commandline.CommandLineClient;
import org.emergent.bzr4j.commandline.internal.Commander;
import org.emergent.bzr4j.commandline.internal.ExecResult;
import org.emergent.bzr4j.commandline.syntax.IPullOptions;
import org.emergent.bzr4j.core.BazaarClientPreferences;
import org.emergent.bzr4j.core.BazaarException;
import org.emergent.bzr4j.core.BazaarPreference;
import org.emergent.bzr4j.core.BazaarRevision;
import org.emergent.bzr4j.core.BranchLocation;
import org.emergent.bzr4j.core.IBazaarClient;
import org.emergent.bzr4j.core.IBazaarInfo;
import org.emergent.bzr4j.utils.BzrUtil;
import org.emergent.bzr4j.utils.IOUtil;
import org.emergent.bzr4j.utils.LogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Bazaar VCS plugin for TeamCity works as follows:
 * <ul>
 * <li>clones repository to internal storage
 * <li>before any operation with working copy of repository pulls changes from the original repository
 * <li>executes corresponding bzr command
 * </ul>
 *
 * <p>Working copy of repository is created in the $TEAMCITY_DATA_PATH/system/caches/bzr_&lt;hash&gt; folder.
 * <p>Personal builds (remote runs) are not yet supported, they require corresponding functionality from the IDE.
 */
public class BazaarServerVcsSupport extends VcsSupport
        implements AgentSideCheckoutAbility, CollectChangesByIncludeRule
{
    public static final Logger LOG = Loggers.VCS;

    private static final int OLD_WORK_DIRS_CLEANUP_PERIOD = 600;

    private VcsManager m_vcsManager;

    private String m_workDirParent;

    private ConcurrentMap<String, Lock> m_workDirLocks = new ConcurrentHashMap<String, Lock>();

    public BazaarServerVcsSupport(
            @NotNull final VcsManager vcsManager,
            @NotNull ServerPaths paths,
            @NotNull SBuildServer server )
    {
        LogUtil.dumpImportantData( null );

        vcsManager.registerVcsSupport( this );
        m_vcsManager = vcsManager;
        server.getExecutor().scheduleAtFixedRate( new Runnable()
        {
            public void run()
            {
                removeOldWorkFolders();
            }
        }, 0, OLD_WORK_DIRS_CLEANUP_PERIOD, TimeUnit.SECONDS );
        m_workDirParent = paths.getCachesDir();
    }

    public String getName()
    {
        return TCConstants.VCS_NAME;
    }

    @Used("jsp")
    public String getDisplayName()
    {
        return "Bazaar";
    }

    public String getVcsSettingsJspFilePath()
    {
        return "bzrSettings.jsp";
    }

    public String describeVcsRoot( final VcsRoot vcsRoot )
    {
        return "bzr: " + vcsRoot.getProperty( TCConstants.REPOSITORY_PROP );
    }

    public boolean isTestConnectionSupported()
    {
        return true;
    }

    public boolean isAgentSideCheckoutAvailable()
    {
        return false;
    }

    @Override
    public boolean ignoreServerCachesFor( @NotNull final VcsRoot root )
    {
        // since a copy of repository for each VCS root is already stored on disk
        // we do not need separate cache for our patches
        return true;
    }

    @Nullable
    public Map<String, String> getDefaultVcsProperties()
    {
        return Collections.singletonMap( TCConstants.BZR_COMMAND_PATH_PROP, "bzr" );
    }

    @Nullable
    public PropertiesProcessor getVcsPropertiesProcessor()
    {
        return new AbstractVcsPropertiesProcessor()
        {
            public Collection<InvalidProperty> process( Map<String, String> props )
            {
                List<InvalidProperty> retval = new ArrayList<InvalidProperty>();

                if ( isEmpty( props.get( TCConstants.BZR_COMMAND_PATH_PROP ) ) )
                    retval.add( new InvalidProperty( TCConstants.BZR_COMMAND_PATH_PROP,
                            "Path to 'bzr' command must be specified" ) );

                if ( isEmpty( props.get( TCConstants.REPOSITORY_PROP ) ) )
                    retval.add( new InvalidProperty( TCConstants.REPOSITORY_PROP,
                            "Repository must be specified" ) );

                return retval;
            }
        };
    }

    @NotNull
    public Comparator<String> getVersionComparator()
    {
        return new VcsSupportUtil.StringVersionComparator();
    }

    public IBazaarClient getBzrClient( Settings settings )
    {
        BazaarClientPreferences.getInstance().set(
                BazaarPreference.EXECUTABLE,
                settings.getBzrCommandPath() );

        return new CommandLineClient();
    }

    protected void lockWorkDir( @NotNull File workDir )
    {
        getWorkDirLock( workDir ).lock();
    }

    protected void unlockWorkDir( @NotNull File workDir )
    {
        getWorkDirLock( workDir ).unlock();
    }

    private Lock getWorkDirLock( final File workDir )
    {
        String path = workDir.getAbsolutePath();
        Lock lock = m_workDirLocks.get( path );
        if ( lock == null )
        {
            lock = new ReentrantLock();
            Lock curLock = m_workDirLocks.putIfAbsent( path, lock );
            if ( curLock != null )
            {
                lock = curLock;
            }
        }
        return lock;
    }

    public List<ModificationData> collectBuildChanges(
            final VcsRoot root,
            @NotNull final String fromVersion,
            @NotNull final String currentVersion,
            final CheckoutRules checkoutRules ) throws VcsException
    {
        syncClonedRepository( root );
        return VcsSupportUtil.collectBuildChanges( root, fromVersion, currentVersion, checkoutRules, this );
    }

    public List<ModificationData> collectBuildChanges( final VcsRoot root,
            final String fromVersion,
            final String currentVersion,
            final IncludeRule includeRule ) throws VcsException
    {
        LogUtil.LOG.info(
                String.format( "Collecting changes for %s; from version %s to version %s;\n%s",
                    root.getProperty( TCConstants.BRANCH_NAME_PROP ),
                    fromVersion, currentVersion, root.convertToPresentableString() )
        );
        LOG.info(
                String.format( "Collecting changes for %s; from version %s to version %s;\n%s",
                    root.getProperty( TCConstants.BRANCH_NAME_PROP ),
                    fromVersion, currentVersion, root.convertToPresentableString() )
        );
        try
        {
            // first obtain changes between specified versions
            List<ModificationData> result = new ArrayList<ModificationData>();
            Settings settings = createSettings( root );

//            LogCommand lc = new LogCommand( settings );
            String fromId = new ChangeSet( fromVersion ).getId();
//            lc.setFromRevId( fromId );
//            lc.setToRevId( new ChangeSet( currentVersion ).getId() );
//            List<ChangeSet> changeSets = lc.execute();
            ExecResult res = getCommander( settings ).log()
                    .addOpts( "--show-ids" )
                    .addOpts( "-r", fromVersion + ".." + currentVersion )
                    .exec( true );
            List<ChangeSet> changeSets = TCUtil.parseChangeSets( res.getStdout() );
            if ( changeSets.isEmpty() )
            {
                return result;
            }

            Commander commander = getCommander( settings );

            ChangeSet prev = new ChangeSet( fromVersion );
            for ( int i = 0; i < changeSets.size(); i++ )
            {
                ChangeSet cur = changeSets.get( i );
                if ( cur.getId().equals( fromId ) ) continue; // skip already reported changeset

                List<VcsChange> files =
                        TCUtil.getVcsChanges( commander, prev, cur, includeRule );
                if ( files.isEmpty() ) continue;
                ModificationData md = new ModificationData( cur.getTimestamp(), files, cur.getSummary(),
                        cur.getUser(), root, cur.getFullVersion(), cur.getId() );
                result.add( md );
                prev = cur;
            }

            return result;
        }
        catch ( BazaarException e )
        {
            throw new VcsException( e );
        }
    }

    // Thu 2008-04-10 16:30:56 +0300

    @NotNull
    public byte[] getContent( final VcsModification vcsModification,
            final VcsChangeInfo change,
            final VcsChangeInfo.ContentType contentType,
            final VcsRoot vcsRoot ) throws VcsException
    {
        LOG.debug( "getContent(VcsModification,VcsChangeInfo,VcsChangeInfo.ContentType,VcsRoot)" );
        String version = contentType == VcsChangeInfo.ContentType.AFTER_CHANGE
                ? change.getAfterChangeRevisionNumber()
                : change.getBeforeChangeRevisionNumber();
        return getContent( change.getRelativeFileName(), vcsRoot, version );
    }

    @NotNull
    public byte[] getContent( final String filePath, final VcsRoot vcsRoot, final String version )
            throws VcsException
    {
        LOG.debug( "getContent(String,VcsRoot,String)" );
        syncClonedRepository( vcsRoot );
        Settings settings = createSettings( vcsRoot );
        File parentDir = null;
        try
        {
            parentDir = doCat( settings, version, Collections.singletonList( filePath ) );
            File file = new File( parentDir, filePath );
            if ( file.isFile() )
            {
                return FileUtil.loadFileBytes( file );
            }
            else
            {
                LOG.warn( "Unable to obtain content of the file: " + filePath );
            }
        }
        catch ( IOException e )
        {
            throw new VcsException( "Failed to load content of file", e );
        }
        catch ( BazaarException e )
        {
            throw new VcsException( "Failed to load content of file", e );
        }
        finally
        {
            if (parentDir != null)
                FileUtil.delete( parentDir );
        }
        return new byte[0];
    }

    @NotNull
    public String getCurrentVersion( final VcsRoot root ) throws VcsException
    {
        LOG.debug( "getCurrentVersion()" );
        // we will return full version of the most recent change as current version
        syncClonedRepository( root );
        Settings settings = createSettings( root );
        IBazaarClient bzr = getBzrClient( settings );
        try
        {
            BazaarRevision result = bzr.revno( new BranchLocation( settings.getRepository() ) );
            return result.getValue();
        }
        catch ( BazaarException e )
        {
            throw new VcsException( e );
        }
//        RevnoCommand revnoCmd = new RevnoCommand( settings );
//        return revnoCmd.execute();
    }

    @Nullable
    public String testConnection( final VcsRoot vcsRoot ) throws VcsException
    {
        Settings settings = createSettings( vcsRoot );
        IBazaarClient bzr = getBzrClient( settings );
        try
        {
            IBazaarInfo result = bzr.info( new BranchLocation( settings.getRepository() ) );
            return result.getBranchFormat();
        }
        catch ( BazaarException e )
        {
            throw new VcsException( e );
        }
//        InfoCommand id = new InfoCommand( settings );
//        return id.execute();
    }

    public String getVersionDisplayName( final String version, final VcsRoot root )
            throws VcsException
    {
        LOG.debug( "getVersionDisplayName()" );
        return new ChangeSet( version ).getId();
    }

    public void buildPatch( final VcsRoot root,
            @Nullable final String fromVersion,
            @NotNull final String toVersion,
            final PatchBuilder builder,
            final CheckoutRules checkoutRules ) throws IOException, VcsException
    {
        LOG.debug( "buildPatch()" );
        syncClonedRepository( root );
        Settings settings = createSettings( root );
        try
        {
            if ( fromVersion == null )
            {
                buildFullPatch( settings, new ChangeSet( toVersion ), builder );
            }
            else
            {
                    buildIncrementalPatch( settings, new ChangeSet( fromVersion ),
                            new ChangeSet( toVersion ), builder );
            }
        }
        catch ( BazaarException e )
        {
            throw new VcsException( e );
        }
    }

    // builds patch from version to version
    private void buildIncrementalPatch( final Settings settings, @NotNull final ChangeSet fromVer,
            @NotNull final ChangeSet toVer, final PatchBuilder builder )
            throws VcsException, IOException, BazaarException
    {
        Commander commander = getCommander( settings );
        List<ModifiedFile> modifiedFiles = TCUtil.getModifiedFiles( commander, fromVer, toVer );

        List<String> notDeletedFiles = new ArrayList<String>();
        for ( ModifiedFile f : modifiedFiles )
        {
            if ( f.getStatus() != ModifiedFile.Status.REMOVED )
            {
                notDeletedFiles.add( f.getPath() );
            }
        }

        if ( notDeletedFiles.isEmpty() ) return;

        File parentDir = doCat( settings, toVer.getId(), notDeletedFiles );

        try
        {
            for ( ModifiedFile f : modifiedFiles )
            {
                final File virtualFile = new File( f.getPath() );
                if ( f.getStatus() == ModifiedFile.Status.REMOVED )
                {
                    builder.deleteFile( virtualFile, true );
                }
                else
                {
                    File realFile = new File( parentDir, f.getPath() );
                    FileInputStream is = new FileInputStream( realFile );
                    try
                    {
                        builder.changeOrCreateBinaryFile( virtualFile, null, is,
                                realFile.length() );
                    }
                    finally
                    {
                        is.close();
                    }
                }
            }
        }
        finally
        {
            FileUtil.delete( parentDir );
        }
    }

    private File doCat( Settings settings, String myRevId, List<String> relPaths )
            throws IOException, BazaarException
    {
        File tempDir = IOUtil.createTempDirectory( "bazaar", "catresult" );
        for ( String path : relPaths )
        {
            final File parentFile = new File( tempDir, path ).getParentFile();
            if ( !parentFile.isDirectory() && !parentFile.mkdirs() )
            {
                throw new BazaarException(
                        "Failed to create directory: " + parentFile.getAbsolutePath() );
            }
        }

        Commander commander = getCommander( settings );
        for ( String p : relPaths )
        {
            ExecResult eres = commander.cat()
                    .addOpts( "-r", myRevId )
                    .addOpts( IOUtil.deNormalizeSeparator( p ) )
                    .exec( true );
            IOUtil.writeToFile( new File( tempDir.getAbsolutePath(), p ), eres.getByteOut() );
        }
        return tempDir;
    }


    // builds patch by exporting files using specified version
    private void buildFullPatch( final Settings settings, @NotNull final ChangeSet toVer,
            final PatchBuilder builder )
            throws IOException, VcsException, BazaarException
    {
        File tempDir = FileUtil.createTempDirectory( "bazaar", toVer.getId() );
        try
        {
            final File repRoot = new File( tempDir, "rep" );
            getCommander( settings ).export()
                    .addOpts( "-q", "--format=dir" )
                    .addOpts( "-r", toVer.getId() )
                    .addOpts( IOUtil.deNormalizeSeparator( repRoot.getAbsolutePath() ) ).exec(true);

            buildPatchFromDirectory( builder, repRoot, new FileFilter()
            {
                public boolean accept( final File file )
                {
                    return !(file.isDirectory() && ".bzr".equals( file.getName() ));
                }
            } );
        }
        finally
        {
            FileUtil.delete( tempDir );
        }
    }

    private void buildPatchFromDirectory( final PatchBuilder builder, final File repRoot,
            final FileFilter filter ) throws IOException
    {
        buildPatchFromDirectory( repRoot, builder, repRoot, filter );
    }

    private void buildPatchFromDirectory( File curDir, final PatchBuilder builder,
            final File repRoot, final FileFilter filter ) throws IOException
    {
        File[] files = curDir.listFiles( filter );
        if ( files != null )
        {
            for ( File realFile : files )
            {
                String relPath =
                        realFile.getAbsolutePath().substring( repRoot.getAbsolutePath().length() );
                final File virtualFile = new File( relPath );
                if ( realFile.isDirectory() )
                {
                    builder.createDirectory( virtualFile );
                    buildPatchFromDirectory( realFile, builder, repRoot, filter );
                }
                else
                {
                    final FileInputStream is = new FileInputStream( realFile );
                    try
                    {
                        builder.createBinaryFile( virtualFile, null, is, realFile.length() );
                    }
                    finally
                    {
                        is.close();
                    }
                }
            }
        }
    }

    // updates current working copy of repository by pulling changes from the repository specified in VCS root
    private void syncClonedRepository( final VcsRoot root ) throws VcsException
    {
        Settings settings = createSettings( root );
        File workDir = settings.getLocalRepositoryDir();
        lockWorkDir( workDir );
        try
        {
            IBazaarClient bzr = getBzrClient( settings );
            if ( settings.hasCopyOfRepository() )
            {
                bzr.setWorkDir( workDir );
                bzr.pull( new BranchLocation( settings.getRepository() ), IPullOptions.OVERWRITE );
            }
            else
            {
                bzr.branch( new BranchLocation( settings.getRepository() ), workDir.getAbsoluteFile(), null );
            }
        }
        catch ( BazaarException e )
        {
            throw new VcsException( e );
        }
        finally
        {
            unlockWorkDir( workDir );
        }
    }

    private void removeOldWorkFolders()
    {
        File workFoldersParent = new File( m_workDirParent );
        if ( !workFoldersParent.isDirectory() )
            return;

        Set<File> workDirs = new HashSet<File>();
        File[] files = workFoldersParent.listFiles( new FileFilter()
        {
            public boolean accept( final File file )
            {
                return file.isDirectory() && file.getName()
                        .startsWith( Settings.DEFAULT_WORK_DIR_PREFIX );
            }
        } );
        if ( files != null )
        {
            for ( File f : files )
            {
                workDirs.add( BzrUtil.getCanonicalFile( f ) );
            }
        }

        for ( VcsRoot vcsRoot : m_vcsManager.getAllRegisteredVcsRoots() )
        {
            if ( getName().equals( vcsRoot.getVcsName() ) )
            {
                Settings s = createSettings( vcsRoot );
                workDirs.remove( BzrUtil.getCanonicalFile( s.getLocalRepositoryDir() ) );
            }
        }

        for ( File f : workDirs )
        {
            lockWorkDir( f );
            try
            {
                FileUtil.delete( f );
            }
            finally
            {
                unlockWorkDir( f );
            }
        }
    }

    private Settings createSettings( final VcsRoot vcsRoot )
    {
        return createSettings( new File( m_workDirParent ), vcsRoot );
    }

    public static Settings createSettings( File defWorkDirParent, VcsRoot vcsRoot )
    {
        return new Settings( defWorkDirParent, vcsRoot );
    }

    public static Commander getCommander( final Settings settings )
    {
        Commander retval = new Commander()
        {
            public File getDefaultWorkDir()
            {
                return settings.getLocalRepositoryDir();
            }

            @Override
            public String getBzrExePath()
            {
                return settings.getBzrCommandPath();
            }
        };
        return retval;
    }
}
