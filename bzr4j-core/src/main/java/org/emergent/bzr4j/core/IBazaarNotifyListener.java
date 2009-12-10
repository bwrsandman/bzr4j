/**
 * LICENCSE + COPYRIGHT
 */
package org.emergent.bzr4j.core;

import java.io.File;

/**
 * @author Guillermo Gonzalez
 *
 */
public interface IBazaarNotifyListener
{

    /**
     * @param message
     */
    void logMessage( String message );

    /**
     * @param message
     */
    void logError( String message );

    /**
     * @param revision
     * @param path
     */
    void logRevision( long revision, String path );

    /**
     *
     * @param message
     */
    void logCommandOutput( String message );

    /**
     * @param message
     */
    void logCompleted( String message );

    /**
     * @param command
     */
    void setCommand( int command );

    /**
     * @param commandLine
     */
    void logCommandLine( String wrkDir, String commandLine );

    /**
     * @param file
     */
    void onNotify( File file );

}
