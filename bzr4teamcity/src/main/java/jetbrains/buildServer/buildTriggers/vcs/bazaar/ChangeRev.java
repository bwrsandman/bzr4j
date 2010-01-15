/*
 * Copyright 2010 Tripwire, Inc. All Rights Reserved.
 */
package jetbrains.buildServer.buildTriggers.vcs.bazaar;

/**
 * @author Patrick Woodworth
 */
public class ChangeRev {

  private final String myId;

  public ChangeRev(final String fullVersion) {
    myId = fullVersion;
  }

  /**
   * Returns changeset revision id
   * @return changeset revision id
   */
  public String getId() {
    return myId;
  }

  /**
   * Returns full changeset version as reported by bzr log command: revnum:revid
   * @return full changeset version as reported by bzr log
   */
  public String getFullVersion() {
//    return myRevNumber + ":" + myId;
    return myId;
  }
}
