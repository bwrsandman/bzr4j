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
package org.emergent.bzr4j.commandline.internal;

import org.emergent.bzr4j.core.BazaarException;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @author Patrick Woodworth
 */
public class Cmd
{
    ProcessBuilder m_pb;

    public Cmd( File workDir, String... args )
    {
        m_pb = new ProcessBuilder( args );
        if (workDir != null)
            m_pb.directory( workDir );
    }

    public File directory()
    {
        return m_pb.directory();
    }

    public ProcessBuilder directory( File directory )
    {
        return m_pb.directory( directory );
    }

    public Cmd addOpts( String... opts )
    {
        m_pb.command().addAll( Arrays.asList( opts ) );
        return this;
    }

    public List<String> opts()
    {
        return m_pb.command();
    }

    public ExecResult exec() throws BazaarException
    {
        return exec(false);
    }

    public ExecResult exec( boolean errcheck ) throws BazaarException
    {
        ExecResult retval = new ExecResult();
        CommandExecutor executor = new CommandExecutor();
        executor.execAndWait( retval, m_pb );
        if (errcheck)
        {
            retval.validateEmptyStdErr();
            retval.validateNonZeroExitCode();
        }
        return retval;
    }
}
