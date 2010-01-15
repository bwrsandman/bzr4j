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
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ExcludesArtifactFilter;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

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
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Edwin Punzalan
 */
public abstract class AbstractIdeaMojo
    extends AbstractSkipMojo
{
    /**
     * The Maven Project.
     *
     * @parameter expression="${executedProject}"
     * @required
     * @readonly
     */
    protected MavenProject executedProject;

    /* holder for the log object only */
    protected Log log;

    /**
     * Whether to update the existing project files or overwrite them.
     *
     * @parameter expression="${overwrite}" default-value="false"
     */
    protected boolean overwrite;


    /**
     * @component
     */
    protected ArtifactFactory artifactFactory;

    /**
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    protected ArtifactRepository localRepo;

    /**
     * @component
     */
    protected ArtifactResolver artifactResolver;

    /**
     * @component role="org.apache.maven.artifact.metadata.ArtifactMetadataSource" hint="maven"
     */
    protected ArtifactMetadataSource artifactMetadataSource;

    /**
     * Specify the name of the registered IDEA JDK to use
     * for the project.
     *
     * @parameter expression="${jdkName}"
     */
    protected String jdkName;

    /**
     * Specify the version of the JDK to use for the project for the purpose of
     * enabled assertions and Java 5.0 language features.
     * The default value is the specification version of the executing JVM.
     *
     * @parameter expression="${jdkLevel}"
     * @todo would be good to use the compilation source if possible
     */
    protected String jdkLevel;

    /**
     * Specify the resource pattern in wildcard format, for example "?*.xml;?*.properties".
     * Currently supports 4.x and 5.x.
     * Because IDEA doesn't distinguish between source and resources directories, this is needed.
     * The default value corresponds to any file without a java extension.
     * Please note that the default value includes package.html files as it's not possible to exclude those.
     *
     * @parameter expression="${wildcardResourcePatterns}" default-value="!?*.java"
     */
    protected String wildcardResourcePatterns;

    /**
     * Specify the version of IDEA to target.  This is needed to identify the default formatting of
     * project-jdk-name used by IDEA.  Currently supports 4.x and 5.x.
     * <p/>
     * This will only be used when parameter jdkName is not set.
     *
     * @parameter expression="${ideaVersion}" default-value="5.x"
     */
    protected String ideaVersion;


    /**
     * The reactor projects in a multi-module build.
     *
     * @parameter expression="${reactorProjects}"
     * @required
     * @readonly
     */
    protected List reactorProjects;

    /**
     * @component
     */
    protected WagonManager wagonManager;

    /**
     * Whether to link the reactor projects as dependency modules or as libraries.
     *
     * @parameter expression="${linkModules}" default-value="true"
     */
    protected boolean linkModules;

    /**
     * Specify the location of the deployment descriptor file, if one is provided.
     *
     * @parameter expression="${deploymentDescriptorFile}"
     */
    protected String deploymentDescriptorFile;

    /**
     * Whether to use full artifact names when referencing libraries.
     *
     * @parameter expression="${useFullNames}" default-value="false"
     */
    protected boolean useFullNames;

    /**
     * Enables/disables the downloading of source attachments.
     *
     * @parameter expression="${downloadSources}" default-value="false"
     */
    protected boolean downloadSources;

    /**
     * Enables/disables the downloading of javadoc attachments.
     *
     * @parameter expression="${downloadJavadocs}" default-value="false"
     */
    protected boolean downloadJavadocs;

    /**
     * Sets the classifier string attached to an artifact source archive name.
     *
     * @parameter expression="${sourceClassifier}" default-value="sources"
     */
    protected String sourceClassifier;

    /**
     * Sets the classifier string attached to an artifact javadoc archive name.
     *
     * @parameter expression="${javadocClassifier}" default-value="javadoc"
     */
    protected String javadocClassifier;

    /**
     * An optional set of Library objects that allow you to specify a comma separated list of source dirs, class dirs,
     * or to indicate that the library should be excluded from the module. For example:
     * <p/>
     * <pre>
     * &lt;libraries&gt;
     *  &lt;library&gt;
     *      &lt;name&gt;webwork&lt;/name&gt;
     *      &lt;sources&gt;file://$webwork$/src/java&lt;/sources&gt;
     *      &lt;!--
     *      &lt;classes&gt;...&lt;/classes&gt;
     *      &lt;exclude&gt;true&lt;/exclude&gt;
     *      --&gt;
     *  &lt;/library&gt;
     * &lt;/libraries&gt;
     * </pre>
     *
     * @parameter
     */
    protected Library[] libraries;

    /**
     * A comma-separated list of directories that should be excluded. These directories are in addition to those
     * already excluded, such as target.
     *
     * @parameter
     */
    protected String exclude;

    /**
     * Causes the module libraries to use a short name for all dependencies. This is very convenient but has been
     * reported to cause problems with IDEA.
     *
     * @parameter default-value="false"
     */
    protected boolean dependenciesAsLibraries;

    /**
     * A temporary cache of artifacts that's already been downloaded or
     * attempted to be downloaded. This is to refrain from trying to download a
     * dependency that we have already tried to download.
     *
     * @todo this is nasty! the only reason this is static is to use the same cache between reactor calls
     */
    protected static Map attemptedDownloads = new HashMap();

    /**
     * Tell IntelliJ IDEA that this module is an IntelliJ IDEA Plugin.
     *
     * @parameter default-value="false"
     */
    protected boolean ideaPlugin;    

    private Set macros;

    public void initParam( MavenProject project, ArtifactFactory artifactFactory, ArtifactRepository localRepo,
                           ArtifactResolver artifactResolver, ArtifactMetadataSource artifactMetadataSource, Log log,
                           boolean overwrite, boolean skip )
    {
        this.executedProject = project;

        this.log = log;

        this.artifactFactory = artifactFactory;

        this.localRepo = localRepo;

        this.artifactResolver = artifactResolver;

        this.artifactMetadataSource = artifactMetadataSource;

        this.overwrite = overwrite;

        this.skip = skip;
    }

    protected Document readXmlDocument( File file, String altFilename )
        throws DocumentException
    {
        SAXReader reader = new SAXReader();
        if ( file.exists() && !overwrite )
        {
            return reader.read( file );
        }
        else
        {
            File altFile = new File( executedProject.getBasedir(), "src/main/idea/" + altFilename );
            if ( altFile.exists() )
            {
                return reader.read( altFile );
            }
            else
            {
                return reader.read( getClass().getResourceAsStream( "/templates/default/" + altFilename ) );
            }
        }
    }

    protected void writeXmlDocument( File file, Document document )
        throws IOException
    {
        if (skip)
            return;
        XMLWriter writer = new IdeaXmlWriter( file );
        writer.write( document );
        writer.close();
    }

    /**
     * Finds element from the module element.
     *
     * @param module Xpp3Dom element
     * @param name   Name attribute to find
     * @return component  Returns the Xpp3Dom element found.
     */
    protected Element findComponent( Element module, String name )
    {
        return findElement( module, "component", name );
    }

    protected Element findElement( Element element, String elementName, String attributeName )
    {
        for ( Iterator children = element.elementIterator( elementName ); children.hasNext(); )
        {
            Element child = (Element) children.next();
            if ( attributeName.equals( child.attributeValue( "name" ) ) )
            {
                return child;
            }
        }
        return createElement( element, elementName ).addAttribute( "name", attributeName );
    }

    protected Element findElement( Element component, String name )
    {
        Element element = component.element( name );
        if ( element == null )
        {
            element = createElement( component, name );
        }
        return element;
    }

    /**
     * Creates an Xpp3Dom element.
     *
     * @param module Xpp3Dom element
     * @param name   Name of the element
     * @return component Xpp3Dom element
     */
    protected Element createElement( Element module, String name )
    {
        return module.addElement( name );
    }

    /**
     * Translate the absolutePath into its relative path.
     *
     * @param basedir      The basedir of the project.
     * @param absolutePath The absolute path that must be translated to relative path.
     * @return relative  Relative path of the parameter absolute path.
     */
    protected String toRelative( String basedir, String absolutePath )
    {
        String relative;

        // Convert drive letter
        String convertedBasedir = convertDriveLetter( basedir );
        String convertedAbsolutePath = convertDriveLetter( absolutePath );

        // Normalize path separators
        convertedBasedir = StringUtils.replace( convertedBasedir, "\\", "/" );
        convertedAbsolutePath = StringUtils.replace( convertedAbsolutePath, "\\", "/" );

        // Strip trailing slash
        if ( convertedBasedir.endsWith( "/" ) )
        {
            convertedBasedir = convertedBasedir.substring( 0, convertedBasedir.length() - 1 );
        }
        if ( convertedAbsolutePath.endsWith( "/" ) )
        {
            convertedAbsolutePath = convertedAbsolutePath.substring( 0, convertedAbsolutePath.length() - 1 );
        }

        // IDEA-103 Make sure that the basedir is appended with a / before we attempt to match it to the absolute path
        String matchableBasedir = convertedBasedir + "/";
        if ( convertedAbsolutePath.startsWith( matchableBasedir )
            && convertedAbsolutePath.length() > matchableBasedir.length() )
        {
            // Simple case, path starts with basepath
            relative = convertedAbsolutePath.substring( matchableBasedir.length() );
        }
        else
        {
            // It's more complex...
            StringTokenizer baseTokens = new StringTokenizer( convertedBasedir, "/", false );

            int baseCount = baseTokens.countTokens();
            List baseTokenList = new ArrayList( baseCount );
            while ( baseTokens.hasMoreTokens() )
            {
                baseTokenList.add( baseTokens.nextToken() );
            }

            StringTokenizer pathTokens = new StringTokenizer( convertedAbsolutePath, "/", false );

            int pathCount = pathTokens.countTokens();
            List pathTokenList = new ArrayList( pathCount );
            while ( pathTokens.hasMoreTokens() )
            {
                pathTokenList.add( pathTokens.nextToken() );
            }

            int maxCount = Math.max( baseTokenList.size(), pathTokenList.size() );
            int differIndex = -1;
            for ( int i = 0; i < maxCount; i++ )
            {
                if ( i >= pathTokenList.size() || i >= baseTokenList.size() )
                {
                    differIndex = i;
                    break;
                }
                String basePart = (String) baseTokenList.get( i );
                String pathPart = (String) pathTokenList.get( i );
                if ( !basePart.equals( pathPart ) )
                {
                    differIndex = i;
                    break;
                }
            }
            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( "Construction of relative path... differIndex=" + differIndex );
            }
            if ( differIndex < 1 )
            {
                // Paths are either equal or completely different
                relative = convertedAbsolutePath;
            }
            else
            {
                StringBuffer result = new StringBuffer();
                int parentCount = baseTokenList.size() - differIndex;
                if ( getLog().isDebugEnabled() )
                {
                    getLog().debug( "parentCount=" + parentCount );
                }
                boolean isFirst = true;
                for ( int i = 0; i < parentCount; i++ )
                {
                    // Add parents
                    if ( isFirst )
                    {
                        isFirst = false;
                    }
                    else
                    {
                        result.append( "/" );
                    }
                    result.append( ".." );
                }
                for ( int i = differIndex; i < pathTokenList.size(); i++ )
                {
                    // Add the remaining path elements
                    if ( isFirst )
                    {
                        isFirst = false;
                    }
                    else
                    {
                        result.append( "/" );
                    }
                    result.append( pathTokenList.get( i ) );
                }
                relative = result.toString();
            }
        }

        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( "toRelative(" + basedir + ", " + absolutePath + ") => " + relative );
        }

        return relative;
    }

    /**
     * Convert the drive letter, if there is one, to upper case. This is done
     * to avoid case mismatch when running cygwin on Windows.
     *
     * @param absolutePath The path to convert
     * @return The path that came in with its drive letter converted to upper case
     */
    String convertDriveLetter( String absolutePath )
    {
        if ( absolutePath != null && absolutePath.length() >= 3 && !absolutePath.startsWith( "/" ) )
        {
            // See if the path starts with "?:\", where ? must be a letter
            if ( Character.isLetter( absolutePath.substring( 0, 1 ).charAt( 0 ) )
                && absolutePath.substring( 1, 3 ).equals( ":\\" ) )
            {
                // In that case we convert the first character to upper case
                return absolutePath.substring( 0, 1 ).toUpperCase() + absolutePath.substring( 1 );
            }
        }
        return absolutePath;
    }

    /**
     * Remove elements from content (Xpp3Dom).
     *
     * @param content Xpp3Dom element
     * @param name    Name of the element to be removed
     */
    protected void removeOldElements( Element content, String name )
    {
        for ( Iterator children = content.elementIterator(); children.hasNext(); )
        {
            Element child = (Element) children.next();
            if ( name.equals( child.getName() ) )
            {
                content.remove( child );
            }
        }
    }

    protected void doDependencyResolution( MavenProject project, ArtifactRepository localRepo )
        throws InvalidDependencyVersionException, ProjectBuildingException, InvalidVersionSpecificationException
    {
        Map managedVersions =
            createManagedVersionMap( artifactFactory, project.getId(), project.getDependencyManagement() );

        try
        {
            ArtifactResolutionResult result = artifactResolver.resolveTransitively( getProjectArtifacts(),
                                                                                    project.getArtifact(),
                                                                                    managedVersions, localRepo,
                                                                                    project.getRemoteArtifactRepositories(),
                                                                                    artifactMetadataSource );

            project.setArtifacts( result.getArtifacts() );
        }
        catch ( ArtifactNotFoundException e )
        {
            getLog().debug( e.getMessage(), e );

            StringBuffer msg = new StringBuffer();
            msg.append( "An error occurred during dependency resolution.\n\n" );
            msg.append( "    Failed to retrieve " + e.getDownloadUrl() + "\n" );
            msg.append( "from the following repositories:" );
            for ( Iterator repositories = e.getRemoteRepositories().iterator(); repositories.hasNext(); )
            {
                ArtifactRepository repository = (ArtifactRepository) repositories.next();
                msg.append( "\n    " + repository.getId() + "(" + repository.getUrl() + ")" );
            }
            msg.append( "\nCaused by: " + e.getMessage() );

            getLog().warn( msg );
        }
        catch ( ArtifactResolutionException e )
        {
            getLog().debug( e.getMessage(), e );

            StringBuffer msg = new StringBuffer();
            msg.append( "An error occurred during dependency resolution of the following artifact:\n\n" );
            msg.append( "    " + e.getGroupId() + ":" + e.getArtifactId() + e.getVersion() + "\n\n" );
            msg.append( "Caused by: " + e.getMessage() );

            getLog().warn( msg );
        }
    }

    /*
    * @todo we need a more permanent feature that does this properly
    */
    protected String getPluginSetting( String artifactId, String optionName, String defaultValue )
    {
        for ( Iterator it = executedProject.getBuildPlugins().iterator(); it.hasNext(); )
        {
            Plugin plugin = (Plugin) it.next();
            if ( plugin.getArtifactId().equals( artifactId ) )
            {
                Xpp3Dom o = (Xpp3Dom) plugin.getConfiguration();
                if ( o != null && o.getChild( optionName ) != null )
                {
                    return o.getChild( optionName ).getValue();
                }
            }
        }
        return defaultValue;
    }

    private Set getProjectArtifacts()
        throws InvalidVersionSpecificationException
    {
        Set artifacts = new HashSet();

        for ( Iterator dependencies = executedProject.getDependencies().iterator(); dependencies.hasNext(); )
        {
            Dependency dep = (Dependency) dependencies.next();

            String groupId = dep.getGroupId();
            String artifactId = dep.getArtifactId();
            VersionRange versionRange = VersionRange.createFromVersionSpec( dep.getVersion() );
            String type = dep.getType();
            if ( type == null )
            {
                type = "jar";
            }
            String classifier = dep.getClassifier();
            boolean optional = dep.isOptional();
            String scope = dep.getScope();
            if ( scope == null )
            {
                scope = Artifact.SCOPE_COMPILE;
            }

            Artifact artifact = artifactFactory.createDependencyArtifact( groupId, artifactId, versionRange, type,
                                                                          classifier, scope, optional );

            if ( scope.equalsIgnoreCase( Artifact.SCOPE_SYSTEM ) )
            {
                artifact.setFile( new File( dep.getSystemPath() ) );
            }

            List exclusions = new ArrayList();
            for ( Iterator j = dep.getExclusions().iterator(); j.hasNext(); )
            {
                Exclusion e = (Exclusion) j.next();
                exclusions.add( e.getGroupId() + ":" + e.getArtifactId() );
            }

            ArtifactFilter newFilter = new ExcludesArtifactFilter( exclusions );

            artifact.setDependencyFilter( newFilter );

            artifacts.add( artifact );
        }

        return artifacts;
    }

    private Map createManagedVersionMap( ArtifactFactory artifactFactory, String projectId,
                                         DependencyManagement dependencyManagement )
        throws ProjectBuildingException
    {
        Map map;
        if ( dependencyManagement != null && dependencyManagement.getDependencies() != null )
        {
            map = new HashMap();
            for ( Iterator i = dependencyManagement.getDependencies().iterator(); i.hasNext(); )
            {
                Dependency d = (Dependency) i.next();

                try
                {
                    VersionRange versionRange = VersionRange.createFromVersionSpec( d.getVersion() );
                    Artifact artifact = artifactFactory.createDependencyArtifact( d.getGroupId(), d.getArtifactId(),
                                                                                  versionRange, d.getType(),
                                                                                  d.getClassifier(), d.getScope(),
                                                                                  d.isOptional() );
                    map.put( d.getManagementKey(), artifact );
                }
                catch ( InvalidVersionSpecificationException e )
                {
                    throw new ProjectBuildingException( projectId, "Unable to parse version '" + d.getVersion()
                        + "' for dependency '" + d.getManagementKey() + "': " + e.getMessage(), e );
                }
            }
        }
        else
        {
            map = Collections.EMPTY_MAP;
        }
        return map;
    }

    public Log getLog()
    {
        if ( log == null )
        {
            log = super.getLog();
        }

        return log;
    }

    public void rewriteProject()
        throws MojoExecutionException
    {
        File projectFile = new File( executedProject.getBasedir(), executedProject.getArtifactId() + ".ipr" );

        try
        {
            Document document = readXmlDocument( projectFile, "project.xml" );

            Element module = document.getRootElement();

            // Set the jdk name if set
            if ( jdkName != null )
            {
                setJdkName( module, jdkName );
            }
            else
            {
                String javaVersion = System.getProperty( "java.version" );
                String defaultJdkName;

                if ( ideaVersion.startsWith( "4" ) )
                {
                    defaultJdkName = "java version &quot;" + javaVersion + "&quot;";
                }
                else
                {
                    defaultJdkName = javaVersion.substring( 0, 3 );
                }
                getLog().info( "jdkName is not set, using [java version" + javaVersion + "] as default." );
                setJdkName( module, defaultJdkName );
            }

            setWildcardResourcePatterns( module, wildcardResourcePatterns );

            Element component = findComponent( module, "ProjectModuleManager" );
            Element modules = findElement( component, "modules" );

            removeOldElements( modules, "module" );

            if ( executedProject.getCollectedProjects().size() > 0 )
            {
                Element m = createElement( modules, "module" );
                String projectPath =
                    new File( executedProject.getBasedir(),
                              executedProject.getArtifactId() + ".iml" ).getAbsolutePath();
                m.addAttribute( "filepath",
                                "$PROJECT_DIR$/" + toRelative( executedProject.getBasedir().getAbsolutePath(),
                                                               projectPath ) );

                for ( Iterator i = executedProject.getCollectedProjects().iterator(); i.hasNext(); )
                {
                    MavenProject p = (MavenProject) i.next();

                    m = createElement( modules, "module" );
                    String modulePath = new File( p.getBasedir(), p.getArtifactId() + ".iml" ).getAbsolutePath();
                    m.addAttribute( "filepath",
                                    "$PROJECT_DIR$/" + toRelative( executedProject.getBasedir().getAbsolutePath(),
                                                                   modulePath ) );
                }
            }
            else
            {
                Element m = createElement( modules, "module" );
                String modulePath =
                    new File( executedProject.getBasedir(),
                              executedProject.getArtifactId() + ".iml" ).getAbsolutePath();
                m.addAttribute( "filepath",
                                "$PROJECT_DIR$/" + toRelative( executedProject.getBasedir().getAbsolutePath(),
                                                               modulePath ) );
            }

            // add any PathMacros we've come across
            if ( macros != null && module.elements( "UsedPathMacros" ).size() > 0 )
            {
                Element usedPathMacros = (Element) module.elements( "UsedPathMacros" ).get( 0 );
                removeOldElements( usedPathMacros, "macro" );
                for ( Iterator iterator = macros.iterator(); iterator.hasNext(); )
                {
                    String macro = (String) iterator.next();
                    Element macroElement = createElement( usedPathMacros, "macro" );
                    macroElement.addAttribute( "name", macro );
                }
            }

            writeXmlDocument( projectFile, document );
        }
        catch ( DocumentException e )
        {
            throw new MojoExecutionException( "Error parsing existing IPR file: " + projectFile.getAbsolutePath(), e );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error parsing existing IPR file: " + projectFile.getAbsolutePath(), e );
        }
    }

    /**
     * Sets the name of the JDK to use.
     *
     * @param content Xpp3Dom element.
     * @param jdkName Name of the JDK to use.
     */
    private void setJdkName( Element content, String jdkName )
    {
        Element component = findComponent( content, "ProjectRootManager" );
        component.addAttribute( "project-jdk-name", jdkName );

        String jdkLevel = this.jdkLevel;
        if ( jdkLevel == null )
        {
            jdkLevel = System.getProperty( "java.specification.version" );
        }

        if ( jdkLevel.startsWith( "1.4" ) )
        {
            component.addAttribute( "assert-keyword", "true" );
            component.addAttribute( "jdk-15", "false" );
        }
        else if ( jdkLevel.compareTo( "1.5" ) >= 0 )
        {
            component.addAttribute( "assert-keyword", "true" );
            component.addAttribute( "jdk-15", "true" );
        }
        else
        {
            component.addAttribute( "assert-keyword", "false" );
        }
    }

    /**
     * Sets the wilcard resource patterns.
     *
     * @param content                  Xpp3Dom element.
     * @param wildcardResourcePatterns The wilcard resource patterns.
     */
    private void setWildcardResourcePatterns( Element content, String wildcardResourcePatterns )
    {
        Element compilerConfigurationElement = findComponent( content, "CompilerConfiguration" );
        if ( !StringUtils.isEmpty( wildcardResourcePatterns ) )
        {
            removeOldElements( compilerConfigurationElement, "wildcardResourcePatterns" );
            Element wildcardResourcePatternsElement =
                createElement( compilerConfigurationElement, "wildcardResourcePatterns" );
            StringTokenizer wildcardResourcePatternsTokenizer = new StringTokenizer( wildcardResourcePatterns, ";" );
            while ( wildcardResourcePatternsTokenizer.hasMoreTokens() )
            {
                String wildcardResourcePattern = wildcardResourcePatternsTokenizer.nextToken();
                Element entryElement = createElement( wildcardResourcePatternsElement, "entry" );
                entryElement.addAttribute( "name", wildcardResourcePattern );
            }
        }
    }


    public void rewriteModule()
        throws MojoExecutionException
    {
        File moduleFile = new File( executedProject.getBasedir(), executedProject.getArtifactId() + ".iml" );
        try
        {
            Document document = readXmlDocument( moduleFile, "module.xml" );

            Element module = document.getRootElement();

            // TODO: how can we let the WAR/EJBs plugin hook in and provide this?
            // TODO: merge in ejb-module, etc.
            if ( "war".equals( executedProject.getPackaging() ) )
            {
                addWebModule( module );
            }
            else if ( "ejb".equals( executedProject.getPackaging() ) )
            {
                addEjbModule( module );
            }
            else if ( "ear".equals( executedProject.getPackaging() ) )
            {
                addEarModule( module );
            }
            else if ( ideaPlugin )
            {
                addPluginModule( module );
            }

            Element component = findComponent( module, "NewModuleRootManager" );
            Element output = findElement( component, "output" );
            output.addAttribute( "url", getModuleFileUrl( executedProject.getBuild().getOutputDirectory() ) );

            Element outputTest = findElement( component, "output-test" );
            outputTest.addAttribute( "url", getModuleFileUrl( executedProject.getBuild().getTestOutputDirectory() ) );

            Element content = findElement( component, "content" );

            removeOldElements( content, "sourceFolder" );

            for ( Iterator i = executedProject.getCompileSourceRoots().iterator(); i.hasNext(); )
            {
                String directory = (String) i.next();
                addSourceFolder( content, directory, false );
            }
            for ( Iterator i = executedProject.getTestCompileSourceRoots().iterator(); i.hasNext(); )
            {
                String directory = (String) i.next();
                addSourceFolder( content, directory, true );
            }

            for ( Iterator i = executedProject.getBuild().getResources().iterator(); i.hasNext(); )
            {
                Resource resource = (Resource) i.next();
                String directory = resource.getDirectory();
                if ( resource.getTargetPath() == null && !resource.isFiltering() )
                {
                    addSourceFolder( content, directory, false );
                }
                else
                {
                    getLog().info(
                        "Not adding resource directory as it has an incompatible target path or filtering: "
                            + directory );
                }
            }

            for ( Iterator i = executedProject.getBuild().getTestResources().iterator(); i.hasNext(); )
            {
                Resource resource = (Resource) i.next();
                String directory = resource.getDirectory();
                if ( resource.getTargetPath() == null && !resource.isFiltering() )
                {
                    addSourceFolder( content, directory, true );
                }
                else
                {
                    getLog().info(
                        "Not adding test resource directory as it has an incompatible target path or filtering: "
                            + directory );
                }
            }

            removeOldElements( content, "excludeFolder" );

            //For excludeFolder
            File target = new File( executedProject.getBuild().getDirectory() );
            File classes = new File( executedProject.getBuild().getOutputDirectory() );
            File testClasses = new File( executedProject.getBuild().getTestOutputDirectory() );

            List sourceFolders = content.elements( "sourceFolder" );

            List filteredExcludes = new ArrayList();
            filteredExcludes.addAll( getExcludedDirectories( target, filteredExcludes, sourceFolders ) );
            filteredExcludes.addAll( getExcludedDirectories( classes, filteredExcludes, sourceFolders ) );
            filteredExcludes.addAll( getExcludedDirectories( testClasses, filteredExcludes, sourceFolders ) );

            if ( exclude != null )
            {
                String[] dirs = exclude.split( "[,\\s]+" );
                for ( int i = 0; i < dirs.length; i++ )
                {
                    File excludedDir = new File( executedProject.getBasedir(), dirs[i] );
                    filteredExcludes.addAll( getExcludedDirectories( excludedDir, filteredExcludes, sourceFolders ) );
                }
            }

            // even though we just ran all the directories in the filteredExcludes List through the intelligent
            // getExcludedDirectories method, we never actually were guaranteed the order that they were added was
            // in the order required to make the most optimized exclude list. In addition, the smart logic from
            // that method is entirely skipped if the directory doesn't currently exist. A simple string matching
            // will do pretty much the same thing and make the list more concise.
            ArrayList actuallyExcluded = new ArrayList();
            Collections.sort( filteredExcludes );
            for ( Iterator i = filteredExcludes.iterator(); i.hasNext(); )
            {
                String dirToExclude = i.next().toString();
                String dirToExcludeTemp = dirToExclude.replace( '\\', '/' );
                boolean addExclude = true;
                for ( Iterator iterator = actuallyExcluded.iterator(); iterator.hasNext(); )
                {
                    String dir = iterator.next().toString();
                    String dirTemp = dir.replace( '\\', '/' );
                    if ( dirToExcludeTemp.startsWith( dirTemp + "/" ) )
                    {
                        addExclude = false;
                        break;
                    }
                    else if ( dir.startsWith( dirToExcludeTemp + "/" ) )
                    {
                        actuallyExcluded.remove( dir );
                    }
                }

                if ( addExclude )
                {
                    actuallyExcluded.add( dirToExclude );
                    addExcludeFolder( content, dirToExclude );
                }
            }

            //Remove default exclusion for output dirs if there are sources in it
            String outputModuleUrl = getModuleFileUrl( executedProject.getBuild().getOutputDirectory() );
            String testOutputModuleUrl = getModuleFileUrl( executedProject.getBuild().getTestOutputDirectory() );
            for ( Iterator i = content.elements( "sourceFolder" ).iterator(); i.hasNext(); )
            {
                Element sourceFolder = (Element) i.next();
                String sourceUrl = sourceFolder.attributeValue( "url" ).replace( '\\', '/' );
                if ( sourceUrl.startsWith( outputModuleUrl + "/" ) || sourceUrl.startsWith( testOutputModuleUrl ) )
                {
                    component.remove( component.element( "exclude-output" ) );
                    break;
                }
            }

            rewriteDependencies( component );

            writeXmlDocument( moduleFile, document );
        }
        catch ( DocumentException e )
        {
            throw new MojoExecutionException( "Error parsing existing IML file " + moduleFile.getAbsolutePath(), e );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error parsing existing IML file " + moduleFile.getAbsolutePath(), e );
        }
    }

    private void rewriteDependencies( Element component )
    {
        Map modulesByName = new HashMap();
        Map modulesByUrl = new HashMap();
        Set unusedModules = new HashSet();
        for ( Iterator children = component.elementIterator( "orderEntry" ); children.hasNext(); )
        {
            Element orderEntry = (Element) children.next();

            String type = orderEntry.attributeValue( "type" );
            if ( "module".equals( type ) )
            {
                modulesByName.put( orderEntry.attributeValue( "module-name" ), orderEntry );
            }
            else if ( "module-library".equals( type ) )
            {
                // keep track for later so we know what is left
                unusedModules.add( orderEntry );

                Element lib = orderEntry.element( "library" );
                String name = lib.attributeValue( "name" );
                if ( name != null )
                {
                    modulesByName.put( name, orderEntry );
                }
                else
                {
                    Element classesChild = lib.element( "CLASSES" );
                    if ( classesChild != null )
                    {
                        Element rootChild = classesChild.element( "root" );
                        if ( rootChild != null )
                        {
                            String url = rootChild.attributeValue( "url" );
                            if ( url != null )
                            {
                                // Need to ignore case because of Windows drive letters
                                modulesByUrl.put( url.toLowerCase(), orderEntry );
                            }
                        }
                    }
                }
            }
        }

        List testClasspathElements = executedProject.getTestArtifacts();
        for ( Iterator i = testClasspathElements.iterator(); i.hasNext(); )
        {
            Artifact a = (Artifact) i.next();

            Library library = findLibrary( a );
            if ( library != null && library.isExclude() )
            {
                continue;
            }

            String moduleName;
            if ( useFullNames )
            {
                moduleName = a.getGroupId() + ':' + a.getArtifactId() + ':' + a.getType() + ':' + a.getVersion();
            }
            else
            {
                moduleName = a.getArtifactId();
            }

            Element dep = (Element) modulesByName.get( moduleName );

            if ( dep == null )
            {
                // Need to ignore case because of Windows drive letters
                dep = (Element) modulesByUrl.get( getLibraryUrl( a ).toLowerCase() );
            }

            if ( dep != null )
            {
                unusedModules.remove( dep );
            }
            else
            {
                dep = createElement( component, "orderEntry" );
            }

            boolean isIdeaModule = false;
            if ( linkModules )
            {
                isIdeaModule = isReactorProject( a.getGroupId(), a.getArtifactId() );

                if ( isIdeaModule )
                {
                    dep.addAttribute( "type", "module" );
                    dep.addAttribute( "module-name", moduleName );
                    setupExportedAndScope( dep, a, library );
                }
            }

            if ( a.getFile() != null && !isIdeaModule )
            {
                dep.addAttribute( "type", "module-library" );

                setupExportedAndScope( dep, a, library );

                Element lib = dep.element( "library" );

                if ( lib == null )
                {
                    lib = createElement( dep, "library" );
                }

                if ( dependenciesAsLibraries )
                {
                    lib.addAttribute( "name", moduleName );
                }

                // replace classes
                removeOldElements( lib, "CLASSES" );
                Element classes = createElement( lib, "CLASSES" );
                if ( library != null && library.getSplitClasses().length > 0 )
                {
                    lib.addAttribute( "name", moduleName );
                    String[] libraryClasses = library.getSplitClasses();
                    for ( int k = 0; k < libraryClasses.length; k++ )
                    {
                        String classpath = libraryClasses[k];
                        extractMacro( classpath );
                        Element classEl = createElement( classes, "root" );
                        classEl.addAttribute( "url", classpath );
                    }
                }
                else
                {
                    createElement( classes, "root" ).addAttribute( "url", getLibraryUrl( a ) );
                }

                if ( library != null && library.getSplitSources().length > 0 )
                {
                    removeOldElements( lib, "SOURCES" );
                    Element sourcesElement = createElement( lib, "SOURCES" );
                    String[] sources = library.getSplitSources();
                    for ( int k = 0; k < sources.length; k++ )
                    {
                        String source = sources[k];
                        extractMacro( source );
                        Element sourceEl = createElement( sourcesElement, "root" );
                        sourceEl.addAttribute( "url", source );
                    }
                }
                else if ( downloadSources )
                {
                    resolveClassifier( createOrGetElement( lib, "SOURCES" ), a, sourceClassifier );
                }

                if ( library != null && library.getSplitJavadocs().length > 0 )
                {
                    removeOldElements( lib, "JAVADOC" );
                    Element javadocsElement = createElement( lib, "JAVADOC" );
                    String[] javadocs = library.getSplitJavadocs();
                    for ( int k = 0; k < javadocs.length; k++ )
                    {
                        String javadoc = javadocs[k];
                        extractMacro( javadoc );
                        Element sourceEl = createElement( javadocsElement, "root" );
                        sourceEl.addAttribute( "url", javadoc );
                    }
                }
                else if ( downloadJavadocs )
                {
                    resolveClassifier( createOrGetElement( lib, "JAVADOC" ), a, javadocClassifier );
                }
            }
        }

        for ( Iterator i = unusedModules.iterator(); i.hasNext(); )
        {
            Element orderEntry = (Element) i.next();

            component.remove( orderEntry );
        }
    }

    private void setupExportedAndScope( Element dep, Artifact a, Library library )
    {
        String exported = null;
        if ( library != null && library.isExported() )
        {
            exported = "";
        }
        dep.addAttribute( "exported", exported );

        String scope = null;
        if ( library != null && library.getScope() != null )
        {
            scope = library.getScope();
        }
        else if ( a.getScope() != null )
        {
            String inscope = a.getScope();
            if ("provided".equals(inscope) || "runtime".equals(inscope) || "test".equals(inscope))
            {
                scope = inscope.toUpperCase();
            }
            else if ( "system".equals(inscope) )
            {
                scope = "PROVIDED";
            }
        }
        dep.addAttribute( "scope", scope );
    }

    private Element createOrGetElement( Element lib, String name )
    {
        Element el = lib.element( name );

        if ( el == null )
        {
            el = createElement( lib, name );
        }
        return el;
    }

    private void addEarModule( Element module )
    {
        module.addAttribute( "type", "J2EE_APPLICATION_MODULE" );
        Element component = findComponent( module, "ApplicationModuleProperties" );
        addDeploymentDescriptor( component, "application.xml", "1.3",
                                 executedProject.getBuild().getDirectory() + "/application.xml" );
    }

    private void addEjbModule( Element module )
    {
        String ejbVersion = getPluginSetting( "maven-ejb-plugin", "ejbVersion", "2.x" );

        module.addAttribute( "type", "J2EE_EJB_MODULE" );

        String explodedDir = executedProject.getBuild().getDirectory() + "/" + executedProject.getArtifactId();

        Element component = findComponent( module, "EjbModuleBuildComponent" );

        Element setting = findSetting( component, "EXPLODED_URL" );
        setting.addAttribute( "value", getModuleFileUrl( explodedDir ) );

        component = findComponent( module, "EjbModuleProperties" );
        Element deployDescElement =
            addDeploymentDescriptor( component, "ejb-jar.xml", ejbVersion, "src/main/resources/META-INF/ejb-jar.xml" );
        deployDescElement.addAttribute( "optional", ejbVersion.startsWith( "3" ) + "" );

        removeOldElements( component, "containerElement" );
        List artifacts = executedProject.getTestArtifacts();
        for ( Iterator i = artifacts.iterator(); i.hasNext(); )
        {
            Artifact artifact = (Artifact) i.next();

            Element containerElement = createElement( component, "containerElement" );

            if ( linkModules && isReactorProject( artifact.getGroupId(), artifact.getArtifactId() ) )
            {
                containerElement.addAttribute( "type", "module" );
                containerElement.addAttribute( "name", artifact.getArtifactId() );
                Element methodAttribute = createElement( containerElement, "attribute" );
                methodAttribute.addAttribute( "name", "method" );
                methodAttribute.addAttribute( "value", "6" );
                Element uriAttribute = createElement( containerElement, "attribute" );
                uriAttribute.addAttribute( "name", "URI" );
                uriAttribute.addAttribute( "value", "/lib/" + artifact.getArtifactId() + ".jar" );
            }
            else if ( artifact.getFile() != null )
            {
                containerElement.addAttribute( "type", "library" );
                containerElement.addAttribute( "level", "module" );

                //no longer needed in IntelliJ 6
                if ( StringUtils.isEmpty( ideaVersion ) || !ideaVersion.startsWith( "6" ) )
                {
                    containerElement.addAttribute( "name", artifact.getArtifactId() );
                }

                Element methodAttribute = createElement( containerElement, "attribute" );
                methodAttribute.addAttribute( "name", "method" );
                methodAttribute.addAttribute( "value", "2" );
                Element uriAttribute = createElement( containerElement, "attribute" );
                uriAttribute.addAttribute( "name", "URI" );
                uriAttribute.addAttribute( "value", "/lib/" + artifact.getFile().getName() );
                Element urlElement = createElement( containerElement, "url" );
                urlElement.setText( getLibraryUrl( artifact ) );
            }
        }
    }

    private void extractMacro( String path )
    {
        if ( macros != null )
        {
            Pattern p = Pattern.compile( ".*\\$([^\\$]+)\\$.*" );
            Matcher matcher = p.matcher( path );
            while ( matcher.find() )
            {
                String macro = matcher.group( 1 );
                macros.add( macro );
            }
        }
    }

    private Library findLibrary( Artifact a )
    {
        if ( libraries != null )
        {
            for ( int j = 0; j < libraries.length; j++ )
            {
                Library library = libraries[j];
                if ( a.getArtifactId().equals( library.getName() ) )
                {
                    return library;
                }
            }
        }

        return null;
    }

    private List getExcludedDirectories( File target, List excludeList, List sourceFolders )
    {
        List foundFolders = new ArrayList();

        int totalDirs = 0, excludedDirs = 0;

        if ( target.exists() && !excludeList.contains( target.getAbsolutePath() ) )
        {
            File[] files = target.listFiles();

            for ( int i = 0; i < files.length; i++ )
            {
                File file = files[i];
                if ( file.isDirectory() && !excludeList.contains( file.getAbsolutePath() ) )
                {
                    totalDirs++;

                    String absolutePath = file.getAbsolutePath();
                    String url = getModuleFileUrl( absolutePath );

                    boolean addToExclude = true;
                    for ( Iterator sources = sourceFolders.iterator(); sources.hasNext(); )
                    {
                        String source = ( (Element) sources.next() ).attributeValue( "url" );
                        if ( source.equals( url ) )
                        {
                            addToExclude = false;
                            break;
                        }
                        else if ( source.indexOf( url ) == 0 )
                        {
                            foundFolders.addAll(
                                getExcludedDirectories( new File( absolutePath ), excludeList, sourceFolders ) );
                            addToExclude = false;
                            break;
                        }
                    }
                    if ( addToExclude )
                    {
                        excludedDirs++;
                        foundFolders.add( absolutePath );
                    }
                }
            }

            //if all directories are excluded, then just exclude the parent directory
            if ( totalDirs > 0 && totalDirs == excludedDirs )
            {
                foundFolders.clear();

                foundFolders.add( target.getAbsolutePath() );
            }
        }
        else if ( !target.exists() )
        {
            //might as well exclude a non-existent dir so that it won't show when it suddenly appears
            foundFolders.add( target.getAbsolutePath() );
        }

        return foundFolders;
    }

    /**
     * Adds the Web module to the (.iml) project file.
     *
     * @param module Xpp3Dom element
     */
    private void addWebModule( Element module )
    {
        // TODO: this is bad - reproducing war plugin defaults, etc!
        //   --> this is where the OGNL out of a plugin would be helpful as we could run package first and
        //       grab stuff from the mojo

        String warWebapp = executedProject.getBuild().getDirectory() + "/" + executedProject.getArtifactId();
        String warSrc = getPluginSetting( "maven-war-plugin", "warSourceDirectory", "src/main/webapp" );
        String webXml = warSrc + "/WEB-INF/web.xml";

        module.addAttribute( "type", "J2EE_WEB_MODULE" );

        Element component = findComponent( module, "WebModuleBuildComponent" );
        Element setting = findSetting( component, "EXPLODED_URL" );
        setting.addAttribute( "value", getModuleFileUrl( warWebapp ) );

        component = findComponent( module, "WebModuleProperties" );

        removeOldElements( component, "containerElement" );
        List artifacts = executedProject.getTestArtifacts();
        for ( Iterator i = artifacts.iterator(); i.hasNext(); )
        {
            Artifact artifact = (Artifact) i.next();

            Element containerElement = createElement( component, "containerElement" );

            if ( linkModules && isReactorProject( artifact.getGroupId(), artifact.getArtifactId() ) )
            {
                containerElement.addAttribute( "type", "module" );
                containerElement.addAttribute( "name", artifact.getArtifactId() );
                Element methodAttribute = createElement( containerElement, "attribute" );
                methodAttribute.addAttribute( "name", "method" );
                methodAttribute.addAttribute( "value", "5" );
                Element uriAttribute = createElement( containerElement, "attribute" );
                uriAttribute.addAttribute( "name", "URI" );
                uriAttribute.addAttribute( "value", "/WEB-INF/lib/" + artifact.getArtifactId() + "-"
                    + artifact.getVersion() + ".jar" );
            }
            else if ( artifact.getFile() != null )
            {
                containerElement.addAttribute( "type", "library" );
                containerElement.addAttribute( "level", "module" );
                Element methodAttribute = createElement( containerElement, "attribute" );
                methodAttribute.addAttribute( "name", "method" );
                if ( Artifact.SCOPE_PROVIDED.equalsIgnoreCase( artifact.getScope() )
                    || Artifact.SCOPE_SYSTEM.equalsIgnoreCase( artifact.getScope() )
                    || Artifact.SCOPE_TEST.equalsIgnoreCase( artifact.getScope() ) )
                {
                    // If scope is provided, system or test - do not package.
                    methodAttribute.addAttribute( "value", "0" );
                }
                else
                {
                    methodAttribute.addAttribute( "value", "1" ); // IntelliJ 5.0.2 is bugged and doesn't read it
                }
                Element uriAttribute = createElement( containerElement, "attribute" );
                uriAttribute.addAttribute( "name", "URI" );
                uriAttribute.addAttribute( "value", "/WEB-INF/lib/" + artifact.getFile().getName() );
                Element url = createElement( containerElement, "url" );
                url.setText( getLibraryUrl( artifact ) );
            }
        }

        addDeploymentDescriptor( component, "web.xml", "2.3", webXml );

        Element element = findElement( component, "webroots" );
        removeOldElements( element, "root" );

        element = createElement( element, "root" );
        element.addAttribute( "relative", "/" );
        element.addAttribute( "url", getModuleFileUrl( warSrc ) );
    }

    private void addPluginModule( Element module )
    {
        module.addAttribute( "type", "PLUGIN_MODULE" );

        // this is where the META-INF/plugin.xml file is located
        Element pluginDevElement = findElement( module, "component", "DevKit.ModuleBuildProperties" );
        String urlVal = pluginDevElement.attributeValue( "url" );
        if (urlVal == null || urlVal.trim().length() < 1)
            pluginDevElement.addAttribute( "url", getModuleFileUrl( "src/main/resources/META-INF/plugin.xml" ) );
    }

    /**
     * Translate the relative path of the file into module path
     *
     * @param basedir File to use as basedir
     * @param path    Absolute path string to translate to ModuleFileUrl
     * @return moduleFileUrl Translated Module File URL
     */
    private String getModuleFileUrl( File basedir, String path )
    {
        return "file://$MODULE_DIR$/" + toRelative( basedir.getAbsolutePath(), path );
    }

    private String getModuleFileUrl( String file )
    {
        return getModuleFileUrl( executedProject.getBasedir(), file );
    }

    /**
     * Adds a sourceFolder element to IDEA (.iml) project file
     *
     * @param content   Xpp3Dom element
     * @param directory Directory to set as url.
     * @param isTest    True if directory isTestSource.
     */
    private void addSourceFolder( Element content, String directory, boolean isTest )
    {
        if ( !StringUtils.isEmpty( directory ) && new File( directory ).isDirectory() )
        {
            Element sourceFolder = createElement( content, "sourceFolder" );
            sourceFolder.addAttribute( "url", getModuleFileUrl( directory ) );
            sourceFolder.addAttribute( "isTestSource", Boolean.toString( isTest ) );
        }
    }

    private void addExcludeFolder( Element content, String directory )
    {
        Element excludeFolder = createElement( content, "excludeFolder" );
        excludeFolder.addAttribute( "url", getModuleFileUrl( directory ) );
    }

    private boolean isReactorProject( String groupId, String artifactId )
    {
        if ( reactorProjects != null )
        {
            for ( Iterator j = reactorProjects.iterator(); j.hasNext(); )
            {
                MavenProject p = (MavenProject) j.next();
                if ( p.getGroupId().equals( groupId ) && p.getArtifactId().equals( artifactId ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    private void resolveClassifier( Element element, Artifact a, String classifier )
    {
        String id = a.getId() + '-' + classifier;

        String path;
        if ( attemptedDownloads.containsKey( id ) )
        {
            getLog().debug( id + " was already downloaded." );
            path = (String) attemptedDownloads.get( id );
        }
        else
        {
            getLog().debug( id + " was not attempted to be downloaded yet: trying..." );
            path = resolveClassifiedArtifact( a, classifier );
            attemptedDownloads.put( id, path );
        }

        if ( path != null )
        {
            String jarPath = "jar://" + path + "!/";
            getLog().debug( "Setting " + classifier + " for " + id + " to " + jarPath );
            removeOldElements( element, "root" );
            createElement( element, "root" ).addAttribute( "url", jarPath );
        }
    }

    private String resolveClassifiedArtifact( Artifact artifact, String classifier )
    {
        String basePath = artifact.getFile().getAbsolutePath().replace( '\\', '/' );
        int delIndex = basePath.indexOf( ".jar" );
        if ( delIndex < 0 )
        {
            return null;
        }

        List remoteRepos = executedProject.getRemoteArtifactRepositories();
        try
        {
            Artifact classifiedArtifact = artifactFactory.createArtifactWithClassifier( artifact.getGroupId(),
                                                                                        artifact.getArtifactId(),
                                                                                        artifact.getVersion(),
                                                                                        artifact.getType(),
                                                                                        classifier );
            String dstFilename = basePath.substring( 0, delIndex ) + '-' + classifier + ".jar";
            File dstFile = new File( dstFilename );
            classifiedArtifact.setFile( dstFile );
            //this check is here because wagonManager does not seem to check if the remote file is newer
            //    or such feature is not working
            if ( !dstFile.exists() )
            {
                wagonManager.getArtifact( classifiedArtifact, remoteRepos );
            }
            return dstFile.getAbsolutePath().replace( '\\', '/' );
        }
        catch ( TransferFailedException e )
        {
            getLog().debug( e );
            return null;
        }
        catch ( ResourceDoesNotExistException e )
        {
            getLog().debug( e );
            return null;
        }
    }

    /**
     * Returns an Xpp3Dom element (setting).
     *
     * @param component Xpp3Dom element
     * @param name      Setting attribute to find
     * @return setting Xpp3Dom element
     */
    private Element findSetting( Element component, String name )
    {
        return findElement( component, "setting", name );
    }

    private String getLibraryUrl( Artifact artifact )
    {
        return "jar://" + convertDriveLetter( artifact.getFile().getAbsolutePath() ).replace( '\\', '/' ) + "!/";
    }

    private Element addDeploymentDescriptor( Element component, String name, String version, String file )
    {
        Element deploymentDescriptor = findElement( component, "deploymentDescriptor" );

        if ( deploymentDescriptor.attributeValue( "version" ) == null )
        {
            deploymentDescriptor.addAttribute( "version", version );
        }

        if ( deploymentDescriptor.attributeValue( "name" ) == null )
        {
            deploymentDescriptor.addAttribute( "name", name );
        }

        deploymentDescriptor.addAttribute( "optional", "false" );

        if ( deploymentDescriptorFile == null )
        {
            deploymentDescriptorFile = file;
        }

        deploymentDescriptor.addAttribute( "url", getModuleFileUrl( deploymentDescriptorFile ) );

        return deploymentDescriptor;
    }

}
