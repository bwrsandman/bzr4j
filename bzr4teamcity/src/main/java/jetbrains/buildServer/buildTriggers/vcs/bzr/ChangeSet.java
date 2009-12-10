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

import java.util.Date;

/**
 * Represents Bazaar change set
 */
public class ChangeSet
{
    private int myRevNumber;

    private String myId;

    private String myUser;

    private Date myTimestamp;

    private String mySummary;

    public ChangeSet( final int revNumber, final String id )
    {
        myRevNumber = revNumber;
        myId = id;
    }

    /**
     * Constructor for version in the form revnum:changeset_id or just changeset_id (in this case rev number is set to -1)
     * @param fullVersion full changeset version as reported by bzr log command
     */
    public ChangeSet( final String fullVersion )
    {
        try
        {
//      int colon = fullVersion.indexOf(":");
//      if (colon != -1) {
//        myRevNumber = Integer.parseInt(fullVersion.substring(0, colon));
//        myId = fullVersion.substring(colon+1);
//      } else {
//        myRevNumber = -1;
            myRevNumber = Integer.parseInt( fullVersion );
            myId = fullVersion;
//      }
        }
        catch ( Throwable e )
        {
            throw new IllegalArgumentException( e );
        }
    }

    public void setUser( final String user )
    {
        myUser = user;
    }

    public void setTimestamp( final Date timestamp )
    {
        myTimestamp = timestamp;
    }

    public void setSummary( final String summary )
    {
        mySummary = summary;
    }

    /**
     * Returns changeset revision id
     * @return changeset revision id
     */
    public String getId()
    {
        return myId;
    }

    /**
     * Returns changeset revision number
     * @return changeset revision number
     */
    public int getRevNumber()
    {
        return myRevNumber;
    }

    /**
     * Returns full changeset version as reported by bzr log command: revnum:revid
     * @return full changeset version as reported by bzr log
     */
    public String getFullVersion()
    {
//    return myRevNumber + ":" + myId;
        return myId;
    }

    /**
     * Returns user who made changeset
     * @return user who made changeset
     */
    public String getUser()
    {
        return myUser;
    }

    /**
     * Returns changeset timestamp
     * @return changeset timestamp
     */
    public Date getTimestamp()
    {
        return myTimestamp;
    }

    /**
     * Returns changeset summary specified by user
     * @return changeset summary
     */
    public String getSummary()
    {
        return mySummary;
    }
}
