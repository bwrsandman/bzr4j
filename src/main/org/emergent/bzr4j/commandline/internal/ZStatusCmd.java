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
import org.emergent.bzr4j.core.BazaarStatus;
import org.emergent.bzr4j.core.BazaarStatusKind;
import org.emergent.bzr4j.core.BazaarTreeStatus;
import org.emergent.bzr4j.core.IBazaarLogMessage;
import org.emergent.bzr4j.core.IBazaarStatus;
import org.emergent.bzr4j.utils.IOUtil;
import org.emergent.bzr4j.utils.BzrUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Patrick Woodworth
 */
public class ZStatusCmd extends Cmd
{
    public ZStatusCmd( File workDir, String... args )
    {
        super( workDir, args );
        addOpts( "status" );
    }

    public ZStatusCmd addOpts( String... opts )
    {
        super.addOpts( opts );
        return this;
    }

    public BazaarTreeStatus execAndBuildTreeStatus() throws BazaarException
    {
        List<String> cmdOpts = m_pb.command();
        if (!cmdOpts.contains( "--short" ))
            cmdOpts.add( 2, "--short" );
        ExecResult res = exec();
        final Set<IBazaarStatus> statii = new HashSet<IBazaarStatus>();
        final List<IBazaarLogMessage> merges = new ArrayList<IBazaarLogMessage>();
//        List<ModifiedFile> result = new ArrayList<ModifiedFile>();
        String[] lines = res.getStdout().split( IOUtil.EOL );
        File rootFile = getTargetBranchRoot();
        for ( String line : lines )
        {
            IBazaarStatus status = ZBazaarStatus.parseLine( rootFile, line );
            if ( status != null )
                statii.add( status );
        }
        return new BazaarTreeStatus( statii, merges );
    }

    protected File getTargetBranchRoot()
    {
        File retval = null;
        List<String> opts = m_pb.command();
        String lastOpt = opts.get( opts.size() - 1 );
        if (!lastOpt.startsWith( "-" ))
        {
            if (m_pb.directory() == null)
            {
                retval = new File( lastOpt );
            }
            else
            {
                retval = new File( m_pb.directory(), lastOpt );
            }
        }
        if (retval == null)
        {
            retval = m_pb.directory();
        }
        return BzrUtil.getRootBranch( retval );
    }
}
