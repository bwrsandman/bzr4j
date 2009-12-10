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

import jetbrains.buildServer.vcs.VcsException;
import org.testng.annotations.Test;

import java.io.IOException;

@Test
public class StatusCommandTest extends BaseCommandTestCase
{
    public void testAddedFile() throws IOException, VcsException
    {
//        setRepository( TCTestUtils.TESTDATA_PATH + "/rep1" );
//        List<ModifiedFile> files = runStatus( "9875b412a788", "1d446e82d356" );
//        assertEquals( 1, files.size() );
//        ModifiedFile md = files.get( 0 );
//        assertEquals( ModifiedFile.Status.ADDED, md.getStatus() );
//        assertEquals( "dir1/file3.txt", md.getPath().replace( File.separatorChar, '/' ) );
    }

    public void testRemovedFile() throws IOException, VcsException
    {
//        setRepository( TCTestUtils.TESTDATA_PATH + "/rep1" );
//        List<ModifiedFile> files = runStatus( "7209b1f1d793", "9522278aa38d" );
//        assertEquals( 1, files.size() );
//        ModifiedFile md = files.get( 0 );
//        assertEquals( ModifiedFile.Status.REMOVED, md.getStatus() );
//        assertEquals( "dir1/file4.txt", md.getPath().replace( File.separatorChar, '/' ) );
    }

    public void testModifiedFile() throws IOException, VcsException
    {
//        setRepository( TCTestUtils.TESTDATA_PATH + "/rep1" );
//        List<ModifiedFile> files = runStatus( "9522278aa38d", "b06a290a363b" );
//        assertEquals( 1, files.size() );
//        ModifiedFile md = files.get( 0 );
//        assertEquals( ModifiedFile.Status.MODIFIED, md.getStatus() );
//        assertEquals( "dir1/file3.txt", md.getPath().replace( File.separatorChar, '/' ) );
    }

//    private List<ModifiedFile> runStatus( final String fromId, final String toId )
//            throws IOException, VcsException
//    {
//        return runCommand( new CommandExecutor<List<ModifiedFile>>()
//        {
//            public List<ModifiedFile> execute( @NotNull final Settings settings )
//                    throws VcsException
//            {
//                StatusCommand st = new StatusCommand( settings );
//                st.setFromRevId( fromId );
//                st.setToRevId( toId );
//                try
//                {
//                    return st.execute();
//                }
//                catch ( CommandException e )
//                {
//                    throw new VcsException( e );
//                }
//            }
//        } );
//    }

}
