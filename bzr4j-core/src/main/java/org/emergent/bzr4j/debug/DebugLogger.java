package org.emergent.bzr4j.debug;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author Patrick Woodworth
 */
public interface DebugLogger
{
    public void debug( Object o );
    public void debug( String msg, Object... params );
    public void debug( Throwable e, String msg );
    public void debugf( String msg, Object... params );
    public boolean isDebug();
    public void log( Level level, Object o );
    public void log( Level level, String msg, Object... params );
    public void log( Level level, Throwable e, String msg );
    public void logf( Level level, String msg, Object... params );
    public void addHandler( Handler handler );
    public void setLevel( Level level ) throws SecurityException;

    void fine( Object o );

    void fine( String msg, Object... params );

    void fine( Throwable e, String msg );

    void info( Object o );

    void info( String msg, Object... params );

    void info( Throwable e, String msg );

    boolean isLoggable( Level level );

    void log( LogRecord lr );

    String getName();
}
