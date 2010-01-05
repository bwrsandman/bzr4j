/*
 * Copyright (c) 2009 Tripwire, Inc.
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
package org.emergent.bzr4j.commandline.commands;

import org.emergent.bzr4j.commandline.syntax.IBugsOptions;
import org.emergent.bzr4j.core.BranchLocation;

import java.io.File;
import java.util.List;

/**
 * Bugs command (get bzr bugs from metadata)
 *
 * @author Phan Minh Thang
 */
public class Bugs extends SingleFileCommand implements IBugsOptions
{
    private final BranchLocation location;

    public Bugs( final File workDir, final BranchLocation location )
    {
        super( workDir, null );
        this.location = location;
    }

    public String getCommand()
    {
        return COMMAND;
    }

    protected List<String> getArguments()
    {
        return getArguments( location.toString() );
    }
}
