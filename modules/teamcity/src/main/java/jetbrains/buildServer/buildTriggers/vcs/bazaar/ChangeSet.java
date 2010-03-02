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
package jetbrains.buildServer.buildTriggers.vcs.bazaar;

import java.util.Date;

/**
 * Represents Bazaar change set
 */
public class ChangeSet extends ChangeRev {

  private String myUser;

  private Date myTimestamp;

  private String mySummary;

  public ChangeSet(String version, Date date, String author, String message) {
    super(version);
    myTimestamp = date;
    myUser = author;
    mySummary = message;
  }

  /**
   * Returns user who made changeset
   * @return user who made changeset
   */
  public String getUser() {
    return myUser;
  }

  /**
   * Returns changeset timestamp
   * @return changeset timestamp
   */
  public Date getTimestamp() {
    return myTimestamp;
  }

  /**
   * Returns changeset summary specified by user
   * @return changeset summary
   */
  public String getSummary() {
    return mySummary;
  }
}
