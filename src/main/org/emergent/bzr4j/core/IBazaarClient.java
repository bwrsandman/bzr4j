/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.core;

import org.emergent.bzr4j.commandline.commands.options.Option;
import org.emergent.bzr4j.commandline.syntax.IMergeOptions;
import org.emergent.bzr4j.commandline.syntax.IMissingOptions;
import org.emergent.bzr4j.commandline.syntax.IRemoveOptions;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @author Guillermo Gonzalez
 * @author Phan Minh Thang
 */
public interface IBazaarClient
{

    /**
     * Add specified files or directories.
     *
     * @param files
     * @param options
     * @throws BazaarException
     */
    public void add( File[] files, Option... options ) throws BazaarException;

    /**
     * Get a IBazaarAnnoation which conatins the origin of each line in a file
     *
     * @param file
     * @param options
     * @return IBazaarAnnotation
     * @throws BazaarException
     */
    public IBazaarAnnotation annotate( File file, Option... options ) throws BazaarException;

    /**
     * Create a new copy of a branch.
     *
     * @param fromLocation
     * @param toLocation
     * @param revision (if null the last revision is branched)
     * @param options
     * @throws BazaarException
     */
    public void branch( BranchLocation fromLocation, File toLocation, IBazaarRevisionSpec revision,
            Option... options ) throws BazaarException;

    /**
     * Execute the bugs command.
     * @param location The location of the branch.
     * @param options Command options.
     * @return The command output.
     * @throws BazaarException If a problem occurs.
     */
    public List<String> bugs( BranchLocation location, Option... options ) throws BazaarException;

    /**
     * Convert the current branch into a checkout of the supplied {@link BranchLocation}.
     * @param location
     * @param options
     * @throws BazaarException
     * void
     */
    public void bind( BranchLocation location, Option... options ) throws BazaarException;

    /**
     * Get a Reader of the file content from the specified revision.<br>
     * If revision is null the last revision is used
     *
     * @param file
     * @param revision
     * @return a Reader
     * @throws BazaarException
     */
    public InputStream cat( File file, IBazaarRevisionSpec revision, Option... options )
            throws BazaarException;

    /**
     * @see #cat(java.io.File, IBazaarRevisionSpec, org.emergent.bzr4j.commandline.commands.options.Option[])
     *
     * @param file
     * @param revision
     * @param charsetName
     * @param options
     * @return
     * @throws BazaarException
     */
    public InputStream cat( File file, IBazaarRevisionSpec revision, String charsetName,
            Option... options ) throws BazaarException;

    /**
     * Create a new checkout of an existing branch.
     *
     * @param fromLocation
     * @param toLocation
     * @param options
     * @throws BazaarException
     */
    public void checkout( BranchLocation fromLocation, File toLocation, Option... options )
            throws BazaarException;

    /**
     * Commit the changes in the specified files
     *
     * @param files
     * @param message
     * @throws BazaarException
     */
    public void commit( File[] files, String message, Option... options )
            throws BazaarException;

    /**
     * Show differences in the working tree or between revisions.
     *
     * @param file
     * @param range
     * @param options
     * @throws BazaarException
     */
    public String diff( File file, IBazaarRevisionSpec range, Option... options )
            throws BazaarException;

    /**
     * Find a base revision for merging two branches.
     * @param branch
     * @param other
     * @return
     * @throws BazaarException
     */
    public BazaarRevision findMergeBase( BranchLocation branch, BranchLocation other )
            throws BazaarException;

    /**
     * Show information about a working tree, branch or repository.
     *
     * @param location
     * @param options
     * @return IBazaarInfo
     * @throws BazaarException
     */
    public IBazaarInfo info( BranchLocation location, Option... options )
            throws BazaarException;

    /**
     * @see #info(BranchLocation, Option...)
     */
    public IBazaarInfo info( File location, Option... options ) throws BazaarException;

    /**
     * Make a directory into a versioned branch.
     *
     * @param location
     * @param options
     * @throws BazaarException
     */
    public void init( File location, Option... options ) throws BazaarException;

    /**
     * Add the specified file or pattern to .bzrignore
     *
     * @param pattern
     * @throws BazaarException
     */
    public void ignore( String pattern ) throws BazaarException;

    /**
     * <p>
     * Return the ignore patterns and the files affected by them
     * </p>
     *
     * @return
     * @throws BazaarException
     */
    public Map<String, String> ignored() throws BazaarException;

    /**
     * <p>
     * Get the log of a branch, file, or directory.
     * </p>
     *
     * @param location
     * @return List<IBazaarLogMessage>
     * @throws BazaarException
     */
    public List<IBazaarLogMessage> log( File location, Option... options )
            throws BazaarException;

    /**
     * @see #log(java.io.File, org.emergent.bzr4j.commandline.commands.options.Option[])
     */
    public List<IBazaarLogMessage> log( URI location, Option... options )
            throws BazaarException;

    /**
     * @see #log(java.io.File, org.emergent.bzr4j.commandline.commands.options.Option[])
     */
    public List<IBazaarLogMessage> log( BranchLocation location, Option... options )
            throws BazaarException;

    /**
     * This version provides less options, but it allows clients to
     * use a hanlder to get "notified" on each log retrieval.
     *
     * @see #log(BranchLocation, Option...)
     *
     * @param location
     * @param logHandler
     * @param options
     * @throws BazaarException
     */
    public void log( BranchLocation location, IBzrLogMessageHandler logHandler, Option... options )
            throws BazaarException;

    /**
     * An async version of {@link #log(BranchLocation, IBzrLogMessageHandler, Option[])}
     * The execution is done in a worker thread.
     *
     * @param location
     * @param logHandler
     * @param options
     * @throws BazaarException
     */
    public void logAsync( BranchLocation location, IBzrLogMessageHandler logHandler,
            Option... options ) throws BazaarException;

    /**
     * List files in a tree.
     * @param workDir
     * @param options
     * @return
     * @throws BazaarException
     * String[]
     */
    public IBazaarItemInfo[] ls( File workDir, IBazaarRevisionSpec revision, Option... options )
            throws BazaarException;

    /**
     * Perform a three-way merge.
     *
     * @param otherBranch
     * @param options see {@link IMergeOptions}
     * @throws BazaarException
     */
    public void merge( BranchLocation otherBranch, Option... options ) throws BazaarException;

    /**
     * <p>
     * Move or rename a file.
     * </p>
     * <p>
     * If the last argument is a versioned directory, all the other names are
     * moved into it. Otherwise, there must be exactly two arguments and the
     * file is changed to a new name.
     * </p>
     *
     * @param orig
     * @param dest
     * @throws BazaarException
     */
    public void move( File[] orig, File dest, Option... options ) throws BazaarException;

    /**
     * a convenience method for renames or moves for one file see
     * {@link #move(File[], File, Option...)}
     *
     * @param orig
     * @param dest
     * @throws BazaarException
     */
    public void move( File orig, File dest, Option... options ) throws BazaarException;

    /**
     * Gets the unmerged/unpulled revisions between two branches.
     * @param workdir the local branch
     * @param otherBranch the other branch (can be local or remote)
     * @param options that belongs to {@link IMissingOptions}
     * @return {@link Map} with only two keys MINE and OTHER, and the values are a list of logs
     * @throws BazaarException
     */
    public Map<String, List<IBazaarLogMessage>> missing( File workdir, URI otherBranch,
            Option... options ) throws BazaarException;

    /**
     * @see #missing(File, URI, Option...)
     *
     * @param workdir
     * @param otherBranch
     * @param options
     * @return
     * @throws BazaarException
     */
    public Map<String, List<IBazaarLogMessage>> missing( File workdir, BranchLocation otherBranch,
            Option... options ) throws BazaarException;

    /**
     * <p>
     * If newNick is null return the branch nickname, otherwise set the branch
     * nickname to the given newNick
     * </p>
     *
     * @param newNick
     * @return String Branch nickname
     * @throws BazaarException
     */
    public String nick( String newNick ) throws BazaarException;

    /**
     * <p>
     * Turn this branch into a mirror of another branch.
     * </p>
     * <p>
     * This command only works on branches that have not diverged. Branches are
     * considered diverged if the destination branch's most recent commit is one
     * that has not been merged (directly or indirectly) into the parent.
     * </p>
     * <p>
     * If branches have diverged, you can use <code>merge</code> to integrate
     * the changes from one into the other. Once one branch has merged, the
     * other should be able to pull it again.
     * </p>
     * <p>
     * If you want to forget your local changes and just update your branch to
     * match the remote one, use overwrite == true.
     * </p>
     *
     * @throws BazaarException
     */
    public void pull( BranchLocation location, Option... options ) throws BazaarException;

    /**
     * see {@link #pull(BranchLocation, Option...)}
     * @param location as {@link java.net.URI}
     * @param options
     * @throws BazaarException
     */
    public void pull( URI location, Option... options ) throws BazaarException;

    /**
     * Update a mirror of the branch setted in this.workDir.
     *
     * @param location
     * @param options
     * @throws BazaarException
     */
    public void push( URI location, Option... options ) throws BazaarException;

    /**
     * @see #push(URI, Option...)
     *
     * @param location
     * @param options
     * @throws BazaarException
     */
    public void push( BranchLocation location, Option... options ) throws BazaarException;

    /**
     * <p>
     * Make the given files unversioned.
     * </p>
     * <p>
     * This makes bzr stop tracking changes to a versioned file. It does not
     * delete the working copy
     * </p>
     * <p>
     * If onlyAdded is true, only 'added' files will be removed. If you specify
     * both (files != null && onlyAdded == true), then new files in the
     * specified directories will be removed. If the directories are also new,
     * they will also be removed.
     * </p>
     *
     * @param files
     * @param options
     *            look at {@link IRemoveOptions} for posible values
     * @throws BazaarException
     */
    public void remove( File[] files, Option... options ) throws BazaarException;

    /**
     * Mark a conflict as resolved.
     * @param files
     * @param options
     * @throws BazaarException
     */
    public void resolve( List<File> files, Option... options ) throws BazaarException;

    /**
     *
     * @param files
     * @throws BazaarException
     */
    public void revert( File[] files, Option... options ) throws BazaarException;

    /**
     * <p>
     * Returns the current revision for the branch under #location, if location
     * is null the revno for the branch under 'workDir'. (The file could be the
     * branch root)
     * </p>
     *
     * @param location
     * @return BazaarRevision
     * @throws BazaarException
     */
    public BazaarRevision revno( File location ) throws BazaarException;

    /**
     * @see #revno(File)
     */
    public BazaarRevision revno( BranchLocation location ) throws BazaarException;

    /**
     * Create a merge-directive for submiting changes.
     *
     * @param submitBranch
     * @param options
     * @throws BazaarException
     */
    public void send( BranchLocation submitBranch, Option... options ) throws BazaarException;

    /**
     * Returns a BazaarRevision containing the revid: for the nominated
     * location and revision.
     * If revision is null, the current tip revision is returned.
     * @param location
     * @param revision
     * @return
     * @throws BazaarException
     */
    public BazaarRevision revisionInfo( File location, IBazaarRevisionSpec revision )
            throws BazaarException;

    /**
     * Return the status IBazaarStatus for each file. (is recursive in case a
     * File is a directory)
     *
     * @param files
     * @return BazaarStatus[]
     * @throws BazaarException
     */
    public BazaarTreeStatus status( File[] files, Option... options ) throws BazaarException;

    /**
     * Set the branch of a checkout and update.
     *
     * @param location
     * @param options
     * @throws BazaarException
     */
    public void switchBranch( BranchLocation location, Option... options )
            throws BazaarException;

    /**
     * Convert the current checkout into a regular branch.
     * @param options
     * @throws BazaarException
     */
    public void unBind( Option... options ) throws BazaarException;

    /**
     * Remove the last committed revision
     *
     * @param location
     *            the branch root
     * @throws BazaarException
     */
    public void unCommit( File location, Option... options ) throws BazaarException;

    /**
     * List unknown files.
     *
     * @return String[] which contains the paths or null if the {@link File} is
     *         not under version control
     * @throws BazaarException
     */
    public String[] unknowns() throws BazaarException;

    /**
     * Update a tree to have the latest code committed to its branch.
     * @param file - A directory
     * @param options
     * @return a message from stdout as String
     * @throws BazaarException
     */
    public String update( File file, Option... options ) throws BazaarException;

    /**
     * Show version information about this tree.
     * @param location - a branch location
     * @param options
     * @return a {@link BazaarVersionInfo}
     * @throws BazaarException
     */
    public BazaarVersionInfo versionInfo( BranchLocation location, Option... options )
            throws BazaarException;

    /**
     * Add a notification listener
     *
     * @param listener
     */
    public void addNotifyListener( IBazaarNotifyListener listener );

    /**
     * Remove a notification listener
     *
     * @param listener
     */
    public void removeNotifyListener( IBazaarNotifyListener listener );

    /**
     * Add a callback for prompting for username (when neccesary) and password
     *
     * @param callback
     */
    public void addPasswordCallback( IBazaarPromptUserPassword callback );

    /**
     * Set the working directory (the root of the local branch)
     *
     * @param workDir
     */
    public void setWorkDir( File workDir );

}
