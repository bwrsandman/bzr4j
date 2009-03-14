package org.emergent.bzr4j.commandline.commands;

import java.io.File;

import org.emergent.bzr4j.commandline.syntax.IRevisionInfoOptions;

public class RevisionInfo extends SingleFileCommand implements IRevisionInfoOptions
{

    public RevisionInfo( final File workDir )
    {
        super( workDir );
    }

    @Override
    public String getCommand()
    {
        return COMMAND;
    }

}
