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

import org.testng.annotations.Test;

@Test
public class LogCommandTest extends BaseCommandTestCase
{
    public void testOneChangeSet() throws Exception
    {
//        setRepository( TCTestUtils.TESTDATA_PATH + "/rep1" );
//        final String toId = "9875b412a788";
//        List<ChangeSet> changes = runLog( null, toId );
//        assertEquals( 1, changes.size() );
//        final ChangeSet changeSet = changes.get( 0 );
//        assertEquals( 0, changeSet.getRevNumber() );
//        assertEquals( toId, changeSet.getId() );
//        assertEquals( "pavel@localhost", changeSet.getUser() );
//        assertEquals( "dir1 created", changeSet.getSummary() );
    }

    public void testMoreThanOneChangeSet() throws Exception
    {
//        setRepository( TCTestUtils.TESTDATA_PATH + "/rep1" );
//        final String fromId = "9875b412a788";
//        final String toId = "7209b1f1d793";
//        List<ChangeSet> changes = runLog( fromId, toId );
//        assertEquals( 3, changes.size() );
//        ChangeSet changeSet1 = changes.get( 0 );
//        final ChangeSet changeSet2 = changes.get( 1 );
//        final ChangeSet changeSet3 = changes.get( 2 );
//        assertEquals( "dir1 created", changeSet1.getSummary() );
//        assertEquals( "new file added", changeSet2.getSummary() );
//        assertEquals( "file4.txt added", changeSet3.getSummary() );
//
//        changes = runLog( null, toId );
//        assertEquals( 3, changes.size() );
//        changeSet1 = changes.get( 2 );
//        assertEquals( "file4.txt added", changeSet1.getSummary() );
    }

//    private List<ChangeSet> runLog( final String fromId, final String toId )
//            throws IOException, VcsException
//    {
//        return runCommand( new CommandExecutor<List<ChangeSet>>()
//        {
//            public List<ChangeSet> execute( @NotNull final Settings settings ) throws VcsException
//            {
//                LogCommand lc = new LogCommand( settings );
//                lc.setFromRevId( fromId );
//                lc.setToRevId( toId );
//                try
//                {
//                    return lc.execute();
//                }
//                catch ( CommandException e )
//                {
//                    throw new VcsException( e );
//                }
//            }
//        } );
//    }
}
