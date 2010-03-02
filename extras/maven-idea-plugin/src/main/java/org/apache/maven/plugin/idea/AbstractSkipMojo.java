/*
 * Copyright (c) 2010 Patrick Woodworth
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
package org.apache.maven.plugin.idea;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;

/**
 * @author Patrick Woodworth
 */
public abstract class AbstractSkipMojo extends AbstractMojo {

    /**
     * Allow for disabling this thing.
     *
     * @parameter expression="${skip}" default-value="false"
     */
    protected boolean skip;

    /**
     * Allow for disabling project-level changes.
     *
     * @parameter expression="${skipProject}" default-value="false"
     */
    protected boolean skipProject;

    /**
     * Allow for disabling module-level changes.
     *
     * @parameter expression="${skipModule}" default-value="false"
     */
    protected boolean skipModule;

    /**
     * Allow for disabling workspace-level changes.
     *
     * @parameter expression="${skipWorkspace}" default-value="false"
     */
    protected boolean skipWorkspace;

    /**
     * Put the ipr file in a non-standard location.
     *
     * @parameter expression="${iprPath}"
     */
    protected String iprPath;

    protected File getProjectDir(MavenProject project) throws MojoExecutionException
    {
        File projectDir = project.getBasedir().getAbsoluteFile();
        if (iprPath != null) {
            File adjustedPath = new File( projectDir, iprPath );
            try {
                projectDir = adjustedPath.getCanonicalFile();
            } catch (IOException e) {
                throw new MojoExecutionException("Couldn't resolve canonical path of \"" + adjustedPath + "\"", e);
            }
        }
        return projectDir;
    }
}
