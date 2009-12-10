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

import jetbrains.buildServer.BaseTestCase;
import jetbrains.buildServer.vcs.VcsException;
import jetbrains.buildServer.buildTriggers.vcs.bzr.Settings;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class BaseCommandTestCase extends BaseTestCase
{
    private String myRepository;

    protected void setRepository( final String repository )
    {
        myRepository = repository;
    }

    protected <T> T runCommand( CommandExecutor<T> executor ) throws IOException, VcsException
    {
//        final Settings settings = new Settings();
//        settings.setBzrCommandPath( TCTestUtils.EXE_PATH );
//        File repository =
//                LocalRepositoryUtil.prepareRepository( new File( myRepository ).getAbsolutePath() );
//        settings.setRepository( repository.getAbsolutePath() );
//        TempFiles tf = new TempFiles();
//        File parentDir = tf.createTempDir();
//        settings.setWorkingDir( new File( parentDir, "rep" ).getAbsoluteFile() );
//        try
//        {
//            CloneCommand cl = new CloneCommand( settings );
//            cl.setDestDir( settings.getLocalRepositoryDir().getAbsolutePath() );
//            cl.execute();
//
//            return executor.execute( settings );
//        }
//        finally
//        {
//            tf.cleanup();
//        }
        return null;
    }

    public interface CommandExecutor<T>
    {
        T execute( @NotNull Settings settings ) throws VcsException;
    }
}
