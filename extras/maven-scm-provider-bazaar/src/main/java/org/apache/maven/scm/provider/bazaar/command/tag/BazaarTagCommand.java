package org.apache.maven.scm.provider.bazaar.command.tag;

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
import org.apache.maven.scm.ScmFile;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmFileStatus;
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.ScmTagParameters;
import org.apache.maven.scm.command.tag.AbstractTagCommand;
import org.apache.maven.scm.command.tag.TagScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.hg.command.inventory.BazaarListConsumer;
import org.apache.maven.scm.provider.bazaar.BazaarUtils;
import org.apache.maven.scm.provider.bazaar.command.BazaarConstants;
import org.apache.maven.scm.provider.bazaar.command.BazaarConsumer;
import org.apache.maven.scm.provider.bazaar.repository.BazaarScmProviderRepository;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:torbjorn@smorgrav.org">Torbj?rn Eikli Sm?rgrav</a>
 * @version $Id: BazaarCheckInCommand.java 685721 2008-08-13 22:57:17Z olamy $
 */
public class BazaarTagCommand
        extends AbstractTagCommand
{
    /** {@inheritDoc} */
    protected ScmResult executeTagCommand( ScmProviderRepository repo, ScmFileSet fileSet, String tag,
            ScmTagParameters scmTagParameters ) throws ScmException
    {
        if (tag == null || StringUtils.isEmpty( tag.trim() ))
        {
            throw new ScmException( "tag must be specified" );
        }

        if (fileSet.getFiles().length != 0)
        {
            throw new ScmException( "This provider doesn't support tagging subsets of a directory" );
        }

        // Commit to local branch
        String[] tagCmd = new String[] { BazaarConstants.TAG_CMD, tag };
//        commitCmd = BazaarUtils.expandCommandLine( commitCmd, fileSet );

        // keep the command about in string form for reporting
        StringBuffer cmd = BazaarUtils.joinCmd( tagCmd );

        ScmResult result =
                BazaarUtils.execute( new BazaarConsumer( getLogger() ), getLogger(), fileSet.getBasedir(), tagCmd );

        BazaarScmProviderRepository repository = (BazaarScmProviderRepository)repo;

        if (result.isSuccess())
        {
            // Push to parent branch if any
            if (!repository.getURI().equals( fileSet.getBasedir().getAbsolutePath() ))
            {
                String[] pushCmd = new String[] { BazaarConstants.PUSH_CMD, repository.getURI() };
                result =
                        BazaarUtils.execute( new BazaarConsumer( getLogger() ), getLogger(), fileSet.getBasedir(),
                                pushCmd );
            }
        }
        else
        {
            throw new ScmException( "Error while executing command " + cmd.toString() );
        }

        // do an inventory to return the files tagged (all of them)
        String[] listCmd = new String[] { BazaarConstants.LIST_CMD, BazaarConstants.RECURSIVE_OPTION,
                BazaarConstants.REVISION_OPTION, "tag:" + tag };

        cmd = BazaarUtils.joinCmd( listCmd );

        BazaarListConsumer listconsumer = new BazaarListConsumer( getLogger() );
        result = BazaarUtils.execute( listconsumer, getLogger(), fileSet.getBasedir(), listCmd );
        if (result.isSuccess())
        {
            List files = listconsumer.getFiles();
            ArrayList fileList = new ArrayList();
            for (Iterator i = files.iterator(); i.hasNext();)
            {
                ScmFile f = (ScmFile)i.next();

                if (!f.getPath().endsWith( ".bzr" ))
                {
                    fileList.add( new ScmFile( f.getPath(), ScmFileStatus.TAGGED ) );
                }
            }

            return new TagScmResult( fileList, result );
        }
        else
        {
            throw new ScmException( "Error while executing command " + cmd.toString() );
        }

    }
}
