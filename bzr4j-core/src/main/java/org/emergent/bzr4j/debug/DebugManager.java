package org.emergent.bzr4j.debug;

import java.util.WeakHashMap;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author Patrick Woodworth
 */
public class DebugManager
{
    public static final boolean DEBUG_ENABLED = true;

    private static final DebugLogger NULL_LOGGER_SINGLETON = new NullLoggerImpl();

    private static final WeakHashMap<Logger, DebugLogger> sm_loggers = new WeakHashMap<Logger, DebugLogger>();

    @SuppressWarnings({ "PointlessBooleanExpression", "ConstantConditions" })
    public static DebugLogger getLogger( Class clazz )
    {
        return getLogger( clazz.getName() );
    }

    @SuppressWarnings({ "PointlessBooleanExpression", "ConstantConditions" })
    public static DebugLogger getLogger( String name )
    {
        if (!DEBUG_ENABLED)
            return NULL_LOGGER_SINGLETON;

        Logger jdkLogger = Logger.getLogger( name );
        synchronized (sm_loggers)
        {
            DebugLogger retval = sm_loggers.get( jdkLogger );
            if (retval == null)
            {
                retval = new DebugLoggerImpl( name );
                sm_loggers.put( jdkLogger, retval );
            }
            return retval;
        }
    }

    private abstract static class AbstractLoggerImpl implements DebugLogger
    {
        public void debug( Object o )
        {
            log( Level.FINE, o );
        }

        public void debug( String msg, Object... params )
        {
            log( Level.FINE, params );
        }

        public void debug( Throwable e, String msg )
        {
            log( Level.FINE, e, msg );
        }

        public void fine( Object o )
        {
            log( Level.FINE, o );
        }

        public void fine( String msg, Object... params )
        {
            log( Level.FINE, params );
        }

        public void fine( Throwable e, String msg )
        {
            log( Level.FINE, e, msg );
        }

        public void info( Object o )
        {
            log( Level.INFO, o );
        }

        public void info( String msg, Object... params )
        {
            log( Level.INFO, params );
        }

        public void info( Throwable e, String msg )
        {
            log( Level.INFO, e, msg );
        }

        public boolean isLoggable(Level level) {
            return false;
        }

        public void log( LogRecord lr ) {

        }

        public String getName() {
            return null;
        }
    }

    /**
     * A logger that outputs nothing.
     */
    private final static class NullLoggerImpl extends AbstractLoggerImpl implements DebugLogger
    {
        public void debugf( String msg, Object... params )
        {
        }

        public void log( Level level, Object o )
        {
        }

        public void log( Level level, String msg, Object... params )
        {
        }

        public void log( Level level, Throwable e, String msg )
        {
        }

        public void logf( Level level, String msg, Object... params )
        {
        }

        public boolean isDebug()
        {
            return false;
        }

        public void addHandler( Handler handler )
        {
        }

        public void setLevel( Level newLevel ) throws SecurityException
        {
        }
    }

    private final static class DebugLoggerImpl extends AbstractLoggerImpl implements DebugLogger
    {
        private final Logger m_delegate;

        DebugLoggerImpl( String name )
        {
            m_delegate = Logger.getLogger( name );
        }

        public void debugf( String msg, Object... params )
        {
            if (!m_delegate.isLoggable( Level.FINE ))
                return;
            else
            {
                System.out.println( String.format( msg, params ) );
                if (true)
                    return;
            }
            LogRecord lr = new DebugLogRecord( Level.FINE, String.format( msg, params ) );
            lr.setLoggerName( m_delegate.getName() );
            m_delegate.log( lr );
        }

        public void log( Level level, Object o )
        {
            if (!m_delegate.isLoggable( level ))
                return;
            if (o instanceof Throwable)
                log( level, (Throwable)o, "Something was thrown!" );
            else
                log( level, "{0}", o );
        }

        public void log( Level level, String msg, Object... params )
        {
            if (!m_delegate.isLoggable( level ))
                return;
            LogRecord lr = new DebugLogRecord( level, msg );
            lr.setParameters( params );
            lr.setLoggerName( m_delegate.getName() );
            m_delegate.log( lr );
        }

        public void log( Level level, Throwable e, String msg )
        {
            if (!m_delegate.isLoggable( level ))
                return;
            LogRecord lr = new DebugLogRecord( level, msg );
            lr.setThrown( e );
            lr.setLoggerName( m_delegate.getName() );
            m_delegate.log( lr );
        }

        public void logf( Level level, String msg, Object... params )
        {
            if (!m_delegate.isLoggable( level ))
                return;
            LogRecord lr = new DebugLogRecord( level, String.format( msg, params ) );
            lr.setLoggerName( m_delegate.getName() );
            m_delegate.log( lr );
        }

        public boolean isDebug()
        {
            return m_delegate.isLoggable( Level.FINE );
        }

        public void addHandler( Handler handler )
        {
            m_delegate.addHandler( handler );
        }

        public void setLevel( Level newLevel ) throws SecurityException
        {
            m_delegate.setLevel( newLevel );
        }

        public boolean isLoggable(Level level) {
            return m_delegate.isLoggable( level );
        }

        public void log( LogRecord lr ) {
            m_delegate.log( lr );
        }

        public String getName() {
            return m_delegate.getName();
        }
    }
}
