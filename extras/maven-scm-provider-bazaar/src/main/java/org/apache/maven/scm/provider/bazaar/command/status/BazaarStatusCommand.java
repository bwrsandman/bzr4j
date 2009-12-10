package org.apache.maven.scm.provider.bazaar.command.status;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.ScmFileStatus;
import org.apache.maven.scm.command.Command;
import org.apache.maven.scm.command.status.AbstractStatusCommand;
import org.apache.maven.scm.command.status.StatusScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.bazaar.BazaarUtils;
import org.apache.maven.scm.provider.bazaar.repository.BazaarScmProviderRepository;
import org.apache.maven.scm.provider.bazaar.command.BazaarConstants;
import org.apache.maven.scm.provider.bazaar.command.BazaarConsumer;

import java.io.File;

/**
 * @author <a href="mailto:torbjorn@smorgrav.org">Torbj�rn Eikli Sm�rgrav</a>
 * @version $Id: BazaarStatusCommand.java 685721 2008-08-13 22:57:17Z olamy $
 */
public class BazaarStatusCommand
    extends AbstractStatusCommand
    implements Command
{

    public BazaarStatusCommand()
    {
        super();
    }

    /** {@inheritDoc} */
    public StatusScmResult executeStatusCommand( ScmProviderRepository repo, ScmFileSet fileSet )
        throws ScmException
    {
        BazaarScmProviderRepository repository = (BazaarScmProviderRepository)repo;
        File workingDir = fileSet.getBasedir();

        String[] rootCmd = new String[] { BazaarConstants.ROOT_CMD };
//        commitCmd = BazaarUtils.expandCommandLine( commitCmd, fileSet );

        // keep the command about in string form for reporting
        StringBuffer cmd = BazaarUtils.joinCmd( rootCmd );

        final StringBuffer rootRes = new StringBuffer();

        ScmResult result =
                BazaarUtils.execute( new BazaarConsumer( getLogger() )
                {
                    public void doConsume( ScmFileStatus status, String trimmedLine )
                    {
                        if (rootRes.length() <= 0)
                            rootRes.append( trimmedLine );
                    }
                }, getLogger(), workingDir, rootCmd );

        File rootDir = workingDir;

        if (result.isSuccess())
        {
            rootDir = new File( result.getCommandOutput().trim().replace( '/', File.separatorChar ) );
            getLogger().info( "rootDir: " + rootDir );            
        }



        BazaarStatusConsumer consumer = new BazaarStatusConsumer( getLogger(), rootDir );
        String[] statusCmd = new String[] { BazaarConstants.STATUS_CMD };
        result = BazaarUtils.execute( consumer, getLogger(), workingDir, statusCmd );

        return new StatusScmResult( consumer.getStatus(), result );
    }
}
