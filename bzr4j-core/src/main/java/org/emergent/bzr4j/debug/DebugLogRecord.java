/*
 * Copyright (c) 2009 Emergent.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.emergent.bzr4j.debug;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
* @author Patrick Woodworth
*/
public class DebugLogRecord extends LogRecord
{
    private static final String PACKAGE_NAME = DebugLogRecord.class.getPackage().getName();

    /**
     * @serial Class that issued logging call
     */
    private String sourceClassName;

    /**
     * @serial Method that issued logging call
     */
    private String sourceMethodName;

    private transient boolean needToInferCaller = true;

    public DebugLogRecord( Level level, String msg )
    {
        super( level, msg );
    }

    /** {@inheritDoc} */
    public String getSourceClassName()
    {
        if (sourceClassName == null)
        {
            inferCaller();
        }
        return sourceClassName;
    }

    /** {@inheritDoc} */
    public void setSourceClassName( String sourceClassName )
    {
        this.sourceClassName = sourceClassName;
        needToInferCaller = false;
    }

    /** {@inheritDoc} */
    public String getSourceMethodName()
    {
        if (needToInferCaller)
        {
            inferCaller();
        }
        return sourceMethodName;
    }

    /** {@inheritDoc} */
    public void setSourceMethodName( String sourceMethodName )
    {
        this.sourceMethodName = sourceMethodName;
        needToInferCaller = false;
    }

    private boolean isLogInfrastructureClass( String cname )
    {
//            if (cname.equals( DebugLoggerImpl.class.getName() ) || cname.equals(AbstractLoggerImpl.class.getName() ))
//                return true;
        if (cname.startsWith( PACKAGE_NAME ))
            return true;
        return false;
    }

    private void inferCaller()
    {
        // Get the stack trace.
        StackTraceElement stack[] = (new Throwable()).getStackTrace();
        // First, search back to a method in the Logger class.
        int ix = 0;
        while (ix < stack.length)
        {
            StackTraceElement frame = stack[ix];
            String cname = frame.getClassName();
            if (isLogInfrastructureClass( cname ))
            {
                break;
            }
            ix++;
        }
        // Now search for the first frame before the "Logger" class.
        while (ix < stack.length)
        {
            StackTraceElement frame = stack[ix];
            String cname = frame.getClassName();
            if (!isLogInfrastructureClass( cname ))
            {
                // We've found the relevant frame.
                setSourceClassName( cname );
                setSourceMethodName( frame.getMethodName() );
                return;
            }
            ix++;
        }
        // We haven't found a suitable frame, so just punt.  This is
        // OK as we are only committed to making a "best effort" here.
    }
}
