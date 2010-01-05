/*
 * Copyright (c) 2009 Patrick Woodworth.
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
package org.emergent.bzr4j.core;

import org.emergent.bzr4j.commandline.internal.ExecResult;
import org.emergent.bzr4j.debug.LogUtil;

/**
 * @author Patrick Woodworth
 */
public class BazaarException extends Exception
{
    private static final LogUtil sm_logger = LogUtil.getLogger( BazaarException.class );

    public BazaarException()
    {
    }

    public BazaarException( String message )
    {
        super( message );
    }

    public BazaarException( String message, Throwable cause )
    {
        super( message, cause );
//        super( message );
//        initCause( cause );
    }

    public BazaarException( Throwable cause )
    {
        super( cause );
//        super( cause.getMessage() );
//        initCause( cause );
    }

    public BazaarException( ExecResult res )
    {
        this( "exitCode==" + res.getExitCode() + " ; stderr: " + res.getStderr() );
//        sm_logger.error( "Command failed.", this );
    }

    public BazaarException( ExecResult res, Throwable e )
    {
        this( "exitCode==" + res.getExitCode() + " ; stderr: " + res.getStderr(), e );
//        sm_logger.error( "Command failed.", this );
    }
}
