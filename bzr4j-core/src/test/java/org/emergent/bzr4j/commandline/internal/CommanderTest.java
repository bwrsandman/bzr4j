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

import org.emergent.bzr4j.core.BazaarStatusKind;
import org.emergent.bzr4j.testUtils.BazaarTest;
import org.emergent.bzr4j.testUtils.Environment;
import static org.emergent.bzr4j.testUtils.FileUtils.addContentToFile;
import org.testng.annotations.Test;

import java.io.File;

/**
 * @author Patrick Woodworth
 */
public class CommanderTest extends BazaarTest
{
    @Test
    public final void testBuildStatusTree() throws Exception
    {
        final Environment testEnv;
        testEnv = new Environment( "basicAdd", getTestConfig() );
        Commander cmdr = new Commander()
        {
            public File getDefaultWorkDir()
            {
                return testEnv.getWorkingTreeLocation();
            }

            public String getBzrExePath()
            {
                return "bzr";
            }
        };
        cmdr.status().execAndBuildTreeStatus();
    }

    @Test
    public final void testAdd() throws Exception
    {
        Environment testEnv;
        testEnv = new Environment( "basicAdd", getTestConfig() );
        client.setWorkDir( testEnv.getWorkingTreeLocation() );

        File newDir = new File( testEnv.getWorkingTreeLocation(), "new_Dir" );
        newDir.mkdirs();
        File newFile = new File( newDir, "new_File.txt" );
        addContentToFile( newFile, "a new File" );

        client.add( new File[]{newDir} );
        testEnv.getExpectedWorkingTree().addItem( "new_Dir", null );
        testEnv.getExpectedWorkingTree().setItemStatus( "new_Dir", BazaarStatusKind.CREATED );
        testEnv.getExpectedWorkingTree().addItem( "new_Dir/new_File.txt", "a new File" );
        testEnv.getExpectedWorkingTree()
                .addItem( "new_Dir/a file with spaces in its name.txt", "a file with spaces" );
        testEnv.getExpectedWorkingTree()
                .setItemStatus( "new_Dir/new_File.txt", BazaarStatusKind.CREATED );

        testEnv.checkStatusesExpectedWorkingTree( testEnv.getWorkingTreeLocation() );
    }    
}
