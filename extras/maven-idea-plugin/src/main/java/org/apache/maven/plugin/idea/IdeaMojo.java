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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.util.HashSet;
import java.util.Set;

/**
 * Goal for generating IDEA files from a POM.
 * This plug-in provides the ability to generate project files (.ipr, .iml and .iws files) for IDEA.
 *
 * @goal idea
 * @execute phase="generate-resources"
 */
public class IdeaMojo extends AbstractIdeaMojo
{
    public void execute() throws MojoExecutionException
    {
        try
        {
            doDependencyResolution( executedProject, localRepo );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Unable to build project dependencies.", e );
        }

        Set macros = new HashSet();
        rewriteModule( /* macros */ );

        if ( executedProject.isExecutionRoot() )
        {
            rewriteProject( /* macros */ );
        }
    }

    public void setProject( MavenProject project )
    {
        this.executedProject = project;
    }
}
