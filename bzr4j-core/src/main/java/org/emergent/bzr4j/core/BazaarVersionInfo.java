package org.emergent.bzr4j.core;

/**
 * A simple container for the revision-info values.
 *
 * @author Guillermo Gonzalez <guillo.gonzo@gmail.com>
 *
 */
public final class BazaarVersionInfo {

  private final BazaarRevision revno;

  private final BazaarRevision revid;

  private final String branchNick;

  private final String date;

  private final String buildDate;

  public static BazaarVersionInfo parse(String versionInfoStr) {
    String[] lines = versionInfoStr.split("\n");
    final BazaarRevision revid = BazaarRevision.getRevision(BazaarRevision.Prefix.REVID, lines[0].split(":")[1]);
    String date = lines[1].split(":")[1];
    String buildDate = lines[2].split(":")[1];
    final BazaarRevision revno = BazaarRevision.getRevision(BazaarRevision.Prefix.REVNO, lines[3].split(":")[1]);
    final String nick = lines[4].split(":")[1];
    return new BazaarVersionInfo(revno, revid, nick, date, buildDate);
  }

  private BazaarVersionInfo(BazaarRevision revno, BazaarRevision revid, String branchNick,
      String date, String buildDate) {
    this.revno = revno;
    this.revid = revid;
    this.branchNick = branchNick;
    this.date = date;
    this.buildDate = buildDate;
  }

  public BazaarRevision getRevno() {
    return revno;
  }

  public BazaarRevision getRevid() {
    return revid;
  }

  public String getBranchNick() {
    return branchNick;
  }

  public String getDate() {
    return date;
  }

  public String getBuildDate() {
    return buildDate;
  }

}
