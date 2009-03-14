/*
 * Copyright 2000-2007 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.buildServer.buildTriggers.vcs.bzr;

import jetbrains.buildServer.TempFiles;
import jetbrains.buildServer.util.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Pavel.Sher
 *         Date: 14.07.2008
 */
public class LocalRepositoryUtil
{
    private final static TempFiles myTempFiles = new TempFiles();

    private final static Map<String, File> myRepositories = new HashMap<String, File>();

    static
    {
        Runtime.getRuntime().addShutdownHook( new Thread( new Runnable()
        {
            public void run()
            {
                myTempFiles.cleanup();
            }
        } ) );
    }

    public static File prepareRepository( @NotNull String repPath ) throws IOException
    {
        File repository = myRepositories.get( repPath );
        if ( repository != null ) return repository;
        final File tempDir = myTempFiles.createTempDir();
        FileUtil.copyDir( new File( repPath ), tempDir );
        if ( new File( tempDir, "bzr" ).isDirectory() )
        {
            FileUtil.rename( new File( tempDir, "bzr" ), new File( tempDir, ".bzr" ) );
        }
        myRepositories.put( repPath, tempDir );
        return tempDir;
    }

    public static void forgetRepository( @NotNull String repPath )
    {
        myRepositories.remove( repPath );
    }
}
