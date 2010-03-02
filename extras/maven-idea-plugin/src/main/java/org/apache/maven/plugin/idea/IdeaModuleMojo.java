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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.codehaus.plexus.util.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates the module files (*.iml) for IntelliJ IDEA.
 *
 * @author Edwin Punzalan
 * @goal module
 * @execute phase="generate-sources"
 */
public class IdeaModuleMojo
    extends AbstractIdeaMojo
{
    private Set macros;

//    public void initParam( MavenProject project, ArtifactFactory artifactFactory, ArtifactRepository localRepo,
//                           ArtifactResolver artifactResolver, ArtifactMetadataSource artifactMetadataSource, Log log,
//                           boolean overwrite, boolean skip, MavenProject executedProject, List reactorProjects,
//                           WagonManager wagonManager, boolean linkModules, boolean useFullNames,
//                           boolean downloadSources, String sourceClassifier, boolean downloadJavadocs,
//                           String javadocClassifier, Library[] libraries, Set macros, String exclude,
//                           boolean useShortDependencyNames, String deploymentDescriptorFile, boolean ideaPlugin,
//                           String ideaVersion )
//    {
//        super.initParam( project, artifactFactory, localRepo, artifactResolver, artifactMetadataSource, log,
//                         overwrite, skip );
//
//        this.reactorProjects = reactorProjects;
//
//        this.wagonManager = wagonManager;
//
//        this.linkModules = linkModules;
//
//        this.useFullNames = useFullNames;
//
//        this.downloadSources = downloadSources;
//
//        this.sourceClassifier = sourceClassifier;
//
//        this.downloadJavadocs = downloadJavadocs;
//
//        this.javadocClassifier = javadocClassifier;
//
//        this.libraries = libraries;
//
//        this.macros = macros;
//
//        this.exclude = exclude;
//
//        this.dependenciesAsLibraries = useShortDependencyNames;
//
//        this.deploymentDescriptorFile = deploymentDescriptorFile;
//
//        this.ideaPlugin = ideaPlugin;
//
//        this.ideaVersion = ideaVersion;
//    }

    /**
     * Create IDEA (.iml) project files.
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

        rewriteModule();
    }
}
