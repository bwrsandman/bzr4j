/**
 * LICENSE + COPYRIGHT
 */
package org.emergent.bzr4j.commandline;

import org.emergent.bzr4j.core.IPlugin;

/**
 * Implementation of {@link IPlugin} for the commandline adapter.<br>
 *
 * @author Guillermo Gonzalez <guillo.gonzo AT gmail DOT com>
 *
 */
public class CommandLinePlugin implements IPlugin
{

    private String description;

    private String name;

    private String path;

    private String[] version;

    /**
     * Full Constructor.<br>
     *
     * @param description
     * @param name
     * @param path
     * @param version
     */
    public CommandLinePlugin( String description, String name, String path, String version )
    {
        super();
        this.description = description;
        this.name = name;
        this.path = path;
        if ( version != null )
            this.version = version.split( "\\." );
    }

    public String getDescription()
    {
        return description;
    }

    public String getName()
    {
        return name;
    }

    public String getPath()
    {
        return path;
    }

    public String[] getVersion()
    {
        return version;
    }

}
