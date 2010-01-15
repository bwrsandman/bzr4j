package org.apache.maven.plugin.idea;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.DocumentException;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Creates the project file (*.ipr) for IntelliJ IDEA.
 *
 * @author Edwin Punzalan
 * @goal project
 * @execute phase="generate-sources"
 */
public class IdeaProjectMojo
    extends AbstractIdeaMojo
{
    private Set macros;

//    public void initParam( MavenProject project, ArtifactFactory artifactFactory, ArtifactRepository localRepo,
//                           ArtifactResolver artifactResolver, ArtifactMetadataSource artifactMetadataSource, Log log,
//                           boolean overwrite, boolean skip, String jdkName, String jdkLevel, String wildcardResourcePatterns,
//                           String ideaVersion, Set macros )
//    {
//        super.initParam( project, artifactFactory, localRepo, artifactResolver, artifactMetadataSource, log,
//                         overwrite, skip );
//
//        this.jdkName = jdkName;
//
//        this.jdkLevel = jdkLevel;
//
//        this.wildcardResourcePatterns = wildcardResourcePatterns;
//
//        this.ideaVersion = ideaVersion;
//
//        this.macros = macros;
//    }

    /**
     * Create IDEA (.ipr) project files.
     *
     * @throws org.apache.maven.plugin.MojoExecutionException
     *
     */
    public void execute()
        throws MojoExecutionException
    {
        try
        {
            doDependencyResolution( executedProject, localRepo );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Unable to build project dependencies.", e );
        }

        rewriteProject();
    }
}
