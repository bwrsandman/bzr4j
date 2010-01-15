/*
 * Copyright (c) 2009 Patrick Woodworth.
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
package org.emergent.bzr4j.commandline.internal;

import java.io.File;

/**
 * @author Patrick Woodworth
 */
public abstract class Commander
{
    private String[] m_stdArgs = new String[] { "--no-aliases" };

    protected Commander()
    {
    }

    public abstract File getDefaultWorkDir();

    public abstract String getBzrExePath();

    public Cmd rawCmd( String name )
    {
        Cmd retval = new Cmd( getDefaultWorkDir(), getBzrExePath(), name );
        return retval.addOpts( m_stdArgs );
    }

    public Cmd cat()
    {
        Cmd retval = new Cmd( getDefaultWorkDir(), getBzrExePath(), "cat" );
        return retval.addOpts( m_stdArgs );
    }

    public Cmd deleted()
    {
        Cmd retval = new Cmd( getDefaultWorkDir(), getBzrExePath(), "deleted" );
        return retval.addOpts( m_stdArgs );
    }

    public Cmd export()
    {
        Cmd retval = new Cmd( getDefaultWorkDir(), getBzrExePath(), "export" );
        return retval.addOpts( m_stdArgs );
    }

    public Cmd log()
    {
        Cmd retval = new Cmd( getDefaultWorkDir(), getBzrExePath(), "log" );
        return retval.addOpts( m_stdArgs );
    }

    public Cmd ls()
    {
        Cmd retval = new Cmd( getDefaultWorkDir(), getBzrExePath(), "ls" );
        return retval.addOpts( m_stdArgs );
    }

    public Cmd push()
    {
        Cmd retval = new Cmd( getDefaultWorkDir(), getBzrExePath(), "push" );
        return retval.addOpts( m_stdArgs );
    }

    public Cmd tag()
    {
        Cmd retval = new Cmd( getDefaultWorkDir(), getBzrExePath(), "tag" );
        return retval.addOpts( m_stdArgs );
    }

    public ZStatusCmd status()
    {
        ZStatusCmd retval = new ZStatusCmd( getDefaultWorkDir(), getBzrExePath() );
        return retval.addOpts( m_stdArgs );
    }
}
